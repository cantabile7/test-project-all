package com.example.jq.calorierecorder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText begin,end;
    Button edit,query;
    MyOpenHelper myOpenHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        begin = (EditText)findViewById(R.id.begin);
        end = (EditText)findViewById(R.id.end);
        edit = (Button) findViewById(R.id.edit);
        query = (Button) findViewById(R.id.query);

        begin.setInputType(InputType.TYPE_NULL);
        end.setInputType(InputType.TYPE_NULL);

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(begin);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(end);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
        });
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataPoint[] points = new DataPoint[10];
                SQLiteDatabase db=myOpenHelper.getWritableDatabase();
                cursor=db.query("calorie",null,null,null,null,null,null);
                GraphView graph = (GraphView) findViewById(R.id.graph);
                if(cursor!=null&&cursor.getCount()>0){
                    while (cursor.moveToNext()) {
                        int a= Integer.parseInt(cursor.getString(1));
                        int b= Integer.parseInt(cursor.getString(2));
                        points=new DataPoint[]{new DataPoint(a,b)};
                    }
                }
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
                graph.addSeries(series);

            }
        });
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                editText.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}
