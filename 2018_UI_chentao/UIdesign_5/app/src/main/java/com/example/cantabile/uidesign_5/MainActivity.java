package com.example.cantabile.uidesign_5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
    }
    private void initview() {
        Button ptbtn = findViewById(R.id.pt_btn);
        Button lbbtn = findViewById(R.id.lb_btn);
        Button dxbtn = findViewById(R.id.dx_btn);
        Button ddxbtn = findViewById(R.id.ddx_btn);
        Button bjbtn = findViewById(R.id.bj_btn);
        ptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNormal();// 普通
            }
        });
        lbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogList(); // 列表
            }
        });
        dxbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChoice(); // 单选
            }
        });
        ddxbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMoreChoice();// 多选
            }
        });
        bjbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditText();//可编辑
            }
        });
    }

    /*** 普通 ***/
    private void dialogNormal() {

        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        Toast.makeText(MainActivity.this, "确认",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        Toast.makeText(MainActivity.this, "取消",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Dialog.BUTTON_NEUTRAL:
                        Toast.makeText(MainActivity.this, "忽略",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("普通对话框");
        builder.setMessage("是否确认退出?");
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.setNeutralButton("忽略", dialogOnclicListener);
        builder.create().show();
    }

    /*** 列表 ***/
    private void dialogList() {
        final String items[] = {"计科1班", "计科2班", "计科3班", "计科4班","计科5班","计科6班"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setTitle("列表");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, items[which],
                        Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.create().show();
    }

    /*** 单选 ***/
    private void dialogChoice() {
        final String items[] = {"男", "女", "其他"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setTitle("单选");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, items[which],
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.create().show();
    }

    /*** 多选 ***/
    private void dialogMoreChoice() {
        final String items[] = {"学习", "运动", "摄影", "旅行"};
        final boolean selected[] = {true, false, true, false};
        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setTitle("多选");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMultiChoiceItems(items, selected,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {

                        Toast.makeText(MainActivity.this,
                                items[which] + isChecked, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.create().show();
    }

    /*** 可输入的对框框 ***/
    private void dialogEditText() {
        final EditText editText = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setTitle("可编辑");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, editText.getText().toString() + "", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


}
