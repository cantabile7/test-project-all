package com.example.dictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String SWORD="SWORD";
    public DatabaseHelper(Context context) {
        super(context, "dianzicidian.db", null, 1);
    }
    //三个不同参数的构造函数
    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);

    }
    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public DatabaseHelper(Context context, String name){
        this(context,name,VERSION);
    }
    //带三个参数的构造函数，调用的是带所有参数的构造函数
    public DatabaseHelper(Context context,String name,int version){
        this(context, name,null,version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句
        //创建表 table_part
        //部分
        String sql_1 = "create table if not exists table_part(id integer primary key autoincrement," +
                       "Part integer," +
                       "PartName varchar(20))";

        //创建表 table_chapter
        //章节
        String sql_2 = "create table if not exists table_chapter(id integer primary key autoincrement," +
                "Chapter integer," +
                "ChapterName varchar(20)," +
                "Part integer,"+
                "FOREIGN KEY (Part) REFERENCES table_part(Part))";  //外键

        //创建表 table_word
        //单词及翻译
        String sql_3 = "create table if not exists table_word(id integer primary key autoincrement," +
                "Word varchar(20)," +
                "WordTranslation varchar(40)," +
                "Chapter integer,"+
                "FOREIGN KEY (Chapter) REFERENCES table_chapter(Chapter))";  //外键

        //创建表 table_example
        //例句及翻译
        String sql_4 = "create table if not exists table_example(id integer primary key autoincrement," +
                "Example varchar(40)," +
                "ExampleTranslation varchar(100)," +
                "Word varchar(20),"+
                "FOREIGN KEY (Word) REFERENCES table_word(Word))";  //外键

        //执行创建数据库操作
        db.execSQL(sql_1);
        db.execSQL(sql_2);
        db.execSQL(sql_3);
        db.execSQL(sql_4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
