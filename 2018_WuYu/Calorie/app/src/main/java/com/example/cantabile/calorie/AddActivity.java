package com.example.cantabile.calorie;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;


public class AddActivity extends AppCompatActivity {
    Button updata_btn;
    EditText add_calorie;
    CalendarView calendar;
    String add_datestr;  //选定的日期
    String recordestr;  //日期对应的卡路里记录
    Integer recorde;   //将string型记录转成int型
    Integer add_date;  //将string型日期转成int型
    DBHelper dbHelper;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calorie);

        //从calendar获取选定的日期
        calendar = findViewById(R.id.add_calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(AddActivity.this,
                        "你选择的是"+year+"年"+month+"月"+dayOfMonth+"日", Toast.LENGTH_LONG).show();
                add_datestr =""+year+""+month+""+dayOfMonth;
                add_date = Integer.parseInt(add_datestr);
                Toast.makeText(AddActivity.this,
                        add_datestr, Toast.LENGTH_LONG).show();

            }
        });

        //从edittext中获取该日的卡路里摄取量
       add_calorie = findViewById(R.id.add_calorie_tv);


        //update_btn set listener 点击updata按钮时的事件
        updata_btn = (Button) findViewById(R.id.updata_btn);
        updata_btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                recordestr=add_calorie.getText().toString();
                recorde = Integer.valueOf(recordestr);
                insertDatabase(add_date,recorde);
                }
        });

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
        database.insert(DBHelper.TABLE_NAME, null, cV);

        Toast.makeText(this, "插入数据成功", Toast.LENGTH_SHORT).show();
    }
}

