package com.example.cantabile.blowcandle;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements RecordThread.ChangeState {

    public static boolean isRunning = true;

    private DevicePolicyManager policyManager;

    private static RelativeLayout bg;

    private static final int MY_PERMISSIONS_REQUEST_RECORDE = 1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bg = (RelativeLayout)findViewById(R.id.bg);

        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, MyReceiver.class);

        if (!policyManager.isAdminActive(componentName)){
            //激活设备管理器
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "锁屏");
            startActivity(intent);
        }


        //权限检查
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORDE);
        }
        else
        {
            new RecordThread(this).start();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_RECORDE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                new RecordThread(this).start();
            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void change() {
        // TODO Auto-generated method stub
        bg.setBackgroundResource(R.drawable.candle_off);
        //延时锁屏
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1500);//休眠1.5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                policyManager.lockNow();
            }
        }.start();

    }


}
