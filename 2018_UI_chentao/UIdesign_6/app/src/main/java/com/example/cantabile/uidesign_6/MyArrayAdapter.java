package com.example.cantabile.uidesign_6;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter {
    private Activity mContext=null;
    private int mResourceId;
    private String[] mItems;

    public MyArrayAdapter(Activity context, int resId, String[] items){
        super(context,resId,items);
        mContext=context;
        mResourceId=resId;
        mItems=items;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //获取layoutInflater对象
        LayoutInflater inflater = mContext.getLayoutInflater();
        //装载列表项视图
        View itemView = inflater.inflate(mResourceId,null);
        //获取列表项组件
        TextView contentTv = (TextView) itemView.findViewById(R.id.tv);
        ImageView letterImg = (ImageView) itemView.findViewById(R.id.img);
        //取出需要显示的数据
        String content = mItems[position].trim();

        //给textview设置显示值
        contentTv.setText(content);
        letterImg.setImageResource(R.drawable.beiwei);

        return itemView;
    }

}
