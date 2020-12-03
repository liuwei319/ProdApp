package com.kevin.prodapp.ui.list;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DefectAddActivity extends AppCompatActivity {

    private String serverUrl;
    private SharePManager spManager;
    private String cookie;
    private String jksessionid;
    private Map cookieMap;
    private Spinner serveritySpinner;
    private Spinner prioritySpinner;
    private String serverity;
    private String priority;
    private EditText addTitle;
    private EditText addSubmitVersion;
    private EditText addDesc;
    private Button addBtn;
    private Button chooseImageBtn;
    private LinearLayout addImage;
    private List<String> imageString;
    private ImageView image;
    private String imageBase64;
    private SharePManager sPManger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defect_detail_view_add);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息


        addTitle = findViewById(R.id.add_title);
        serveritySpinner = (Spinner) findViewById(R.id.yanzhongjibie);
        prioritySpinner = (Spinner) findViewById(R.id.youxianjibie);
        addSubmitVersion = findViewById(R.id.add_submitVersion);
        addDesc = findViewById(R.id.add_desc);
        addBtn = findViewById(R.id.save_add);
        chooseImageBtn = findViewById(R.id.chooseImage);
        imageString = new ArrayList<>();
        image = findViewById(R.id.defect_imageview);
        imageBase64 = "";
        TextView title = findViewById(R.id.txt_title);
        title.setText("新增缺陷");

        try {
            Properties props = new Properties();
            props.load(getApplicationContext().getAssets().open("config.properties"));
            serverUrl = props.getProperty("servers_url");;
            //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
            String sPMangerIP=sPManger.getString("servers_url");
            if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                serverUrl=sPMangerIP;
            }
            spManager = new SharePManager(DefectAddActivity.this, SharePManager.USER_FILE_NAME);
            cookie = spManager.getString("cookie");
            jksessionid = spManager.getString("jksessionid");
            cookieMap = new HashMap();
            cookieMap.put("cookie", cookie);
            cookieMap.put("jksessionid", jksessionid);
        } catch (Exception e) {

        }
//        addDesc.setOnClickListener(new View.OnClickListener() {
//            Boolean flag = true;
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (flag) {
//                    flag = false;
//                    addDesc.setEllipsize(null);// 展开
//                    addDesc.setSingleLine(flag);
//                } else {
//                    flag = true;
//                    addDesc.setEllipsize(TextUtils.TruncateAt.END); // 收缩
//                    addDesc.setSingleLine(flag);
//                }
//            }
//        });
        serveritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serverity = serveritySpinner.getItemAtPosition(position).toString();
                TextView tv = (TextView) view;
//                tv.setTextColor(getResources().getColor(R.color.gray));    //设置颜色
//                tv.setTextSize(15.0f);    //设置大小
//
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority = prioritySpinner.getItemAtPosition(position).toString();
                // curyid = position;
                //showPrice(position);
                TextView tv = (TextView) view;
//                tv.setTextColor(getResources().getColor(R.color.gray));    //设置颜色
//
//                tv.setTextSize(15.0f);    //设置大小
//
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(DefectAddActivity.this, "", "保存中...");
                new Thread() {
                    public void run() {
                        try{
                            sleep(500);
                        } catch (Exception e) {
                            Log.e("tag", e.getMessage());
                        }
                        dialog.dismiss();
                    }
                }.start();

                String defectTitle = addTitle.getText().toString();
                String defectDesc = addDesc.getText().toString();
                String defectSubmitVersion = addSubmitVersion.getText().toString();
                JSONObject body = new JSONObject();
                try {
                    body.put("defectTitle", defectTitle);
                    body.put("defectServerty", serverity);
                    body.put("defectPriority", priority);
                    body.put("defectSubmitVersion", defectSubmitVersion);
                    body.put("defectDesc", defectDesc);
                    body.put("defectImageBase64", imageBase64);

                    String params = String.valueOf(body);
                    final String path = serverUrl + "/saveDefect";
                    HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                    String result = httpsUrlConnection.post(path, params, cookieMap, DefectAddActivity.this);
                    JSONObject object = new JSONObject(result);
                    if (object.getString("code").equals("0")) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        Bundle bundle1 = DefectAddActivity.this.getIntent().getExtras();


                        intent.putExtras(bundle1);
                        intent.setClass(DefectAddActivity.this, MoreInfoActivity.class);

                        startActivity(intent);

                        DefectAddActivity.this.finish();
                        Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "保存失败！请完善信息！", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "保存失败！请完善信息！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 把用户选择的图片显示在imageview中
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if (resultCode == RESULT_OK) {
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {

                // 根据路径获得图片并压缩，返回bitmap用于显示
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, 480, 800);
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] bitmapBytes = baos.toByteArray();
                imageBase64 = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                image.setImageBitmap(BitmapFactory.decodeStream(cr.openInputStream(uri), null, options));
                baos.flush();
                baos.close();
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(), e);
            }
        } else {
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
