package com.example.ssq_dlt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button ssq = findViewById(R.id.ssq);   //生成双色球
        Button dlt = findViewById(R.id.dlt);   //生成大乐透
        //双色球监听
        ssq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_ssq();
            }
        });

        //大乐透监听
        dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_dlt();
            }
        });

    }

    //监听方法--生成双色球
    public void create_ssq(){
        TextView red_text = findViewById(R.id.red);  //红区
        TextView blue_text = findViewById(R.id.blue); //蓝区
        String[] red = {
                "01","02","03","04","05","06","07","08","09","10",
                "11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30",
                "31","32","33"
        };
        String[] blue = {
                "01","02","03","04","05","06","07","08","09","10",
                "11","12","13","14","15","16"
        };
        boolean[] flags = new boolean[red.length];
        String[] redResult = new String[6];
        // 选红球
        for(int i=0;i<redResult.length;i++) {
            int index;
            do {
                index = new Random().nextInt(red.length);
            }
            while(flags[index]);
            flags[index] = true;
            redResult[i] = red[index];
        }
        // 选蓝球
        String blueResult = blue[new Random().nextInt(blue.length)];
        // 显示
        red_text.setText(Arrays.toString(redResult));
        blue_text.setText(blueResult);
    }

    //监听方法--生成大乐透
    public void create_dlt(){
        TextView red_text = findViewById(R.id.red);  //红区
        TextView blue_text = findViewById(R.id.blue); //蓝区
        String[] red = {
                "01","02","03","04","05","06","07","08","09","10",
                "11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30",
                "31","32","33","34","35"
        };
        String[] blue = {
                "01","02","03","04","05","06","07","08","09","10",
                "11","12"
        };
        boolean[] flags = new boolean[red.length];
        boolean[] flags2 = new boolean[blue.length];
        String[] redResult = new String[5];
        String[] blueResult = new String[2];
        // 选红球
        for(int i=0;i<redResult.length;i++) {
            int index;
            do {
                index = new Random().nextInt(red.length);
            }
            while(flags[index]);
            flags[index] = true;
            redResult[i] = red[index];
        }
        // 选蓝球
        for(int i=0;i<blueResult.length;i++) {
            int index;
            do {
                index = new Random().nextInt(blue.length);
            }
            while(flags2[index]);
            flags2[index] = true;
            blueResult[i] = blue[index];
        }
        // 显示
        red_text.setText(Arrays.toString(redResult));
        blue_text.setText(Arrays.toString(blueResult));
    }


}
