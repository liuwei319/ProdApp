package com.kevin.prodapp.ui.list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.entity.DeviceBaseInfo;
import com.kevin.prodapp.utils.MyImageZoomOnTouchListener;

public class ImageZoomActivity extends AppCompatActivity {
 
    public static ImageZoomActivity imageZoomActivity = null;
    private ImageView ivZooming, ivClose;
    private Bitmap mBitmap;
 
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏和任务栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_imagezoom);
 
        imageZoomActivity = this;
        //获取设备的宽高
        getActivityWidthAndHeight(getWindowManager());
 
        ivZooming = findViewById(R.id.imgzoom_imgZooming);
        ivClose = findViewById(R.id.imagezoom_imgClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
 
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String filePath = bundle.getString("filePath");
        String base64 = filePath.substring(filePath.indexOf("base64") + 7);
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        mBitmap = BitmapFactory.decodeByteArray(decodedString ,0, decodedString.length);
        ivZooming.setImageBitmap(mBitmap);
        ivZooming.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivZooming.setOnTouchListener(new MyImageZoomOnTouchListener(ImageZoomActivity.this, ivZooming, mBitmap));
 
    }
 
    public void imageZoomSceneClose() {
        imageZoomActivity.finish();
    }
 
    //获取设备的宽度和高度
    public void getActivityWidthAndHeight(WindowManager windowManager){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        DeviceBaseInfo.DEVICE_WIDTH = displayMetrics.widthPixels;
        Log.e("width", String.valueOf(DeviceBaseInfo.DEVICE_WIDTH));
        DeviceBaseInfo.DEVICE_HEIGHT = displayMetrics.heightPixels;
        Log.e("height", String.valueOf(DeviceBaseInfo.DEVICE_HEIGHT));
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}