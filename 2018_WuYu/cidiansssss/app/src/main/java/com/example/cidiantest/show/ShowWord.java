package com.example.cidiantest.show;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cidiantest.DatabaseHelper;
import com.example.cidiantest.Item;
import com.example.cidiantest.R;
import com.example.cidiantest.adapter.MyAdapter_Chapter;
import com.example.cidiantest.adapter.MyAdapter_Word;

import java.util.ArrayList;

public class ShowWord extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        final ListView lv_chapter = (ListView) findViewById(R.id.lv_word);
        final ArrayList<Item> wordlist = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("chapter");
        String chapterName = bundle.getString("chaptername");

        //设置上方标题 (chapter)
        TextView tv = (TextView) findViewById(R.id.chapter_name);
        tv.setText(chapterName);

        //打开数据库读取
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //创建游标对象

        Cursor cursor_word = db.query("table_word", new String[]{"Word, WordTranslation, Chapter"},
                                  "Chapter=?", new String[]{String.valueOf(id)},
                null, null, "Word", null);

        //判断该章节是否存在单词数据..
        if(cursor_word.getCount()==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("暂无数据！");
            builder.setTitle("错误提示");
            builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        }
        else{
            //利用游标遍历所有数据对象
            while (cursor_word.moveToNext()) {
                //int part = cursor_word.getInt(cursor_word.getColumnIndex("Part"));
                int chapter = cursor_word.getInt(cursor_word.getColumnIndex("Chapter"));
                String word = cursor_word.getString(cursor_word.getColumnIndex("Word"));
                String wordtranslation = cursor_word.getString(cursor_word.getColumnIndex("WordTranslation"));
                wordlist.add(new Item(chapter, word, wordtranslation));
                //Log.d("测试：", "从数据库中读取了数据：chapter:" + chapter + ",  " + word + ",  " + wordtranslation);
            }

            //构建第一个界面：Part的选择
            final MyAdapter_Word myAdapterWord = new MyAdapter_Word(this, R.layout.list_view_word, wordlist);
            lv_chapter.setAdapter(myAdapterWord);

            //添加监听 实现word -> example
            lv_chapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chapter", wordlist.get(position).getChapter());
                    bundle.putString("word", wordlist.get(position).getWord());
                    bundle.putString("wordtranslation", wordlist.get(position).getWordTranslation());
                    //bundle.putInt("skill_detail", skillList.get(position).getSkillDetail());
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(ShowWord.this, ShowExample.class);
                    startActivity(intent);
                }
            });

        }


    }
}
