package mytest.mapphotos;

import mytest.mapphotos.util.CommonUtils;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GalleryActivity extends Activity {
	private LinearLayout gallery;
	private ImageView pictureView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		gallery = (LinearLayout) findViewById(R.id.gallery);
		pictureView = (ImageView) findViewById(R.id.imageview_picture);
		// 获取传递过来的相册id
		int albumId = getIntent().getIntExtra("album_id", -1);
//		if(albumId == -1){
//			// 显示所有照片
//			getAllPicture();
//		}
//		else {
//			// 显示相册中的照片
//			getAllPictureById(albumId);
//		}
		getPicture(albumId);
	}
	private View getImageView(final String path) {
		int width = dip2px(80);
		int height = dip2px(80);
		// 从照片解码80x80的缩略图
		Bitmap bitmap = CommonUtils.decodeBitmapFromFile(path, width, height);
		
		ImageView imageView = new ImageView(this);
		imageView.setLayoutParams(new LayoutParams(width, height));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bitmap);
		
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LayoutParams(width, height));
		layout.setGravity(Gravity.CENTER);
		layout.addView(imageView);
		
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int w = pictureView.getWidth();
				int h = pictureView.getHeight();
				
				Bitmap picture = CommonUtils.decodeBitmapFromFile(path, w, h);
				pictureView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				pictureView.setImageBitmap(picture);
			}
		});
		// 设置右边距
		layout.setPadding(0, 0, dip2px(5), 0);
		
		return layout;
	}
	/**
	 * 获取所有照片
	 */

	private void getPicture(int albumId){
		if(!(Environment.MEDIA_MOUNTED.equals(
				Environment.getExternalStorageState()))){
			return;
		}
		SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
				Context.MODE_PRIVATE, null);
		String sql ="";
		Cursor cursor = null;
		if(albumId == -1){
			// 显示所有照片
			sql = "select * from t_album_picture order by _id desc";
			cursor = db.rawQuery(sql, null);
		}
		else {
			// 显示相册中的照片
			sql = "select * from t_album_picture " +
					"where album_id=? order by _id desc";
			cursor = db.rawQuery(sql,
					new String[]{String.valueOf(albumId)});
		}
		while (cursor.moveToNext()) {
			String picture =
					cursor.getString(cursor.getColumnIndex("picture"));
			String path = CommonUtils.PICTURE_PATH + picture;
			gallery.addView(getImageView(path));
		}
		cursor.close();
		db.close();
	}

//	private void getAllPicture() {
//		if(!(Environment.MEDIA_MOUNTED.equals(
//				Environment.getExternalStorageState()))){
//			return;
//		}
//		SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
//										Context.MODE_PRIVATE, null);
//		String sql = "select * from t_album_picture order by _id desc";
//		Cursor cursor = db.rawQuery(sql, null);
//		while (cursor.moveToNext()) {
//			String picture =
//						cursor.getString(cursor.getColumnIndex("picture"));
//			String path = CommonUtils.PICTURE_PATH + picture;
//			gallery.addView(getImageView(path));
//		}
//		cursor.close();
//		db.close();
//	}
//	/**
//	 * 获取指定相册的照片
//	 */
//	private void getAllPictureById(int albumId) {
//		if(!(Environment.MEDIA_MOUNTED.equals(
//				Environment.getExternalStorageState()))) {
//			return;
//		}
//		SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
//										Context.MODE_PRIVATE, null);
//		String sql = "select * from t_album_picture " +
//					  "where album_id=? order by _id desc";
//		Cursor cursor = db.rawQuery(sql,
//								new String[]{String.valueOf(albumId)});
//		while (cursor.moveToNext()) {
//			String picture =
//						cursor.getString(cursor.getColumnIndex("picture"));
//			String path = CommonUtils.PICTURE_PATH + picture;
//			gallery.addView(getImageView(path));
//		}
//		cursor.close();
//		db.close();
//	}

	private int dip2px(float dip) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}	
}
