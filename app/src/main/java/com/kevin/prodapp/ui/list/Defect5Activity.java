package com.kevin.prodapp.ui.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.utils.MyTagHandler;
import com.kevin.prodapp.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Defect5Activity extends AppCompatActivity {

    private String serverUrl;
    private Bundle bunde;
    private String dpUser;
    private String detaildata;
    private Bitmap decodedByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect5);

        //退出
        SysApplication.getInstance().addActivity(this);

        TextView tcNoText = findViewById(R.id.tcno);
        TextView tcDseText = findViewById(R.id.DSeverity);
        TextView tcDpText = findViewById(R.id.DPriority);

        TextView tcDSUText = findViewById(R.id.DSubmitUser);
        TextView tcDSTText = findViewById(R.id.dsubmitTime);
        TextView tcDSVText = findViewById(R.id.DSubmitVersion);
        TextView tcDSText = findViewById(R.id.DSummary);
        TextView tcDDText = findViewById(R.id.DDesc);
        tcDDText.setMovementMethod(ScrollingMovementMethod.getInstance());
//        LinearLayout linearLayout = findViewById(R.id.ll_group);
        final Bundle bunde = this.getIntent().getExtras();
        final String dpUser = bunde.getString("dpUser");
        TextView DRespUser = findViewById(R.id.DRespUser);
        DRespUser.setText(dpUser);
        TextView state = findViewById(R.id.state);

        state.setText("拒绝");

        final String data = new String(bunde.getString("data"));
        try {
            JSONObject object = null;
            try {
                object = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String DDesc = "";
             detaildata = object.getString("DDesc");
            if (detaildata.indexOf("<img") != -1) {
                DDesc = detaildata.substring(0, detaildata.indexOf("<img"));
                String imgString = detaildata.substring(detaildata.indexOf("<img"));
                String imgs[] = imgString.split("<img");
//                for (String img : imgs) {
                ImageView imageView = new ImageView(this);
                String base64 = detaildata.substring(detaildata.indexOf("base64") + 7, detaildata.indexOf("\" />"));
                byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                 decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                imageView.setImageBitmap(decodedByte);
//                linearLayout.addView(imageView);
            } else {
                DDesc = detaildata;
            }

            String titleName = object.getString("DSno");

            TextView title = findViewById(R.id.txt_title);
            title.setText(titleName);
            tcNoText.setText(object.getString("DSno"));
            String DSeverity = object.getString("DSeverity");
            switch (DSeverity) {
                case "3501":
                    DSeverity = "灾难";
                    break;
                case "3502":
                    DSeverity = "严重";
                    break;
                case "3503":
                    DSeverity = "一般";
                    break;
                case "3504":
                    DSeverity = "轻微";
                    break;
            }
            String DPriority = object.getString("DPriority");
            switch (DPriority) {
                case "3401":
                    DPriority = "高";
                    break;
                case "3402":
                    DPriority = "中";
                    break;
                case "3403":
                    DPriority = "低";
                    break;
            }
            tcDseText.setText(DSeverity);
            tcDpText.setText(DPriority);
            tcDSVText.setText(object.getString("DSubmitVersion"));
            tcDSText.setText(object.getString("DSummary"));

            tcDSUText.setText(object.getString("DSubmitUser"));
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
            Date date = new Date();
            date.setTime(Long.parseLong(object.getString("dsubmitTime")));
            tcDSTText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
            //描述设置图文显示
            tcDDText.setMovementMethod(LinkMovementMethod.getInstance());

            MyTagHandler tagHandler = new MyTagHandler(this);
            String html=detaildata;

            CharSequence charsequence= Html.fromHtml(html,new Html.ImageGetter(){
                @Override
                public Drawable getDrawable(String source) {
                    Drawable d = null;
                    try {
                        Bitmap bm = decodedByte;
                        d = new BitmapDrawable(bm);
                        d.setBounds(0, 0, 100, 50);

                    } catch (Exception e)
                    {e.printStackTrace();}

                    return d;
                }

            },tagHandler);
            tcDDText.setScrollbarFadingEnabled(false);
            tcDDText.setText(charsequence);
        } catch (Exception e) {
            e.toString();
        }
    }
}