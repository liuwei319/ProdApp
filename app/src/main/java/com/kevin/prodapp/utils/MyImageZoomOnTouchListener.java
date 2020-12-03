package com.kevin.prodapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.kevin.prodapp.entity.DeviceBaseInfo;
import com.kevin.prodapp.ui.list.ImageZoomActivity;

public class MyImageZoomOnTouchListener implements View.OnTouchListener {
 
    private ImageView ivZooming;
    private Bitmap mBitmap;
    private Context mContext;
    // 縮放控制
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
 
    // 不同状态的表示：
    //空模式
    private static final int MODE_NONE = 0;
    //拖动模式
    private static final int MODE_DRAG = 1;
    //缩放模式
    private static final int MODE_ZOOM = 2;
    //当前模式为：空模式
    private int currentMode = MODE_NONE;
 
    // 定义第一个按下的点，两只接触点的重点，以及出事的两指按下的距离：
    //记录开始时候的坐标位置
    private PointF startPoint = new PointF();
    private PointF midPoint = new PointF();
    private float oriDis = 1f;
    //拖放的最小间距
    private static float MINI_DISTANCE = 50f;
    //最大放大比例
    private static float MAX_SCALE = 20f;
    //最小缩放比例
    private static float MINI_SCALE = 1f;
 
    public MyImageZoomOnTouchListener(Context mContext, ImageView ivZooming, Bitmap mBitmap) {
        this.mContext = mContext;
        this.ivZooming = ivZooming;
        this.mBitmap = mBitmap;
    }
 
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 单指
            case MotionEvent.ACTION_DOWN:
                matrix.set(view.getImageMatrix());
                savedMatrix.set(matrix);
                startPoint.set(event.getX(), event.getY());
                currentMode = MODE_DRAG;
                break;
            // 双指
            case MotionEvent.ACTION_POINTER_DOWN:
                oriDis = distance(event);
                if (oriDis > MINI_DISTANCE) {
                    savedMatrix.set(matrix);
                    midPoint = middle(event);
                    currentMode = MODE_ZOOM;
                }
                break;
            // 手指放开
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (isOnClick(event.getX(), startPoint.x) && isOnClick(event.getY(), startPoint.y)) {
                    ImageZoomActivity.imageZoomActivity.imageZoomSceneClose();
                } else {
                    Log.e("Action_up", "两个位置不一致！");
                }
                currentMode = MODE_NONE;
                break;
            // 单指滑动事件
            case MotionEvent.ACTION_MOVE:
                if (currentMode == MODE_DRAG) {
                    // 是一个手指拖动
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                } else if (currentMode == MODE_ZOOM) {
                    // 两个手指滑动
                    float newDist = distance(event);
                    if (newDist > MINI_DISTANCE) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oriDis;
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;
        }
        // 设置ImageView的Matrix
        view.setImageMatrix(matrix);
        //检查当前缩放比例并做出相应处理
        checkView();
        return true;
    }
 
    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Float.valueOf(String.valueOf(Math.sqrt(x * x + y * y))) ;
    }
 
    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }
 
    //判断是否为点击事件（按下和抬起的坐标是否一致，在误差范围内）
    private boolean isOnClick(float pointOne, float pointTwo) {
        //误差范围为10f
        float maxError = 10f;
        float result = pointOne - pointTwo;
        if (result >= 0 && result <= maxError) {
            return true;
        } else if (result <= 0 && result >= maxError) {
            return true;
        }
        return false;
    }
 
    //检查当前缩放比例并做出相应处理
    private void checkView() {
        float[] p = new float[9];
        matrix.getValues(p);
        float p1 = p[0];
        if (currentMode == MODE_ZOOM) {
            if (p1 < MINI_SCALE) {
                matrix.setScale(MINI_SCALE, MINI_SCALE);
                //最小缩放比例时，居中显示
                this.setCenter(true, true);
            }
            if (p1 > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
    }
 
    //居中显示
    private void setCenter(boolean horizontal, boolean vertical) {
        Matrix mMatrix = new Matrix();
        mMatrix.set(matrix);
        RectF mRectF = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mMatrix.mapRect(mRectF);
        float height = mRectF.height();
        float width = mRectF.width();
        float deltaX = 0, deltaY = 0;
        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight = DeviceBaseInfo.DEVICE_HEIGHT;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - mRectF.top;
            } else if (mRectF.top > 0) {
                deltaY = -mRectF.top;
            } else if (mRectF.bottom < screenHeight) {
                deltaY = ivZooming.getHeight() - mRectF.bottom;
            }
        }
 
        if (horizontal) {
            int screenWidth = DeviceBaseInfo.DEVICE_WIDTH;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - mRectF.left;
            } else if (mRectF.left > 0) {
                deltaX = -mRectF.left;
            } else if (mRectF.right < screenWidth) {
                deltaX = screenWidth - mRectF.right;
            }
 
        }
        matrix.postTranslate(deltaX, deltaY);
    }
}