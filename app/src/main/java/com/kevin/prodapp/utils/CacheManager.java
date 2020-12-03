//package com.kevin.prodapp.utils;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.kevin.prodapp.entity.CacheEntity;
//import com.kevin.prodapp.entity.HistoryCache;
//
//public  class CacheManager {
//    private static SQLiteDatabase mSQLiteDatabase;
//    private static DBHelper mCacheDBHelper=new DBHelper(null,"",null,0);
//    private static String DBName;
//    private static int DBVersion;
//    private static Context mContext ;
//    public HistoryCache historyCache;
//    /**
//     * 更新缓存
//     *
//     * @param key   预定义名称
//     * @param value 待缓存数据
//     */
//    public synchronized static void updateCache (String key, String value) {
//        updateCache (new CacheEntity().setKey (key).setValue (value));
//    }
//
//    /**
//     * 更新缓存
//     * 不能手动更新id、key和lasttime
//     *
//     * @param mCacheEntity
//     */
//    public synchronized static void updateCache (CacheEntity mCacheEntity) {
//
//        if (mCacheDBHelper == null) {
//
//            mCacheDBHelper = new DBHelper (mContext, DBName, null, DBVersion);
//        }
//        if (mSQLiteDatabase == null) {
//            mSQLiteDatabase = mCacheDBHelper.getWritableDatabase ();
//        }
//        ContentValues m = new ContentValues ();
//        m.put ("value", mCacheEntity.value);
//        m.put ("lasttime", System.currentTimeMillis ());
//        m.put ("bak", mCacheEntity.bak);
//        m.put ("flag", mCacheEntity.flag);
//        try {
//            mSQLiteDatabase.update (HistoryCache.class.getSimpleName (), m, "key=?", new String[]{mCacheEntity.key});
//        } catch (Exception mE) {
//            mE.printStackTrace ();
//        }
//    }
//    /**
//     * 获取缓存数据
//     *
//     * @param key 预定义名称
//     * @return 缓存数据，异常或者不存在则返回null
//     */
//    public static CacheEntity getCache (String key) {
//        CacheEntity mCacheEntity = new CacheEntity ();
//
//        if (mCacheDBHelper == null) {
//            mCacheDBHelper = new DBHelper (mContext, DBName, null, DBVersion);
//        }
//        if (mSQLiteDatabase == null) {
//            mSQLiteDatabase = mCacheDBHelper.getWritableDatabase ();
//        }
//        Cursor mCursor = null;
//        try {
//            mCursor = mSQLiteDatabase.rawQuery ("select * from " + HistoryCache.class.getSimpleName () + " where key=?", new String[]{key});
//            if (mCursor != null && mCursor.getCount () == 1) {
//                mCursor.moveToNext ();
//                mCacheEntity.id = mCursor.getInt (0);
//                mCacheEntity.key = mCursor.getString (1);
//                mCacheEntity.value = mCursor.getString (2);
//                mCacheEntity.lasttime = mCursor.getLong (3);
//                mCacheEntity.bak = mCursor.getString (4);
//                mCacheEntity.flag = mCursor.getString (5);
//
//            }
//        } catch (Exception mE) {
//            mE.printStackTrace ();
//        } finally {
//            if (mCursor != null) {
//                mCursor.close ();
//            }
//            return mCacheEntity;
//        }
//    }
//    /**
//     * 删除数据库
//     */
//    public synchronized static void deleteDB () {
//        mContext.deleteDatabase (DBName);
//    }
//}