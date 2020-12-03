package com.kevin.prodapp.ui.main;


import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.utils.ActivityManager;
import com.kevin.prodapp.utils.SysApplication;

import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    // 都是static声明的变量，避免被实例化多次；因为整个app只需要一个计时任务就可以了。
    private static Timer mTimer; // 计时器，每1秒执行一次任务
    private static MyTimerTask mTimerTask; // 计时任务，判断是否未操作时间到达5s
    private static long mLastActionTime; // 上一次操作时间

    public static int activityActive; //全局变量,表示当前在前台的activity数量
    public static boolean isBackground ;

    // 每当用户接触了屏幕，都会执行此方法
    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastActionTime = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        SysApplication.getInstance().addActivity(this);
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SysApplication.getInstance().removeActivity(this);
//    }
//final Intent it = new Intent(this, LoginActivity.class); //你要转向的Activity
    private static class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            // 5s未操作
            if (System.currentTimeMillis() - mLastActionTime > 15*60000) {
                // 退出登录
                exit();
                //15分没操作退出应用
                SysApplication.getInstance().exit();
                // 停止计时任务
                stopTimer();
            }
        }
    }

    // 退出登录
    protected static void exit() {
        ActivityManager.getInstance().exit();
    }

    // 登录成功，开始计时
    protected static void startTimer() {
        mTimer = new Timer();
        mTimerTask = new MyTimerTask();
        // 初始化上次操作时间为登录成功的时间
        mLastActionTime = System.currentTimeMillis();
        // 每过1s检查一次
        mTimer.schedule(mTimerTask, 0, 60000);
    }

    // 停止计时任务
    protected static void stopTimer() {
        mTimer.cancel();
    }

    @Override
    protected void onStart() {
        //app 从后台唤醒，进入前台
        activityActive++;
        isBackground = false;
        super.onStart();
    }

    @Override
    protected void onStop() {
        activityActive--;
        if (activityActive == 0) {
            //app 进入后台
            isBackground = true;//记录当前已经进入后台
        }
        super.onStop();
    }
}
