package com.kevin.prodapp.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.ui.main.MainActivity;
import com.kevin.prodapp.utils.AllowX509TrustManager;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import org.json.JSONObject;

import java.util.Properties;

public class LoginActivity extends AppCompatActivity {

    private SharePManager sPManger;
    private CheckBox memberCheckBox;
    private String menuData;
    private ProgressDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //修改ip信任所有证书
        AllowX509TrustManager.allowAllSSL();

        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_login);
//        ActivityA=this;
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            finish();
//            return;
//        }
        //退出
        SysApplication.getInstance().addActivity(this);
        //buildDialog();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final ProgressBar loadingProgressBar = findViewById(R.id.login_progress);
        final EditText usernameEditText = findViewById(R.id.et_username);
        final EditText passwordEditText = findViewById(R.id.password);
        memberCheckBox = findViewById(R.id.remembercheckBox);

        final Button loginButton = findViewById(R.id.email_sign_in_button);

        //记住密码功能
        sPManger = new SharePManager(LoginActivity.this, SharePManager.USER_FILE_NAME);//获取本地数据库信息
        boolean isremember = sPManger.getBoolean("isRemember", false);
        memberCheckBox.setChecked(isremember);
        if (isremember){
            usernameEditText.setText(sPManger.getString("uname"));
            passwordEditText.setText(sPManger.getString("pwd"));
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // MQTTService.publish("ceshimq");
                //dialog.show();
                buildDialog();
                loadingProgressBar.setVisibility(View.VISIBLE);
                String uname = usernameEditText.getText().toString();
                String pwd = passwordEditText.getText().toString();
                sPManger.putString("uname",uname);
                sPManger.putString("pwd",pwd);
                if (memberCheckBox.isChecked()){
                    sPManger.putBoolean("isRemember",true);
                }else {
                    sPManger.putBoolean("isRemember",false);
                }
                if (uname != null && !uname.equals("") && pwd != null && !pwd.equals("")) {
                    try {

                        Properties props = new Properties();
                        props.load(getApplicationContext().getAssets().open("config.properties"));
                        String serverurl = props.getProperty("servers_url");
                        //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                       String sPMangerIP=sPManger.getString("servers_url");
                        if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                            serverurl=sPMangerIP;
                        }

                        final String path = serverurl + "/user/doLogin";
                        JSONObject body = new JSONObject();
                        body.put("username", uname);
                        body.put("password", pwd);
                        String params = String.valueOf(body);
                        HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                        String result = httpsUrlConnection.post(path, params, null, LoginActivity.this);
                        if (result != null) {
                            JSONObject object = new JSONObject(result);

                            if (object.get("code").toString().equals("0")) {

                                loadingProgressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Bundle bundle = new Bundle();
                                intent.putExtra("username", uname);
                                //先不校验
//                                intent.putExtra("menuelist",object.getString("data"));
                                String menu="[{\"MPid\":\"\",\"MTitle\":\"测试缺陷\"},{\"MPid\":\"\",\"MTitle\":\"测试案例\"}]";
                                intent.putExtra("menuelist",menu);


                                startActivity(intent);

                                Toast.makeText(getApplicationContext(), "欢迎使用！", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                loadingProgressBar.setVisibility(View.GONE);
                                JSONObject object1 =new JSONObject(object.get("msg").toString());
                                Toast.makeText(getApplicationContext(), object1.get("msg").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }else {

                            dialog.dismiss();
                            loadingProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "网络异常！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "程序异常！" + e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        //关闭对话框
       new  ProgressDialog(this).dismiss();
    }
    private void buildDialog(){
//        dialog = new ProgressDialog(LoginActivity.this);
//        dialog.setTitle("正在登陆...");
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog=ProgressDialog.show(LoginActivity.this, "", "正在登陆...");
        new Thread() {
            public void run() {
                try{
                    sleep(2000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                dialog.dismiss();
            }
        }.start();
    }
}
