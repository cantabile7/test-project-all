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
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	private Bitmap background;		// ��Ϸ����ͼ
	private Bitmap puzzImage;		// ƴͼͼ��
	
	private Rect puzzRect;			// ƴͼ����
	private Rect thumbRect;			// ƴͼ����ͼ����
	private Rect cellsRect;			// ���ҵ�ƴͼ����������

	private double pw;				// ƴͼ��Ŀ��
	private double ph;				// ƴͼ��ĸ߶�
	
	private Paint paint;			// ���Ƽ���ͼ�εĻ���
	
	// �洢����ƴͼ��Ķ�̬����
	public List<PuzzleCell> puzzCells = new ArrayList<PuzzleCell>();
	// ��Ϸ�����б����ƴͼ��״̬����̬����
	public List<PuzzCellState> cellStates = new ArrayList<PuzzCellState>();
	
	private PuzzleCell touchedCell;	// ��ǰ����������ƴͼ��
	private Bitmap backDrawing;		// ��̨����ͼ��
	private Canvas backCanvas;		// ��̨���滭��
	private int screenW;			// ��ǰ�豸��Ļ���
	private int screenH;			// ��ǰ�豸��Ļ�߶�
	
	private SoundPool soundPool;	// ��Ч�غ���Ч��Դ
	private final int SOUND_PT = 1;
	private Map<Integer, Integer> soundMap;
	
	
	public GameView(Context context) {
		super(context);
		// ���û��ʣ���ɫ���޾��ƽ����ʵ����
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		// ��ʼ����Ч
		initSounds();
	}
	/**
	 * ��ʼ����Ч
	 */
	private void initSounds() {
		// ��ʼ����Ч�ء�SoundPool�ĸ������ֱ���ͬʱ�ɲ�����Ч������Ч���ͺ�����
	    soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
	    // ������Ч��Դ
		int soundId = soundPool.load(getContext(), R.raw.ir_begin, 1);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(SOUND_PT, soundId);
	}
	/**
	 * ����ָ������Ч
	 */
	public void playSound(int sound) {
		// ��ȡϵͳ��������
	    AudioManager mgr = (AudioManager)getContext()
						.getSystemService(Context.AUDIO_SERVICE);
	    // ��ȡϵͳ��ǰ�������������ֵ
	    float currVol =	mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float maxVol =	mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    float volume = currVol / maxVol;
	    // ������Ч���ĸ������ֱ�����Чid�����������������������������ȼ���ѭ����ʽ���ط�
	    soundPool.play(sound, volume, volume, 1, 0, 1.0f);
	}	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//int screenW = (w > h) ? w : h;
		//int screenH = (w > h) ? h : w;
		// ������Ļ�����С��ʹ��͸�Ӧ���Ϻ���Ҫ��
		screenW = (w > h) ? w : h;
		screenH = (w > h) ? h : w;
		//
		// ��ʼ����Ϸ���ָ�ƴͼ�飬�ں�̨�����ϻ�����Ϸ����
		initGame();
		// ��������ݱ������Ϸ���ȷָ�ƴͼ�������·ָ�ƴͼ
		if (cellStates.size() > 0)
			loadPuzzCells();
		else
			makePuzzCells();
		//
		drawPuzzle(backCanvas, null);		
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// �Ӻ�̨����ͼ���л���
		canvas.drawBitmap(backDrawing, 0, 0, null);
		// �����������ƶ���ƴͼ�黭����
		if (touchedCell != null) {
			touchedCell.draw(canvas);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ��ȡ�����������ͺʹ���λ�õ�����
		int act = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (act) {
		case MotionEvent.ACTION_DOWN :
			// ȷ�������������ƴͼ�飬�������ö���ʾ
			for (int i=0; i<puzzCells.size(); i++) {
				PuzzleCell cell = puzzCells.get(i);
				// ���ƴͼ���ѱ��̶������ܴ���Ҳ�����ƶ�
				if (cell.fixed) continue;
				if (cell.isTouched(x, y)) {
					// ����ǰƴͼ����ʾ������Ϊ���
					cell.zOrder = getCellMaxzOrder() + 1;
					// ����ƴͼ��Ĵ���
					sortPuzzCells();
					
					// ���浱ǰ�����㣬Ϊƴͼ����ƶ���׼��
					cell.touchPoint = new Point(x, y);
					touchedCell = cell;
					
					// �ں�̨�����ϻ���һ�ݡ��ɾ��ġ����棨�ų���ǰ������ƴͼ�飩
					drawPuzzle(backCanvas, cell);
					// ֪ͨϵͳ���±�������ƴͼ��
					invalidate(cell.rect);
					
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE :
			// �����ƴͼ�鱻�������������ƶ�ƴͼ��
			if (touchedCell != null) {
				// ����ƴͼ��ԭλ������
				Rect rect = new Rect(touchedCell.rect);
				// �ƶ�ƴͼ�鵽��λ��
				touchedCell.moveTo(x, y);
				// �ϲ��¾�����λ�õ�ƴͼ�������Թ���һ���ֲ��ػ�����
				rect.union(touchedCell.rect);
				// ��Android�ػ�rect����Ľ���
				invalidate(rect);
				
				touchedCell.touchPoint = new Point(x, y);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP :
			if (touchedCell != null) {
				// �Ƚ�ƴͼ�����Ͻ����λĿ��ľ��룬��С��10dip���Զ���������
				Point p1 = new Point(touchedCell.rect.left, 
									 touchedCell.rect.top);
				Point p2 = new Point(touchedCell.homeRect.left, 
									 touchedCell.homeRect.top);
				double d = Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + 
	 	 	 	 	 	 	 	 	 	(p1.y-p2.y)*(p1.y-p2.y));
				if (d <= dip2px(10)) {
					// ��λ
					touchedCell.fixed = true;
					touchedCell.rect = touchedCell.homeRect;
					// ������Ч
					playSound(SOUND_PT);
				}
			}			
			// �ػ���������
			drawPuzzle(backCanvas, null);
			invalidate();
			// �ÿ�touchedCell��������ǰƴͼ��Ĵ������ƶ�����
			touchedCell = null;
			break;
		}
		
		return super.onTouchEvent(event);
	}
	/**
	 * ��ʼ����Ϸ������ͼ������㡢ͼƬ��Դ���ء���̨����׼����
	 */
	private void initGame() {
		// ------------------------------------------------
		// ����ƴͼ���С��ƴͼ����
		// ˮƽ����[10] + 4pw + [10] + 1.5pw + [10] = screenW
		// ��ֱ����[20] + 3ph + [20] = screenH
		// ------------------------------------------------
		pw = (screenW - dip2px(10) - dip2px(10) - dip2px(10)) / 5.5;
		ph = (screenH - dip2px(20) - dip2px(20)) / 3.0;

		// ����ƴͼ��������ͼ���򡢴���ƴͼ�������
		puzzRect = new Rect(dip2px(10), 
							dip2px(20), 
							dip2px(10)+(int)(4*pw), 
							dip2px(20)+(int)(3*ph));
		thumbRect = new Rect(dip2px(10)+(int)(4*pw)+dip2px(10), 
							 dip2px(20), 
							 screenW-dip2px(10), 
							 (int)(dip2px(20)+ph));
		cellsRect = new Rect(dip2px(10)+(int)(4*pw)+dip2px(10), 
							 (int)(dip2px(20)+ph+dip2px(5)), 
						     (int)(screenW-dip2px(10)-pw), 
						     (int)(screenH-dip2px(20)-ph));
		// ���ر���ͼƬ������Ļ��С����
		background = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(),
						 R.drawable.wallpaper), screenW, screenH, false);
		// ����ƴͼͼƬ����ƴͼ��������
		puzzImage = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), 
	 	 	 	 	 	 R.drawable.pic02),	
					 	 puzzRect.width(), puzzRect.height(), false);
		// ������̨����ͼ�񣬲�����Ž���̨��������Ϊͼ�����ݱ���ͨ���������в���
		backDrawing = Bitmap.createBitmap(screenW, screenH, 
									Bitmap.Config.ARGB_8888);
		backCanvas = new Canvas(backDrawing);
	}
	/**
	 * ��ƴͼ��3x4�Ĵ�С�и��12��ƴͼ��
	 */
	private void makePuzzCells() {
		// ��ƴͼ��3x4��С�и����ƴͼ�鱣�浽puzzCells��̬������
		Set<Integer> zOrders = new HashSet<Integer>();
		Rect puzzR, destR;
		for (int i=0; i<3; i++) {
			for (int j=0; j<4; j++) {
				// �����(i,j)λ�õĸ�����ԭƴͼ�ж�Ӧ�ľ�������
				puzzR = new Rect((int)(j*pw), (int)(i*ph), 
		 		 		 		    (int)((j+1)*pw), (int)((i+1)*ph));
				// ����ƴͼ��ͼ�ڴ���������ʾ��λ�ã����Ͻ���cellsRect������
				destR = new Rect();
				destR.left = cellsRect.left + 
								(int)(Math.random()*cellsRect.width());
				destR.top = cellsRect.top + 
								(int)(Math.random()*cellsRect.height());
				destR.right = destR.left + (int)pw;
				destR.bottom = destR.top + (int)ph;
				
				// �������һ�����ظ���ƴͼ��ѵ���ʾ����
				int zOrder;
				do {
					zOrder = (int)(12 * Math.random());
				} while (zOrders.contains(zOrder));

				// ����zOrder����ʹƴͼ���zOrder���ظ�
				zOrders.add(zOrder);

				// ����PuzzleCell���󣬱���ƴͼ��ͼ����ʾ���򡢶ѵ�����
				PuzzleCell cell = new PuzzleCell();
				cell.image = Bitmap.createBitmap(puzzImage, puzzR.left,
							 	puzzR.top, puzzR.width(), puzzR.height());
				// ��¼ƴͼ��ͼ����
				cell.imgId = i*4+j;
				//
				cell.rect = destR;
				cell.zOrder = zOrder;
				// ȷ��ƴͼ��Ĺ�λ����
				puzzR.offset(dip2px(10), dip2px(20));
				cell.homeRect = puzzR;
				cell.fixed = false;		

				puzzCells.add(cell);
			}
		}
		// ����ƴͼ���zOrder������zOrder���ƴͼ������ǰ��
		sortPuzzCells();
	}
	/**
	 * �ӱ������Ϸ�����м���ƴͼ��
	 */
	private void loadPuzzCells() {
		int row, col;
		Rect puzzR;
		for (PuzzCellState one : cellStates) {
			// ����ͼ���ż���ԭƴͼ�ָ��е�����λ��
			row = one.imgId / 4;
			col = one.imgId % 4;
			// �����(row,col)λ�õĸ�����ԭƴͼ�ж�Ӧ�ľ�������
			puzzR = new Rect((int)(col*pw), (int)(row*ph), 
	 		 		 	     (int)((col+1)*pw), (int)((row+1)*ph));
			// ����PuzzleCell���󣬱���ƴͼ��ͼ����ʾ���򡢶ѵ�����
			PuzzleCell cell = new PuzzleCell();
			cell.image = Bitmap.createBitmap(puzzImage, puzzR.left,
				 				puzzR.top, puzzR.width(), puzzR.height());
			cell.imgId = one.imgId;
			cell.rect = new Rect(one.posx, one.posy, one.posx+(int)pw, one.posy+(int)ph);
			cell.zOrder = one.zOrder;
			cell.fixed = one.fixed;
			// ȷ��ƴͼ��Ĺ�λ����
			puzzR.offset(dip2px(10), dip2px(20));
			cell.homeRect = puzzR;
			puzzCells.add(cell);
		}
		// ����ƴͼ���zOrder������zOrder���ƴͼ������ǰ��
		sortPuzzCells();
	}
	/**
	 * ����ƴͼ����
	 * @param canvas ���ƽ���Ļ���
	 * @param ignoredCell ����ʱӦ���Ե�ƴͼ��
	 */
	private void drawPuzzle(Canvas canvas, PuzzleCell ignoredCell) {
		// ���Ʊ���ͼ
		canvas.drawBitmap(background, 0, 0, null);
		
		// ����ƴͼ��alphaֵ��ΧΪ0-255��0Ϊ��͸����255Ϊ��ȫ͸��
		Paint p = new Paint();
		p.setAlpha(120);
		canvas.drawBitmap(puzzImage, null, puzzRect, p);
		
		// ����ƴͼ����߿�
		canvas.drawRect(puzzRect, paint);
		
		// ����ˮƽ�����ߣ�3�У�
		canvas.drawLine(puzzRect.left, (int)ph+puzzRect.top, 
						puzzRect.right, (int)ph+puzzRect.top, paint);
		canvas.drawLine(puzzRect.left, (int)(ph*2)+puzzRect.top, 
						puzzRect.right, (int)(ph*2)+puzzRect.top, paint);
		// ���ƴ�ֱ�����ߣ�4�У�
		canvas.drawLine((int)pw+puzzRect.left, puzzRect.top, 
						(int)pw+puzzRect.left, puzzRect.bottom, paint);
		canvas.drawLine((int)(pw*2)+puzzRect.left, puzzRect.top, 
						(int)(pw*2)+puzzRect.left, puzzRect.bottom, paint);
		canvas.drawLine((int)(pw*3)+puzzRect.left, puzzRect.top, 
						(int)(pw*3)+puzzRect.left, puzzRect.bottom, paint);
		
		// ��������ͼ
		canvas.drawBitmap(puzzImage, null, thumbRect, null);
		// ��������ƴͼ��
		for (int i=puzzCells.size()-1; i>=0; i--) {
			PuzzleCell cell = puzzCells.get(i);
			if (cell == ignoredCell) continue;
			cell.draw(canvas);
		}
	}
	/**
	 * ��ȡ����ƴͼ����zOrder�����Ǹ�ֵ
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
	 * ����ƴͼ���zOrder���е�������
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
	 * ��dipת��Ϊ��ǰ�豸��pxֵ
	 */
	private int dip2px(float dip) {
		final float scale = 
			getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}
}
