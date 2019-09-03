package com.example.dictionary.show;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionary.DatabaseHelper;
import com.example.dictionary.Item;
import com.example.dictionary.R;
import com.example.dictionary.activity.MainActivity;
import com.example.dictionary.adapter.MyAdapter_Word;

import java.util.ArrayList;

public class ShowWord extends Activity {
    private int item_id;
    private ArrayList<Item> res=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        final ListView lv_chapter = (ListView) findViewById(R.id.lv_word);
        final ArrayList<Item> wordlist = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("chapter");
        int part = bundle.getInt("part");
        String chapterName = bundle.getString("chaptername");

        //设置上方标题 (chapter)
        TextView tv = (TextView) findViewById(R.id.chapter_name);
        tv.setText(chapterName);

        //打开数据库读取
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //创建游标对象
        String sql = "SELECT * FROM table_word WHERE Part="+part+" AND Chapter="+id+";";
        Cursor cursor_word = db.query("table_word", new String[]{"Word, WordTranslation, Chapter"},
                                  "Chapter=?", new String[]{String.valueOf(id)},
               null, null, "Word", null);
        //Cursor cursor_word = db.query(sql);
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
            res=wordlist;
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

            registerForContextMenu(lv_chapter);

        }


    }

    //创建listview上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //获取对应的item的positon
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        item_id = info.position;
        //设置菜单布局
        menu.setHeaderTitle("选择对该单词的操作:");
        menu.add(1,1,1,"删除该单词");
        menu.add(1,2,1,"修改该单词");
    }
    //菜单响应
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String str = res.get(info.position).getWord();
        switch(item.getItemId()){
            case 1:
                //删除
                deleteWord(str);
                break;
            case 2:
                //修改
                updateWord(str);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //删除单词
    private void deleteWord(String delword){
        //创建一个DatabaseHelper对象
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        //取得一个读写的数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //删除
        db.delete("table_word",
                "Word=?",  new String[]{delword} );

        if(db.delete("table_word",
                "Word=?",  new String[]{delword} )>0){
            Toast.makeText(getApplicationContext(),"删除失败！",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"删除成功！",Toast.LENGTH_SHORT).show();
        }
    }

    //修改单词
    private void updateWord(final String updateword){
        LayoutInflater factory = LayoutInflater.from(this);
        final View addView = factory.inflate(R.layout.addword, null);
        final EditText part = addView.findViewById(R.id.part);
        final EditText chapter = addView.findViewById(R.id.chapter);
        final EditText word = addView.findViewById(R.id.word);
        final EditText wordTrans = addView.findViewById(R.id.wordTrans);
        final EditText example = addView.findViewById(R.id.example);
        final EditText exampleTrans = addView.findViewById(R.id.exampleTrans);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(ShowWord.this);
        word.setText(updateword);
        ad1.setTitle("修改单词:");
        ad1.setView(addView);
        ad1.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String partstr = part.getText().toString();
                String chapterstr = chapter.getText().toString();
                String wordstr = word.getText().toString();
                String wordTransstr = wordTrans.getText().toString();
                String examplestr = example.getText().toString();
                String exampleTransstr = exampleTrans.getText().toString();
                //如果有任一信息为填，则不能操作
                if(partstr.equals("") || chapterstr.equals("") || wordstr.equals("") ||
                        wordTransstr.equals("") || examplestr.equals("") || exampleTransstr.equals("")){
                    Toast.makeText(getApplicationContext(), "修改失败！请填写完整的单词信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //创建一个DatabaseHelper对象
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                //取得一个读写的数据库对象
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //修改到 单词 数据表
                ContentValues values = new ContentValues();
                values.put("Chapter", chapterstr);
                values.put("Word", wordstr);
                values.put("WordTranslation", wordTransstr);
                db.update("table_word", values, "Word=?",new String[]{updateword});
                //修改到 用例 数据表
                ContentValues values2 = new ContentValues();
                values2.put("Example", examplestr);
                values2.put("ExampleTranslation", exampleTransstr);
                values2.put("Word", wordstr);
                db.update("table_example",values2,"Word=?", new String[]{updateword});
                db.close();
                Toast.makeText(getApplicationContext(), "修改完成", Toast.LENGTH_SHORT).show();
            }
        });
        ad1.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();


    }
}
