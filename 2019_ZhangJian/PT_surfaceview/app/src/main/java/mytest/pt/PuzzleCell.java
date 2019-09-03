package mytest.pt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class PuzzleCell {
	public Bitmap image;		// ƴͼ���Ӧ��Сͼ
	public int imgId;			// ƴͼ���Ӧ��Сͼ���
	public Rect rect;			// ƴͼ������Ļ�ϵ���ʾ����
	public int zOrder;			// ƴͼ��ѵ���ʾ�����´���

	public Point touchPoint;	// ƴͼ�鱻�������ƶ�ʱ������λ��
	public Rect homeRect;		// ƴͼ���λĿ������
	public boolean fixed;		// ƴͼ���Ƿ��ѱ���λ�̶�����λ��ƴͼ�鲻���ƶ�

	/**
	 * ����ǰƴͼ����Ƴ��� 
	 * @param canvas ��������ƴͼ��Ļ�����ƴͼ�齫��canvas����ʾ 
	 */
	public void draw(Canvas canvas) {
		canvas.drawBitmap(image, null, rect, null);
	}
	/**
	 * �жϵ�ǰƴͼ���Ƿ񱻴�����
	 * @param x ��ǰ����λ�õ�x������
	 * @param y ��ǰ����λ�õ�y������
	 * @return �����������ƴͼ�������ڣ�����true�����򷵻�false
	 */
	public boolean isTouched(int x, int y) {
		return rect.contains(x, y);
	}
	/**
	 * ����ǰƴͼ���ƶ�����λ�ã�ƫ������һ�δ���λ���뵱ǰ����λ�õľ���
	 * @param x ��λ�õ�x������
	 * @param y ��λ�õ�y������
	 */
	public void moveTo(int x, int y) {
		rect.offset(x - touchPoint.x, y - touchPoint.y);
	}
}
