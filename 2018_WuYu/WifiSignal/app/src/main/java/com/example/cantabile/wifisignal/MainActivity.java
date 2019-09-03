package com.example.cantabile.wifisignal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageView signimg;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                checkwifisign();
                sendEmptyMessageDelayed(0, 5000);  //延时5s
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signimg = (ImageView) findViewById(R.id.sign_img);

        handler.sendEmptyMessageDelayed(0,5000);
    }

    //检查wifi是否连接
    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }

    //检测wifi信号强度
    public void checkwifisign(){
        if (isWifiConnect()) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int level = wifiInfo.getRssi();//获取wifi信号强度
            if (level > -30 && level < 0) {//强
                signimg.setImageResource(R.drawable.wifi_good);
                Toast.makeText(this,"信号良好",Toast.LENGTH_SHORT).show();
            } else if (level > -60 && level < -30) {//一般
                signimg.setImageResource(R.drawable.wifi_normal);
                Toast.makeText(this,"信号一般",Toast.LENGTH_SHORT).show();
            } else if (level > -100 && level < -60) {//较弱
                signimg.setImageResource(R.drawable.wifi_weak);
                Toast.makeText(this,"信号较弱",Toast.LENGTH_SHORT).show();
            }
        } else {
            //无连接
            signimg.setImageResource(R.drawable.wifi_non);
            Toast.makeText(this,"no sign",Toast.LENGTH_SHORT).show();
        }

    }
}
