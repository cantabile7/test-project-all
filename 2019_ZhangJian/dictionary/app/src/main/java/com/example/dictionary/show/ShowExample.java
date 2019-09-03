package com.example.dictionary.show;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.dictionary.DatabaseHelper;
import com.example.dictionary.Item;
import com.example.dictionary.R;
import com.example.dictionary.adapter.MyAdapter_example;

import java.util.ArrayList;

public class ShowExample extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        final ListView lv_chapter = (ListView) findViewById(R.id.lv_example);
        final ArrayList<Item> examplelist = new ArrayList<>();

        //获取传过来的数据（word, word translation)
        Bundle bundle=getIntent().getExtras();
        //int id=bundle.getInt("part");
        String word = bundle.getString("word");
        String wordtranslation = bundle.getString("wordtranslation");
        //int id2=bundle.getInt("skill_detail");
        //String message=bundle.getString("skill_detail");
        //TextView tv=(TextView) findViewById(R.id.tv_chapter);

        //打开数据库读取 以 word 为查询条件，查询table_example对应的翻译例子
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor = db.query("table_example", new String[]{"Example, ExampleTranslation"},
                "Word=?", new String[]{word}, null, null, null, null);

        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            String example = cursor.getString(cursor.getColumnIndex("Example"));
            String exampletranslation = cursor.getString(cursor.getColumnIndex("ExampleTranslation"));
            examplelist.add(new Item(word,wordtranslation,example,exampletranslation));
            //Log.d("测试：", "从数据库中读取了数据："+chapter +","+chaptername);
        }
        //构建第一个界面：Part的选择
        final MyAdapter_example myAdapterChapter = new MyAdapter_example(this, R.layout.list_view_example, examplelist);
        lv_chapter.setAdapter(myAdapterChapter);



    }

}
