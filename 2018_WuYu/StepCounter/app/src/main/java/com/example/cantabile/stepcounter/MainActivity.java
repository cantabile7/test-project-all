package com.example.cantabile.stepcounter;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private BindService bindService;
    private TextView textView;
    private boolean isBind;
    DBHelper dbHelper;
    SQLiteDatabase database;
    public static int TODAYSTEP=0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                textView.setText(msg.arg1 + "");
                //Toast.makeText(MainActivity.this,"步数已经更新！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.busu);
        //绑定并且开始服务
        Intent intent = new Intent(MainActivity.this, BindService.class);
        isBind =  bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

        //获得今日时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        String today=simpleDateFormat.format(new Date());
        int today_int=Integer.parseInt(today);
        //绘制图表
        if(dbHelper == null)
        {
            dbHelper = new DBHelper(this);
        }
        database=dbHelper.getWritableDatabase();
        int i=0;
        Cursor cursor=database.query(DBHelper.TABLE_NAME,null,null,null,null,null,null);
        int sum=cursor.getCount();
        DataPoint[] points = new DataPoint[sum];
        GraphView graph = (GraphView) findViewById(R.id.graph);
        if(cursor.moveToFirst()){
            for(; !cursor.isAfterLast(); cursor.moveToNext()) {
                int a= cursor.getInt(cursor.getColumnIndex(DBHelper.DATE));
                int b= cursor.getInt(cursor.getColumnIndex(DBHelper.RECORD));
               // points=new DataPoint[]{new DataPoint(a,b)};
                points[i]=new DataPoint(a,b);
                i++;

                Toast.makeText(MainActivity.this,""+a+","+b+"",Toast.LENGTH_SHORT).show();
                if(cursor.getInt(cursor.getColumnIndex(DBHelper.DATE))==today_int){
                    textView.setText(Integer.toString(b));
                    TODAYSTEP=b;
                }
            }
            cursor.close(); // 记得关闭游标对象
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
            graph.addSeries(series);
            //Toast.makeText(MainActivity.this,""+points+"",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(MainActivity.this,"数据库为空！",Toast.LENGTH_SHORT).show();
        }

    }

    //和绷定服务数据交换的桥梁，可以通过IBinder service获取服务的实例来调用服务的方法或者数据
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BindService.LcBinder lcBinder = (BindService.LcBinder) service;
            bindService = lcBinder.getService();
            bindService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    //当前接收到stepCount数据，就是最新的步数
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                    String date_str=simpleDateFormat.format(new Date());
                    int date_int=Integer.parseInt(date_str);
                    Message message = Message.obtain();
                    message.what = 1;
                    message.arg1 = stepCount;
                    message.arg2 = date_int;
                    handler.sendMessage(message);
                    Log.i("MainActivity—updateUi","当前步数"+stepCount);
                    //在此存储数据
                    if(dbHelper == null)
                    {
                        dbHelper = new DBHelper(MainActivity.this);
                    }
                    database = dbHelper.getWritableDatabase();
                    //Toast.makeText(MainActivity.this, "插入数据成功", Toast.LENGTH_SHORT).show();
                    String step_str=String.valueOf(stepCount);
                    ContentValues cV = new ContentValues();

                    cV.put(DBHelper.RECORD, stepCount);
                    //查询是否存在该日期记录 有则更新 无则插入
                    Cursor cursor=database.query(DBHelper.TABLE_NAME,null,null,null,null,null,null);
                    if(cursor!=null&&cursor.getCount()>0){
                        while (cursor.moveToNext()) {
                            int a= cursor.getInt(cursor.getColumnIndex(DBHelper.DATE));
                            int b= cursor.getInt(cursor.getColumnIndex(DBHelper.RECORD));
                            //Toast.makeText(MainActivity.this, "正在遍历", Toast.LENGTH_SHORT).show();
                            if(a==date_int){
                                //记录存在 更新
                                database.update(DBHelper.TABLE_NAME,cV,DBHelper.DATE+"=?",new String[]{date_str});
                                Toast.makeText(MainActivity.this,"今天是"+date_int+"号！",Toast.LENGTH_SHORT).show();
                                cursor.close(); // 记得关闭游标对象
                                return;
                            }
                        }
                        cV.put(DBHelper.DATE, date_int);
                        database.insert(DBHelper.TABLE_NAME, null, cV);
                        Toast.makeText(MainActivity.this,"本日无步数，已存储！",Toast.LENGTH_SHORT).show();
                    }
                    else if(cursor==null){
                        Toast.makeText(MainActivity.this,"数据库为空！",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public void onDestroy() {  //app被关闭之前，service先解除绑定
        super.onDestroy();
        if (isBind) {
            this.unbindService(serviceConnection);
        }
    }
}
