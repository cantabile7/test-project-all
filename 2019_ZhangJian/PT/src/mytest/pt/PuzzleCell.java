package mytest.pt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class PuzzleCell {
	public Bitmap image;		// 拼图块对应的小图
	public int imgId;			// 拼图块对应的小图编号
	public Rect rect;			// 拼图块在屏幕上的显示区域
	public int zOrder;			// 拼图块堆叠显示的上下次序

	public Point touchPoint;	// 拼图块被触摸或移动时的坐标位置
	public Rect homeRect;		// 拼图块归位目标区域
	public boolean fixed;		// 拼图块是否已被归位固定。归位的拼图块不能移动

	/**
	 * 将当前拼图块绘制出来 
	 * @param canvas 用来绘制拼图块的画布，拼图块将在canvas上显示 
	 */
	public void draw(Canvas canvas) {
		canvas.drawBitmap(image, null, rect, null);
	}
	/**
	 * 判断当前拼图块是否被触摸到
	 * @param x 当前触摸位置的x横坐标
	 * @param y 当前触摸位置的y纵坐标
	 * @return 如果触摸点在拼图块区域内，返回true，否则返回false
	 */
	public boolean isTouched(int x, int y) {
		return rect.contains(x, y);
	}
	/**
	 * 将当前拼图块移动到新位置，偏移是上一次触摸位置与当前触摸位置的距离
	 * @param x 新位置的x横坐标
	 * @param y 新位置的y纵坐标
	 */
	public void moveTo(int x, int y) {
		rect.offset(x - touchPoint.x, y - touchPoint.y);
	}
}
