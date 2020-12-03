package com.kevin.prodapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;


import com.kevin.prodapp.ui.main.MainActivity;

import java.util.LinkedHashSet;


/**
 * Created by 16509 on 2018/8/8.
 */

public class ActivityManager extends Application {

    /**
     *  定义HashSet集合来装Activity，是可以防止Activity不被重复
     */
    private static LinkedHashSet<Activity> hashSet = new LinkedHashSet<Activity>();

    private static ActivityManager instance = new ActivityManager();

    private ActivityManager() {}

    private SharePManager sPManger;

    public static ActivityManager getInstance() {
        return instance;
    }

    /**
     * 每一个Activity 在 onCreate 方法的时候，可以装入当前this
     * @param activity
     */
    public void addActivity(Activity activity) {
        try {
            hashSet.add(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : hashSet) {
            activity.finish();
        }

        System.exit(0);
    }

    /**
     * 调用此方法用于退出整个Project
     */
    public void exit() {
        try {
            for (Activity activity : hashSet) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用此方法返回MainActivity
     */
    public void exitOther() {
        try {
            for (Activity activity : hashSet) {
                if(activity!=null){
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    sPManger = new SharePManager(activity, SharePManager.USER_FILE_NAME);//获取本地数据库信息
                    sPManger.putBoolean("BASActivity",true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
