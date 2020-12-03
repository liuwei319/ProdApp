package com.kevin.prodapp.utils;
 
import android.app.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
 
/**
 * 读取本地保存的proper文件,保存的行为记录
 * 
 * @author qiulinhe
 * @createTime 2016年6月7日 下午1:28:16
 */
public class ProperUtil {
	/**
	 * 得到netconfig.properties配置文件中的所有配置属性
	 * 
	 * @return Properties对象
	 */
	public static Properties getNetConfigProperties() {
		Properties props = new Properties();
		//		InputStream in = ProperUtil.class.getResourceAsStream("/operation.properties");
		InputStream in = null;
		try {
			in = new FileInputStream(android.os.Environment.getExternalStorageDirectory() + File.separator
					+ "config.properties");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
 
	/**
	 * 给属性文件添加属性
	 * 
	 * @param param
	 * @param value
	 * @author qiulinhe
	 * @createTime 2016年6月7日 下午1:46:53
	 */
	public static void put(Activity activity, String key, String value) {
		String PROP_PATH = activity.getApplicationContext().getFilesDir().getAbsolutePath();
		Properties p = new Properties();
		try {
			InputStream in = new FileInputStream(android.os.Environment.getExternalStorageDirectory() + File.separator
					+ "config.properties");
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.setProperty(key, value);
		OutputStream fos;
		try {
			fos = new FileOutputStream(android.os.Environment.getExternalStorageDirectory() + File.separator
					+ "config.properties");
			p.store(fos, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}