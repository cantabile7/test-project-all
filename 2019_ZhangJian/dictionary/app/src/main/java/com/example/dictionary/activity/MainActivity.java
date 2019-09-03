package com.example.dictionary.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionary.DatabaseHelper;
import com.example.dictionary.Item;
import com.example.dictionary.R;
import com.example.dictionary.adapter.MyAdapter_Part;
import com.example.dictionary.adapter.MyAdapter_Word;
import com.example.dictionary.show.ShowChapter;
import com.example.dictionary.show.ShowExample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 主界面上的3个Tab选项卡
    private LinearLayout sampleTab = null;
    private LinearLayout searchTab = null;
    private LinearLayout addTab = null;
    private LinearLayout[] tabs;
    // Tab选项卡对应的界面
    private View sampleTabView = null;
    private View searchTabView = null;
    private View addTabView = null;
    // 选项卡下方的布局
    private LinearLayout content = null;
    private EditText word = null;

    private DatabaseHelper dbhelper;
    private static final int FILE_SELECT_CODE = 0;
    private String filepath="";
    private TextView path = null;

    private ListView result =null;  //展示搜索结果
    private int item_id;
    private ArrayList<Item> res=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ArrayList<Item> partandchapter = new ArrayList<>();
        //region 控件的初始化
        // 初始化Tab控件
        sampleTab = (LinearLayout) findViewById(R.id.sampleTab);
        searchTab = (LinearLayout) findViewById(R.id.searchTab);
        addTab = (LinearLayout) findViewById(R.id.addTab);
        tabs = new LinearLayout[] { sampleTab, searchTab, addTab};

        // 初始化选项卡对应的布局界面
        LayoutInflater factory = LayoutInflater.from(this);
        sampleTabView = factory.inflate(R.layout.activity_main, null);
        searchTabView = factory.inflate(R.layout.search, null);
        addTabView = factory.inflate(R.layout.addcsv, null);

        // 程序启动时默认显示展示界面
        content = (LinearLayout) findViewById(R.id.content);
        content.addView(sampleTabView);
        final ListView list_test = findViewById(R.id.simpleListView);
        //endregion

        //region 展示tab监听
        //顶部展示界面选项卡监听
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
        //endregion

        //region 搜索tab监听
        //搜索tab监听
        searchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setTabChecked(searchTab);
                content.removeAllViews();
                content.addView(searchTabView);
                //*******************************搜索功能*************************************//
                Button search = findViewById(R.id.search);    //搜索按钮
                result = findViewById(R.id.result);  //展示搜索结果
                final ArrayList<Item> result_array = new ArrayList<>();   //用于存放搜索结果
                word = findViewById(R.id.search_word);   //输入需要搜索的单词
                dbhelper = new DatabaseHelper(getApplicationContext());

                //点击搜索按钮
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获得输入的单词
                        String sw = word.getText().toString();
                        //调用搜索方法
                        searchWord(result,sw,result_array);
                        res = result_array;
                        //添加监听 实现 word -> example 跳转
                        result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("chapter", result_array.get(position).getChapter());
                                bundle.putString("word", result_array.get(position).getWord());
                                bundle.putString("wordtranslation", result_array.get(position).getWordTranslation());
                                Intent intent = new Intent();
                                intent.putExtras(bundle);
                                intent.setClass(MainActivity.this, ShowExample.class);
                                startActivity(intent);
                            }
                        });

                        //region 长按删除
