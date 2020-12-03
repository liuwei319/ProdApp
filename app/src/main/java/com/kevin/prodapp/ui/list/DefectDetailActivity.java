package com.kevin.prodapp.ui.list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.prodapp.R;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.MyTagHandler;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class DefectDetailActivity extends Activity {
    private List<String> list_name = new ArrayList<>();
    private ArrayAdapter<String> arr_adapter;
    private Spinner spinner;
    private Map<String, String> linkedHashMap = new LinkedHashMap<String, String>();
    private String DRespUserKey;
    private String username;
    private String serverUrl;
    private Bundle bunde;
    private TextView tcDDText;
    private TextView tcDSText;
    private ImageView image;
    private Bitmap decodedByte;
    private SpannableString spannableString;
    private String value;
    private String detaildata;
    private SharePManager sPManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defect_detail_view);
//退出
        SysApplication.getInstance().addActivity(this);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息


//        TextView casename = findViewById(R.id.casename);
        TextView tcNoText = findViewById(R.id.tcno);
        TextView tcDseText = findViewById(R.id.DSeverity);
        TextView tcDpText = findViewById(R.id.DPriority);
//        Spinner tcDRUText = findViewById(R.id.DRespUser);
        //TextView tcDRUText = findViewById(R.id.DRespUser);
        TextView tcDSUText = findViewById(R.id.DSubmitUser);
        TextView tcDSTText = findViewById(R.id.dsubmitTime);
        TextView tcDSVText = findViewById(R.id.DSubmitVersion);
        tcDSText = findViewById(R.id.DSummary);

        tcDDText = findViewById(R.id.DDesc);
        tcDDText.setMovementMethod(ScrollingMovementMethod.getInstance());
        tcDDText.setScrollbarFadingEnabled(false);
//        LinearLayout linearLayout = findViewById(R.id.ll_group);
        bunde = this.getIntent().getExtras();
        final String data = new String(bunde.getString("data"));
        try {
            JSONObject object = new JSONObject(data);
            String DDesc = "";
            detaildata = object.getString("DDesc");
            if (detaildata.indexOf("<img") != -1) {
                DDesc = detaildata.substring(0, detaildata.indexOf("<img"));
                String imgString = detaildata.substring(detaildata.indexOf("<img"));
                String imgs[] = imgString.split("<img");
//                for (String img : imgs) {
//                ImageView imageView = new ImageView(this);
//                image = findViewById(R.id.defect_imageview2);
                String base64 = detaildata.substring(detaildata.indexOf("base64") + 7, detaildata.indexOf("\" />"));
                byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);


                // 根据路径获得图片并压缩，返回bitmap用于显示

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;


                // Calculate inSampleSize
                options.inSampleSize = DefectAddActivity.calculateInSampleSize(options, 480, 800);
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);


            } else {
                DDesc = detaildata;
            }

            String titleName = object.getString("DSno");

            TextView title = findViewById(R.id.txt_title);
            title.setText(titleName);
