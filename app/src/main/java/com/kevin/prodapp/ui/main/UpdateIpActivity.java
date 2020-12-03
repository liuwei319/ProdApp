package com.kevin.prodapp.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.ui.login.LoginActivity;
import com.kevin.prodapp.utils.SharePManager;

import java.io.FileOutputStream;
import java.util.Properties;

public class UpdateIpActivity extends AppCompatActivity {
    private static String configPath = "config.properties";

    private String serverUrl;
    private String editIp;
    private  Properties props;
    public Context context;
    private SharePManager sPManger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ip);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息

        TextView tv_ip = findViewById(R.id.show_ip);
        Button btn = findViewById(R.id.button_config);
        try {
            props = new Properties();

            props.load(getApplicationContext().getAssets().open("config.properties"));
            serverUrl = props.getProperty("servers_url");
            String sPMangerIP=sPManger.getString("servers_url");
            if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                serverUrl=sPMangerIP;
            }

            String showip = serverUrl.substring(serverUrl.indexOf("//") + 2, serverUrl.lastIndexOf(":"));
            tv_ip.setText(showip);


            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(UpdateIpActivity.this).setTitle("修改后将重新登录!")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = findViewById(R.id.editText_ip);
                                    editIp = editText.getText().toString();
                                    // 存入本地数据库
                                    sPManger.putString("servers_url","https://"+editIp+":443");
                                  //跳转登录界面
                                    Intent intent = new Intent(UpdateIpActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("返回", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“返回”后的操作,这里不设置没有任何操作
                        }
                    }).show();


                }
            });

        } catch (Exception e) {

        }


    }

    //修改配置文件
    public static String setProperties(Context context, String keyName, String keyValue) {
        Properties properties = new Properties();
        try {
            properties.load(context.openFileInput("config.properties"));
            properties.setProperty(keyName, keyValue);
            FileOutputStream out = context.openFileOutput("config.properties",Context.MODE_PRIVATE);
            properties.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
            return "修改配置文件失败!";
        }
        return "设置成功";
    }
    /**
     * 获取
     */
    public static  String getUrl(Context context) {
        Properties properties = new Properties();
        String url = null;
        try {
            properties.load(context.openFileInput("config.properties"));
            url =properties.getProperty("servers_url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    //初始化配置文件
    //最好放在Application中，初始化。
    public static String initProperties(Context context) {
        Properties props = new Properties();
        try {
            props.load(context.getAssets().open("config.properties"));
            FileOutputStream out = context.openFileOutput("config.properties",Context.MODE_PRIVATE);
            props.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
            return "修改配置文件失败!";
        }
        return "设置成功";
    }
}

