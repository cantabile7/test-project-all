package com.example.dictionary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dictionary.Item;
import com.example.dictionary.R;

import java.util.ArrayList;

public class MyAdapter_Chapter extends ArrayAdapter<Item>{
    ArrayList<Item> ChapterList = new ArrayList<>();

    public MyAdapter_Chapter(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        ChapterList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_chapter, null);
        TextView chapterName = (TextView) v.findViewById(R.id.tv_chapter);
        chapterName.setText(ChapterList.get(position).getChapterName());

//        TextView partName = (TextView) v.findViewById(R.id.tv_chapter);
//        partName.setText(ChapterList.get(position).getPartName());
        return v;

    }
}
