package com.example.easycounter;

import android.net.sip.SipSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private EditText oper1_et;
    private EditText oper2_et;
    private TextView op_tv;
    private TextView result_tv;
    private Button add;
    private Button sub;
    private Button mul;
    private Button div;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        oper1_et = (EditText) findViewById(R.id.op1);
        oper2_et = (EditText) findViewById(R.id.op2);
        op_tv = (TextView) findViewById(R.id.op);
        result_tv = (TextView) findViewById(R.id.result);
        add = (Button) findViewById(R.id.add);
        sub = (Button) findViewById(R.id.sub);
        mul = (Button) findViewById(R.id.mul);
        div = (Button) findViewById(R.id.div);

        //设置输入类型,只允许输入数字和小数点
        oper1_et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        oper2_et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        //设置监听
        add.setOnClickListener(new MyListener());
        sub.setOnClickListener(new MyListener());
        mul.setOnClickListener(new MyListener());
        div.setOnClickListener(new MyListener());

    }

//    public void onClick(View v) {
//        Double op1;
//        Double op2;
//        Double result = 0.0;
//        switch (v.getId()){
//            case R.id.add:
//                op1 = Double.parseDouble(oper1_et.getText().toString());
//                op2 = Double.parseDouble(oper1_et.getText().toString());
//                result = op1 + op2;
//                result_tv.setText(result.toString());
//                break;
//            case R.id.sub:
//                op1 = Double.parseDouble(oper1_et.getText().toString());
//                op2 = Double.parseDouble(oper1_et.getText().toString());
//                result = op1 - op2;
//                result_tv.setText(result.toString());
//                break;
//            case R.id.mul:
//                op1 = Double.parseDouble(oper1_et.getText().toString());
//                op2 = Double.parseDouble(oper1_et.getText().toString());
//                result = op1 * op2;
//                result_tv.setText(result.toString());
//                break;
//            case R.id.div:
//                op1 = Double.parseDouble(oper1_et.getText().toString());
//                op2 = Double.parseDouble(oper1_et.getText().toString());
//                result = op1 / op2;
//                result_tv.setText(result.toString());
//                break;
//        }

    public class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Double op1;
            Double op2;
            Double result = 0.0;
            switch (v.getId()) {
                case R.id.add:
                    op1 = Double.parseDouble(oper1_et.getText().toString());
                    op2 = Double.parseDouble(oper2_et.getText().toString());
                    result = op1 + op2;
                    op_tv.setText("+");
                    result_tv.setText(result.toString());
                    break;
                case R.id.sub:
                    op1 = Double.parseDouble(oper1_et.getText().toString());
                    op2 = Double.parseDouble(oper2_et.getText().toString());
                    result = op1 - op2;
                    op_tv.setText("-");
                    result_tv.setText(result.toString());
                    break;
                case R.id.mul:
                    op1 = Double.parseDouble(oper1_et.getText().toString());
                    op2 = Double.parseDouble(oper2_et.getText().toString());
                    result = op1 * op2;
                    op_tv.setText("*");
                    result_tv.setText(result.toString());
                    break;
                case R.id.div:
                    op1 = Double.parseDouble(oper1_et.getText().toString());
                    op2 = Double.parseDouble(oper2_et.getText().toString());
                    result = op1 / op2;
                    op_tv.setText("/");
                    result_tv.setText(result.toString());
                    break;
            }

        }
    }
}
