package mytest.mapphotos;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mytest.mapphotos.util.CommonUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

public class MapViewActivity extends FragmentActivity {
//	private GoogleMap gmap;
// ===========高德修改==============
	private MapView mapView;
	private AMap aMap;
	
	// 定位要用的对象
	private LocationManager manager;
	private LocationListener listener;
	private String provider;
	// 默认地理位置
	private double myLatitude = 30.31032;
	private double myLongitude = 120.38104;
	
	// 拍照界面
	private ImageView popCamera;
	private LinearLayout cameraBar;
	private LinearLayout previewArea;
	private LinearLayout snapArea;
	private ImageView snap;

	private Camera camera;
	private CameraSurfaceView cameraSurfaceView;
	private Bitmap picture;

	private int albumId;
	private String albumTitle;

	private TextView currentPhoto;

	private LinearLayout showImgXml;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_view);

		showImgXml = (LinearLayout) findViewById(R.id.showImgXml);

		// 获取从MainActivity传递过来的相册信息
		Intent intent = getIntent();
		albumId = intent.getIntExtra("album_id", -1);
		albumTitle = intent.getStringExtra("album_title");

		currentPhoto = (TextView) findViewById(R.id.current_photo) ;
		currentPhoto.setText("当前相册："+albumTitle);
		// 初始化地图组件，设置显示道路地图的混合空照图，且有缩放控制
//		SupportMapFragment fm = (SupportMapFragment) 
//					getSupportFragmentManager().findFragmentById(R.id.map);
//		gmap = fm.getMap();
//		gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//		gmap.getUiSettings().setZoomControlsEnabled(true);
// ===========高德修改==============
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
			aMap.getUiSettings().setZoomControlsEnabled(true);//启用缩放控件
			aMap.getUiSettings().setZoomGesturesEnabled(true);//启用手势进行地图缩放
		}
		// 创建位置变化监听对象
		listener = new MyLocationListener();

		// 获取系统定位服务
		manager = (LocationManager) 
				getSystemService(Context.LOCATION_SERVICE);

		// 设置定位参数：最大精度， 不要求海拔信息，使用省电模式
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// 选择最佳定位方式(GPS或NETWORK)
		provider = manager.getBestProvider(criteria, true);


		// 从数据库中加载相册中的照片
		SQLiteDatabase db = openOrCreateDatabase("maphotos.db", 
												Context.MODE_PRIVATE, null);
		String sql = "select * from t_album_picture where album_id=" + albumId;
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			double latitude = 
					   cursor.getDouble(cursor.getColumnIndex("latitude"));
			double longitude = 
					   cursor.getDouble(cursor.getColumnIndex("longitude"));
			String thumb = CommonUtils.THUMB_PATH + 
					   cursor.getString(cursor.getColumnIndex("thumb"));
			String picture = 
					   cursor.getString(cursor.getColumnIndex("picture"));
			// 添加地标
			Bitmap bmp = BitmapFactory.decodeFile(thumb);
			MarkerOptions mo = new MarkerOptions();
			mo.position(new LatLng(latitude, longitude));
			mo.icon(BitmapDescriptorFactory.fromBitmap(bmp));
			// 将照片文件名设置为地标的title
			mo.title(picture);
//			gmap.addMarker(mo);
// ===========高德修改==============
			aMap.addMarker(mo);
		}
		// 关闭数据库
		cursor.close();
		db.close();
		
		// 点击地标，打开系统图库浏览器显示图片
//		gmap.setOnMarkerClickListener(new OnMarkerClickListener() {
// ===========高德修改==============
		aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				String picture = marker.getTitle();
				String path = CommonUtils.PICTURE_PATH + picture;
//				File file = new File(path);
//
//				Intent intent = new Intent();
//		        intent.setAction(Intent.ACTION_VIEW);
//		        intent.setDataAndType(Uri.fromFile(file), "image/*");
//		        startActivity(intent);
				AlertDialog.Builder showDialog = new AlertDialog.Builder(MapViewActivity.this);
				showDialog.setTitle("地标相册");
				showDialog.setCancelable(true);

				View contentView = LayoutInflater.from(MapViewActivity.this).inflate(R.layout.activity_show,null);
				ImageView img = (ImageView) contentView.findViewById(R.id.show);
				//用dialog显示地标相册图片
				Bitmap bmp = BitmapFactory.decodeFile(path);
				img.setImageBitmap(bmp);
				showDialog.setView(contentView);
				showDialog.show();
				return false;
			}
		});

		// 定位地图到当前位置
