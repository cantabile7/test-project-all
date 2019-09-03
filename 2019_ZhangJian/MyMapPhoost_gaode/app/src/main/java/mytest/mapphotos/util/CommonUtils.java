package mytest.mapphotos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;

public class CommonUtils {
	public static final String PICTURE_PATH;
	public static final String THUMB_PATH;
	static {
		String sdPath = Environment
					.getExternalStorageDirectory().getAbsolutePath();
		PICTURE_PATH = sdPath + "/MapPhotos/Picture/";
		THUMB_PATH = PICTURE_PATH + ".thumb/";
	}
	/**
	 * 生成一个以当前时间命名的照片文件名字符串
	 */
	public static String getPictureNameByNowTime(){
		String filename = null;
		SimpleDateFormat sdf = 
				new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		Date now = new Date();
		filename = sdf.format(now) + ".jpg";
		return filename;
	}
	
	/**
	 * 保存照片文件，并返回最终生成的照片文件完整路径
	 */
	public static String savePicture(Context context, Bitmap bitmap, 
																String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		
		String filename = getPictureNameByNowTime();
		String completePath = path + filename;
		// save picture
		try {
			FileOutputStream fos = new FileOutputStream(completePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return completePath;
	}
	/**
	 * 解码照片文件，返回指定尺寸的Bitmap对象
	 */
	public static Bitmap decodeBitmapFromFile(String absolutePath, 
												int reqWidth, int reqHeight) {
		Bitmap bm = null;
		// 获取指定照片文件的分辨率
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(absolutePath, options);
		// 计算采样倍率
		options.inSampleSize = calcInSampleSize(options, reqWidth, reqHeight);
		// 按照指定倍率对照片进行解码
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(absolutePath, options);
		return bm;
	}
	/**
	 * 计算解码尺寸倍率。结果是1则为原始图像大小，2则为原图像的二分之一
	 */
	public static int calcInSampleSize(Options options, 
										int reqWidth, int reqHeight) {
		// 图像原始尺寸
		final float height = options.outHeight;
		final float width = options.outWidth;
		int inSampleSize = 1;
		// 根据宽高计算倍率，并四舍五入取整
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round(height/reqHeight);
			} else {
				inSampleSize = Math.round(width/reqWidth);
			}
		}
		return inSampleSize;
	}
	/**
	 * 将图像文件解码为128x128的尺寸
	 */
	public static Bitmap getPicture128(String path, String filename)  {
		String imageFile = path + filename;
		return decodeBitmapFromFile(imageFile, 128, 128);
	}
	public static Bitmap getPicture128(String absolutePath) {
		return decodeBitmapFromFile(absolutePath, 128, 128);
	}
	/**
	 * 将图像文件解码为64x64的尺寸
	 */
	public static Bitmap getPicture64(String path, String filename) {
		String imageFile = path + filename;
		return decodeBitmapFromFile(imageFile, 64, 64);
	}
	public static Bitmap getPicture64(String absolutePath) {
		return decodeBitmapFromFile(absolutePath, 64, 64);
	}
 }
