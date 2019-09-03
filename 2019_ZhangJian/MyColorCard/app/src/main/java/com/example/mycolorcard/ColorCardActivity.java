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
	// 主界面上的三个Tab选项卡
	private LinearLayout sampleTab = null;
	private LinearLayout searchTab = null;
	private LinearLayout identifyTab = null;
	private LinearLayout[] tabs;
	private LinearLayout det = null;

	// Tab选项卡对应的界面
	private View sampleTabView = null;
	private View searchTabView = null;
	private View identifyTabView = null;
	//详细信息界面
	private View detailedView;
	private Button btn_back;

	// 选项卡下方的布局
	private LinearLayout content = null;

	// 色卡列表
	private List<ColorSample> sampleList = new ArrayList<ColorSample>();

	public static final int PHOTO_CAPTURE = 100;// 拍照
	public static final int PHOTO_CROP = 200;// 剪裁
	private View imageview;// 显示图片的View 控件
	private Bitmap pickbmp;// 拾色图片

    private EditText et;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_card);

		//my new


		// 初始化Tab控件
		sampleTab = (LinearLayout) findViewById(R.id.sampleTab);
		searchTab = (LinearLayout) findViewById(R.id.searchTab);
		identifyTab = (LinearLayout) findViewById(R.id.identifyTab);
		det = (LinearLayout) findViewById(R.id.sampleTable_detailed);
		tabs = new LinearLayout[] { sampleTab, searchTab, identifyTab };

		// 初始化选项卡对应的布局界面
		LayoutInflater factory = LayoutInflater.from(this);
		sampleTabView = factory.inflate(R.layout.color_sample, null);
		searchTabView = factory.inflate(R.layout.color_search, null);
		identifyTabView = factory.inflate(R.layout.color_identify, null);

		detailedView = factory.inflate(R.layout.color_detailed, null);

		// 程序启动时默认显示色卡样例界面
		content = (LinearLayout) findViewById(R.id.content);
		content.addView(sampleTabView);
		// 分别为这3 个Tab 添加单击事件，被单击代表该选项卡被选中
		sampleTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setTabChecked(sampleTab);
				// 清除content 中的界面内容，加载
				// 当前被选中选项卡对应的界面
				content.removeAllViews();
				content.addView(sampleTabView);

				// 加载资源文件定义的动画效果
				Animation animation = AnimationUtils.loadAnimation(
						ColorCardActivity.this, R.anim.myanim);
				// 在组件上运用动画效果
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
		//返回按钮添加监听器
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

		//监听色卡列表

	}

	private void loadColorCards() {
		// 从资源中获得色卡数据，并按“\n”切割成字符串数组，每个元素就是一个色卡
		String sampleColors = getResources().getString(
				R.string.sample_color_list);
		String[] ss = sampleColors.split("\n");

		sampleList.clear();
		// 解析色卡数据字符串，并转换成ColorSample对象保存
		// -----------------------------------------------
		// 格式：[十六进制RGB][颜色名][所属类别]
		// -----------------------------------------------
		String rgb, name, category;
		int i, j;
		for (String s : ss) {
			rgb = name = category = null;

			if (s.trim().length() > 0) {
				// 寻找十六进制RGB子串，在第一对[]中
				i = s.indexOf('[');
				j = s.indexOf(']');
				if (j > i && i >= 0) {
					rgb = s.substring(i + 1, j);
				}

				// 寻找颜色名子串，在第二对[]中
				i = s.indexOf('[', j);
				j = s.indexOf(']', i);
				if (j > i && i >= 0) {
					name = s.substring(i + 1, j);
				}

				// 寻找类别子串，在第三对[]中
				if (j > 0) {
					i = s.indexOf('[', j);
					j = s.indexOf(']', i);
					if (j > i && i >= 0) {
						category = s.substring(i + 1, j);
					}
				}

				// 保存有效色卡
				if (rgb != null && name != null && category != null) {
					sampleList.add(new ColorSample(rgb, name, category));
				}
			}
		} // end of for
	}

	private void initSampleTabView() {
		// 获取当前设备的逻辑像素密度，以便将逻辑像素dip转换成px物理像素
		final float scale = getResources().getDisplayMetrics().density;

		// 将色卡动态加入到TableLayout布局。每一行色卡的属性与界面设计相一致
		TableLayout sampleTable = (TableLayout) sampleTabView
				.findViewById(R.id.sampleTable);
		// 清空sampleTable中的原有色卡控件
		sampleTable.removeAllViews();
		//
		for (final ColorSample samp : sampleList) {
			// 每一行的上端有20dip的留空(android:paddingTop="20dip")
			TableRow row = new TableRow(this);
			row.setPadding(0, (int) (20 * scale + 0.5f), 0, 0);

			// 第0列(android:layout_height="80dip")
			final View col00 = new View(this);
			col00.setBackgroundColor(samp.val);
			col00.setMinimumHeight((int) (80 * scale + 0.5f));

			//给每列添加监听器
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
					//RGB转CMYK
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




			// 第1列(android:gravity="center" android:layout_height="80dip")
			final TextView col01 = new TextView(this);
			col01.setText(samp.name);
			col01.setGravity(Gravity.CENTER);
			col01.setHeight((int) (80 * scale + 0.5f));

			//设置长按事件
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
						@Override//设置确定事件
						public void onClick(DialogInterface dialog, int which) {
							EditText editText_newname = et.findViewById(R.id.newName);
							String new_name = editText_newname.getText().toString();
							col01.setText(new_name);
						}
					});
					dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						@Override  //设置取消事件
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					dialog.show();
					return false;
				}
			});

			// 将TableRow放进TableLayout布局
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
				// 判断现有色卡数据列表中是否存在符合条件的色卡，将其显示出来
				final float scale = getResources().getDisplayMetrics().density;

				TableLayout resultTable = (TableLayout) searchTabView
						.findViewById(R.id.resultTable);
				resultTable.removeAllViews();
				//
				for (final ColorSample samp : sampleList) {
					if (!samp.name.contains(txtColor.getText().toString()))
						continue;

					// 每一行的上端有20dip的留空(android:paddingTop="20dip")
					TableRow row = new TableRow(ColorCardActivity.this);
					row.setPadding(0, (int) (20 * scale + 0.5f), 0, 0);

					// 第0列(android:layout_height="80dip")
					View col00 = new View(ColorCardActivity.this);
					col00.setBackgroundColor(samp.val);
					col00.setMinimumHeight((int) (80 * scale + 0.5f));

					//给每列添加监听器
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
							//RGB转CMYK
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


					// 第1列(android:gravity="center"
					// android:layout_height="80dip")
					TextView col01 = new TextView(ColorCardActivity.this);
					col01.setText(samp.name);
					col01.setGravity(Gravity.CENTER);
					col01.setHeight((int) (80 * scale + 0.5f));

					// 将TableRow放进TableLayout布局
					row.addView(col00);
					row.addView(col01);
					resultTable.addView(row);
				}

				//此处隐藏软键盘
                // 获得Android输入法服务管理器对象
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘，editColorName是要获得软键盘焦点的控件
                imm.hideSoftInputFromWindow(searchTabView.getWindowToken(), 0);



			}
		});
	}

	public void initIdentifyTabView() {
		// 初始化辨色界面中的各组件对象
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
		// 设置初始默认显示的辨色图片
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.android);
		imageview = identifyTabView.findViewById(R.id.viewPicture);
		imageview.setBackgroundDrawable(new BitmapDrawable(bmp));
		// 从触摸图片以辨别颜色
		imageview.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				int w = imageview.getWidth();
				int h = imageview.getHeight();
				// 限制触摸位置的范围，避免超出图片之外
				if (x < 0 || y < 0 || x >= w || y >= h) {
					textColorInfo.setText("超出范围");
					return true;
				}
				// 如果图像没有初始化，则先获得图像数据
				if (pickbmp == null) {
					Bitmap bgBmp = ((BitmapDrawable) imageview.getBackground())
							.getBitmap();
					pickbmp = Bitmap.createScaledBitmap(bgBmp, w, h, false);
				}
				// 得到当前触摸的像素颜色并显示
				int pixel = pickbmp.getPixel(x, y);
				viewPickedColor.setBackgroundColor(pixel);
				// 显示其RGB 值
				String rgb = "#"
						+ Integer.toHexString(pixel).substring(2).toUpperCase();
				textColorInfo.setText("当前颜色：" + rgb);
				// 转换当前像素为HSV 颜色，为颜色差异计算做准备
				int r = Color.red(pixel);
				int g = Color.green(pixel);
				int b = Color.blue(pixel);
				float[] hsv = new float[3];
				Color.RGBToHSV(r, g, b, hsv);
				//
				// TODO 获取最接近的3 种色卡，显示在界面上
				//
				// 将当前触摸颜色与各色卡的“距离”即色差值存放到Map中
				Map<Double, ColorSample> mapHSV = new HashMap<Double, ColorSample>();
				for (ColorSample sample : sampleList) {
					double dHSV = ColorSample.distHSV(hsv[0], hsv[1], hsv[2],
							sample.h, sample.s, sample.v);
					// 增加一个随机的微量值，避免重复，因为可能存在多个色卡与指定颜色的距离相等
					dHSV = dHSV + Math.random() / 1000000.0;
					mapHSV.put(dHSV, sample);
				}
				// 对颜色的色差值进行大小排序
				List<Double> distHSVList = new ArrayList<Double>(mapHSV
						.keySet());
				Collections.sort(distHSVList);
				// 得到最接近的三种颜色
				ColorSample hitted01 = mapHSV.get(distHSVList.get(0));
				ColorSample hitted02 = mapHSV.get(distHSVList.get(1));
				ColorSample hitted03 = mapHSV.get(distHSVList.get(2));
				String diff01 = String.format("%.4f", distHSVList.get(0));
				String diff02 = String.format("%.4f", distHSVList.get(1));
				String diff03 = String.format("%.4f", distHSVList.get(2));
				textColorDiff.setText("差异度：" + diff01 + ", " + diff02 + ", "
						+ diff03);
				// 显示找到的三种颜色
				sample01.setBackgroundColor(hitted01.val);
				sample02.setBackgroundColor(hitted02.val);
				sample03.setBackgroundColor(hitted03.val);
				sample01.setText(hitted01.rgb + "\n" + hitted01.name);
				sample02.setText(hitted02.rgb + "\n" + hitted02.name);
				sample03.setText(hitted03.rgb + "\n" + hitted03.name);
				// 清空内存
				distHSVList.clear();
				mapHSV.clear();		
				
				return true;
			}
		});
		//
		// TODO 启动相机拍照
		//
		btnCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 启动系统相机程序进行拍照
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指明需要返回拍照数据，即照片
				intent.putExtra("return-data", true);
				startActivityForResult(intent, PHOTO_CAPTURE);
			}
		});
	}

	/**
	 * 设置Tab 选项卡被单击时的属性状态
	 * 
	 * @param tab
	 *            被单击的“选项卡”（即LinearLayout 组件）
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
		// 如果拍照或剪裁被取消，则不作任何处理
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		// 若从拍照返回，则启动剪裁处理
		else if (requestCode == PHOTO_CAPTURE) {
			Log.d("TAG", "***********************************************已从照相机返回");

			// 获取拍照的图像
			Bitmap photo = data.getParcelableExtra("data");
			if (photo != null) {
				// 准备启动剪裁程序
				Intent intent_2 = new Intent("com.android.camera.action.CROP");
				intent_2.setType("image/*");
				// 设置要裁剪的源图和是否裁剪图像
				intent_2.putExtra("data", photo);
				intent_2.putExtra("crop", true);
				// 设置裁剪框的比例1∶1，不设置则可任意比例
				intent_2.putExtra("aspectX", 1);
				intent_2.putExtra("aspectY", 1);
				// outputX outputY 是输出裁剪图片的大小
				intent_2.putExtra("outputX", imageview.getWidth());
				intent_2.putExtra("outputY", imageview.getHeight());
				// 设置需要将数据返回给调用者
				intent_2.putExtra("return-data", true);
				// 启动系统图像剪裁组件
				startActivityForResult(intent_2, PHOTO_CROP);
			}
		}
		// 若是从剪裁返回，则显示剪裁好的图像
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
