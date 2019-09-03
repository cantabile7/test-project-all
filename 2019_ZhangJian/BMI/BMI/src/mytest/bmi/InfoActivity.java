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

		//��ʼ���ؼ�
		EditText info = (EditText)findViewById(R.id.editText1);
		TextView b = (TextView)findViewById(R.id.pass);
		//���ù�겻�ɼ�
		info.setCursorVisible(false);
		//���ò��ɾ۽�
		info.setFocusable(false);
		//���ô����ò�������
		info.setFocusableInTouchMode(false);
		//���ò��ɳ���
		info.setLongClickable(false);
		//����ֻ��ʱ��ɫ
		info.setTextColor(Color.DKGRAY);

		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bun");
		String bmi = bundle.getString("bmi");
		b.setText("����BMIָ����"+bmi);

	}

}
