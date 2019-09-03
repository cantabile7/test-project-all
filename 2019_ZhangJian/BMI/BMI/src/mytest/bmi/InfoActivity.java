package mytest.bmi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		//初始化控件
		EditText info = (EditText)findViewById(R.id.editText1);
		TextView b = (TextView)findViewById(R.id.pass);
		//设置光标不可见
		info.setCursorVisible(false);
		//设置不可聚焦
		info.setFocusable(false);
		//设置触摸得不到焦点
		info.setFocusableInTouchMode(false);
		//设置不可长按
		info.setLongClickable(false);
		//设置只读时颜色
		info.setTextColor(Color.DKGRAY);

		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bun");
		String bmi = bundle.getString("bmi");
		b.setText("您的BMI指数："+bmi);

	}

}
