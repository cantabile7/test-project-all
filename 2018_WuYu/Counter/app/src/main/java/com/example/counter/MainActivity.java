package com.example.counter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    int opsum=0; //记录使用操作符的次数
    int nosum=0; //记录按数字键次数
    double op=0;  //操作数1
    int operations=0;  //运算符
    double result=0;  //操作数2（结果）
    private TextView res;
    boolean is_op = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = findViewById(R.id.result);
    }

    boolean flag=false;

    public void onClick(View v) {
        String s1= (String) res.getText();
        switch(v.getId()){
            case R.id.btn0:
                is_op=false;
                if(nosum==0){
                    res.setText("0");
                    Toast.makeText(this, "请输入正确数字!", Toast.LENGTH_SHORT).show();
                    break;
                }
                s1+="0";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn1:
                is_op=false;
                s1+="1";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn2:
                is_op=false;
                s1+="2";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn3:
                is_op=false;
                s1+="3";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn4:
                is_op=false;
                s1+="4";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn5:
                is_op=false;
                s1+="5";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn6:
                is_op=false;
                s1+="6";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn7:
                is_op=false;
                s1+="7";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn8:
                is_op=false;
                s1+="8";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.btn9:
                is_op=false;
                s1+="9";
                res.setText(s1);
                flag=true;
                nosum++;
                break;
            case R.id.add:    //加法
                //检验是否连续输入操作符
                if(is_op == true){
                    Toast.makeText(this, "不能连续输入操作符!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else is_op = true;
                operations=1;
                if(flag==false){
                    Toast.makeText(this, "操作数不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(opsum==0){
                    result=Double.parseDouble(res.getText().toString());
                    res.setText("");
                    opsum++;
                    break;
                }
                else if(opsum>0){
                    op=Double.parseDouble(res.getText().toString());
                    result+=op;
                    res.setText("");
                    break;
                }
                break;
            case R.id.sub:   //减法
                //检验是否连续输入操作符
                if(is_op == true){
                    Toast.makeText(this, "不能连续输入操作符!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else is_op = true;
                operations=2;
                if(flag==false){
                    Toast.makeText(this, "操作数不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else if(opsum==0){
                    result=Double.parseDouble(res.getText().toString());
                    res.setText("");
                    opsum++;
                    break;
                }
                else if(opsum>0){
                    op=Double.parseDouble(res.getText().toString());
                    result=result-op;
                    res.setText("");
                    opsum++;
                    break;
                }
                break;
            case R.id.mul:   //乘法
                //检验是否连续输入操作符
                if(is_op == true){
                    Toast.makeText(this, "不能连续输入操作符!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else is_op = true;
                operations=3;
                if(flag==false){
                    Toast.makeText(this, "操作数不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else if(opsum==0){
                    result=Double.parseDouble(s1);
                    res.setText("");
                    opsum++;
                    break;
                }
                else if(opsum>0){
                    op=Double.parseDouble(res.getText().toString());
                    result=result*op;
                    res.setText("");
                    opsum++;
                    break;
                }
                break;
            case R.id.div:  //除法
                //检验是否连续输入操作符
                if(is_op == true){
                    Toast.makeText(this, "不能连续输入操作符!", Toast.LENGTH_SHORT).show();
                    res.setText("");
                    result=0;
                    opsum=0;
                    nosum=0;
                    break;
                }
                else is_op = true;
                operations=4;
                if(flag==false){
                    Toast.makeText(this, "操作数不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else if(opsum==0){
                    result=Double.parseDouble(s1);
                    res.setText("");
                    opsum++;
                    break;
                }
                else if(opsum>0){
                    op=Double.parseDouble(res.getText().toString());;
                    result=result/op;
                    res.setText("");
                    opsum++;
                    break;
                }
                break;
            case R.id.ac:   //清空
                res.setText("");
                result=0;
                opsum=0;
                nosum=0;
                is_op=false;
                break;
            case R.id.action:
                if(opsum==0){
                    Toast.makeText(this, "操作数不能为空!", Toast.LENGTH_SHORT).show();
                    is_op=false;
                }
                else if(opsum>0){
                    op=Integer.valueOf(res.getText().toString());
                    result=operation(result,op,operations);
                    String r = String.valueOf(result);
                    res.setText(r);
                    opsum=0;
                    is_op=false;
                    break;
                }


            default:
                break;
        }
    }


   public double operation(double a,double b,int ops){
        double this_result=0;
        switch(ops){
            case 1:
                this_result=a+b;   //加法
                break;
            case 2:
                this_result=a-b;  //减法
                break;
            case 3:
                this_result=a*b; //乘法
                break;
            case 4:
                if(b==0){
                    Toast.makeText(this, "除数不能为0!", Toast.LENGTH_SHORT).show();
                    res.setText("");
                    result=0;
                    opsum=0;
                    nosum=0;
                    break;
                }
                this_result=a/b; //除法
                break;
            default:
                break;
         }

         return this_result;
   }





}
