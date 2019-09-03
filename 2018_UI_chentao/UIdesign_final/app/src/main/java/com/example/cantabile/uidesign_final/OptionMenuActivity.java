package com.example.cantabile.uidesign_final;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class OptionMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //不显示标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.option_activity);
        Button imgquality = (Button) findViewById(R.id.img_quality);
        Button textstyle = (Button) findViewById(R.id.text_style);
        Button quit = (Button) findViewById(R.id.quit);
        Typeface tps = Typeface.createFromAsset(this.getAssets(), "next.ttf");
        imgquality.setTypeface(tps);  //类型
        imgquality.setTextSize(36);  //大小
        imgquality.setTextColor(0xDD07CCC2);
        textstyle.setTypeface(tps);  //类型
        textstyle.setTextSize(36);  //大小
        textstyle.setTextColor(0xDD07CCC2);
        quit.setTypeface(tps);  //类型
        quit.setTextSize(36);  //大小
        quit.setTextColor(0xDD07CCC2);

    }
    public void onImgQuality(View v){
        Util.clikeAudioNormal(this);
        Log.d("TAG","onCampaign");
        imgChoice();
    }
    public void onTextStyle(View v){
        Util.clikeAudioNormal(this);
        Log.d("TAG","onMarathon");
        textChoice();
    }
    public void onQuit(View v){
        Util.clikeAudioNormal(this);
        Log.d("TAG","onQuit");
        this.finish();
    }

    //画质选择
    private void imgChoice() {
        final String items[] = {"精细画质", "普通画质", "流畅画质"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setTitle("画质选择");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(OptionMenuActivity.this, items[which],
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(OptionMenuActivity.this, "确定", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.create().show();
    }
    //字体选择
    private void textChoice() {
        final String items[] = {"默认", "轻柔", "赛博朋克"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setTitle("字体选择");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(OptionMenuActivity.this, items[which],
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(OptionMenuActivity.this, "确定", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.create().show();
    }
}
