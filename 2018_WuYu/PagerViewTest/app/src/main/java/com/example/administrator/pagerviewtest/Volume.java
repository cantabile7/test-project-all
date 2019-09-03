package com.example.administrator.pagerviewtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

public class Volume extends AppCompatActivity {
    private SeekBar seekBar;
    private ContentObserver mVoiceObserver;
    private ImageButton back;
    private RadioButton open;
    private RadioButton close;
    private RadioGroup vibrate_switch;
    public static boolean isVibrate=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);

        open = (RadioButton) findViewById(R.id.open);
        close = (RadioButton) findViewById(R.id.close);
        vibrate_switch = (RadioGroup) findViewById(R.id.vibrate_switch);

        //默认选择
        if(isVibrate){
            open.setChecked(true);
        }
        else{
            close.setChecked(true);
        }
        //改变选中时：
        vibrate_switch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.open:
                        isVibrate=true;
                        break;
                    case R.id.close:
                        isVibrate=false;
                        break;
                }
            }
        });
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar= (SeekBar) findViewById(R.id.seek_bar);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(maxVolume);//设置最大音量
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(currentVolume);
        myRegisterReceiver();//注册同步更新的广播

        back = (ImageButton)findViewById(R.id.back);

        Log.i("lyj_ring", "mVoiceSeekBar max voluem = "+audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar arg0) {
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                //AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                Log.v("lyj_ring", "mVoiceSeekBar max progress = "+arg1);
                //系统音量和媒体音量同时更新
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, arg1, 0);
                audioManager.setStreamVolume(3, arg1, 0);
            }
        });
        mVoiceObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
            }
        };

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void myRegisterReceiver(){
        MyVolumeReceiver  mVolumeReceiver = new MyVolumeReceiver() ;
        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.media.VOLUME_CHANGED_ACTION") ;
        registerReceiver(mVolumeReceiver, filter) ;
    }

    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果音量发生变化则更改seekbar的位置
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;// 当前的媒体音量
                seekBar.setProgress(currVolume) ;
            }
        }
    }
}
