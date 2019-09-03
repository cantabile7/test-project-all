package mytest.colorcard;

import android.graphics.Color;

public class ColorSample {
	public final String rgb; // ��ɫRGB ��(��ʽ��#FF00FF)
	public final String name; // ��ɫ��
	public final String category; // �������
	public final int val; // RGB ��ɫ��Ӧ������ֵ
	public final int r;// RGB �ռ�ɫ�ʷ���ֵ
	public final int g;
	public final int b;
	public final float h;// HSV �ռ�ɫ�ʷ���ֵ
	public final float s;
	public final float v;

	public ColorSample(String rgb, String name, String category) {
		this.rgb = rgb;
		this.name = name;
		this.category = category;
		// ת����ɫRGB �ַ���Ϊ������ʾ
		val = Color.parseColor(rgb);
		r = Color.red(val);
		g = Color.green(val);
		b = Color.blue(val);
		// ת��RGB ��ɫΪHSV ��ɫ
		float[] hsv = new float[3];
		Color.RGBToHSV(r, g, b, hsv);
		h = hsv[0];
		s = hsv[1];
		v = hsv[2];
	}

	/**
	 * ����HSV ��ɫ�ռ���������ɫ��ɫ��ֵ
	 */
	public static double distHSV(double h1 , double s1, double v1, double h2, double s2, double v2) {
		return Math.sqrt((h1-h2)*(h1-h2) + (s1-s2)*(s1-s2) + (v1-v2)*(v1-v2));
}

	/**
	 * ����RGB ��ɫ�ռ���������ɫ��ɫ��ֵ
	 */
	public static double distRGB(int r1, int g1, int b1, int r2, int g2, int b2) {
		return Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2)
				+ (b1 - b2) * (b1 - b2));
	}
	
}
