//package com.kevin.prodapp.utils;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.kevin.prodapp.entity.HistoryCache;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by xiong on 2016/11/29.
// * SQLiteOpenHelper
// *
// * @author xiong
// */
//public  class DBHelper extends SQLiteOpenHelper {
//
//    private static final String CREATE_CacheTABLE = "create table historycache (id integer primary key autoincrement, key text, value text, lasttime long, bak text, flag text)";
//    public DBHelper (final Context context, final String name, final SQLiteDatabase.CursorFactory factory, final int version) {
//        super (context, name, factory, version);
//    }
//
//    @Override
//    public void onCreate (final SQLiteDatabase db) {
//        db.execSQL (CREATE_CacheTABLE);
//        updatetable (db, HistoryCache.class);
//    }
//
//    @Override
//    public void onUpgrade (final SQLiteDatabase db, final int oldVersion, final int newVersion) {
//        updatetable (db, HistoryCache.class);
//    }
//
//    /**
//     * 传入的类名即为表名，传入的类的属性即为表内的记录，字段固定，用来实现动态增减记录，记录为缓存内容，所以数量较少，
//     * 只需要更改实体类属性，就可以管理数据库了，动态升级。
//     *
//     * @param db
//     * @param mClass
//     */
//    private void updatetable (final SQLiteDatabase db, Class mClass) {
//        /**
//         * 通过反射拿到当前所有cache名
//         */
//        List<String> mList = new ArrayList<>();
//        Field[] fields = mClass.getDeclaredFields ();
//        for (Field fd : fields) {
//            fd.setAccessible (true);
//            mList.add (fd.getName ());
//        }
//
//        Cursor mCursor = db.rawQuery ("select * from " + mClass.getSimpleName (), null);
//        while (mCursor.moveToNext ()) {
//            boolean ishave = false;
//            String string = mCursor.getString (1);
//            Iterator<String> mStringIterator = mList.iterator ();
//            while (mStringIterator.hasNext ()) {
//                if (mStringIterator.next ().equals (string)) {
//                    ishave = true;
//                    mStringIterator.remove ();
//                    break;
//                }
//            }
//            /**
//             * 类里没有这个缓存名就将其删掉
//             */
//            if (!ishave) {
//                db.delete (mClass.getSimpleName (), "key=?", new String[]{string});
//            }
//        }
//        mCursor.close ();
//        for (int mI = 0; mI < mList.size (); mI++) {
//            ContentValues values = new ContentValues ();
//            values.put ("key", mList.get (mI));
//            values.put ("lasttime", System.currentTimeMillis ());
//            db.insert (mClass.getSimpleName (), null, values);
//        }
//    }
//}
