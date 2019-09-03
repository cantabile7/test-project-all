package mytest.pt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private Bitmap background;		// 游戏背景图
	private Bitmap puzzImage;		// 拼图图像
	
	private Rect puzzRect;			// 拼图区域
	private Rect thumbRect;			// 拼图缩略图区域
	private Rect cellsRect;			// 打乱的拼图块所在区域

	private double pw;				// 拼图块的宽度
	private double ph;				// 拼图块的高度
	
	private Paint paint;			// 绘制几何图形的画笔
	
	// 存储所有拼图块的动态数组
	public List<PuzzleCell> puzzCells = new ArrayList<PuzzleCell>();
	// 游戏进度中保存的拼图块状态对象动态数组
	public List<PuzzCellState> cellStates = new ArrayList<PuzzCellState>();
	
	private PuzzleCell touchedCell;	// 当前被触摸到的拼图块
	private Bitmap backDrawing;		// 后台界面图像
	private Canvas backCanvas;		// 后台界面画布
	private int screenW;			// 当前设备屏幕宽度
	private int screenH;			// 当前设备屏幕高度
	
	private SoundPool soundPool;	// 音效池和音效资源
	private final int SOUND_PT = 1;
	private Map<Integer, Integer> soundMap;
	
	private SurfaceHolder holder;	// holder对象，用于获取canvas绘图
	private boolean finished;		// 绘图线程是否要终止
	private Rect drawingRect;

	public int row=3;              //分割的行
	public int col = 4;           //分割的列

	//是否完成拼图把标志
	public boolean allPuzzCellState = true;
	public boolean isFirst = true;

	// 定义枚举类型，即游戏的两种状态：运行，暂停
	enum GameState { RUNNING, PAUSE}
	// 游戏初始状态为暂停
	GameState gstate = GameState.PAUSE;

	public GameView(Context context, AttributeSet attrs){
		super(context,attrs);
		// 设置画笔：颜色、无锯齿平滑、实心线
		paint = new Paint();
		paint.setTextSize(70);
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		// 初始化音效
		initSounds();
	}

	public GameView(Context context) {
		super(context);
		// 设置画笔：颜色、无锯齿平滑、实心线
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setTextSize(70);
		paint.setStyle(Paint.Style.STROKE);
		// 初始化音效
		initSounds();
		
		// 获取holder，以便我们控制游戏界面的刷新工作
		holder = this.getHolder();
		holder.addCallback(this);
		// 设置游戏界面可触摸
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

	}




	/**
	 * 初始化音效
	 */
	private void initSounds() {
		// 初始化音效池。SoundPool四个参数分别是同时可播放音效数、音效类型和质量
	    soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
	    // 加载音效资源
		int soundId = soundPool.load(getContext(), R.raw.ir_begin, 1);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(SOUND_PT, soundId);
	}
	/**
	 * 播放指定的音效
	 */
	public void playSound(int sound) {
		// 获取系统声音服务
	    AudioManager mgr = (AudioManager)getContext()
						.getSystemService(Context.AUDIO_SERVICE);
	    // 获取系统当前音量和最大音量值
	    float currVol =	mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float maxVol =	mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    float volume = currVol / maxVol;
	    // 播放音效，四个参数分别是音效id、左声道音量、右声道音量、优先级、循环方式、回放
	    soundPool.play(sound, volume, volume, 1, 0, 1.0f);
	}	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//int screenW = (w > h) ? w : h;
		//int screenH = (w > h) ? h : w;
		// 计算屏幕界面大小，使宽和高应符合横屏要求
		screenW = (w > h) ? w : h;
		screenH = (w > h) ? h : w;
		//
		// 初始化游戏，分割拼图块，在后台画布上绘制游戏界面
		initGame();
		// 视情况根据保存的游戏进度分割拼图，或者新分割拼图
		if (cellStates.size() > 0)
			loadPuzzCells();
		else
			makePuzzCells();
		//
		drawPuzzle(backCanvas, null);
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		// 从后台界面图像中绘制
//		canvas.drawBitmap(backDrawing, 0, 0, null);
//		// 将被触摸或移动的拼图块画出来
//		if (touchedCell != null) {
//			touchedCell.draw(canvas);
//		}
//	}
//	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 获取触摸动作类型和触摸位置的坐标
		int act = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (act) {
		case MotionEvent.ACTION_DOWN :
			// 响应首次触屏点击事件，开始游戏
			if (gstate == GameState.PAUSE) {
				gstate = GameState.RUNNING;
				return true;
			}

			// 确定被触摸点击的拼图块，并将其置顶显示
			for (int i=0; i<puzzCells.size(); i++) {
				PuzzleCell cell = puzzCells.get(i);
				// 如果拼图块已被固定，则不能触摸也不能移动
				if (cell.fixed) continue;
				if (cell.isTouched(x, y)) {
					// 将当前拼图块显示次序设为最大
					cell.zOrder = getCellMaxzOrder() + 1;
					// 重排拼图块的次序
					sortPuzzCells();
					
					// 保存当前触摸点，为拼图块的移动做准备
					cell.touchPoint = new Point(x, y);
					touchedCell = cell;
					
					Canvas canvas = holder.lockCanvas(null);
					// 在后台画布上绘制一份“干净的”界面（排除当前触摸的拼图块）

					//将原来的drawPuzzle()方法用synchronized包住
					synchronized(backDrawing){
						drawPuzzle(backCanvas, cell);
					}

					drawingRect = null;
					holder.unlockCanvasAndPost(canvas);
					// 通知系统更新被触摸的拼图块
					//invalidate(cell.rect);
					drawingRect = cell.rect;
					
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE :
			// 如果有拼图块被触摸滑动，则移动拼图块
			if (touchedCell != null) {
				// 保存拼图块原位置区域
				Rect rect = new Rect(touchedCell.rect);
				// 移动拼图块到新位置
				touchedCell.moveTo(x, y);
				// 合并新旧两个位置的拼图块区域，以构成一个局部重绘区域
				rect.union(touchedCell.rect);
				// 让Android重绘rect区域的界面
				//invalidate(rect);
				drawingRect = rect;
				
				touchedCell.touchPoint = new Point(x, y);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP :
			if (touchedCell != null) {
				// 比较拼图块左上角与归位目标的距离，若小于10dip则自动“吸附”
				Point p1 = new Point(touchedCell.rect.left, 
									 touchedCell.rect.top);
				Point p2 = new Point(touchedCell.homeRect.left, 
									 touchedCell.homeRect.top);
				double d = Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + 
	 	 	 	 	 	 	 	 	 	(p1.y-p2.y)*(p1.y-p2.y));
				if (d <= dip2px(10)) {
					// 归位
					touchedCell.fixed = true;
					touchedCell.rect = touchedCell.homeRect;
					//设置显示次序为最小
					touchedCell.zOrder = getCellMinzOrder() - 1;
					// 重排拼图块的次序
					sortPuzzCells();
					// 播放音效
					playSound(SOUND_PT);
				}
			}
			Canvas canvas = holder.lockCanvas(null);

            //将原来的drawPuzzle()方法用synchronized包住
			synchronized(backDrawing){
				drawPuzzle(backCanvas, null);
			}

			drawingRect = null;
			holder.unlockCanvasAndPost(canvas);

			// 重绘整个界面
			//drawPuzzle(backCanvas, null);
			//invalidate();
			// 置空touchedCell，表明当前拼图块的触摸或移动结束
			touchedCell = null;
			//判断是否全部归位以判断拼图是否完成

			for (PuzzleCell cell : puzzCells) {
				//如果存在一块拼图未归位，改变状态
				if (!cell.fixed){
					allPuzzCellState = false;
					break;
				}
				//如果都归位，恢复状态
				allPuzzCellState = true;
			}
			if (allPuzzCellState){
				//如果全部拼图归位
				isFirst = false;
				levelup();
				initGame();
				//自动进入下一关
				cellStates.clear();
				puzzCells.clear();
				makePuzzCells();
				restart();
				Toast.makeText(getContext(),"任务完成，难度上升！ \n  请长按进入下一关",Toast.LENGTH_SHORT).show();
			}

			break;
		}
		
		return super.onTouchEvent(event);
	}



	/**
	 * 初始化游戏：各绘图区域计算、图片资源加载、后台画布准备等
	 */
	public void initGame() {
		// ------------------------------------------------
		// 计算拼图块大小和拼图区域
		// 水平方向：[10] + 4pw + [10] + 1.5pw + [10] = screenW
		// 垂直方向：[20] + 3ph + [20] = screenH
		// ------------------------------------------------
		pw = (screenW - dip2px(10) - dip2px(10) - dip2px(10)) / (col+1.5);
		ph = (screenH - dip2px(20) - dip2px(20)) / (1.0*row);

		// 计算拼图区域、缩略图区域、打乱拼图块的区域
		puzzRect = new Rect(dip2px(10), 
							dip2px(20), 
							dip2px(10)+(int)(col*pw),
							dip2px(20)+(int)(row*ph));
		thumbRect = new Rect(dip2px(10)+(int)(col*pw)+dip2px(10),
							 dip2px(20), 
							 screenW-dip2px(10), 
							 (int)(dip2px(20)+ph));
		cellsRect = new Rect(dip2px(10)+(int)(col*pw)+dip2px(10),
							 (int)(dip2px(20)+ph+dip2px(5)), 
						     (int)(screenW-dip2px(10)-pw), 
						     (int)(screenH-dip2px(20)-ph));
		// 加载背景图片，按屏幕大小缩放
		background = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(),
						 R.drawable.wallpaper), screenW, screenH, false);
		// 加载拼图图片，按拼图区域缩放
		puzzImage = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), 
	 	 	 	 	 	 R.drawable.pic02),	
					 	 puzzRect.width(), puzzRect.height(), false);
		// 创建后台界面图像，并将其放进后台画布，因为图像内容必须通过画布进行操作
		backDrawing = Bitmap.createBitmap(screenW, screenH, 
									Bitmap.Config.ARGB_8888);
		backCanvas = new Canvas(backDrawing);
	}
	/**
	 * 将拼图按3x4的大小切割成12个拼图块
	 */
	public List<PuzzleCell> makePuzzCells() {
		// 将拼图按3x4大小切割，并将拼图块保存到puzzCells动态数组中
//		Set<Integer> zOrders = new HashSet<Integer>();
		//initGame();
		//Log.d("TAG","new row,line:"+row+","+line+"    "+"new pw:"+pw+"new ph:"+ph);
		Rect puzzR, destR;
		for (int i=0; i<row; i++) {
			for (int j=0; j<col; j++) {
				// 计算第(i,j)位置的格子在原拼图中对应的矩形区域
				puzzR = new Rect((int)(j*pw), (int)(i*ph), 
		 		 		 		    (int)((j+1)*pw), (int)((i+1)*ph));
				// 计算拼图块图在打乱区域显示的位置，左上角在cellsRect区域内
				destR = new Rect();
				destR.left = cellsRect.left + 
								(int)(Math.random()*cellsRect.width());
				destR.top = cellsRect.top + 
								(int)(Math.random()*cellsRect.height());
				destR.right = destR.left + (int)pw;
				destR.bottom = destR.top + (int)ph;
				
//				// 随机产生一个不重复的拼图块堆叠显示次序
//				int zOrder;
//				do {
//					zOrder = (int)(12 * Math.random());
//				} while (zOrders.contains(zOrder));

//				// 保存zOrder，以使拼图块的zOrder不重复
//				zOrders.add(zOrder);

				// 创建PuzzleCell对象，保存拼图块图像、显示区域、堆叠次序
				PuzzleCell cell = new PuzzleCell();
				cell.image = Bitmap.createBitmap(puzzImage, puzzR.left,
							 	puzzR.top, puzzR.width(), puzzR.height());
				// 记录拼图块图像编号
				cell.imgId = i*col+j;
				//
				cell.rect = destR;
//				cell.zOrder = zOrder;
				// 确定拼图块的归位区域
				puzzR.offset(dip2px(10), dip2px(20));
				cell.homeRect = puzzR;
				cell.fixed = false;		

				puzzCells.add(cell);
			}
		}
		Collections.shuffle(puzzCells);
		//拼图块在动态数组中的顺序乱了，其本身的zOrder值也要根据当前次序调整
		for(int i=0; i<puzzCells.size(); i++){
			puzzCells.get(i).zOrder=i;
		}
		// 根据拼图块的zOrder倒排序，zOrder大的拼图块排在前面
		sortPuzzCells();
		return puzzCells;
	}
	/**
	 * 从保存的游戏进度中加载拼图块
	 */
	private void loadPuzzCells() {
		int arow, acol;
		Rect puzzR;
		for (PuzzCellState one : cellStates) {
			// 根据图像编号计算原拼图分割中的行列位置
			arow = one.imgId / col;
			acol = one.imgId % col;
			// 计算第(arow,acol)位置的格子在原拼图中对应的矩形区域
			puzzR = new Rect((int)(acol*pw), (int)(arow*ph),
	 		 		 	     (int)((acol+1)*pw), (int)((arow+1)*ph));
			// 创建PuzzleCell对象，保存拼图块图像、显示区域、堆叠次序
			PuzzleCell cell = new PuzzleCell();
			cell.image = Bitmap.createBitmap(puzzImage, puzzR.left,
				 				puzzR.top, puzzR.width(), puzzR.height());
			cell.imgId = one.imgId;
			cell.rect = new Rect(one.posx, one.posy, one.posx+(int)pw, one.posy+(int)ph);
			cell.zOrder = one.zOrder;
			cell.fixed = one.fixed;
			// 确定拼图块的归位区域
			puzzR.offset(dip2px(10), dip2px(20));
			cell.homeRect = puzzR;
			puzzCells.add(cell);
		}
		// 根据拼图块的zOrder倒排序，zOrder大的拼图块排在前面
		sortPuzzCells();
	}
	/**
	 * 绘制拼图界面
	 * @param canvas 绘制界面的画布
	 * @param ignoredCell 绘制时应忽略的拼图块
	 */
	private void drawPuzzle(Canvas canvas, PuzzleCell ignoredCell) {
		// 绘制背景图
		canvas.drawBitmap(background, 0, 0, null);
		
		// 绘制拼图，alpha值范围为0-255，0为不透明，255为完全透明
		Paint p = new Paint();
		p.setAlpha(120);
		canvas.drawBitmap(puzzImage, null, puzzRect, p);
		
		// 绘制拼图区域边框
		canvas.drawRect(puzzRect, paint);
		
		// 绘制水平格子线（3行）
		for(int i=1;i<row;i++){
			canvas.drawLine(puzzRect.left, (int)(ph*i)+puzzRect.top,
					puzzRect.right, (int)(ph*i)+puzzRect.top, paint);
		}
//		canvas.drawLine(puzzRect.left, (int)ph+puzzRect.top,
//						puzzRect.right, (int)ph+puzzRect.top, paint);
//		canvas.drawLine(puzzRect.left, (int)(ph*2)+puzzRect.top,
//						puzzRect.right, (int)(ph*2)+puzzRect.top, paint);
		// 绘制垂直格子线（4列）
		for(int i=1;i<col;i++){
			canvas.drawLine((int)(pw*i)+puzzRect.left, puzzRect.top,
					(int)(pw*i)+puzzRect.left, puzzRect.bottom, paint);
		}


		// 绘制缩略图
		canvas.drawBitmap(puzzImage, null, thumbRect, null);
		// 绘制所有拼图块
		for (int i=puzzCells.size()-1; i>=0; i--) {
			PuzzleCell cell = puzzCells.get(i);
			if (cell == ignoredCell) continue;
			cell.draw(canvas);
		}
	}
	/**
	 * 获取所有拼图块中zOrder最大的那个值
	 */
	private int getCellMaxzOrder() {
		int zOrder = -1;
		for (PuzzleCell cell : puzzCells) {
			if (cell.zOrder > zOrder)
				zOrder = cell.zOrder;
		}
		return zOrder;
	}
	/**
	 * 获取所有拼图块中zOrder最小的那个值
	 */
	private int getCellMinzOrder() {
		int zOrder = -1;
		for (PuzzleCell cell : puzzCells) {
			if (cell.zOrder < zOrder)
				zOrder = cell.zOrder;
		}
		return zOrder;
	}

	/**
	 * 根据拼图块的zOrder进行倒序排序
	 */
	private void sortPuzzCells() {
		Collections.sort(puzzCells, new Comparator<PuzzleCell>() {
			@Override
			public int compare(PuzzleCell c0, PuzzleCell c1) {
				return c1.zOrder - c0.zOrder;
			}
		});
	}	
	/**
	 * 将dip转换为当前设备的px值
	 */
	private int dip2px(float dip) {
		final float scale = 
			getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 启动界面刷新线程
		Thread drawer = new Thread(new GameDrawer());
		finished = false;
		drawer.start();
		
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 通知界面绘制线程终止
		finished = true;
	}
	
	/**
	 * 内部类GameDrawer，用于绘制游戏界面
	 */
	private class GameDrawer implements Runnable{
		@Override
		public void run() {
			while (!finished) {
				// 锁住画布准备绘制游戏界面
				Canvas canvas = holder.lockCanvas(drawingRect);
				//游戏暂停时，在游戏中心提示信息

				if (canvas != null) {
					canvas.drawBitmap(backDrawing, 0, 0, null);
					// 将被触摸或移动的拼图块画出来
					if (touchedCell != null) {
						touchedCell.draw(canvas);
					}
					if (gstate==GameState.PAUSE) {
						canvas.drawText("点击屏幕开始游戏", screenW/2-400, screenH/2, paint);
					}
					// 解锁画布，以便画面在屏幕上显示
					holder.unlockCanvasAndPost(canvas);
				}
				try {
					Thread.sleep(10);
				} catch (Exception ex) {
				}

			}			
		}
	}

	public void restart(){
		// 重绘整个界面
		drawPuzzle(backCanvas, null);
		invalidate();
	}

	public void levelup(){
		row +=1 ;
		col += 1;
	}
}
