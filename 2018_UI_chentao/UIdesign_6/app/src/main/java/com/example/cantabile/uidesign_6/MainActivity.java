package com.example.cantabile.uidesign_6;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adapter_layout);

        ListView listView = (ListView) findViewById(R.id.listview);
        String[] contents={"4月1日     班级活动","4月1日     班级活动","4月1日     班级活动","4月1日     班级活动","4月1日     班级活动"};
        MyArrayAdapter adapter = new MyArrayAdapter(this,R.layout.custom_arrayadapter_item,contents);
        listView.setAdapter(adapter);
    }
}
