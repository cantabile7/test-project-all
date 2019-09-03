package mytest.mapphotos;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mytest.mapphotos.bean.RowInfoBean;
import mytest.mapphotos.util.CommonUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int REQUEST_MAPVIEW = 11;
	public static final int RESULT_MAPVIEW_BACK = 12;

	private ListView photoListView;
	// ListView显示的数据行
	private List<RowInfoBean> photoList = new ArrayList<RowInfoBean>();
	// 用于ListView组件显示行的Adapter
	private PhotoAdapter photoAdapter;
	
	private int seledRowIndex = -1;
	private Set<Integer> seledRowIndexes;

	private MenuItem editMenu;
	private MenuItem removeMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 初始化数据库
		initDB();
		// 显示ActionBar的溢出菜单(即右端的三个点)
		showOverflowMenu();
		
        //初始化多选条目
		seledRowIndexes = new HashSet<Integer>();

		// 从数据库获取相册以便ListView组件显示
		photoList.clear();
		loadAlbumFromDb();
		// 初始化ListView组件，设定其Adapter以便加载数据行
		photoListView = (ListView) findViewById(R.id.photoListView);
		photoAdapter = new PhotoAdapter(this);
		photoListView.setAdapter(photoAdapter);
		
		// 长按条目事件
		photoListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
				// 处理选中或取消选中
				if (seledRowIndexes.contains(position)) {
					seledRowIndex = -1;
					editMenu.setEnabled(false);
					removeMenu.setEnabled(false);
					seledRowIndexes.remove(position);
				}
				else {
					seledRowIndex = position;
					editMenu.setEnabled(true);
					removeMenu.setEnabled(true);
					seledRowIndexes.add(position);
				}
				// 通知ListView更新显示
				photoAdapter.notifyDataSetInvalidated();
				
				return true;
			}
		});

		photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int location, long id) {
				RowInfoBean bean = photoList.get(location);
				Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
				intent.putExtra("album_id", bean.id);
				intent.putExtra("album_title", bean.title);
				startActivityForResult(intent, REQUEST_MAPVIEW);
			}
		});

	}

	private void loadAlbumFromDb() {
		// 打开数据库
		SQLiteDatabase db = openOrCreateDatabase("maphotos.db", 
												Context.MODE_PRIVATE, null);
		// 设定默认的缩略图
		Drawable defaultThumb = 
						getResources().getDrawable(R.drawable.emblem);
		// 执行表查询获取所有相册数据
		String sql = "select * from t_album";
		Cursor cursor = db.rawQuery(sql, null);
		// 循环处理查询结果，并生成对应的相册条目
		while(cursor.moveToNext()) {
			RowInfoBean bean = new RowInfoBean();
			bean.id = cursor.getInt(cursor.getColumnIndex("_id"));
			bean.title = cursor.getString(cursor.getColumnIndex("title"));
			// 处理缩略图
			String thumb = cursor.getString(cursor.getColumnIndex("thumb"));
			if (thumb==null || thumb.equals("")) {
				bean.thumb = defaultThumb;
			}
			else {
				thumb = CommonUtils.THUMB_PATH + thumb;
				bean.thumb = new BitmapDrawable(getResources(), 
											BitmapFactory.decodeFile(thumb));
			}
			photoList.add(bean);
		}
		// 关闭数据库
		cursor.close();
		db.close();
	}
	private void initDB() {
		// 打开数据库（如果不存在则自动创建）
		SQLiteDatabase db = openOrCreateDatabase("maphotos.db", 
												Context.MODE_PRIVATE, null);
		String sql;
		
		// 创建t_album表
		sql = "create table if not exists t_album(" +
				" _id integer primary key autoincrement," +
				" title varchar, thumb varchar, description varchar)";
		db.execSQL(sql);
		
		// 创建t_album_picture表
		sql = "create table if not exists t_album_picture(" +
		            "_id integer primary key autoincrement," +
		            " latitude double, longitude double," +
				    " picture varchar, thumb varchar, album_id integer)";
		db.execSQL(sql);
		
		// 关闭数据库
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		// 禁用"修改名称"菜单项
		editMenu = menu.findItem(R.id.menu_item_edit);
		editMenu.setEnabled(false);
		//禁用"移除条目"菜单项
		removeMenu = menu.findItem(R.id.menu_item_remove);
		removeMenu.setEnabled(false);
		
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// 菜单项的点击事件响应处理
		switch (item.getItemId()) {
		case R.id.menu_item_gallery:
			int albumId = -1;
			if (seledRowIndex != -1) {
				RowInfoBean bean = photoList.get(seledRowIndex);
				albumId = bean.id;
			}
			// 启动相册浏览
			Intent intent = new Intent(this, GalleryActivity.class);
			intent.putExtra("album_id", albumId);
			startActivity(intent);
			break;
		case R.id.menu_item_add:
			// 设定输入框
			final EditText txtTitle = new EditText(this);
			txtTitle.setInputType(InputType.TYPE_CLASS_TEXT);
			// 动态创建对话框
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			// 设定对话框中的按钮（修改和返回）
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which){
							String title = txtTitle.getText().toString();
							// 将新增条目数据保存到数据库
							SQLiteDatabase db = openOrCreateDatabase(
									"maphotos.db", 
									Context.MODE_PRIVATE, null);
							String sql = "insert into t_album(title, thumb)" +
 										"values('" + title + "', '')";
							db.execSQL(sql);
							db.close();
							// 重新加载数据库数据并显示
							photoList.clear();
							loadAlbumFromDb();
							photoAdapter.notifyDataSetChanged();
						}
					});
			dialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which){
							dialog.cancel();
						}
					});
			// 设定对话框标题和输入框，然后显示
			dialog.setTitle("新相册名称");
			dialog.setView(txtTitle);
			dialog.show();
			break;
		case R.id.menu_item_remove:
			if (seledRowIndex != -1) {
				//同步数据库
				RowInfoBean bean = photoList.get(seledRowIndex);
				String del_id = String.valueOf(bean.id);
//				String title = txtTitle.getText().toString();
				// 删除数据库中对应条目
				SQLiteDatabase db = openOrCreateDatabase(
						"maphotos.db",
						Context.MODE_PRIVATE, null);
				String sql = "delete from t_album where _id = "+del_id+"";
				db.execSQL(sql);
				db.close();
				photoList.remove(seledRowIndex);
				// 重新加载数据库数据并显示
				photoList.clear();
				loadAlbumFromDb();
				photoAdapter.notifyDataSetChanged();

				// 重置选中项
				seledRowIndex = -1;
				//重新禁用"移除条目"菜单项
				removeMenu.setEnabled(false);
				editMenu.setEnabled(false);
				photoAdapter.notifyDataSetChanged();
			}
			else {
				Toast.makeText(getApplicationContext(), "长按数据行以选中，再执行删除操作",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.menu_item_edit:
			// 得到当前选中的数据行
	        final RowInfoBean bean = photoList.get(seledRowIndex);

			// 设定对话框中的输入框
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
	        input.setText(bean.title);
			//RowInfoBean bean = photoList.get(seledRowIndex);

	        // 动态创建对话框
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);

	        // 设定对话框中的按钮（修改和返回）
			builder.setPositiveButton("修改", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	// 禁用修改条目菜单项
			        seledRowIndex = -1;
			        editMenu.setEnabled(false);
                    removeMenu.setEnabled(false);

					String update_id = String.valueOf(bean.id);
					String new_title = input.getText().toString();
					// 将新增条目数据保存到数据库
					SQLiteDatabase db = openOrCreateDatabase(
							"maphotos.db",
							Context.MODE_PRIVATE, null);
					String sql = "update t_album set title = '"+new_title+"' where _id = "+update_id+"";
					db.execSQL(sql);
					db.close();

			        // 修改选中的数据行，并通知ListView更新界面显示
			        bean.title = input.getText().toString();
			        photoAdapter.notifyDataSetChanged();
			    }
			});
			builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	// 禁用修改条目菜单项
			        seledRowIndex = -1;
			        editMenu.setEnabled(false);
					removeMenu.setEnabled(false);
			        // 关闭对话框
			        photoAdapter.notifyDataSetChanged();
			        dialog.cancel();
			    }
			});

			// 设定对话框标题和输入框，然后显示
			builder.setTitle("修改相册名称");
			builder.setView(input);
			builder.show();
			break;
		case R.id.menu_item_des:
			//得到当前选中的行的相册id
			RowInfoBean des_bean = photoList.get(seledRowIndex);
			final String des_id = String.valueOf(des_bean.id);
			final EditText des = new EditText(this);
			// 将数据库中的描述先写在文本框中以编辑查看
			SQLiteDatabase db = openOrCreateDatabase(
					"maphotos.db",
					Context.MODE_PRIVATE, null);
			Cursor cursor = db.query("t_album", new String[]{"description"},"_id=?", new String[]{des_id}, null,null,null);
			if(cursor == null){
				//如果查找为空
				cursor.close();
			}
			else{
				//如果查找不为空
				while(cursor.moveToNext()){
					des.setText(cursor.getString(cursor.getColumnIndex("description")));
				}
				cursor.close();
			}
			db.close();

			new AlertDialog.Builder(this).setTitle("请输入描述")
					.setView(des)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							//将描述添加到数据库中
							String new_des = des.getText().toString();
							// 将新增条目数据保存到数据库
							SQLiteDatabase db = openOrCreateDatabase(
									"maphotos.db",
									Context.MODE_PRIVATE, null);
							String sql = "update t_album set description = '"+new_des+"' where _id = "+des_id+"";
							db.execSQL(sql);
							db.close();
							//已添加
							Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
						}
					}).setNegativeButton("取消",null).show();
		}

		return super.onOptionsItemSelected(item);
	}

	private void showOverflowMenu() {
		// 通过Java反射手工设置显示溢出菜单
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * adapter for listView
	 */
	protected class PhotoAdapter extends BaseAdapter {
		//
		private Context context;
		private LayoutInflater layoutInflater;
		
		public PhotoAdapter(Context context){
			this.context = context;
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return photoList.size();
		}

		@Override
		public Object getItem(int position) {
			return photoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			if (view == null) {
				LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.activity_main_listview_row, null);
				view = layout;
			}
			
			// 初始化“行”布局中的组件
			ImageView thumbView = (ImageView) view.findViewById(R.id.imageViewThumb);
			TextView titleView = (TextView) view.findViewById(R.id.textViewTitle);
			
			final RowInfoBean bean = photoList.get(position);
			thumbView.setBackgroundDrawable(bean.thumb);
			titleView.setText(bean.title);
			
			// 处理被选中行的高亮显示
			if (seledRowIndexes.contains(position)) {
				view.setBackgroundColor(Color.parseColor("#63B8FF"));
			}
			else {
				view.setBackgroundColor(Color.parseColor("#F0F8FF"));
			}
			
//			ImageView imageViewMap = (ImageView) view.findViewById(R.id.imageViewMap);
//			imageViewMap.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
//					intent.putExtra("album_id", bean.id);
//					intent.putExtra("album_title", bean.title);
//					startActivityForResult(intent, REQUEST_MAPVIEW);
//				}
//			});
			
			return view;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, 
														Intent data) {
		switch(resultCode){
		case RESULT_MAPVIEW_BACK:
			// 从MapViewActivity返回则重新加载相册条目
			photoList.clear();
			loadAlbumFromDb();
			// 更新ListView组件显示
			photoAdapter.notifyDataSetChanged();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		// 重置选中项
		seledRowIndexes.clear();
		photoAdapter.notifyDataSetChanged();
	}
}
