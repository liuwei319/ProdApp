package com.kevin.prodapp.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kevin.prodapp.ui.login.LoginActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Http请求的工具类
public class HttpUtils {

    private static final int TIMEOUT_IN_MILLIONS = 5000;



    public interface CallBack {
        void onRequestComplete(String result);
    }

    /**
     * 异步的Get请求
     *
     * @param urlStr
     * @param callBack
     */
    public static void doGetAsyn(final String urlStr, final CallBack callBack) {
        new Thread() {
            public void run() {
                try {
                    String result = doGet(urlStr);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            };
        }.start();
    }

    /**
     * 异步的Post请求
     *
     * @param urlStr
     * @param params
     * @param callBack
     * @throws Exception
     */
    public static void doPostAsyn(final String urlStr, final String params, final Context cxt, final Map<String, String> headers, final CallBack callBack) throws Exception {
        new Thread() {
            public void run() {
                try {

                    String result = doPost(urlStr, params,cxt,headers);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            };
        }.start();
    }

    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static String doGet(String urlStr) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
            }
            conn.disconnect();
        }
        return null;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 json形式的字符串 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String doPost(String url, String param,Context cxt, Map<String, String> headers) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        DataOutputStream os = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            if (null != headers && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
//            conn.setRequestProperty("charset", "utf-8");
//            conn.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

            if (param != null && !param.trim().equals("")) {
                // 获取URLConnection对象对应的输出流
//                out = new PrintWriter(conn.getOutputStream());
                os = new DataOutputStream(conn.getOutputStream());
                // 发送请求参数
//                out.print(param);
                os.writeBytes(param);
                // flush输出流的缓冲
//                out.flush();
                os.flush();
            }
//            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                result = getTextFromStream(is);
                String cookie = "";
                String jksessionid = null;
                Map<String, List<String>> map=conn.getHeaderFields();
                if (map.containsKey("Set-Cookie")) {
                    List<String> cookies = map.get("Set-Cookie");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < cookies.size(); i ++){
                        builder.append(cookies.get(i));
                        if (cookies.get(i).indexOf("JSESSIONID") != -1){
                            jksessionid = cookies.get(i);
                        }
                    }
                    cookie = builder.toString();
                }
                SharePManager spm = new SharePManager(cxt,
                        SharePManager.USER_FILE_NAME);
                spm.putString("cookie", cookie);
                spm.putString("jksessionid", jksessionid);
            }else {
                result = "{\"code\":\"-1\",\"msg\":\"请求失败\",\"data\":\"\"}";
            }
        } catch (Exception e) {
            result = "{\"code\":\"-1\",\"msg\":\"请求异常\",\"data\":" + e.toString() + "}";
            e.printStackTrace();
        } finally {       // 使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (os != null){
                    os.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    //读取放回信息
    public static String getTextFromStream(InputStream is) {

        try {
            int len = 0;
            byte[] b = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = is.read(b)) != -1) {
                bos.write(b, 0, len);
            }

            String text = new String(bos.toByteArray());
            bos.close();
            return text;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}

