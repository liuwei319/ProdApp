package com.kevin.prodapp.utils;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * 轻量级存储管理
 * 
 * @author gujs
 * 
 */
public class SharePManager {

	//用户编号
	public static String USER_ID = "user_id";
	//用户名
	public static String USER_NAME = "username";
	public static String ISREMB = "isRemenber";
	public static String PWD = "password";
	
	
	/** 默认情况下的文件名 **/
	private static final String DEFAULT_FILE_NAME = "sysData";
	/**
	 * loadSecretNumber：int:默认每次加载密钥的数量 <br />
	 * alarmsNumber:int:密钥警告<br />
	 * firstLogin:boolean:是否第一次登陆，是否需要内网激活
	 */

	public static final String USER_FILE_NAME = "usersflie";

	private SecritySharePManagece mSharedP;

	/**
	 * 以默认的文件名实例化轻量级存储对象 <br />
	 * DEFAULT_FILE_NAME = "sysData";
	 * 
	 * @param context
	 */
	public SharePManager(Context context) {
		mSharedP =new SecritySharePManagece(context, DEFAULT_FILE_NAME, Context.MODE_PRIVATE);
		//mSharedP.handleTranstion();
	}

	/**
	 * 以指定的文件名实例化轻量级存储对象 <br />
	 * 如果指定的文件名为null则采用默认的文件名
	 * 
	 * @param context
	 * @param fileName
	 */
	public SharePManager(Context context, String fileName) {
		mSharedP = new SecritySharePManagece(context, fileName, Context.MODE_PRIVATE);
		//mSharedP.handleTranstion();
	}
	/**
	 * 移除字段
	 * 
	 * @return
	 */
	public void removeString(String key) {
		mSharedP.edit().remove(key).commit();
	}

	/**
	 * 清空保存在SharePreference下的所有数据
	 */
	public void clear() {
		mSharedP.edit().clear().commit();
	}

	/**
	 * 获取当前轻量级存储对象
	 */
	public SharedPreferences getShareP() {
		return mSharedP;
	}

	/***************************************************************************************************/
	/**
	 * 读取字符串
	 */
	public String getString(String key) {
		return mSharedP.getString(key, null);
	}

	/**
	 * 读取整型值
	 */
	public int getInt(String key) {
		return mSharedP.getInt(key, 0);
	}

	/**
	 * 读取Long
	 */
	public long getLong(String key) {
		return mSharedP.getLong(key, 0);
	}

	/**
	 * 读取布尔值
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return mSharedP.getBoolean(key, defValue);
	}

	/**
	 * 保存字符串
	 */
	public void putString(String key, String value) {
		mSharedP.edit().putString(key, value).commit();
	}

	/**
	 * 保存整型值
	 */
	public void putInt(String key, int value) {
		mSharedP.edit().putInt(key, value).commit();
	}

	/**
	 * 保存布尔值
	 */
	public void putBoolean(String key, Boolean value) {
		mSharedP.edit().putBoolean(key, value).commit();
	}
}
