package com.example.cantabile.uidesign_final;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class BattleMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //不显示标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.battle_activity);
        Button campaign = (Button) findViewById(R.id.campaign);
        Button marathon = (Button) findViewById(R.id.marathon);
        Button quit = (Button) findViewById(R.id.quit);
        Typeface tps = Typeface.createFromAsset(this.getAssets(), "next.ttf");
        campaign.setTypeface(tps);  //类型
        campaign.setTextSize(36);  //大小
        campaign.setTextColor(0xDD07CCC2);
        marathon.setTypeface(tps);  //类型
        marathon.setTextSize(36);  //大小
        marathon.setTextColor(0xDD07CCC2);
        quit.setTypeface(tps);  //类型
        quit.setTextSize(36);  //大小
        quit.setTextColor(0xDD07CCC2);

    }
    public void onCampaign(View v){
        Util.clikeAudioNormal(this);
        Log.d("TAG","onCampaign");
    }
    public void onMarathon(View v){
        Util.clikeAudioNormal(this);
        Log.d("TAG","onMarathon");
    }
    public void onQuit(View v){
        Util.clikeAudioNormal(this);
        Log.d("TAG","onQuit");
        this.finish();
    }
}
