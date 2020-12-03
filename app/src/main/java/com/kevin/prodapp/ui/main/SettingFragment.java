package com.kevin.prodapp.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.kevin.prodapp.R;
import com.kevin.prodapp.ui.login.LoginActivity;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;


public class SettingFragment extends Fragment {

    private RelativeLayout ll_about;
    private RelativeLayout email_switch;
    private RelativeLayout rl_shark;
    private RelativeLayout es_gesture;
    private Button logut_in_button;
    private RelativeLayout ll_clear;
    private RelativeLayout es_clearcache;
//    protected NetWork netWork = new NetWork(getActivity());
    private static  final String UPDATA_APKNAME = "iTest.apk";
//    private SharePManager sPManger;
//    ObjectMapper mapper = new ObjectMapper();
    /****************** 版本更新 ***********************/
    private ProgressDialog progressDialog;
    private boolean cancleUpdate = false;
    /**
     * 版本更新实体类
     */
//    private VersionApp versionApp;
    /**
     * 记录进度条数量
     */
    private int progress;

    /**
     * 成功获取服务器版本号
     */
    private static final int VERSION_CONFIG = 3;
    /**
     * 范文服务器异常
     */
    private static final int SERVICE_EXCEPTION = -1;
    /**
     * 下载中
     */
    private static final int DOWNLOAD = 1;
    /**
     * 下载结束
     */
    private static final int DOWNLOAD_FINISH = 2;
    /**
     * 是否允许关闭此界面,true允许关闭，false不允许
     */
    private boolean finishflag = false;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        Button but_id = view.findViewById(R.id.logut_in_button);
        but_id.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {
                new AlertDialog.Builder(getActivity()).setTitle("确认退出吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //退出
                                        SysApplication.getInstance().exit();
                                    }
                                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();




            }

        });
//修改ip
        TextView updateip = view.findViewById(R.id.xiugaiip);
        updateip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateIpActivity.class);
                startActivity(intent);

            }
        });


        TextView textView1 = view.findViewById(R.id.zhuxiao);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle("确认注销吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                SharePManager sharePManager =new SharePManager(getActivity(), SharePManager.USER_FILE_NAME);

                                String sPMangerIP=sharePManager.getString("servers_url");

                                //清除所有缓存
                                sharePManager.clear();
//                                sharePManager.removeString("uname");
                                //注销只保留修改后的ip
                                sharePManager.putString("servers_url",sPMangerIP);

                                startActivity(intent);
                                Toast.makeText(getContext(),"注销成功", Toast.LENGTH_SHORT).show();
                                //  SysApplication.getInstance().exit();
                            }
                        }).setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();

            }
        });


//        ll_about = (RelativeLayout) view.findViewById(R.id.ll_about);
//        ll_about.setOnClickListener(viewOnClick);
//
//        rl_shark = (RelativeLayout) view.findViewById(R.id.rl_shark);
//        rl_shark.setOnClickListener(viewOnClick);
//
//        logut_in_button=(Button)view.findViewById(R.id.logut_in_button);
//        logut_in_button.setOnClickListener(viewOnClick);
//
//        ll_clear = (RelativeLayout) view.findViewById(R.id.ll_clear);
//        ll_clear.setOnClickListener(viewOnClick);
//
//        es_clearcache = (RelativeLayout) view.findViewById(R.id.es_clearcache);
//        es_clearcache.setOnClickListener(viewOnClick);
//
////        es_clearcache.setVisibility(View.GONE);//不可见
//        es_gesture = (RelativeLayout) view.findViewById(R.id.es_gesture);
//        es_gesture.setOnClickListener(viewOnClick);
//
//        email_switch = (RelativeLayout) view.findViewById(R.id.email_switch);
//        email_switch.setOnClickListener(viewOnClick);
//        email_switch.setVisibility(View.GONE);
//
//        sPManger = new SharePManager(getActivity(),SharePManager.USER_FILE_NAME);
//        if(sPManger.getString("username").equals("zhangml2")||sPManger.getString("username").equals("yuliang")){
//            email_switch.setVisibility(View.VISIBLE);
//
//        }
//
//        TextView tvmeinfo=view.findViewById(R.id.tvmeinfo);
//        tvmeinfo.setText("欢迎您,"+sPManger.getString("username")+"!");
        return view;
    }





    DialogInterface.OnClickListener upData = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case AlertDialog.BUTTON_POSITIVE:
                    initDownloadData();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;
                default:
                    break;
            }
        }
    };

    DialogInterface.OnClickListener cClick=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
