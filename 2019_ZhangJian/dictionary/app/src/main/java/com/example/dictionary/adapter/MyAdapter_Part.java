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

public class MyAdapter_Part extends ArrayAdapter<Item> {

    ArrayList<Item> PartAndChapterList = new ArrayList<>();

    public MyAdapter_Part(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        PartAndChapterList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_items, null);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        //ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        textView.setText(PartAndChapterList.get(position).getPartName());
        //imageView.setImageResource(WordList.get(position).getSkillImage());
        return v;

    }

}
