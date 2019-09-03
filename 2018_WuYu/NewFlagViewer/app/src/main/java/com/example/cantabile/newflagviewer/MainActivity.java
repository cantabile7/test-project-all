package com.example.cantabile.newflagviewer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView simpleList;
    ArrayList<Item> skillList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleList = (ListView) findViewById(R.id.simpleListView);
        skillList.add(new Item("闪现", R.drawable.flash));
        skillList.add(new Item("点燃", R.drawable.dr));
        skillList.add(new Item("治疗术", R.drawable.zls));
        skillList.add(new Item("惩戒", R.drawable.cj));
        skillList.add(new Item("传送", R.drawable.tp));
        skillList.add(new Item("屏障", R.drawable.pz));
        skillList.add(new Item("净化", R.drawable.jh));
        skillList.add(new Item("清晰术", R.drawable.qxs));
        skillList.add(new Item("虚弱", R.drawable.xr));
        skillList.add(new Item("幽灵疾步", R.drawable.yljb));

        final MyAdapter myAdapter = new MyAdapter(this, R.layout.list_view_items, skillList);
        simpleList.setAdapter(myAdapter);


        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(skillList.remove(position)!=null){
                            System.out.println("success");
                        }else {
                            System.out.println("failed");
                        }
                        myAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "已删除列表项", Toast.LENGTH_SHORT).show();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();

                return false;
            }
        });


        //设置单击事件
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }



}