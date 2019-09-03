package com.example.cantabile.uidesign_7;

import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private int[] date = new int[100];
    private int hasDate = 0;
    //记录ProgressBar的完成进度
    private int status = 0;
    private ProgressBar bar;
    private ClipDrawable clip;

    //创建一个负责更新进度的Handler
    private Handler mHandler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //表明消息是由该程序发送的
            if(msg.what==0x111){
                bar.setProgress(status);
                clip.setLevel(status*100);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasDate=0;
                status=0;
                restart();
            }
        });

        restart();

    }

    private  void restart(){
        bar = findViewById(R.id.progressBar);
        final ImageView imageview = (ImageView) findViewById(R.id.image_s);
        ClipDrawable drawable = (ClipDrawable) imageview.getBackground();
        clip=drawable;
        //启动线程来执行任务
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(status<100){
                    //获取耗时操作的完成百分比
                    status = doWork();
                    //发送消息
                    mHandler.sendEmptyMessage(0x111);
                }
            }
        }.start();
    }

    private int doWork(){
        //为数组元素赋值
        date[hasDate++] = (int)Math.random()*100;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return hasDate;
    }

}
