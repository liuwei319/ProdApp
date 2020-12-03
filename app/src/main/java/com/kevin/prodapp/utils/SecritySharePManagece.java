package com.kevin.prodapp.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 轻量安全文件存储
 * @author t-wenll
 *
 */
public class SecritySharePManagece implements SharedPreferences {
	
	private SharedPreferences msharepreference;
	private static final String TAG=SecritySharePManagece.class.getSimpleName();
	private Context mcontext;
	
	public SecritySharePManagece(Context context,String name,int mode){
		mcontext=context;
		if(TextUtils.isEmpty(name)){
			msharepreference=PreferenceManager.getDefaultSharedPreferences(context);
		}else{
			msharepreference=context.getSharedPreferences(name, mode);
		}
	}

//	private String encryptPreference(String plainText){
//		return AESEyDy.getInstance(mcontext).encrypt(plainText);
//	}
//
//	private String decryptPreference(String plainText){
//		return AESEyDy.getInstance(mcontext).decrypt(plainText);
//	}
	
//	public void handleTranstion(){
//		Map<String, ?> oldMap = msharepreference.getAll();
//
//		Map<String, String> newMap= new HashMap<String, String>();
//
//		for (Entry<String, ?> entry : oldMap.entrySet()) {
//			newMap.put(encryptPreference(entry.getKey()), encryptPreference(entry.getValue().toString()));
//		}
//		Editor editor = msharepreference.edit();
//		editor.clear().commit();
//
//		for (Entry<String, String> entry : newMap.entrySet()) {
//			editor.putString(entry.getKey(), entry.getValue());
//		}
//		editor.commit();
//	}
//

	@Override
	public boolean contains(String key) {
		// TODO Auto-generated method stub
		return msharepreference.contains(key);
	}

	/**
	 * 编辑加密
	 */
	@Override
	public SecurityEditor edit() {
		// TODO Auto-generated method stub
		return new SecurityEditor();
	}

	@Override
	public Map<String, String> getAll() {
		final Map<String, ?> encryptMap=msharepreference.getAll();
		final Map<String, String> decryptMap= new HashMap<String, String>();
		for (Entry<String, ?>  entry : encryptMap.entrySet()) {
			Object cipherText = entry.getValue();
			if(cipherText!=null){
				decryptMap.put(entry.getKey(), entry.getValue().toString());
			}
		}
		return decryptMap;
	}

	@Override
	public boolean getBoolean(String key, boolean defvalue) {
		// TODO Auto-generated method stub
		final String encyptValue=msharepreference.getString(key, null);
		return encyptValue==null?defvalue :Boolean.parseBoolean(encyptValue);
	}

	@Override
	public float getFloat(String key, float defvalue) {
		// TODO Auto-generated method stub
		final String encyptValue=msharepreference.getString(key, null);
		return encyptValue==null?defvalue :Float.parseFloat(encyptValue);
	}

	@Override
	public int getInt(String key, int defvalue) {
		// TODO Auto-generated method stub
		final String encyptValue=msharepreference.getString(key, null);
		return encyptValue==null?defvalue :Integer.parseInt(encyptValue);
	}

	@Override
	public long getLong(String key, long defvalue) {
		// TODO Auto-generated method stub
		final String encyptValue=msharepreference.getString(key, null);
		return encyptValue==null?defvalue :Long.parseLong(encyptValue);
	}

	@Override
	public String getString(String key, String defvalue) {
		// TODO Auto-generated method stub
		final String encyptValue=msharepreference.getString(key, null);
		return encyptValue==null?defvalue :encyptValue;
	}

	@Override
	public Set<String> getStringSet(String key, Set<String> defvalues) {
		// TODO Auto-generated method stub
		final Set<String> encryptSet =msharepreference.getStringSet(key, null);
		if(encryptSet==null){
			return defvalues;
		}

		final Set<String> decryptSet = new HashSet<String>();
		for (String encryptValue : encryptSet) {
			decryptSet.add(encryptValue);
		}
		return decryptSet;
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener arg0) {
		// TODO Auto-generated method stub
		msharepreference.registerOnSharedPreferenceChangeListener(arg0);
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener arg0) {
		// TODO Auto-generated method stub
		msharepreference.unregisterOnSharedPreferenceChangeListener(arg0);
	}

	public final class SecurityEditor implements Editor{

		private Editor meditor;
		private SecurityEditor(){
			meditor=msharepreference.edit();
		}

		@Override
		public void apply() {
			// TODO Auto-generated method stub
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD){
				meditor.apply();
			}else{
				commit();
			}

		}

		@Override
		public Editor clear() {
			// TODO Auto-generated method stub
			meditor.clear();
			return this;
		}

		@Override
		public boolean commit() {
			// TODO Auto-generated method stub
			return meditor.commit();
		}

		@Override
		public Editor putBoolean(String key, boolean value) {
			// TODO Auto-generated method stub
			meditor.putString(key,Boolean.toString(value));
			return this;
		}

		@Override
		public Editor putFloat(String key, float value) {
			// TODO Auto-generated method stub
			meditor.putString(key, Float.toString(value));
			return this;
		}

		@Override
		public Editor putInt(String key, int value) {
			// TODO Auto-generated method stub
			meditor.putString(key,Integer.toString(value));
			return this;
		}

		@Override
		public Editor putLong(String key, long value) {
			// TODO Auto-generated method stub
			meditor.putString(key, Long.toString(value));
			return this;
		}

		@Override
		public Editor putString(String key, String value) {
			// TODO Auto-generated method stub
			meditor.putString(key, value);
			return this;
		}

		@Override
		public Editor putStringSet(String key, Set<String> values) {
			// TODO Auto-generated method stub
			final Set<String> encryptSet = new HashSet<String>();
			for (String value : values) {
				encryptSet.add(value);
			}
			meditor.putStringSet(key, encryptSet);
			return this;
		}

		@Override
		public Editor remove(String arg0) {
			// TODO Auto-generated method stub
			meditor.remove(arg0);
			return this;
		}
	}

}
