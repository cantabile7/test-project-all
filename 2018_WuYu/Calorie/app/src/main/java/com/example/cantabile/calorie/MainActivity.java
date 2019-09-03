package com.example.cantabile.calorie;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button edit_btn;
    Button query_btn;
    EditText begin_text,end_text;
    DBHelper dbHelper;
    SQLiteDatabase database;
    String begindate_str,enddate_str;
    Integer begindate,enddate;
    StringBuilder result=new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //edit_btn set listener  (设置编辑、添加卡路里记录按钮的监听器)
        edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });
        //begin_text set listener  (设置查询起始、结束日期的监听器)
        begin_text = (EditText) findViewById(R.id.begin_text);
        begin_text.setInputType(InputType.TYPE_NULL);   //设置不显示系统输入键盘
        begin_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDatePickerDialog(begin_text);
                }
            }
        });
        end_text = (EditText) findViewById(R.id.end_text);
        end_text.setInputType(InputType.TYPE_NULL);   //设置不显示系统输入键盘
        end_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDatePickerDialog(end_text);
                }
            }
        });



        query_btn = (Button) findViewById(R.id.query_btn);
        query_btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                begindate_str=begin_text.getText().toString();
                begindate=Integer.parseInt(begindate_str);
                enddate_str=end_text.getText().toString();
                enddate=Integer.parseInt(enddate_str);
                searchDatabase(begindate,enddate,result);
                AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("查询结果")//标题
                        .setMessage(result)//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
                result.setLength(0);
            }
        });

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(5, 11)
        });
        graph.addSeries(series);

        //创建数据库
        createDatabase();
    }

    //显示日期选择器
    private void showDatePickerDialog(final EditText editText){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editText.setText(year+""+month+""+dayOfMonth);
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // 创建或者打开数据库

    public void createDatabase()
    {
        dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase();
        Toast.makeText(this,
                "**成功创建数据库**", Toast.LENGTH_LONG).show();
    }

    public void insertDatabase(Integer date,Integer record) // 向数据库中插入新数据
    {

        if(dbHelper == null)
        {
            dbHelper = new DBHelper(this);
        }
        database = dbHelper.getWritableDatabase();
       // Toast.makeText(this, "插入数据成功", Toast.LENGTH_SHORT).show();
        ContentValues cV = new ContentValues();
        cV.put(DBHelper.DATE, date);
        cV.put(DBHelper.RECORD, record);


        Toast.makeText(this, "插入数据成功", Toast.LENGTH_SHORT).show();
    }

    public void updateDatabase(String new_record,String date) // 更新数据
    {
        if(dbHelper == null)
        {
            dbHelper = new DBHelper(this);
        }
        database = dbHelper.getWritableDatabase();

        ContentValues cV = new ContentValues();
        cV.put(DBHelper.RECORD, new_record);
        /*
         * 调用 update 方法 更新
         */
        database.update(DBHelper.TABLE_NAME, cV,
                DBHelper.DATE + "= ?", new String[]{date});
        /*
         * 对应的SQL语句：
         * database.execSQL("update " + DatabaseStatic.TABLENAME + " set " + DatabaseStatic.AUTHOR +
         *  "= ? where " + DatabaseStatic.BOOKNAME + " = ?", new String[]{"xiaoming", "C Language"});
         */

        Toast.makeText(this, "数据更新成功", Toast.LENGTH_SHORT).show();
    }

    public boolean deleteDatabase(String del_date) // 数据库中删除数据
    {
        if(dbHelper == null)
        {
            dbHelper = new DBHelper(this);
        }
        database = dbHelper.getWritableDatabase();

        /*
         * 调用 delete 方法删除数据库中的数据
         * 对应的SQL语句：
         * database.execSQL("delete from " +
         * DatabaseStatic.TABLE_NAME + " where " +
         * DatabaseStatic.BOOK_NAME + " = ?", new
         * String[]{"C Language"});
         */
        database.delete(DBHelper.TABLE_NAME, DBHelper.DATE + " = ? ",
                new String[]{del_date});

        Toast.makeText(this, "数据删除成功", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void searchDatabase(Integer bd, Integer ed,StringBuilder str) // 查询数据库中的数据
    {
        if(dbHelper == null)
        {
            dbHelper = new DBHelper(this);
        }
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        //StringBuilder str = new StringBuilder();
        String flagstr;
        Integer flag;
        if(cursor.moveToFirst()) // 显示数据库的内容
        {
            for(; !cursor.isAfterLast(); cursor.moveToNext()) // 获取查询游标中的数据
            {
                flagstr=cursor.getString(cursor.getColumnIndex(DBHelper.DATE));
                flag=Integer.parseInt(flagstr);
                if(flag>=bd && flag<=ed){
                    str.append(cursor.getString(cursor.getColumnIndex(DBHelper.DATE)) + " ");
                    str.append(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD)) + "\n");
                }
            }
        }
        cursor.close(); // 记得关闭游标对象

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
