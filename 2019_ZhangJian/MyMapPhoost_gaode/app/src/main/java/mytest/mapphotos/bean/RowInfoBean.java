package mytest.mapphotos.bean;

import android.graphics.drawable.Drawable;

public class RowInfoBean {
	public int id;		   // 相册id
	public Drawable thumb; // 相册图标
	public String title;   // 相册标题

	public String description;  //相册描述

	public RowInfoBean(Drawable thumb, String title) {
		this.thumb = thumb;
		this.title = title;
	}
	public RowInfoBean() {
		// do nothing
	}
}
