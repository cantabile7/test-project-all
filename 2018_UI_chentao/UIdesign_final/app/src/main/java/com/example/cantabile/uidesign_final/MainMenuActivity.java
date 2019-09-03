package com.example.cantabile.uidesign_final;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainMenuActivity extends Activity {
    private AudioService audioService;
    Intent intent = new Intent();
    //使用ServiceConnection来监听Service状态的变化
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //这里我们实例化audioService,通过binder来实现
            audioService = ((AudioService.AudioBinder)binder).getService();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //不显示标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                              WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        Button start = (Button) findViewById(R.id.start);
        Button battle = (Button) findViewById(R.id.battle);
        Button stars = (Button) findViewById(R.id.stars);
        Button options = (Button) findViewById(R.id.options);
        Button quit = (Button) findViewById(R.id.quit);
        Typeface tp = Typeface.createFromAsset(this.getAssets(), "main.ttf");
        //设置start字体
        start.setTypeface(tp);  //类型
        start.setTextSize(26);  //大小
        //设置battle字体
        battle.setTypeface(tp);  //类型
        battle.setTextSize(26);  //大小
        //设置stars字体
        stars.setTypeface(tp);  //类型
        stars.setTextSize(26);  //大小
        //设置options字体
        options.setTypeface(tp);  //类型
        options.setTextSize(26);  //大小
        //设置quit字体
        quit.setTypeface(tp);  //类型
        quit.setTextSize(26);  //大小
        //设置背景音乐
        intent.setClass(this,AudioService.class);
        startService(intent);
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
        //finish();
    }
    public void onStart(View v){
        Util.clikeAudioNormal(this);
        Log.d("MyTAG","onStart");
    }
    public void onBattle(View v){
        MediaPlayer mp=MediaPlayer.create(this,R.raw.audio_click);
        mp.start();
        //页面跳转
        Intent intent=new Intent(MainMenuActivity.this,BattleMenuActivity.class);
        startActivity(intent);
        Util.clikeAudioNormal(this);
        Log.d("MyTAG","onBattle");
    }
    public void onStars(View v){
        Intent intent=new Intent(MainMenuActivity.this,StarsActivity.class);
        startActivity(intent);
        Log.d("MyTAG","onStars");
        Util.clikeAudioNormal(this);
    }
    public void onOptions(View v){
        Intent intent=new Intent(MainMenuActivity.this,OptionMenuActivity.class);
        startActivity(intent);
        Log.d("MyTAG","onOptions");
        Util.clikeAudioNormal(this);
    }
    public void onQuit(View v){
        Log.d("MyTAG","onQuit");
        MediaPlayer mp=MediaPlayer.create(this,R.raw.audio_quit);
        mp.start();
        unbindService(conn);
        stopService(intent);
        this.finish();
    }
}