//            switch (i){
//                case AlertDialog.BUTTON_POSITIVE:
//                    String cookies = sPManger.getString("cookie");
//                    String jksessionid = sPManger.getString("jksessionid");
//                    String username =sPManger.getString("username");
//                    UserService userService= new UserService();
//                    Map<String, String> cookie = new HashMap<>();
//                    cookie.put("cookie",cookies);
//                    cookie.put("jksessionid",jksessionid);
//                    ResultData resultData = userService.clearSession(username,cookie,
//                                getContext());
//                    if(resultData.getRet_code().equals("0")){
//                        sPManger.clear();
//                        Toast.makeText(getContext(),"注销成功", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(getContext(),LoginActivity.class));
//                        clearCache();
//                        ActivityManager.getInstance().exit();
//                    }else
//                        Toast.makeText(getContext(),"操作失败,请重试", Toast.LENGTH_SHORT).show();
//                    break;
//                case AlertDialog.BUTTON_NEGATIVE:
//                    break;
//                default:
//                    break;
//            }
        }
    };

    DialogInterface.OnClickListener dClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int buttionId) {
//            switch (buttionId){
//                case AlertDialog.BUTTON_POSITIVE:
//                    ActivityManager.getInstance().exit();
//                    break;
//                case AlertDialog.BUTTON_NEGATIVE:
//                    break;
//                default:
//                    break;
//            }
        }
    };