//		gmap.setMyLocationEnabled(true);
// ===========高德修改==============
		aMap.setMyLocationEnabled(true);
		
		LatLng latLng = getMyLocation();
//		gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
// ===========高德修改==============
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
		
		// 初始化拍照部分
		popCamera = (ImageView) findViewById(R.id.popCamera);
		cameraBar = (LinearLayout) findViewById(R.id.cameraBar);
		previewArea = (LinearLayout) findViewById(R.id.previewArea);
		snapArea = (LinearLayout) findViewById(R.id.snapArea);
		snap = (ImageView) findViewById(R.id.snap);
		
		// 隐藏相机拍照界面
		cameraBar.setVisibility(View.INVISIBLE);
		
		// 动态弹出拍照界面
		popCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cameraBar.getVisibility() == View.VISIBLE) {
					cameraBar.removeAllViews();
					cameraBar.setVisibility(View.INVISIBLE);
				}else if(cameraBar.getVisibility() == View.INVISIBLE) {
					// new camera surface view then add to preview area
					if (cameraSurfaceView == null) {
						cameraSurfaceView = new CameraSurfaceView(getApplicationContext());
						// 设置置顶显示，否则将被地图覆盖住
						cameraSurfaceView.setZOrderOnTop(true);
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT);
						previewArea.addView(cameraSurfaceView, param);
					}
					
					cameraBar.removeAllViews();
					
					cameraBar.addView(previewArea);
					cameraBar.addView(snapArea);
					cameraBar.setVisibility(View.VISIBLE);
				}
			}
		});
		// 拍照处理
		snap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(camera != null){
					// 启动相机聚焦拍照
					camera.autoFocus(new AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean success, Camera camera) {
							if (success) {
								camera.takePicture(null, null, new PictureTakenCallback());
							}
							else {
								Toast.makeText(getApplicationContext(), "警告：相机无法聚焦", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		});
	}

	public LatLng getMyLocation() {
		LatLng position = null;

		// 得到系统最近一次检测到的地理位置
		Location location = manager.getLastKnownLocation(provider);
		// 如果系统检测到的位置无效，则使用默认位置
		if (location == null) {
			position = new LatLng(myLatitude, myLongitude);
		} else {
			position = new LatLng(location.getLatitude(),
									location.getLongitude());
		}

		// 记录当前位置经纬度值
		myLatitude = position.latitude;
		myLongitude = position.longitude;
		
		return position;
	}
	
	
	
	/**
	 * CameraSurfaceView
	 */
	private class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

		private SurfaceHolder surfaceHolder = null;
		
		public CameraSurfaceView(Context context) {
			super(context);
			surfaceHolder = this.getHolder();
			surfaceHolder.addCallback(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			//
			camera.stopPreview();
			surfaceHolder = holder;
			
			Camera.Parameters param = camera.getParameters();
			// 指定拍照图片的大小
            List<Size> sizes = param.getSupportedPictureSizes();
            Collections.sort(sizes, new Comparator<Size>() {
				@Override
				public int compare(Size s1, Size s2) {
					// 倒排序，确保大的预览分辨率在前
					return s2.width - s1.width;
				}
            });
            for (Size size : sizes) {
            	// 拍照分辨率不能设置过大，否则会造成OutOfMemoryException异常
            	if (size.width <= 1200) {
            		param.setPictureSize(size.width, size.height);
            		break;
            	}
            }
            
			// 横竖屏镜头自动调整
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				param.set("orientation", "portrait");
				camera.setDisplayOrientation(90);
			}
			else {// 如果是横屏
				param.set("orientation", "landscape");
				camera.setDisplayOrientation(0);
			}
			
			// 自动聚焦
			List<String> focusModes = param.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			    param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			
			camera.setParameters(param);

			
    		int imgformat = param.getPreviewFormat();
    		int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
    		Camera.Size camerasize = param.getPreviewSize();
    		int frame_size = ((camerasize.width * camerasize.height) * bitsperpixel) / 8;
    		byte[] frame = new byte[frame_size];
    		
    		camera.addCallbackBuffer(frame);
    		camera.setPreviewCallbackWithBuffer(previewCallback);
			camera.startPreview();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if(camera == null){
				camera = Camera.open();
			}
			try{
				camera.setPreviewDisplay(surfaceHolder);
			}catch(Exception e){
				camera.release();
				camera = null;
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.setPreviewCallback(null); //！！这个必须在前，不然退出出错
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		
		/*
		 * Camera回调函数
		 */
		private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback(){
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				camera.addCallbackBuffer(data);
			}
		};
	}
	
	/**
	 * ButtonTakePictureCallback
	 */
	private class PictureTakenCallback implements PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (picture != null && !picture.isRecycled()) {
				picture.recycle();
			}

			//stop camera preview
			camera.stopPreview();
			
			System.out.println("data.length:" + data.length);
			picture = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			// 因为竖屏预览时旋转了90度，故照片需往回旋转90度
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				Matrix matrix = new Matrix();  
				matrix.postRotate(90); /*翻转90度*/
				int w = picture.getWidth();  
				int h = picture.getHeight();
				try {
					Bitmap tbmp = Bitmap.createBitmap(picture, 0, 0, w, h, matrix, true);
					picture.recycle();
					picture = tbmp;
				}
				catch (OutOfMemoryError oom) {
					// rotate failed
				}
			} else {
				// do nothing
			}
			
			if(picture != null){
// ===========高德修改==============
				// 更新当前地理位置
//				Location location = gmap.getMyLocation();

				// 保存照片文件
				String picPath = CommonUtils.savePicture(
							getApplicationContext(), picture,
							CommonUtils.PICTURE_PATH);
				Bitmap thumb64 = CommonUtils.getPicture64(picPath);
				// 保存缩略图文件
				String thumb64Path = CommonUtils.savePicture(
							getApplicationContext(), thumb64,
							CommonUtils.THUMB_PATH);
				// 获得照片、缩略图文件名
				String picname = new File(picPath).getName();
				String thumb64name = new File(thumb64Path).getName();
				// 保存照片数据到数据库
				SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
												Context.MODE_PRIVATE, null);
				String sql = 
						String.format("insert into t_album_picture(" + 
						" latitude, longitude, picture, thumb, album_id)" + 
						" values(%f, %f, '%s', '%s', %d)", 
						myLatitude, myLongitude, picname, thumb64name, 
						albumId);
				db.execSQL(sql);
				// 修改相册条目的缩略图
				sql = String.format(
							"update t_album set thumb='%s' where _id=%d", 
							thumb64name, albumId);
				db.execSQL(sql);
				db.close();
				// 在地图上显示缩略图
