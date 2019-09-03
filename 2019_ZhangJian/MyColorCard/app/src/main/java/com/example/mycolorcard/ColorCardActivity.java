package com.example.mycolorcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ColorCardActivity extends Activity {
	// �������ϵ�����Tabѡ�
	private LinearLayout sampleTab = null;
	private LinearLayout searchTab = null;
	private LinearLayout identifyTab = null;
	private LinearLayout[] tabs;
	private LinearLayout det = null;

	// Tabѡ���Ӧ�Ľ���
	private View sampleTabView = null;
	private View searchTabView = null;
	private View identifyTabView = null;
	//��ϸ��Ϣ����
	private View detailedView;
	private Button btn_back;

	// ѡ��·��Ĳ���
	private LinearLayout content = null;

	// ɫ���б�
	private List<ColorSample> sampleList = new ArrayList<ColorSample>();

	public static final int PHOTO_CAPTURE = 100;// ����
	public static final int PHOTO_CROP = 200;// ����
	private View imageview;// ��ʾͼƬ��View �ؼ�
	private Bitmap pickbmp;// ʰɫͼƬ

    private EditText et;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_card);

		//my new


		// ��ʼ��Tab�ؼ�
		sampleTab = (LinearLayout) findViewById(R.id.sampleTab);
		searchTab = (LinearLayout) findViewById(R.id.searchTab);
		identifyTab = (LinearLayout) findViewById(R.id.identifyTab);
		det = (LinearLayout) findViewById(R.id.sampleTable_detailed);
		tabs = new LinearLayout[] { sampleTab, searchTab, identifyTab };

		// ��ʼ��ѡ���Ӧ�Ĳ��ֽ���
		LayoutInflater factory = LayoutInflater.from(this);
		sampleTabView = factory.inflate(R.layout.color_sample, null);
		searchTabView = factory.inflate(R.layout.color_search, null);
		identifyTabView = factory.inflate(R.layout.color_identify, null);

		detailedView = factory.inflate(R.layout.color_detailed, null);

		// ��������ʱĬ����ʾɫ����������
		content = (LinearLayout) findViewById(R.id.content);
		content.addView(sampleTabView);
		// �ֱ�Ϊ��3 ��Tab ��ӵ����¼��������������ѡ���ѡ��
		sampleTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setTabChecked(sampleTab);
				// ���content �еĽ������ݣ�����
				// ��ǰ��ѡ��ѡ���Ӧ�Ľ���
				content.removeAllViews();
				content.addView(sampleTabView);

				// ������Դ�ļ�����Ķ���Ч��
				Animation animation = AnimationUtils.loadAnimation(
						ColorCardActivity.this, R.anim.myanim);
				// ����������ö���Ч��
				sampleTabView.startAnimation(animation);
			}
		});
		searchTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setTabChecked(searchTab);
				content.removeAllViews();
				content.addView(searchTabView);
			}
		});
		identifyTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setTabChecked(identifyTab);
				content.removeAllViews();
				content.addView(identifyTabView);
			}
		});
		//���ذ�ť��Ӽ�����
		btn_back = (Button)detailedView.findViewById(R.id.button_zuoye);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTabChecked(sampleTab);
				content.removeAllViews();
				content.addView(sampleTabView);

				Animation animation = AnimationUtils.loadAnimation(ColorCardActivity.this,R.anim.myanim);
				sampleTabView.startAnimation(animation);

			}
		});



		loadColorCards();
		initSampleTabView();
		initSearchTabView();
		initIdentifyTabView();

		//����ɫ���б�

	}

	private void loadColorCards() {
		// ����Դ�л��ɫ�����ݣ�������\n���и���ַ������飬ÿ��Ԫ�ؾ���һ��ɫ��
		String sampleColors = getResources().getString(
				R.string.sample_color_list);
		String[] ss = sampleColors.split("\n");

		sampleList.clear();
		// ����ɫ�������ַ�������ת����ColorSample���󱣴�
		// -----------------------------------------------
		// ��ʽ��[ʮ������RGB][��ɫ��][�������]
		// -----------------------------------------------
		String rgb, name, category;
		int i, j;
		for (String s : ss) {
			rgb = name = category = null;

			if (s.trim().length() > 0) {
				// Ѱ��ʮ������RGB�Ӵ����ڵ�һ��[]��
				i = s.indexOf('[');
				j = s.indexOf(']');
				if (j > i && i >= 0) {
					rgb = s.substring(i + 1, j);
				}

				// Ѱ����ɫ���Ӵ����ڵڶ���[]��
				i = s.indexOf('[', j);
				j = s.indexOf(']', i);
				if (j > i && i >= 0) {
					name = s.substring(i + 1, j);
				}

				// Ѱ������Ӵ����ڵ�����[]��
				if (j > 0) {
					i = s.indexOf('[', j);
					j = s.indexOf(']', i);
					if (j > i && i >= 0) {
						category = s.substring(i + 1, j);
					}
				}

				// ������Чɫ��
				if (rgb != null && name != null && category != null) {
					sampleList.add(new ColorSample(rgb, name, category));
				}
			}
		} // end of for
	}

	private void initSampleTabView() {
		// ��ȡ��ǰ�豸���߼������ܶȣ��Ա㽫�߼�����dipת����px��������
		final float scale = getResources().getDisplayMetrics().density;

		// ��ɫ����̬���뵽TableLayout���֡�ÿһ��ɫ������������������һ��
		TableLayout sampleTable = (TableLayout) sampleTabView
				.findViewById(R.id.sampleTable);
		// ���sampleTable�е�ԭ��ɫ���ؼ�
		sampleTable.removeAllViews();
		//
		for (final ColorSample samp : sampleList) {
			// ÿһ�е��϶���20dip������(android:paddingTop="20dip")
			TableRow row = new TableRow(this);
			row.setPadding(0, (int) (20 * scale + 0.5f), 0, 0);

			// ��0��(android:layout_height="80dip")
			final View col00 = new View(this);
			col00.setBackgroundColor(samp.val);
			col00.setMinimumHeight((int) (80 * scale + 0.5f));

			//��ÿ����Ӽ�����
			col00.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int val;

					int r;
					int g;
					int b;

					int c;
					int m;
					int y;
					int k = 0;
					content.removeAllViews();
					content.addView(detailedView);

					View detailedColor=(View)detailedView.findViewById(R.id.detailed_color);
					detailedColor.setBackgroundColor(samp.val);
					Animation animation = AnimationUtils.loadAnimation(ColorCardActivity.this,R.anim.myanim);
					detailedView.startAnimation(animation);

					TextView detailedText =(TextView)detailedView.findViewById(R.id.TextView01);
					//RGBתCMYK
					val = Color.parseColor(samp.rgb);
					r = Color.red(val);
					g = Color.green(val);
					b = Color.blue(val);

					c = 255-r;
					m = 255-g;
					y = 255-b;
					k = c;

					if(k>m)
					{
						k=m;
					}
					if(k>y)
					{
						k=y;
					}

					c = c - k;
					m = m - k;
					y = y - k;

					detailedText.setText(samp.name + "  "+samp.rgb  + " c: "+ c+ " m: "+ m+ " y: "+ y+ " k: "+ k);
				}
			});




			// ��1��(android:gravity="center" android:layout_height="80dip")
			final TextView col01 = new TextView(this);
			col01.setText(samp.name);
			col01.setGravity(Gravity.CENTER);
			col01.setHeight((int) (80 * scale + 0.5f));

			//���ó����¼�
			col00.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Log.e("TAG","make listener************");
					AlertDialog.Builder dialog = new AlertDialog.Builder(ColorCardActivity.this);
					dialog.setTitle("Change color card's name");
					dialog.setMessage("Please input new name:");
					final View et =  getLayoutInflater().inflate(R.layout.choose,null);
					dialog.setView(et);
					dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override//����ȷ���¼�
						public void onClick(DialogInterface dialog, int which) {
							EditText editText_newname = et.findViewById(R.id.newName);
							String new_name = editText_newname.getText().toString();
							col01.setText(new_name);
						}
					});
					dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						@Override  //����ȡ���¼�
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					dialog.show();
					return false;
				}
			});

			// ��TableRow�Ž�TableLayout����
			row.addView(col00);
			row.addView(col01);
			sampleTable.addView(row);

		}


	}

	private void initSearchTabView() {
		Button btnSearch = (Button) searchTabView.findViewById(R.id.btnSearch);
		final EditText txtColor = (EditText) searchTabView
				.findViewById(R.id.editColorName);

		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// �ж�����ɫ�������б����Ƿ���ڷ���������ɫ����������ʾ����
				final float scale = getResources().getDisplayMetrics().density;

				TableLayout resultTable = (TableLayout) searchTabView
						.findViewById(R.id.resultTable);
				resultTable.removeAllViews();
				//
				for (final ColorSample samp : sampleList) {
					if (!samp.name.contains(txtColor.getText().toString()))
						continue;

					// ÿһ�е��϶���20dip������(android:paddingTop="20dip")
					TableRow row = new TableRow(ColorCardActivity.this);
					row.setPadding(0, (int) (20 * scale + 0.5f), 0, 0);

					// ��0��(android:layout_height="80dip")
					View col00 = new View(ColorCardActivity.this);
					col00.setBackgroundColor(samp.val);
					col00.setMinimumHeight((int) (80 * scale + 0.5f));

					//��ÿ����Ӽ�����
					col00.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int val;

							int r;
							int g;
							int b;

							int c;
							int m;
							int y;
							int k = 0;
							content.removeAllViews();
							content.addView(detailedView);

							View detailedColor=(View)detailedView.findViewById(R.id.detailed_color);
							detailedColor.setBackgroundColor(samp.val);
							Animation animation = AnimationUtils.loadAnimation(ColorCardActivity.this,R.anim.myanim);
							detailedView.startAnimation(animation);

							TextView detailedText =(TextView)detailedView.findViewById(R.id.TextView01);
							//RGBתCMYK
							val = Color.parseColor(samp.rgb);
							r = Color.red(val);
							g = Color.green(val);
							b = Color.blue(val);

							c = 255-r;
							m = 255-g;
							y = 255-b;
							k = c;

							if(k>m) k=m;

							if(k>y) k=y;

							c = c - k;
							m = m - k;
							y = y - k;

							detailedText.setText(samp.name + "  "+samp.rgb  + " c: "+ c+ " m: "+ m+ " y: "+ y+ " k: "+ k);
						}
					});


					// ��1��(android:gravity="center"
					// android:layout_height="80dip")
					TextView col01 = new TextView(ColorCardActivity.this);
					col01.setText(samp.name);
					col01.setGravity(Gravity.CENTER);
					col01.setHeight((int) (80 * scale + 0.5f));

					// ��TableRow�Ž�TableLayout����
					row.addView(col00);
					row.addView(col01);
					resultTable.addView(row);
				}

				//�˴����������
                // ���Android���뷨�������������
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // ��������̣�editColorName��Ҫ�������̽���Ŀؼ�
                imm.hideSoftInputFromWindow(searchTabView.getWindowToken(), 0);



			}
		});
	}

	public void initIdentifyTabView() {
		// ��ʼ����ɫ�����еĸ��������
		final TextView sample01 = (TextView) identifyTabView
				.findViewById(R.id.sample01);
		final TextView sample02 = (TextView) identifyTabView
				.findViewById(R.id.sample02);
		final TextView sample03 = (TextView) identifyTabView
				.findViewById(R.id.sample03);
		final TextView textColorDiff = (TextView) identifyTabView
				.findViewById(R.id.textColorDiff);
		final TextView textColorInfo = (TextView) identifyTabView
				.findViewById(R.id.textColorInfo);
		final View viewPickedColor = identifyTabView
				.findViewById(R.id.viewPickedColor);
		Button btnCamera = (Button) identifyTabView
				.findViewById(R.id.btnCamera);
		// ���ó�ʼĬ����ʾ�ı�ɫͼƬ
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.android);
		imageview = identifyTabView.findViewById(R.id.viewPicture);
		imageview.setBackgroundDrawable(new BitmapDrawable(bmp));
		// �Ӵ���ͼƬ�Ա����ɫ
		imageview.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				int w = imageview.getWidth();
				int h = imageview.getHeight();
				// ���ƴ���λ�õķ�Χ�����ⳬ��ͼƬ֮��
				if (x < 0 || y < 0 || x >= w || y >= h) {
					textColorInfo.setText("������Χ");
					return true;
				}
				// ���ͼ��û�г�ʼ�������Ȼ��ͼ������
				if (pickbmp == null) {
					Bitmap bgBmp = ((BitmapDrawable) imageview.getBackground())
							.getBitmap();
					pickbmp = Bitmap.createScaledBitmap(bgBmp, w, h, false);
				}
				// �õ���ǰ������������ɫ����ʾ
				int pixel = pickbmp.getPixel(x, y);
				viewPickedColor.setBackgroundColor(pixel);
				// ��ʾ��RGB ֵ
				String rgb = "#"
						+ Integer.toHexString(pixel).substring(2).toUpperCase();
				textColorInfo.setText("��ǰ��ɫ��" + rgb);
				// ת����ǰ����ΪHSV ��ɫ��Ϊ��ɫ���������׼��
				int r = Color.red(pixel);
				int g = Color.green(pixel);
				int b = Color.blue(pixel);
				float[] hsv = new float[3];
				Color.RGBToHSV(r, g, b, hsv);
				//
				// TODO ��ȡ��ӽ���3 ��ɫ������ʾ�ڽ�����
				//
				// ����ǰ������ɫ���ɫ���ġ����롱��ɫ��ֵ��ŵ�Map��
				Map<Double, ColorSample> mapHSV = new HashMap<Double, ColorSample>();
				for (ColorSample sample : sampleList) {
					double dHSV = ColorSample.distHSV(hsv[0], hsv[1], hsv[2],
							sample.h, sample.s, sample.v);
					// ����һ�������΢��ֵ�������ظ�����Ϊ���ܴ��ڶ��ɫ����ָ����ɫ�ľ������
					dHSV = dHSV + Math.random() / 1000000.0;
					mapHSV.put(dHSV, sample);
				}
				// ����ɫ��ɫ��ֵ���д�С����
				List<Double> distHSVList = new ArrayList<Double>(mapHSV
						.keySet());
				Collections.sort(distHSVList);
				// �õ���ӽ���������ɫ
				ColorSample hitted01 = mapHSV.get(distHSVList.get(0));
				ColorSample hitted02 = mapHSV.get(distHSVList.get(1));
				ColorSample hitted03 = mapHSV.get(distHSVList.get(2));
				String diff01 = String.format("%.4f", distHSVList.get(0));
				String diff02 = String.format("%.4f", distHSVList.get(1));
				String diff03 = String.format("%.4f", distHSVList.get(2));
				textColorDiff.setText("����ȣ�" + diff01 + ", " + diff02 + ", "
						+ diff03);
				// ��ʾ�ҵ���������ɫ
				sample01.setBackgroundColor(hitted01.val);
				sample02.setBackgroundColor(hitted02.val);
				sample03.setBackgroundColor(hitted03.val);
				sample01.setText(hitted01.rgb + "\n" + hitted01.name);
				sample02.setText(hitted02.rgb + "\n" + hitted02.name);
				sample03.setText(hitted03.rgb + "\n" + hitted03.name);
				// ����ڴ�
				distHSVList.clear();
				mapHSV.clear();		
				
				return true;
			}
		});
		//
		// TODO �����������
		//
		btnCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ����ϵͳ��������������
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// ָ����Ҫ�����������ݣ�����Ƭ
				intent.putExtra("return-data", true);
				startActivityForResult(intent, PHOTO_CAPTURE);
			}
		});
	}

	/**
	 * ����Tab ѡ�������ʱ������״̬
	 * 
	 * @param tab
	 *            �������ġ�ѡ�������LinearLayout �����
	 */
	public void setTabChecked(LinearLayout tab) {
		for (int i = 0; i < tabs.length; i++) {
			tabs[i].setBackgroundDrawable(null);
			TextView txt = (TextView) tabs[i].getChildAt(0);
			txt.setTextColor(getResources().getColor(R.color.darkgreen));
		}
		tab.setBackgroundResource(R.drawable.tabselected);
		TextView txt = (TextView) tab.getChildAt(0);
		txt.setTextColor(getResources().getColor(R.color.white));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.color_card, menu);
		return true;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ������ջ���ñ�ȡ���������κδ���
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		// �������շ��أ����������ô���
		else if (requestCode == PHOTO_CAPTURE) {
			Log.d("TAG", "***********************************************�Ѵ����������");

			// ��ȡ���յ�ͼ��
			Bitmap photo = data.getParcelableExtra("data");
			if (photo != null) {
				// ׼���������ó���
				Intent intent_2 = new Intent("com.android.camera.action.CROP");
				intent_2.setType("image/*");
				// ����Ҫ�ü���Դͼ���Ƿ�ü�ͼ��
				intent_2.putExtra("data", photo);
				intent_2.putExtra("crop", true);
				// ���òü���ı���1��1������������������
				intent_2.putExtra("aspectX", 1);
				intent_2.putExtra("aspectY", 1);
				// outputX outputY ������ü�ͼƬ�Ĵ�С
				intent_2.putExtra("outputX", imageview.getWidth());
				intent_2.putExtra("outputY", imageview.getHeight());
				// ������Ҫ�����ݷ��ظ�������
				intent_2.putExtra("return-data", true);
				// ����ϵͳͼ��������
				startActivityForResult(intent_2, PHOTO_CROP);
			}
		}
		// ���ǴӼ��÷��أ�����ʾ���úõ�ͼ��
		else if (requestCode == PHOTO_CROP) {
			Bitmap photo = data.getParcelableExtra("data");
			if (photo != null) {
				imageview.setBackgroundDrawable(new BitmapDrawable(photo));
				pickbmp = null;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