//                        result.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                            @Override
//                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//                                return true;
//                            }
//                        });
                        //endregion
                        registerForContextMenu(result);

                    }
                });

                //region 导出搜索结果
                //*********************************导出搜索结果******************************************//
                //导出搜索结果
                Button output = findViewById(R.id.output);
                output.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //调用导出方法 从搜索结果 result_array 中导出到文件保存到手机本地
                        try {
                            outputWord(result_array);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //endregion

            }
        });
        //endregion

        //region 导入tab监听
        addTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabChecked(addTab);
                // 清除content 中的界面内容，加载选中选项卡对应的界面
                content.removeAllViews();
                content.addView(addTabView);
                final Button findfile = findViewById(R.id.select);    //添加文件按钮
                Button input = findViewById(R.id.input);   //导入按钮
                final ArrayList<Item> result_array = new ArrayList<>();   //用于存放搜索结果

                //region 添加文件按钮监听事件（选择文件）
                findfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/csv");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(), "打开文件管理器失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //endregion

                //region 添加导入按钮监听事件
                input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //调用导入单词方法
                        inputWord();
                    }
                });
                //endregion

            }
        });
        //endregion

        //region 初始化函数
        init(partandchapter);
        //构建第一个界面：Part的选择
        final MyAdapter_Part myAdapterPart = new MyAdapter_Part(this, R.layout.list_view_items, partandchapter);
        list_test.setAdapter(myAdapterPart);
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
        //endregion

    }
    //创建菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST+1, 0,"添加单词");
        return super.onCreateOptionsMenu(menu);
    }
    //菜单项监听： 添加单个单词
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case Menu.FIRST+1 :
                //调用添加单词方法
                addWord();
                break;
        }
        return true;
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
        Log.d("TEST", item.getTitle().toString());
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String str = res.get(info.position).getWord();
//        if(item.getMenuInfo() instanceof AdapterView.AdapterContextMenuInfo) {
//            AdapterView.AdapterContextMenuInfo infos = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
//            Log.d("TEST", infos.position+"--");
//            Log.d("TEST", res.get(infos.position).getWord()+"--");
//
//        }
        switch(item.getItemId()){
            case 1:
                //删除
                deleteWord(str);
                //Toast.makeText(getApplicationContext(), "点击了删除", Toast.LENGTH_SHORT).show();
                break;

            case 2:
                //修改
                updateWord(str);
                //Toast.makeText(getApplicationContext(), "点击了修改", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    //初始化函数 导入章节、单词数据
    private ArrayList<Item> init(ArrayList<Item> partandchapter){
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

        //查询完毕，关闭游标和数据库
        db.close();
        cursor_part.close();

        return partandchapter;
    }
    //tab被单击时属性 改变颜色
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
    //search功能  搜索功能的实现
    private ListView searchWord(ListView result, String sw, ArrayList<Item> result_arrays){
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
            result_arrays.clear();
            //利用游标遍历所有数据对象
            while (cursor_word.moveToNext()) {
                //int part = cursor_word.getInt(cursor_word.getColumnIndex("Part"));
                int chapter = cursor_word.getInt(cursor_word.getColumnIndex("Chapter"));
                String word = cursor_word.getString(cursor_word.getColumnIndex("Word"));
                String wordtranslation = cursor_word.getString(cursor_word.getColumnIndex("WordTranslation"));
                result_arrays.add(new Item(chapter, word, wordtranslation));
                //Log.d("测试：", "从数据库中读取了数据：chapter:" + chapter + ",  " + word + ",  " + wordtranslation);
            }
            MyAdapter_Word myAdapterWord = new MyAdapter_Word(getApplicationContext(), R.layout.list_view_word, result_arrays);
            result.setAdapter(myAdapterWord);

        }
        return result;
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
                flag++;
            }
            else{
                //若part一样，同为第 flag 部分，则只插入chapter
                ContentValues values2 = new ContentValues();
                values2.put("Chapter", Integer.parseInt(buffer[2]));
                values2.put("ChapterName", buffer[3]);
                values2.put("Part", Integer.parseInt(buffer[0]));
                db.insert("table_chapter",null,values2);
            }
        }
        db.close();
        br.close();

    }
    //读取单词数据
    private void readMyWord() throws IOException{
        InputStreamReader csv = new InputStreamReader( getResources().getAssets().open("myword.csv"),
                "GBK" );
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
        }
        db.close();
        br.close();
    }
    //添加单个单词
    private void addWord(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View addView = factory.inflate(R.layout.addword, null);
        final EditText part = addView.findViewById(R.id.part);
        final EditText chapter = addView.findViewById(R.id.chapter);
        final EditText word = addView.findViewById(R.id.word);
        final EditText wordTrans = addView.findViewById(R.id.wordTrans);
        final EditText example = addView.findViewById(R.id.example);
        final EditText exampleTrans = addView.findViewById(R.id.exampleTrans);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);
        ad1.setTitle("增加单词:");
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
                    Toast.makeText(getApplicationContext(), "添加失败！请填写完整的单词信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //创建一个DatabaseHelper对象
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                //取得一个读写的数据库对象
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //插入到 单词 数据表
                ContentValues values = new ContentValues();
                values.put("Chapter", chapterstr);
                values.put("Word", wordstr);
                values.put("WordTranslation", wordTransstr);
                db.insert("table_word", null, values);
                //插入到 用例 数据表
                ContentValues values2 = new ContentValues();
                values2.put("Example", examplestr);
                values2.put("ExampleTranslation", exampleTransstr);
                values2.put("Word", wordstr);
                db.insert("table_example",null,values2);
                db.close();
                Toast.makeText(getApplicationContext(), "添加完成", Toast.LENGTH_SHORT).show();
            }
        });
        ad1.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();

    }
    //导出数据
    private void outputWord(ArrayList<Item> result) throws Exception {
        if(!this.isGrantExternalRW(MainActivity.this)){
            return;
        }else{
            //授权后操作
            //如果搜索结果为空则导出失败
            if(result.size()==0){
                Toast.makeText(getApplicationContext(),"搜索结果为空，导出文件失败。", Toast.LENGTH_SHORT).show();
                return;
            }
            //导出到手机本地
            //新建文件夹
            String folderName = "User";
            File sdCardDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), folderName);
            if (!sdCardDir.exists()) {
                if (!sdCardDir.mkdirs()) {
                    try {
                        sdCardDir.createNewFile();
                        Log.d("导出过程：", "新建文件夹成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("导出过程：", "新建文件夹失败");
                    }
                }
            }
            try {
                //新建文件
                File saveFile = new File(sdCardDir, "user.txt");
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                    Log.d("导出过程：", "新建文件成功");
                }
                final FileOutputStream outStream = new FileOutputStream(saveFile);

                try {
                    for(int i=0; i<result.size(); i++){
                        Item array = result.get(i);
                        String str = array.getWord() + "\n";
                        outStream.write(str.getBytes());
                    }
                    outStream.close();
                    Toast.makeText(getApplicationContext(),"导出成功。", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("导出过程：", "新建文件失败");
            }


        }

    }
    //导入数据
    private void inputWord(){
        if(!this.isGrantExternalRW(MainActivity.this)){
            return;
        }else{
            //授权后操作
            //如果搜索结果为空则导出失败
            if(filepath != null){
                try {
                    File csv = new File(filepath); // CSV文件路径
                    InputStreamReader inStream = new InputStreamReader(new FileInputStream(csv), "GBK");
                    BufferedReader br = new BufferedReader(inStream);
                    br.readLine();
                    String line = "";
                    //使用数据库
                    //创建一个DatabaseHelper对象
                    DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                    //取得一个读写的数据库对象
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    while ((line = br.readLine()) != null) { // 这里读取csv文件
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
                    }
                    db.close();
                    br.close();
                    Toast.makeText(getApplicationContext(), "导入成功。", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "导入失败，找不到文件。", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "读取文件失败，导入失败。", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "导入失败，请先选择文件。", Toast.LENGTH_SHORT).show();
            }
            }

    }

    //检查是否授权读写SD卡
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
    //响应选择文件操作
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            Log.e("TAG", "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == FILE_SELECT_CODE) {
            Uri uri = data.getData();
            String sPath1 = null;
            sPath1 = getPath(this, uri); // Paul Burke写的函数，根据Uri获得文件路径
            Log.i("TAG", "文件地址------->" + sPath1);
            filepath = sPath1;
            //显示文件路径
            path = findViewById(R.id.filepath);
            path.setText(filepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);
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

    //region 根据uri获得文件真实路径
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    //endregion




}
