package mytest.pt;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Control extends Activity {
    private Button start;
    private Button openmusic;
    private Button closemusic;
    private Button exit;
    public static Boolean isBgm = true;
    public static MediaPlayer player;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.btn_start);
        openmusic = (Button) findViewById(R.id.btn_open_music);
        closemusic = (Button) findViewById(R.id.btn_close_music);
        exit = (Button) findViewById(R.id.btn_exit);
        //添加监听器
        start.setOnClickListener(new btnListener());
        openmusic.setOnClickListener(new btnListener());
        closemusic.setOnClickListener(new btnListener());
        exit.setOnClickListener(new btnListener());
        // 从raw文件夹中获取一个音乐资源文件
        player = MediaPlayer.create(this, R.raw.bg);


    }

    private class btnListener implements View.OnClickListener{
        public void onClick(View v){
            switch (v.getId()){
                case R.id.btn_close_music:
                    //暂停音乐
                    isBgm = false;
                    if(player.isPlaying()){
                        player.pause();
                    }
                    break;
                case R.id.btn_open_music:
                    //开始音乐
                    isBgm = true;
                    player.start();
                    player.setLooping(true);
                    break;
                case R.id.btn_start:
                    Intent intent = new Intent(Control.this, GameActivity.class);
                    startActivityForResult(intent, 100);
                    break;
                case R.id.btn_exit:
                    finish();
                    break;

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 设置无限循环，然后启动播放
//        Control.player.setLooping(true);
//        Control.player.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停播放
        if(Control.player.isPlaying()){
            Control.player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 停止播放，释放资源
        if(Control.player.isPlaying()){
            Control.player.stop();
        }
        Control.player.release();


    }
}
