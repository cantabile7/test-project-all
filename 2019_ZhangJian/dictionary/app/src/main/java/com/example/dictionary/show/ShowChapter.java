package com.example.dictionary.show;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dictionary.DatabaseHelper;
import com.example.dictionary.Item;
import com.example.dictionary.adapter.MyAdapter_Chapter;
import com.example.dictionary.R;

import java.util.ArrayList;


public class ShowChapter extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        final ListView lv_chapter = (ListView) findViewById(R.id.lv_ccc);
        final ArrayList<Item> chapterlist = new ArrayList<>();

        Bundle bundle=getIntent().getExtras();
        int id=bundle.getInt("part");
        String partname = bundle.getString("partname");

        //设置上方标题（part name）
        TextView partName = (TextView) findViewById(R.id.part_name);
        partName.setText(partname);
        //打开数据库读取
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor_part = db.query("table_chapter", new String[]{"Part, Chapter, ChapterName"}, "Part=?", new String[]{String.valueOf(id)}, null, null, null, null);

        //利用游标遍历所有数据对象
        while(cursor_part.moveToNext()){
            int part = cursor_part.getInt(cursor_part.getColumnIndex("Part"));
            int chapter = cursor_part.getInt(cursor_part.getColumnIndex("Chapter"));
            String chaptername = cursor_part.getString(cursor_part.getColumnIndex("ChapterName"));
            chapterlist.add(new Item(part,partname,chapter,chaptername));
            //Log.d("测试：", "从数据库中读取了数据："+chapter +","+chaptername);
        }
        //构建第一个界面：Part的选择
        final MyAdapter_Chapter myAdapterChapter = new MyAdapter_Chapter(this, R.layout.list_view_chapter, chapterlist);
        lv_chapter.setAdapter(myAdapterChapter);

        //添加监听 实现chapter -> word
        lv_chapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("part", chapterlist.get(position).getPart());
                bundle.putInt("chapter", chapterlist.get(position).getChapter());
                bundle.putString("chaptername", chapterlist.get(position).getChapterName());
                //bundle.putInt("skill_detail", skillList.get(position).getSkillDetail());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(ShowChapter.this, ShowWord.class);
                startActivity(intent);
            }
        });

    }


}