//    自动检测版本更新----下载apk

    private void initDownloadData(){
//        int permission1 = ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permission1 == PackageManager.PERMISSION_GRANTED) {
//            String sdpath = Environment.getExternalStorageDirectory() + "/";//读取路径，无SD卡自动读取手机内存
//            String mSavePath = sdpath + "download";//保存路径
//            File file = new File(mSavePath);
//            //判断文件目录是否存在
//            if (!file.exists()) {
//                file.mkdir();
//            }
//            //下载前删除原文件
//            File apkFile = new File(mSavePath, UPDATA_APKNAME);
//            if (apkFile.exists()) {
//                apkFile.delete();
//            }
//            download();
//        }
    }


    //    直接下载文件
    private void download(){
        //相关属性
//        progressDialog =new ProgressDialog(getActivity());
//        progressDialog.setMessage("正在下载...");
//        progressDialog.setIndeterminate(true);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setCancelable(true);
//        progressDialog.setCanceledOnTouchOutside(false);    //取消屏幕触摸事件
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.update_app_progressbar, null);
//        view.setAlpha(1);
//        progressDialog.show();
//        MyThread mThread = new MyThread("download");
//        mThread.start();
    }

    /**
     * 起一个线程
     */
    public class MyThread extends Thread {
        String state_long = "";

        public MyThread(String state_long) {
            super();
            this.state_long = state_long;

        }

        @Override
        public void run() {
            Message msg = new Message();
//            try {
//                if ("loadversion".equals(state_long)) {
//                    // 获取apk的版本号
//                    PackageManager packageManager = getActivity().getPackageManager();
//                    PackageInfo packageInfo = packageManager.getPackageInfo("", 0);
//                    String apk_name = packageInfo.versionName;
//                    // 获取服务器版本号
//                    Ret retversion = new SettingService().version(apk_name,getActivity());
//                    ObjectMapper mapper = new ObjectMapper();
//                    VersionApp versionApp=  mapper.readValue(retversion.getRet_data(),VersionApp.class);
//                    if (versionApp != null) {
//                        msg.what = VERSION_CONFIG;
//                        msg.obj = versionApp;
//                    } else {
//                        msg.what = -1;
//                        msg.obj = null;
//                    }
//                } else if ("download".equals(state_long)) {
//                    getDataSource(new PropertyUtils(getActivity()).getProperty("DownloadUrl"));// 下载的路径
//                    msg.what = 0;
//                }
//            } catch (Exception e) {
//                msg.what = SERVICE_EXCEPTION;
//                msg.obj = e.getMessage();
//            }
            mHandler.sendMessage(msg);
        }
    }
    /**
     * 获取远程文件资源
     */
    protected void getDataSource(String url) throws IOException {
        //new SharePManager(getContext(),SharePManager.USER_FILE_NAME).clear();
        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 24) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (getContext().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        // 创建连接 https下载
        HttpsURLConnection conn = HttpsUrlConnection.Httpsdownload(url, "GET", null, getActivity());
        // 获取文件大小
        int filesize = conn.getContentLength();
        // InputStream下载文件
        InputStream is = conn.getInputStream();
        if (is == null) {
            throw new RuntimeException("stream is null!");
        }
        String sdpath = Environment.getExternalStorageDirectory() + "/";
        String mSavePath = sdpath + "download";
        File file = new File(mSavePath);
        // 判断文件目录是否存在
        if (!file.exists()) {
            file.mkdir();
        }
        File apkFile = new File(mSavePath, "iTest.apk");
        // 将文件写入暂存盘
        FileOutputStream fos = new FileOutputStream(apkFile);
        byte buf[] = new byte[1024];
        int count = 0;
        // 缓存
        do {
            int numread = is.read(buf);
            count += numread;
            if (cancleUpdate) {// 当点击取消时改进程全部取消
                is.close();
                return;
            }
            // 计算进度条位置
            progress = (int) (((float) count / filesize) * 100);
            // 更新进度
            mHandler.sendEmptyMessage(DOWNLOAD);
            if (numread <= 0) {
                mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                break;
            }
            fos.write(buf, 0, numread);
        } while (!cancleUpdate);// 点击取消就停止下载
        // 打开文件进行安装
        openFile(apkFile);
        is.close();
    }

    /**
     * 在手机上打开文件的method
     */
    private void openFile(File f) {
        try {
            progressDialog.cancel();
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            // 默认的跳转类型,将Activity放到一个新的Task中
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 将下载的文件放在桌面上
            intent.setAction(Intent.ACTION_VIEW);
            // 调用调用getMIMEType()来取得MimeType
            String type = getMIMEType(f);
            // 设置intent的file与MimeType
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getPackageName() + ".fileProvider", f);
                // 默认的跳转类型,将Activity放到一个新的Task中
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(contentUri, "applicati" +
                        "on/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断文件MimeType的method
     */
    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        // 取得扩展名
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        // 依扩展名的类型决定MimeType
        if (end.equals("m4a") || end.equals("map3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        return type;

    }

    /**
     * 接收线程
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case DOWNLOAD:// 正在下载
//                    // 设置进度条
//                    progressDialog.setIndeterminate(false);
//                    progressDialog.setMax(100);
//                    progressDialog.setProgress(progress);
//                    break;
//                case DOWNLOAD_FINISH:// 安装文件
//                    break;
//                case VERSION_CONFIG:// 成功获取服务器版本号
//                    VersionApp vApp = (VersionApp) msg.obj;
//                    if (!"".equals(vApp)) {
//                        updateVersion(vApp);
//                    } else {
//                        progressDialog.setTitle("软件更新");
//                        progressDialog.setMessage("获取版本信息有误,请稍后重试....");
//                        progressDialog.setCanceledOnTouchOutside(false);
//                        progressDialog.setButton("确定",
//                                new DialogInterface.OnClickListener() {
//
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        progressDialog.dismiss();
//                                    }
//                                });
//                        progressDialog.show();
//                    }
//                    break;
//                case SERVICE_EXCEPTION:
//                    Toast.makeText(getActivity(),"" + msg.obj, Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    break;
            }
        };
    };

    /**
     * 判断是否需要更新
     */

//    private void updateVersion(VersionApp version) {
//        try {
//            BaseDialog mDialog = new BaseDialog(getActivity());
//            // 获取apk的版本号
//            PackageManager packageManager = getActivity().getPackageManager();
//            PackageInfo packageInfo = packageManager.getPackageInfo("", 0);
//            String apk_name = packageInfo.versionName;
//            mDialog.setTitle("软件更新");
//            if (!apk_name.equals(version.getVersion_no())
//                    && !"您已是最新版！".equals(version.getVersion_no())) {
//                mDialog.setMessage("最新版本号:" + version.getVersion_no());
//                mDialog.setButton1("下载更新", dListener);
//                mDialog.setButton2("暂不更新", dListener);
//
//            } else {
//                mDialog.setMessage("当前已经是最新版本,无需更新!");
//                mDialog.setButton3("确定", dListener);
//            }
//            mDialog.setCancelable(false);// 取消返回键
//            mDialog.setCanceledOnTouchOutside(false);// 屏蔽其他区域的触摸事件
//            mDialog.setDialog(mDialog);
//            mDialog.show();
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
    DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    finishflag = true;
                    dialog.dismiss();
                    download();
                    break;
                case 1:
                case 2:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO
//    };



//    public static void verifyStoragePermissions(Activity activity) {
//        int permission = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE);
//        }
//
//    }

//    public void clearCache(){
//        ClearData clearData = new ClearData(getContext());
//        clearData.clearAllDataOfApplication();
//    }



}