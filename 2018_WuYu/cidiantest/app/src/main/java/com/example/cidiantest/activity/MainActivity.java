package com.example.cidiantest.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cidiantest.DatabaseHelper;
import com.example.cidiantest.Item;
import com.example.cidiantest.R;
import com.example.cidiantest.adapter.MyAdapter_Part;
import com.example.cidiantest.adapter.MyAdapter_Word;
import com.example.cidiantest.show.ShowChapter;
import com.example.cidiantest.show.ShowExample;
import com.example.cidiantest.show.ShowWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // 主界面上的2个Tab选项卡
    private LinearLayout sampleTab = null;
    private LinearLayout searchTab = null;
    private LinearLayout[] tabs;
    private LinearLayout det = null;
    // Tab选项卡对应的界面
    private View sampleTabView = null;
    private View searchTabView = null;

    // 选项卡下方的布局
    private LinearLayout content = null;
    private EditText word = null;
    private DatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //final ListView list_test = (ListView) findViewById(R.id.simpleListView);
        final ArrayList<Item> partandchapter = new ArrayList<>();

        // 初始化Tab控件
        sampleTab = (LinearLayout) findViewById(R.id.sampleTab);
        searchTab = (LinearLayout) findViewById(R.id.searchTab);
        det = (LinearLayout) findViewById(R.id.first_show);
        tabs = new LinearLayout[] { sampleTab, searchTab};

        // 初始化选项卡对应的布局界面
        LayoutInflater factory = LayoutInflater.from(this);
        sampleTabView = factory.inflate(R.layout.activity_main, null);
        searchTabView = factory.inflate(R.layout.search, null);

        // 程序启动时默认显示色卡样例界面
        content = (LinearLayout) findViewById(R.id.content);
        content.addView(sampleTabView);

        final ListView list_test = findViewById(R.id.simpleListView);

        sampleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setTabChecked(sampleTab);
                // 清除content 中的界面内容，加载
                // 当前被选中选项卡对应的界面
                content.removeAllViews();
                content.addView(sampleTabView);

            }
        });
        //搜索tab监听
        searchTab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setTabChecked(searchTab);
                content.removeAllViews();
                content.addView(searchTabView);
                //*******************************搜索功能*************************************//
                Button search = findViewById(R.id.search);    //搜索按钮
                final ListView result = findViewById(R.id.result);  //展示搜索结果
                final ArrayList<Item> result_array = new ArrayList<>();   //用于存放搜索结果
                word = findViewById(R.id.search_word);   //输入需要搜索的单词
                dbhelper = new DatabaseHelper(getApplicationContext());

                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获得输入的单词
                        String sw = word.getText().toString();
                        //搜索数据库
                        SQLiteDatabase db = dbhelper.getWritableDatabase();
                        //创建游标对象
                        Cursor cursor_word = db.query("table_word", new String[]{"Word, WordTranslation, Chapter"},
                                "Word"+" LIKE?",  new String[]{"%"+sw+"%"},
                                null, null, "Word", null);

                        if(cursor_word.getCount()==0){
                            Toast.makeText(getApplicationContext(),"无搜索结果！",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //先清空结果
                            result_array.clear();
                            //利用游标遍历所有数据对象
                            while (cursor_word.moveToNext()) {
                                //int part = cursor_word.getInt(cursor_word.getColumnIndex("Part"));
                                int chapter = cursor_word.getInt(cursor_word.getColumnIndex("Chapter"));
                                String word = cursor_word.getString(cursor_word.getColumnIndex("Word"));
                                String wordtranslation = cursor_word.getString(cursor_word.getColumnIndex("WordTranslation"));
                                result_array.add(new Item(chapter, word, wordtranslation));
                                //Log.d("测试：", "从数据库中读取了数据：chapter:" + chapter + ",  " + word + ",  " + wordtranslation);
                            }
                            MyAdapter_Word myAdapterWord = new MyAdapter_Word(getApplicationContext(), R.layout.list_view_word, result_array);
                            result.setAdapter(myAdapterWord);

                            //添加监听 实现word -> example
                            result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("chapter", result_array.get(position).getChapter());
                                    bundle.putString("word", result_array.get(position).getWord());
                                    bundle.putString("wordtranslation", result_array.get(position).getWordTranslation());
                                    //bundle.putInt("skill_detail", skillList.get(position).getSkillDetail());
                                    Intent intent = new Intent();
                                    intent.putExtras(bundle);
                                    intent.setClass(MainActivity.this, ShowExample.class);
                                    startActivity(intent);
                                }
                            });


                        }

                    }
                });
            }
        });

        //初始化 插入数据
        //打开数据库读取
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase dbs = dbHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor_parts = dbs.query("table_part", null, null, null, null, null, null, null);
        //如果数据表为空，则插入数据：
        if(cursor_parts.getCount()==0){
            try {
                readPartAndChapterCsv();   //插入章节数据
                readMyWord();              //插入单词数据
                Toast.makeText(this,"插入了pac,myword数据", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cursor_parts.close();
        dbs.close();   //初始化完毕，关闭资源

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor_part = db.query("table_part", null, null, null, null, null, null, null);


        //利用游标遍历所有数据对象
        while(cursor_part.moveToNext()){
            int part = cursor_part.getInt(cursor_part.getColumnIndex("Part"));
            String partname = cursor_part.getString(cursor_part.getColumnIndex("PartName"));
            partandchapter.add(new Item(part,partname));
            //Log.d("测试：", "从数据库中读取了数据："+part +","+partname);
        }
        //构建第一个界面：Part的选择
        final MyAdapter_Part myAdapterPart = new MyAdapter_Part(this, R.layout.list_view_items, partandchapter);
        list_test.setAdapter(myAdapterPart);

        //查询完毕，关闭游标和数据库
        db.close();
        cursor_part.close();
        //添加监听，实现点击part弹出chapter选择
        list_test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("part", partandchapter.get(position).getPart());
                bundle.putString("partname", partandchapter.get(position).getPartName());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, ShowChapter.class);
                startActivity(intent);
            }
        });


    }

    //tab被单击时属性
    public void setTabChecked(LinearLayout tab) {
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setBackgroundDrawable(null);
            TextView txt = (TextView) tabs[i].getChildAt(0);
            //txt.setTextColor(getResources().getColor(R.color.darkgreen));
        }
        tab.setBackgroundResource(R.drawable.tabselected);
        TextView txt = (TextView) tab.getChildAt(0);
        //txt.setTextColor(getResources().getColor(R.color.white));
    }
    //读取章节数据
    private void readPartAndChapterCsv() throws IOException {
        //int i = 0;// 用于标记打印的条数
        int flag = 1;  //第一章
        InputStreamReader csv = new InputStreamReader( getResources().getAssets().open("PartAndChapter.csv"), "GBK" );
        BufferedReader br = new BufferedReader(csv);
        br.readLine();
        String line = "";
        //使用数据库
        //创建一个DatabaseHelper对象
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        //取得一个读写的数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        while ((line = br.readLine()) != null) { // 这里读取csv文件
            //i++;
            String buffer[] = line.split(",");// 以逗号分隔，分割结果存到到数组中,根据数组来取得相应值
            //如果是第一章的内容（避免重复插入多个章节）
            if(Integer.toString(flag).equals(buffer[0].toString())){
                //把章 part partName ( 1  生活常识 )
                ContentValues values = new ContentValues();
                values.put("Part", buffer[0]);
                values.put("PartName", buffer[1]);
                db.insert("table_part", null, values);

                //节  chapter chapterName（ 1  居家生活）
                ContentValues values2 = new ContentValues();
                values2.put("Chapter", Integer.parseInt(buffer[2]));
                values2.put("ChapterName", buffer[3]);
                values2.put("Part", Integer.parseInt(buffer[0]));
                db.insert("table_chapter",null,values2);
                //Log.d("测试：", "插入了part："+buffer[0]+"和"+buffer[2]+"和"+buffer[3]);
                flag++;
            }
            else{
                //若part一样，同为第 flag 部分，则只插入chapter
                ContentValues values2 = new ContentValues();
                values2.put("Chapter", Integer.parseInt(buffer[2]));
                values2.put("ChapterName", buffer[3]);
                values2.put("Part", Integer.parseInt(buffer[0]));
                db.insert("table_chapter",null,values2);
               // Log.d("测试：", "$$$$$$$$$插入了part："+buffer[0]+"和"+buffer[2]+"和"+buffer[3]);
            }

//            System.out.println("第" + i + "行：" + line);// 输出每一行数据
//            System.out.println("第" + i + "行：" + buffer[0]);// 取第一列数据
//            System.out.println("第" + i + "行：" + buffer[1]);
//            System.out.println("第" + i + "行：" + buffer[2]);
//            System.out.println("第" + i + "行：" + buffer[3]);

        }
        db.close();
        br.close();

    }
    //读取单词数据
    private void readMyWord() throws IOException{
        //int i = 0;// 用于标记打印的条数
        //int flag = 1;  //第一章
        InputStreamReader csv = new InputStreamReader( getResources().getAssets().open("myword.csv"), "GBK" );
        BufferedReader br = new BufferedReader(csv);
        br.readLine();
        String line = "";
        //ArrayList<Item> all = new ArrayList<>();

        //使用数据库
        //创建一个DatabaseHelper对象
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        //取得一个读写的数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        while ((line = br.readLine()) != null) { // 这里读取csv文件
            //i++;
            String buffer[] = line.split(",");// 以逗号分隔，分割结果存到到数组中,根据数组来取得相应值

                //myword : Part, Chapter, Word, WordTranslation, Example, ExampleTranslation

                //table_word : Word  WordTranslation  Chapter
                //插入单词 word  wordtranslation chapter
                ContentValues values = new ContentValues();
                values.put("Chapter", buffer[1]);
                values.put("Word", buffer[2]);
                values.put("WordTranslation", buffer[3]);
                db.insert("table_word", null, values);

                //table_example : Example ExampleTranslation Word
                //插入例句翻译
                ContentValues values2 = new ContentValues();
                values2.put("Example", buffer[4]);
                values2.put("ExampleTranslation", buffer[5]);
                values2.put("Word", buffer[2]);
                db.insert("table_example",null,values2);
                //Log.d("测试：", "插入了part："+buffer[0]+" chapter:"
                //        +buffer[1]+" word:"+buffer[2]+" wordtrans:"+buffer[3]+" example:"
                //        +buffer[4]+" examtrans:"+buffer[5]);
               //all.add(new Item(buffer[0],buffer[1],buffer[2],buffer[3]));
        }
        db.close();
        br.close();
    }

    //导入自定义数据

    //导出自定义数据





}
