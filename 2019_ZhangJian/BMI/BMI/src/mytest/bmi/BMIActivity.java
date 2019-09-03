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
	// ��Ա�������壬�����ǳ�������ϵĿؼ�/�������Ҫ�ڴ������õ�
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
		
		// ��ʼ���ؼ���findViewById()�Ǹ��ݲ����ļ����趨��id ֵ�ҵ��������
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
						sex = "man";   //��ѡ������ʱ��sex���Ϊ1
						Toast.makeText(BMIActivity.this,"ѡ����",Toast.LENGTH_SHORT).show();
						break;
					case R.id.women:
						sex = "women";  //��ѡ��ΪŮ��ʱ��sex���Ϊ0
						Toast.makeText(BMIActivity.this,"ѡ��Ů",Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});

		// ��Ӧ��ť�����¼�
		btnCalc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					double h = Double.parseDouble(editHeight.getText()
							.toString()) / 100;
					double w = Double.parseDouble(editWeight.getText()
							.toString());

					double bmi = w / (h * h);

					//all_str������activit�д�������
					all_str = String.valueOf(df.format(bmi));
					//�����Ա��ж��Ƿ���
					double height = h*100;
					double weight = w;
					double standard_weigh;
					//����ѡ�е��Ա�����׼����
					String result=null;
					if(sex=="man"){
						//�Ա�Ϊ��
						standard_weigh = height - 105;
						if(weight<(standard_weigh-3) ){
							result="������������ƫ��,"+"���ı�׼����Ӧ�ǣ�"+standard_weigh+"kg";
						}
						else if(weight>(standard_weigh+3)){
							result="������������ƫ�֡�"+"���ı�׼����Ӧ�ǣ�"+standard_weigh+"kg";
						}
						else{
							result="����������������";
						}
					}
					else if(sex=="women"){
						//�Ա�ΪŮ
						standard_weigh = height - 100;
						if(weight<(standard_weigh-2) ){
							result="������������ƫ��,"+"���ı�׼����Ӧ�ǣ�"+standard_weigh+"kg";
						}
						else if(weight>(standard_weigh+2)){
							result="������������ƫ�֡�"+"���ı�׼����Ӧ�ǣ�"+standard_weigh+"kg";
						}
						else{
							result="����������������";
						}
					}


					if (bmi < 18.5) {
						textResult.setText("���BMIָ���ǣ�"+df.format(bmi)+'\n'+getResources().getString(R.string.str_thin)
						        +'\n'+result);
					} else if (bmi > 24.9) {
						textResult.setText("���BMIָ���ǣ�"+df.format(bmi)+'\n'+getResources().getString(R.string.str_fat)
								+'\n'+result);
					} else {
						textResult.setText("���BMIָ���ǣ�"+df.format(bmi)+'\n'+getResources().getString(R.string.str_normal)
								+'\n'+result);
					}
				} catch (Exception e) {
					Toast.makeText(BMIActivity.this, R.string.str_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		//����򽹵�����¼�
		father.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//���ñ༭������
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
		// ���������Բ˵��е�ĳ���˵���ʱ��ϵͳ���Զ�ִ��onOptionsItemSelected()
		switch (item.getItemId()) {
		case R.id.menu_info:
			Intent intent = new Intent();
			intent.setClass(BMIActivity.this, InfoActivity.class);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			//�˴�����bmiָ��
			Bundle bundle =new Bundle();
			bundle.putString("bmi",all_str);
			intent.putExtra("bun",bundle);
			startActivity(intent);
			break;
		case R.id.menu_quit:
			finish();
			break;
		}
		// ����true �����Ѵ����˵����¼�
		return true;
	}

}
