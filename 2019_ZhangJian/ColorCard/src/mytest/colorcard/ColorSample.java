package mytest.colorcard;

import android.graphics.Color;

public class ColorSample {
	public final String rgb; // 颜色RGB 串(格式：#FF00FF)
	public final String name; // 颜色名
	public final String category; // 所属类别
	public final int val; // RGB 颜色对应的整数值
	public final int r;// RGB 空间色彩分量值
	public final int g;
	public final int b;
	public final float h;// HSV 空间色彩分量值
	public final float s;
	public final float v;

	public ColorSample(String rgb, String name, String category) {
		this.rgb = rgb;
		this.name = name;
		this.category = category;
		// 转换颜色RGB 字符串为整数表示
		val = Color.parseColor(rgb);
		r = Color.red(val);
		g = Color.green(val);
		b = Color.blue(val);
		// 转换RGB 颜色为HSV 颜色
		float[] hsv = new float[3];
		Color.RGBToHSV(r, g, b, hsv);
		h = hsv[0];
		s = hsv[1];
		v = hsv[2];
	}

	/**
	 * 计算HSV 颜色空间中两个颜色的色差值
	 */
	public static double distHSV(double h1 , double s1, double v1, double h2, double s2, double v2) {
		return Math.sqrt((h1-h2)*(h1-h2) + (s1-s2)*(s1-s2) + (v1-v2)*(v1-v2));
}

	/**
	 * 计算RGB 颜色空间中两个颜色的色差值
	 */
	public static double distRGB(int r1, int g1, int b1, int r2, int g2, int b2) {
		return Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2)
				+ (b1 - b2) * (b1 - b2));
	}
	
}
