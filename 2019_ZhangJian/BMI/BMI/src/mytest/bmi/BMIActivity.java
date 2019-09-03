package mytest.bmi;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class BMIActivity extends Activity {
	// 成员变量定义，它们是程序界面上的控件/组件，需要在代码中用到
	private EditText editHeight;
	private EditText editWeight;
	private Button btnCalc;
	private TextView textResult;
	private RadioGroup radioGroup;
	private RadioButton man;
	private RadioButton women;
	private RelativeLayout father;
	String all_str = null;
	String sex = "man";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bmi);
		
		// 初始化控件，findViewById()是根据布局文件中设定的id 值找到组件对象
		editHeight = (EditText) findViewById(R.id.editHeight);
		editWeight = (EditText) findViewById(R.id.editWeight);
		btnCalc = (Button) findViewById(R.id.btnCalc);
		textResult = (TextView) findViewById(R.id.textResult);
		radioGroup = (RadioGroup) findViewById(R.id.sex);
		man = (RadioButton) findViewById(R.id.man);
		women = (RadioButton) findViewById(R.id.women);
        father = (RelativeLayout) findViewById(R.id.father);

		DecimalFormat df = new DecimalFormat("0.00");

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId){
					case R.id.man:
						sex = "man";   //当选择男性时，sex标记为1
						Toast.makeText(BMIActivity.this,"选择：男",Toast.LENGTH_SHORT).show();
						break;
					case R.id.women:
						sex = "women";  //当选择为女性时，sex标记为0
						Toast.makeText(BMIActivity.this,"选择：女",Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});

		// 响应按钮单击事件
		btnCalc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					double h = Double.parseDouble(editHeight.getText()
							.toString()) / 100;
					double w = Double.parseDouble(editWeight.getText()
							.toString());

					double bmi = w / (h * h);

					//all_str用于在activit中传递数据
					all_str = String.valueOf(df.format(bmi));
					//根据性别判断是否超重
					double height = h*100;
					double weight = w;
					double standard_weigh;
					//根据选中的性别计算标准体重
					String result=null;
					if(sex=="man"){
						//性别为男
						standard_weigh = height - 105;
						if(weight<(standard_weigh-3) ){
							result="您的体重属于偏瘦,"+"您的标准体重应是："+standard_weigh+"kg";
						}
						else if(weight>(standard_weigh+3)){
							result="您的体重属于偏胖。"+"您的标准体重应是："+standard_weigh+"kg";
						}
						else{
							result="您的体重属于正常";
						}
					}
					else if(sex=="women"){
						//性别为女
						standard_weigh = height - 100;
						if(weight<(standard_weigh-2) ){
							result="您的体重属于偏瘦,"+"您的标准体重应是："+standard_weigh+"kg";
						}
						else if(weight>(standard_weigh+2)){
							result="您的体重属于偏胖。"+"您的标准体重应是："+standard_weigh+"kg";
						}
						else{
							result="您的体重属于正常";
						}
					}


					if (bmi < 18.5) {
						textResult.setText("你的BMI指数是："+df.format(bmi)+'\n'+getResources().getString(R.string.str_thin)
						        +'\n'+result);
					} else if (bmi > 24.9) {
						textResult.setText("你的BMI指数是："+df.format(bmi)+'\n'+getResources().getString(R.string.str_fat)
								+'\n'+result);
					} else {
						textResult.setText("你的BMI指数是："+df.format(bmi)+'\n'+getResources().getString(R.string.str_normal)
								+'\n'+result);
					}
				} catch (Exception e) {
					Toast.makeText(BMIActivity.this, R.string.str_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		//输入框焦点监听事件
		father.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//设置编辑框属性
				father.setFocusable(true);
				father.setFocusableInTouchMode(true);
				father.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editHeight.getWindowToken(),0);
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bmi, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 当单击属性菜单中的某个菜单项时，系统会自动执行onOptionsItemSelected()
		switch (item.getItemId()) {
		case R.id.menu_info:
			Intent intent = new Intent();
			intent.setClass(BMIActivity.this, InfoActivity.class);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			//此处传递bmi指数
			Bundle bundle =new Bundle();
			bundle.putString("bmi",all_str);
			intent.putExtra("bun",bundle);
			startActivity(intent);
			break;
		case R.id.menu_quit:
			finish();
			break;
		}
		// 返回true 代表已处理了单击事件
		return true;
	}

}
