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

public class MyAdapter_Word extends ArrayAdapter<Item> {
    ArrayList<Item> WordList = new ArrayList<>();

    public MyAdapter_Word(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        WordList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_word, null);
        TextView textView = (TextView) v.findViewById(R.id.tv_word);
        //ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        textView.setText(WordList.get(position).getWord());
        //imageView.setImageResource(WordList.get(position).getSkillImage());
        return v;

    }
}
