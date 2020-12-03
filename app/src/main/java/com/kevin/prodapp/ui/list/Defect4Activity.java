package com.kevin.prodapp.ui.list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.MyTagHandler;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class Defect4Activity extends AppCompatActivity {

    private String serverUrl;
    private Bundle bunde;
    private String dpUser;
    private Bitmap decodedByte;
    private SharePManager sPManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect4);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息

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

        state.setText("已修复");

        final String data = new String(bunde.getString("data"));
        try {
            JSONObject object = null;
            try {
                object = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String DDesc = "";
            String detaildata = object.getString("DDesc");
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
            tcDDText.setScrollbarFadingEnabled(false);
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
            tcDDText.setText(charsequence);
        } catch (Exception e) {
            e.toString();
        }

        Button reOpen=findViewById(R.id.reOpen);
        reOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //获取参数
                    Properties props = new Properties();
                    props.load(Defect4Activity.this.getAssets().open("config.properties"));
                    String serverUrl = props.getProperty("servers_url");;
                    //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                    String sPMangerIP=sPManger.getString("servers_url");
                    if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                        serverUrl=sPMangerIP;
                    }
                    String path1 = serverUrl + "/getformdata";
                    SharePManager sharePManager = new SharePManager(Defect4Activity.this, SharePManager.USER_FILE_NAME);
                    String cookie = sharePManager.getString("cookie");
                    String jksessionid = sharePManager.getString("jksessionid");
                    Map<String, String> map = new HashMap<>();
                    map.put("cookie", cookie);
                    map.put("jksessionid", jksessionid);
                    String param = data;
                    JSONObject json = new JSONObject(data);
                    String xtSid = json.getString("xtSid");
                    HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                    String result1 = httpsUrlConnection.post(path1, xtSid, map, Defect4Activity.this);

                    Object OSid = null;
                    JSONObject jsonObject = new JSONObject(result1);
//                    jsonObject.put("DRespUser",dpUser);
//////                    jsonObject.put("xtStatus","3902");

                    JSONObject jsonObject1 = new JSONObject(jsonObject.get("data").toString());
                    String datajson = jsonObject1.getString("dataItem");
                    JSONArray jsonArray = new JSONArray(jsonObject1.getString("operations"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getString("ODisplay").equals("重新打开")) {
                            OSid = jsonArray.getJSONObject(i).get("OSid");
                        }
                    }
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("param", datajson);
                    jsonObject2.put("oSid", OSid);
                    String param1 = jsonObject2.toString();

                    String path = serverUrl + "/exeOpen";

                    String result = httpsUrlConnection.post(path, param1, map, Defect4Activity.this);
                    JSONObject object = new JSONObject(result);

                    String param2 = jsonObject2.toString();
                    if (object.getString("code").equals("0")) {
                        //刷新缺陷列表

                        Intent intent = new Intent();
                        intent.putExtras(bunde);
                        intent.putExtra("dpUser", dpUser);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(Defect4Activity.this, Defect3Activity.class);
                        startActivity(intent);
                        Defect4Activity.this.finish();
                        Toast.makeText(getApplicationContext(), "打开成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "打开失败！失败原因：" + object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "打开失败！失败原因" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        //关闭按钮
        Button close=findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
