package com.example.jq.calorierecorder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {
    CalendarView calendarView;
    EditText zhi;
    String date;
    Button upoate;
    MyOpenHelper myOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        calendarView = (CalendarView)findViewById(R.id.calendarView);
        zhi = (EditText) findViewById(R.id.zhi);
        upoate= (Button) findViewById(R.id.upoate);
        myOpenHelper=new MyOpenHelper(getApplicationContext());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                date=year+"-"+(month+1)+"-"+dayOfMonth;
            }
        });
        upoate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("date", date);
                values.put("energy", zhi.getText().toString());
                long insert = db.insert("calorie", null, values);
                db.close();
                ;
                if (insert > 0) {
                    Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "添加失败！", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.putExtra("calorie_data",date+":"+" "+zhi.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