// ===========高德修改==============
//				gmap.addMarker(new MarkerOptions().position(
				aMap.addMarker(new MarkerOptions().position(
						new LatLng(myLatitude, myLongitude)).icon( 
						BitmapDescriptorFactory.fromBitmap(thumb64)));
				// 释放图片内存
				picture.recycle();
				picture = null;
				Toast.makeText(getApplicationContext(), "[已拍照]", 
						Toast.LENGTH_SHORT).show();
			}
			
			//拍照结束继续预览
			camera.startPreview();
		}
	}
	
	@Override
	public void onBackPressed() {
		// 设置按Back键时返回给前一个Activity的结果值
	    setResult(MainActivity.RESULT_MAPVIEW_BACK);
	    finish();
	}

// ===========高德修改==============
	// 高德地图需要用到的一些回调方法
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		// 注册位置变化监听器，更新频率为1秒，或者变化超过1米
		manager.requestLocationUpdates(provider, 1000, 1, listener);
	}
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		// 解除位置变化监听(省电目的)
		manager.removeUpdates(listener);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// 系统若检测到位置变化，自动触发本方法，从而更新位置值
			myLatitude = location.getLatitude();
			myLongitude = location.getLongitude();
		}
		@Override
		public void onStatusChanged(String provider, int status,Bundle extras){
			// when a provider is unable to fetch a location
			// or the provider becomes available or unavailability
		}
		@Override
		public void onProviderDisabled(String provider) {
			// when the provider is disabled by the user
		}
		@Override
		public void onProviderEnabled(String provider) {
			// when the provider is enabled by the user
		}
	}
// ===========end高德修改================

}


