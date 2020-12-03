//package com.kevin.prodapp.ui.login;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.spdb.itest.entity.ResultData;
//import com.spdb.itest.entity.Ret;
//import com.spdb.itest.entity.TFunctionMenu;
//import com.spdb.itest.entity.TUser;
//import com.spdb.itest.entity.VersionApp;
//import com.spdb.itest.service.BaseActivity;
//import com.spdb.itest.service.SettingService;
//import com.spdb.itest.service.UserService;
//import com.spdb.itest.tools.SharePManager;
//import com.spdb.itest.util.ActivityManager;
//import com.spdb.itest.util.PropertyUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.Serializable;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.ProtocolException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.net.ssl.HttpsURLConnection;
//
//import static com.spdb.itest.tools.HttpsUrlConnection.setCertificates;
//
//
//public class CaptchaActivity extends BaseActivity {
//
//    private TUser tUser;
//    private UserLoginTask mAuthTaskc = null;
//    public SharePManager sPManger;
//    private Button captchaCheck;
//    private Button captchaRefresh;
//    private ImageView imageView;
//    private ImageView back;
//    private AutoCompleteTextView captchaView;
//    private static final String CAPTCHA_URL = "/SpdbAppPlus/captcha/captcha.jpg";
//    ObjectMapper mapper = new ObjectMapper();
//    private View mCaptchaFormView;
//    private View mProgressView;
//    private boolean mRememberInfoView;
//    private String name;
//    private String password;
//    private String cookie;
//    private String jksessionid;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ActivityManager.getInstance().addActivity(this);
//        setContentView(R.layout.activity_captcha);
//        Intent intent = getIntent();
//        mRememberInfoView = intent.getBooleanExtra("mRememberInfoView", false);
//        name = intent.getStringExtra("name");
//        password = intent.getStringExtra("password");
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//不刷新页面
//        String url = new PropertyUtils(getApplicationContext()).getProperty("CONTROL") + CAPTCHA_URL;//获取验证码
//        Bitmap bitmap = getHttpBitmap(url, getApplicationContext());
//        back = (ImageView) this.findViewById(R.id.captcha_back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (view.getId()) {
//                    case R.id.captcha_back:
//                        CaptchaActivity.this.finish();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        imageView = (ImageView) this.findViewById(R.id.captcha_picture);
//        //显示图片
//        if (bitmap != null)
//            imageView.setImageBitmap(bitmap);
//        captchaRefresh = findViewById(R.id.captcha_refresh);
//        captchaRefresh.setOnClickListener(viewOnClickForRrefesh);
//        captchaCheck = findViewById(R.id.captcha_check);
//        captchaCheck.setOnClickListener(viewOnClickForCheck);
//        captchaView = (AutoCompleteTextView) findViewById(R.id.captcha_content); //tv_main_text是我们控件的id
//        captchaView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (captchaView.getText().length() > 0) { //有值
//                    captchaCheck.setBackground(getResources().getDrawable(R.drawable.loginlablebgimage));
//                    captchaCheck.setTextColor(Color.parseColor("#3f3f3e"));
//                } else { //没有值
//                    captchaCheck.setBackground(getResources().getDrawable(R.drawable.navigationbgimage));
//                    captchaCheck.setTextColor(Color.parseColor("#9e9d9c"));
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        mCaptchaFormView = findViewById(R.id.captcha_form);
//        mProgressView = findViewById(R.id.captcha_progress);
//        sPManger = new SharePManager(getApplicationContext(), SharePManager.USER_FILE_NAME);//获取本地数据库信息
//    }
//
//    View.OnClickListener viewOnClickForRrefesh = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.captcha_refresh:
//                    String url = new PropertyUtils(getApplicationContext()).getProperty("CONTROL") + CAPTCHA_URL;//获取验证码
//                    Bitmap bitmap = getHttpBitmap(url, getApplicationContext());
//                    //显示图片
//                    imageView.setImageBitmap(bitmap);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//
//    View.OnClickListener viewOnClickForCheck = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.captcha_check:
//                    if (captchaView.getText().length() != 5) {
//                        Toast.makeText(CaptchaActivity.this, "验证码长度不对,请重新输入", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Map<String, String> map = new HashMap<>();
//                        map.put("cookie", cookie);
//                        map.put("jksessionid", jksessionid);
//                        UserService userService = new UserService();
//                        ResultData mresult = userService.checkCaptcha(map, captchaView.getText().toString(), CaptchaActivity.this);
//                        if (mresult.getRet_code().equals("0")) {
//                            showProgress(true);
//                            mAuthTaskc = new UserLoginTask();
//                            mAuthTaskc.execute(name, password);
//                        } else if (mresult.getRet_code().equals("-1")) {
//                            String url = new PropertyUtils(getApplicationContext()).getProperty("CONTROL") + CAPTCHA_URL;//获取验证码
//                            Bitmap bitmap = getHttpBitmap(url, getApplicationContext());
//                            //显示图片
//                            imageView.setImageBitmap(bitmap);
//                            Toast.makeText(CaptchaActivity.this, mresult.getRet_msg(), Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(CaptchaActivity.this, mresult.getRet_msg(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private HttpsURLConnection initHttps(String url, String method,
//                                         Map<String, String> headers, Context cxt) {
//        HttpsURLConnection http = null;
//        try {
//            URL _url = new URL(url);
//            http = (HttpsURLConnection) _url.openConnection();
//            http.setSSLSocketFactory(setCertificates(cxt.getAssets().open(
//                    "weblogic.cer")));
//            // 连接超时
//            http.setConnectTimeout(25000);
//            // 读取超时 --服务器响应比较慢，增大时间
//            http.setReadTimeout(25000);
//            http.setRequestMethod(method);
//            http.setRequestProperty("Content-Type",
//                    "application/x-www-form-urlencoded");
//            http.setRequestProperty(
//                    "User-Agent",
//                    "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
//            if (null != headers && !headers.isEmpty()) {
//                for (Map.Entry<String, String> entry : headers.entrySet()) {
//                    Log.e("http-head", "key:" + entry.getKey() + " --value:"
//                            + entry.getValue());
//                    http.setRequestProperty(entry.getKey(), entry.getValue());
//                }
//            }
//            // http.setDoOutput(true);
//            // http.setDoInput(true);
//            http.connect();
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return http;
//    }
//
//    /**
//     * 初始化http请求参数
//     */
//    private static HttpURLConnection initHttp(String url, String method,
//                                              Map<String, String> headers, Context cxt) throws IOException {
//        URL _url = new URL(url);
//
//        HttpURLConnection http = (HttpURLConnection) _url.openConnection();
//
//        // 连接超时
//        http.setConnectTimeout(25000);
//        // 读取超时 --服务器响应比较慢，增大时间
//        http.setReadTimeout(25000);
//        http.setRequestMethod(method);
//        http.setRequestProperty("Content-Type",
//                "application/x-www-form-urlencoded");
//        http.setRequestProperty(
//                "User-Agent",
//                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
//        if (null != headers && !headers.isEmpty()) {
//            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                http.setRequestProperty(entry.getKey(), entry.getValue());
//            }
//        }
//        http.connect();
//        return http;
//    }
//
//    public Bitmap getHttpBitmap(String url, Context cxt) {
//        Bitmap bitmap = null;
//        HttpURLConnection conn = null;
//        try {
//            //获得连接
//            if (url.startsWith("https"))
//                conn = initHttps(url, "GET", null, cxt);
//            else
//                conn = initHttp(url, "GET", null, cxt);
//            int responsecode = conn.getResponseCode();
//            Log.e("responsecode", responsecode + "");
//            if (responsecode == 200) {
//                cookie = conn.getHeaderField("Set-Cookie");
//                Log.e("cookie", cookie + "");
//                if (cookie != null) {
//                    String[] s = cookie.split(";");
//                    jksessionid = s[0];
//                }
//                //得到数据流
//                InputStream is = conn.getInputStream();
//                //解析得到图片
//                bitmap = BitmapFactory.decodeStream(is);
//                //关闭数据流
//                is.close();
//            }
//            if (conn != null) {
//                conn.disconnect();// 关闭连接
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    public class UserLoginTask extends AsyncTask<Object, Void, ResultData> {
//        @Override
//        protected ResultData doInBackground(Object... params) {
//            ResultData menueresult = null;
//            try {
//                PackageInfo codeapk = getPackageManager().getPackageInfo(getPackageName(), 0);
//                UserService userService = new UserService();
//                ResultData mresult = userService.login((String) params[0], (String) params[1], codeapk.versionName,
//                        CaptchaActivity.this);//用户名，密码，版本信息
//                sPManger.putString("username", (String) params[0]);
//                sPManger.putString("userpassword", (String) params[1]);
//                if (mresult.getRet_code().equals("0")) {
//                    if (mRememberInfoView) {
//                        sPManger.putBoolean("isRemember", true);
//                    } else {
//                        sPManger.putBoolean("isRemember", false);
//                    }
//                    sPManger.putBoolean("test", true);
//                    sPManger.putString("usersp", ((LinkedHashMap) mresult.getRet_data()).get("userSP") == null ? "" : ((LinkedHashMap) mresult.getRet_data()).get("userSP").toString());
//                    tUser = mapper.convertValue(mresult.getRet_data(), TUser.class);
//                    sPManger = new SharePManager(CaptchaActivity.this, SharePManager.USER_FILE_NAME);//获取本地数据库信息
//                    sPManger.putString("userid", tUser.getUserId());
//                    Map<String, String> cookie = new HashMap<>();
//                    cookie.put("cookie", sPManger.getString("cookie"));
//                    cookie.put("jksessionid", sPManger.getString("jksessionid"));
//                    menueresult = userService.getMenue(tUser.getUserId(), cookie, CaptchaActivity.this);
//                    if (menueresult.getRet_code().equals("-1")) {
//                        sPManger.putBoolean("setpwd", false);
//                        return new ResultData("-1", menueresult.getRet_msg(), "");//报错信息判断
//                    }
//                    JavaType javaType = getCollectionType(ArrayList.class, TFunctionMenu.class);
//                    List<TFunctionMenu> menuelist = mapper.convertValue(menueresult.getRet_data(), javaType);
//                    String nowversion = getVersionName(CaptchaActivity.this);
//                    Ret checkresult = new SettingService().version(nowversion, CaptchaActivity.this);
//                    VersionApp version = mapper.readValue(checkresult.getRet_data(), VersionApp.class);
//                    Map<String, Object> mapresult = new HashMap<>();
//                    mapresult.put("userInfo", tUser);
//                    mapresult.put("menueInfo", menuelist);
//                    mapresult.put("isupdate", checkresult.getRet_code());
//                    mapresult.put("newerversion", version.getVersion_no());
//                    mapresult.put("nowversion", nowversion);
//                    return new ResultData("0", "登录成功", mapresult);
//                }
//                return mresult;
//            } catch (Exception e) {
//                return new ResultData("-1", e.toString(), "");
//            }
//        }
//
//
//        @Override
//        protected void onPostExecute(final ResultData retInfo) {
//            mAuthTaskc = null;
//            if (retInfo.getRet_code().equals("0")) {//验证通过
//                Intent data = new Intent();
//                Map<String, Object> rtMap = (Map<String, Object>) retInfo.getRet_data();
//                TUser user = (TUser) rtMap.get("userInfo");
//                List<TFunctionMenu> listtf = (List<TFunctionMenu>) rtMap.get("menueInfo");
//                data.putExtra("username", user.getUserLoginname());
//                data.putExtra("userid", user.getUserId());
//                data.putExtra("menuelist", (Serializable) listtf);
//                Bundle bundle = new Bundle();
//                bundle.putString("isupdate", (String) rtMap.get("isupdate"));
//                bundle.putString("newerversion", (String) rtMap.get("newerversion"));
//                bundle.putString("nowversion", (String) rtMap.get("nowversion"));
//                data.putExtras(bundle);
//                if (sPManger.getBoolean("setpwd", false) || sPManger.getBoolean("jump", false)) {
//                    data.setClass(CaptchaActivity.this, MainActivity.class);
//                } else {
//                    data.setClass(CaptchaActivity.this, UnlockViewActivity.class);
//                }
//                sPManger.putBoolean("BASActivity", false);
//                sPManger.putBoolean("BAS", false);
//                startActivity(data);
//                CaptchaActivity.this.finish();
//            } else {
//                showProgress(false);
//                Intent intent = new Intent();
//                intent.putExtra("mPasswordView", retInfo.getRet_msg());
//                intent.putExtra("showProgress", false);
//                setResult(RESULT_OK, intent);
//                CaptchaActivity.this.finish();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTaskc = null;
//            showProgress(false);
//        }
//    }
//
//
//    public JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
//        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
//    }
//
//    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
//        // 获取packagemanager的实例
//        PackageManager packageManager = context.getPackageManager();
//        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
//        String version = packInfo.versionName;
//        return version;
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//            mCaptchaFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mCaptchaFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mCaptchaFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate()
//                    .setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mCaptchaFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }
//}
