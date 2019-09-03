package net.culiuliu.dbdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SimpleDBHelper extends SQLiteOpenHelper {

    private static final String DBName = "School.db";
    private static final String STUDENT = "Student";
    private static final String TEACHER = "Teacher";

    private static final String CREATE_STUDENT_TABLE
            = "create table " + DATE + "(id integer primary key autoincrement, name text)";

    private static final String UPDATE_STUDENT_TABLE
            = "alter table " + STUDENT + " add height integer";

    private static final String CREATE_Teacher_TABLE
            = "create table " + TEACHER + "(id integer primary key autoincrement, name text)";

    public SimpleDBHelper(Context context, int version) {
        super(context, DBName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        switch (i) {
            case 1:
                //upgrade logic from 1 to 2
                sqLiteDatabase.execSQL(CREATE_Teacher_TABLE);
            case 2:
                // upgrade logic from 2 to 3
                sqLiteDatabase.execSQL(UPDATE_STUDENT_TABLE);
                break;
            default:
                throw new IllegalStateException("unknown oldVersion " + i);
        }

    }
}
