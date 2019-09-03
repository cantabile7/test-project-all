package com.example.administrator.pagerviewtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.administrator.pagerviewtest.bean.RecordingItem;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Recording.db";
    private static final int DB_VERSION = 1;

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    private Context mContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    public static abstract class DBItem implements BaseColumns {
        public static final String TABLE_NAME = "saved_recordings";

        public static final String COLUMN_RECORDING_NAME = "recording_name";
        public static final String COLUMN_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_RECORDING_LENGTH = "length";
        public static final String COLUMN_ADDED_TIME = "added_time";
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }
    // 注意空格！！！
    private static final String CREATE_RECORDING_TABLE =
            "create table " + DBItem.TABLE_NAME + "("
            + DBItem._ID + " integer primary key,"
            + DBItem.COLUMN_RECORDING_NAME + " text,"
            + DBItem.COLUMN_RECORDING_FILE_PATH + " text,"
            + DBItem.COLUMN_RECORDING_LENGTH + " integer,"
            + DBItem.COLUMN_ADDED_TIME + " integer)";

    public long addItem(String recordingName, String filePath, long length) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBItem.COLUMN_RECORDING_NAME, recordingName);
        cv.put(DBItem.COLUMN_RECORDING_FILE_PATH, filePath);
        cv.put(DBItem.COLUMN_RECORDING_LENGTH, length);
        cv.put(DBItem.COLUMN_ADDED_TIME, System.currentTimeMillis());
        long rowID = db.insert(DBItem.TABLE_NAME, null, cv);


        Cursor cursor = db.query(DBItem.TABLE_NAME, null, null,
                null, null, null, null);
        cursor.moveToLast();
        RecordingItem item = new RecordingItem();
        item.setId(cursor.getInt(cursor.getColumnIndex(DBItem._ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_NAME)));
        item.setFilePath(cursor.getString(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_FILE_PATH)));
        item.setLength(cursor.getInt(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_LENGTH)));
        item.setTime(cursor.getLong(cursor.getColumnIndex(DBItem.COLUMN_ADDED_TIME)));

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onAddNewItem(item);
        }

        return rowID;
    }

    public ArrayList<RecordingItem> getAllItem() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecordingItem> list = new ArrayList<>();

        Cursor cursor = db.query(DBItem.TABLE_NAME, null, null,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                RecordingItem item = new RecordingItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(DBItem._ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_NAME)));
                item.setFilePath(cursor.getString(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_FILE_PATH)));
                item.setLength(cursor.getInt(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_LENGTH)));
                item.setTime(cursor.getLong(cursor.getColumnIndex(DBItem.COLUMN_ADDED_TIME)));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return list;
    }

    public RecordingItem getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DBItem.TABLE_NAME, null, null,
                null, null, null, null);

        if (cursor.moveToPosition(position)) {
            RecordingItem item = new RecordingItem();
            item.setId(cursor.getInt(cursor.getColumnIndex(DBItem._ID)));
            item.setName(cursor.getString(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_NAME)));
            item.setFilePath(cursor.getString(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_FILE_PATH)));
            item.setLength(cursor.getInt(cursor.getColumnIndex(DBItem.COLUMN_RECORDING_LENGTH)));
            item.setTime(cursor.getLong(cursor.getColumnIndex(DBItem.COLUMN_ADDED_TIME)));
            cursor.close();
            return item;
        }
        cursor.close();

        return null;
    }

    public void removeItemById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DBItem.TABLE_NAME, "_id=?", new String[] {String.valueOf(id)});
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DBItem.TABLE_NAME, null, null,
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

//    public Context getContext() {
//        return this.mContext;
//    }

    public void renameItem(RecordingItem item, String fileName, String filePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBItem.COLUMN_RECORDING_NAME, fileName);
        cv.put(DBItem.COLUMN_RECORDING_FILE_PATH, filePath);
        db.update(DBItem.TABLE_NAME, cv, DBItem._ID + "=" +item.getId(),
                null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORDING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
