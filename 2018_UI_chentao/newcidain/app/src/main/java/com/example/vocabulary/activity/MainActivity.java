package com.example.vocabulary.activity;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vocabulary.DatabaseHelper;
import com.example.vocabulary.Item;
import com.example.vocabulary.R;
import com.example.vocabulary.adapter.MyAdapter_Part;
import com.example.vocabulary.show.ShowChapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.view.Menu;
import android.view.MenuItem;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

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

    //
    private ArrayList<Item> result_array=null;
    private ListView result =null;  //展示搜索结果
    private static final int FILE_SELECT_CODE = 0;
    private String filepath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ArrayList<Item> partandchapter = new ArrayList<>();
        final ListView list_test = findViewById(R.id.simpleListView);


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
                Toast.makeText(this,"已插入数据", Toast.LENGTH_SHORT).show();
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

        }
        db.close();
        br.close();
    }

    //导入自定义数据

    //导出自定义数据

    //添加单个单词
    private void addWord(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View addView = factory.inflate(R.layout.add_word, null);
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



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_1:
                //导入
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return true;
                }else{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), "打开文件管理器失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.action_2:
                //搜索
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_3:
                //添加
                addWord();
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
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
            inputWord();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
