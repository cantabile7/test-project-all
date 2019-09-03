package com.example.jq.calorierecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    Button add;
    ListView listview;
    MyOpenHelper myOpenHelper;
    List list;
    Cursor cursor;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        myOpenHelper=new MyOpenHelper(getApplicationContext());
        add = (Button) findViewById(R.id.add);
        listview = (ListView) findViewById(R.id.listview);

        SQLiteDatabase db=myOpenHelper.getWritableDatabase();
        cursor=db.query("calorie",null,null,null,null,null,null);
        list=new ArrayList();
        if(cursor!=null&&cursor.getCount()>0){
            while (cursor.moveToNext()) {
                list.add(cursor.getString(1)  +":" +" " + cursor.getString(2));
            }
        }
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listview.setAdapter(arrayAdapter);

        add.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(Main2Activity.this,Main3Activity.class),1);
        }
    });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Main2Activity.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        String day= (String) list.get(position);
                        day=day.substring(0, day.indexOf(" ")-1);
                        SQLiteDatabase db=myOpenHelper.getWritableDatabase();
                        int delete=db.delete("calorie","date=?",new String[]{day});
                        list.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        }
                    });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if(resultCode == RESULT_OK){
                    String result = data.getStringExtra("calorie_data");
                    list.add(result);
                    listview.setAdapter(arrayAdapter);
                }
    }
}
