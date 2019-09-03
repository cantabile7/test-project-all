package com.example.cantabile.calorie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {
    //数据库名称
    public static final String DBNAME = "CalorieRecorder.db";
    public final static int DATABASE_VERSION = 1;

    public final static String TABLE_NAME = "calorierecord";
    public static final String RECORD = "recorde";  //记录的卡路里
    public static final String DATE = "_date";   //日期作为主键

    public static String CREATE_TABLE = "create table "+ TABLE_NAME +"(" +
            DATE + " integer primary key autoincrement, " +
            RECORD + " integer)"; // 用于创建表的SQL语句
    private Context myContext = null;

    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context)
    {
        super(context, DBNAME, null, DATABASE_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("UseDatabase", "创建数据库");
        Toast.makeText(myContext, "创建数据库", Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }



}