//            casename.setText(object.getString("DSummary"));
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

            //tcDRUText.setText(object.getString("DRespUser"));
            //分配用户
            //1、添加数据源，就是下拉菜单选项
            try {
                spinner = findViewById(R.id.DRespUser);
                Properties props = new Properties();
                props.load(DefectDetailActivity.this.getAssets().open("config.properties"));
                String serverUrl = props.getProperty("servers_url");;
                //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                String sPMangerIP=sPManger.getString("servers_url");
                if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                    serverUrl=sPMangerIP;
                }
                String path = serverUrl + "/getDefectUser";
                SharePManager sharePManager = new SharePManager(DefectDetailActivity.this, SharePManager.USER_FILE_NAME);
                String cookie = sharePManager.getString("cookie");
                String jksessionid = sharePManager.getString("jksessionid");
                final Map<String, String> map = new HashMap<>();
                map.put("cookie", cookie);
                map.put("jksessionid", jksessionid);
                HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                String result = httpsUrlConnection.post(path, "", map, DefectDetailActivity.this);

                JSONObject jsonObject = new JSONObject(new JSONObject(result).get("data").toString());
                JSONArray jsonArray = new JSONArray(jsonObject.get("content").toString());
                String key = "";
                String name = "";
                list_name.add(0, "请选择待分配用户");
                for (int i = 1; i < jsonArray.length(); i++) {
                    name = jsonArray.getJSONObject(i).get("value").toString();
                    key = jsonArray.getJSONObject(i).get("key").toString();
                    list_name.add(i, name);
                    linkedHashMap.put(key, name);
                }
                //2、未下来列表定义一个数组适配器
                arr_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_name);
                //3、为适配器设置下拉菜单的样式
                arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //4、将适配器配置到下拉列表上
                spinner.setAdapter(arr_adapter);
                //5、给下拉菜单设置监听事件
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv = (TextView) view;
//                        tv.setTextColor(getResources().getColor(R.color.black));    //设置颜色
//
                        tv.setTextSize((float) 15);    //设置大小
                        tv.setSingleLine(true);
                        // tv.setGravity(Gravity.CENTER_HORIZONTAL);   //设置居中
                        //    tv.setGravity(Gravity.CENTER_VERTICAL);   //设置居中
                        try {
                            JSONObject jsonObject1 = new JSONObject(data);
                            value = jsonObject1.getString("DRespUser");
                            username = list_name.get(position);
                            //设置默认值
                            for (int i = 1; i < arr_adapter.getCount(); i++) {
                                String s = arr_adapter.getItem(i).toString();
                                s = s.substring(0, s.indexOf("["));
                                if (TextUtils.equals(value, s)) {
                                    spinner.setSelection(i, true);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            tcDSUText.setText(object.getString("DSubmitUser"));
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
            Date date = new Date();
            date.setTime(Long.parseLong(object.getString("dsubmitTime")));
            tcDSTText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

            //描述设置图文显示


            MyTagHandler tagHandler = new MyTagHandler(this);
            String html = detaildata;
            tcDDText.setMovementMethod(LinkMovementMethod.getInstance());
            CharSequence charsequence = Html.fromHtml(html, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable d = null;
                    try {
                        Bitmap bm = decodedByte;
                        d = new BitmapDrawable(bm);
                        d.setBounds(0, 0, 100, 50);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return d;
                }

            }, tagHandler);
            tcDDText.setText(charsequence);

        } catch (Exception e) {
            e.toString();
        }

        //获取缺陷列表信息  用于分配用户的参数
        Button button = findViewById(R.id.fenpai);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Properties props = new Properties();
                    props.load(DefectDetailActivity.this.getAssets().open("config.properties"));
                    serverUrl = props.getProperty("servers_url");;
                    //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                    String sPMangerIP=sPManger.getString("servers_url");
                    if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                        serverUrl=sPMangerIP;
                    }
                    String path1 = serverUrl + "/getformdata";
                    SharePManager sharePManager = new SharePManager(DefectDetailActivity.this, SharePManager.USER_FILE_NAME);
                    String cookie = sharePManager.getString("cookie");
                    String jksessionid = sharePManager.getString("jksessionid");
                    Map<String, String> map = new HashMap<>();
                    map.put("cookie", cookie);
                    map.put("jksessionid", jksessionid);
                    String param = data;
                    JSONObject json = new JSONObject(data);
                    String xtSid = json.getString("xtSid");
                    HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                    String result1 = httpsUrlConnection.post(path1, xtSid, map, DefectDetailActivity.this);
                    Object OSid = null;
                    JSONObject jsonObject = new JSONObject(result1);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.get("data").toString());
                    JSONArray jsonArray = new JSONArray(jsonObject1.getString("operations"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getString("ODisplay").equals("分配")) {
                            OSid = jsonArray.getJSONObject(i).get("OSid");
                        }
                    }
                    Iterator it = linkedHashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry en = (Map.Entry) it.next();
                        if (en.getValue().equals(username))
                            DRespUserKey = en.getKey().toString();
                    }
                    json.put("DRespUser", DRespUserKey);
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("param", json);
                    jsonObject2.put("oSid", OSid);


                    String param1 = jsonObject2.toString();

                    String path = serverUrl + "/exeCooperation";
                    if (username.equals("请选择待分配用户")) {
                        Toast.makeText(getApplicationContext(), "分配失败！请选择分配用户!", Toast.LENGTH_SHORT).show();

                    } else {
                        String result = httpsUrlConnection.post(path, param1, map, DefectDetailActivity.this);
                        JSONObject object = new JSONObject(result);

                        System.out.println(result);

                        if (object.getString("code").equals("0")) {
                            //刷新缺陷列表

                            Intent intent = new Intent();
                            intent.putExtras(bunde);
                            intent.putExtra("dpUser", username);
                            intent.putExtra("DRespUserKey", DRespUserKey);

                            intent.setClass(DefectDetailActivity.this, Defect2Activity.class);
                            startActivity(intent);
                            DefectDetailActivity.this.finish();

                            Toast.makeText(getApplicationContext(), "分配成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "分配失败！请选择分配用户!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "分配失败！请选择分配用户!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }


    //格式化带有图片url的文本
    public String formatTextUrlString(String urlStr){
        if (urlStr.indexOf("[@") >= 0){
            //小于号和大于号的替换
            urlStr = urlStr.replace("<", "&lt;");
            urlStr = urlStr.replace(">", "&gt;");
            String strPre = urlStr.replace("[@","<br /><img src=\"/mnt/sdcard/test/"
                    + "L_39ldqd3m" + "/");
            return strPre.replace("@]","\" /><br />");
        }
        return urlStr;
    }

//    //用于textview中图文显示
//    public Html.ImageGetter imageGetterFromLocal = new Html.ImageGetter() {
//
//        @Override
//        public Drawable getDrawable(String source) {
//            Drawable drawable = null;
////            drawable = Drawable.createFromPath(source);
//            Bitmap bm = decodedByte;
//            drawable = new BitmapDrawable(bm);
//            drawable.setBounds(0, 0, DeviceBaseInfo.DEVICE_WIDTH / 2, getDrawableHeight(drawable.getIntrinsicWidth()
//                    , drawable.getIntrinsicHeight(), DeviceBaseInfo.DEVICE_WIDTH / 2)
//            );
//            return drawable;
//        }
//    };

    public int getDrawableHeight(float originalWidth, float originalHeight, float realWidth ){
        float rate = originalWidth / originalHeight;
        return Math.round(realWidth / rate);
    }
}
