package com.example.vocabulary.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vocabulary.DatabaseHelper;
import com.example.vocabulary.Item;
import com.example.vocabulary.R;
import com.example.vocabulary.adapter.MyAdapter_Word;
import com.example.vocabulary.show.ShowExample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    //
    private ArrayList<Item> result_array;
    private ListView result_lv =null;  //展示搜索结果
    private DatabaseHelper dbhelper;
    private MyAdapter_Word adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        dbhelper = new DatabaseHelper(this);
        searchView = (SearchView) findViewById(R.id.search);
        result_lv = findViewById(R.id.result);
        result_array = new ArrayList<>();
        String sw = "";
       // searchWord(result_lv,sw,result_array);

        //region 获取数据
        //打开数据库读取
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //创建游标对象
        Cursor cursor_word = db.query("table_word", new String[]{"Word, WordTranslation, Chapter"},
                "Word"+" LIKE?",  new String[]{"%"+sw+"%"},
                null, null, "Word", null);

        if(cursor_word.getCount()==0){
            Toast.makeText(getApplicationContext(),"无搜索结果！",Toast.LENGTH_SHORT).show();
        }
        else{
            //利用游标遍历所有数据对象
            while (cursor_word.moveToNext()) {
                //int part = cursor_word.getInt(cursor_word.getColumnIndex("Part"));
                int chapter = cursor_word.getInt(cursor_word.getColumnIndex("Chapter"));
                String word = cursor_word.getString(cursor_word.getColumnIndex("Word"));
                String wordtranslation = cursor_word.getString(cursor_word.getColumnIndex("WordTranslation"));
                result_array.add(new Item(chapter, word, wordtranslation));
            }
            adapter = new MyAdapter_Word(getApplicationContext(), R.layout.list_view_word, result_array);
            result_lv.setAdapter(adapter);
        }
        //endregion

        //搜索框不自动缩小为一个搜索图标，而是match_parent
        searchView.setIconifiedByDefault(false);
        //显示搜索按钮
        searchView.setSubmitButtonEnabled(false);
        //默认提示文本
        searchView.setQueryHint("查找");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //单击搜索按钮的监听
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            //输入字符的监听
            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    result_array.clear();
                    adapter.notifyDataSetChanged();
                }
                else {
                    result_array.clear();
                    searchWord(result_lv,newText,result_array);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        result_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("chapter", result_array.get(position).getChapter());
                bundle.putString("word", result_array.get(position).getWord());
                bundle.putString("wordtranslation", result_array.get(position).getWordTranslation());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(SearchActivity.this, ShowExample.class);
                startActivity(intent);
            }
        });



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
            if(result_array.size()!=0){
                result_array.clear();
                adapter.notifyDataSetChanged();
            }

            //利用游标遍历所有数据对象
            while (cursor_word.moveToNext()) {
                //int part = cursor_word.getInt(cursor_word.getColumnIndex("Part"));
                int chapter = cursor_word.getInt(cursor_word.getColumnIndex("Chapter"));
                String word = cursor_word.getString(cursor_word.getColumnIndex("Word"));
                String wordtranslation = cursor_word.getString(cursor_word.getColumnIndex("WordTranslation"));
                result_arrays.add(new Item(chapter, word, wordtranslation));
                //Log.d("测试：", "从数据库中读取了数据：chapter:" + chapter + ",  " + word + ",  " + wordtranslation);
            }
            MyAdapter_Word myAdapterWord = new MyAdapter_Word(getApplicationContext(), R.layout.list_view_word, result_array);
            result.setAdapter(myAdapterWord);
            adapter.notifyDataSetChanged();
        }
        return result;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_1:
                try {
                    outputWord(result_array);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    //导出数据
    private void outputWord(ArrayList<Item> result) throws Exception {
        if(!this.isGrantExternalRW(SearchActivity.this)){
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
}
