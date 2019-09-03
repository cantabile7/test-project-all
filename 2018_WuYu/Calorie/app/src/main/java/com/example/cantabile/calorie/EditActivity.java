package com.example.cantabile.calorie;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    Button add_btn;
    DBHelper dbHelper;
    SQLiteDatabase database;
    TextView tv;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        //tv = (TextView) findViewById(R.id.result);
        lv = (ListView) findViewById(R.id.reclv);
        searchDatabase(lv);
        add_btn = (Button) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
    }



    public void searchDatabase(ListView lvs) // 查询数据库中的数据
    {
        if(dbHelper == null)
        {
            dbHelper = new DBHelper(this);
        }
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

        StringBuilder str = new StringBuilder();
        if(cursor.moveToFirst()) // 显示数据库的内容
        {
            for(; !cursor.isAfterLast(); cursor.moveToNext()) // 获取查询游标中的数据
            {
                str.append(cursor.getString(cursor.getColumnIndex(DBHelper.DATE)) + "     ");
                str.append(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD)) + "\n");
            }
        }
        cursor.close(); // 记得关闭游标对象

        if(str.toString().equals(""))
        {
            str.append("数据库为空！");
            //lvs.setTextColor(Color.RED);
        }
        else
        {
            // lvs.setTextColor(Color.BLACK);
        }
        List<String> lvss = new ArrayList<String>();
        lvss.add(str.toString());
        ArrayAdapter adapter =new ArrayAdapter(this,android.R.layout.simple_list_item_1,lvss);
        lv.setAdapter(adapter);
        //lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

}