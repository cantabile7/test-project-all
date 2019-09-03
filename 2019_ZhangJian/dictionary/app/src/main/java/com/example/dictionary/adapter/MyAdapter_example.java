package com.example.dictionary.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionary.Item;
import com.example.dictionary.R;

import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter_example extends ArrayAdapter<Item> {
    ArrayList<Item> WordList = new ArrayList<>();

    public MyAdapter_example(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        WordList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //从table_example表中读取 word 对应的翻译

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_example, null);
        final TextView tv_word = (TextView) v.findViewById(R.id._word);   //单词
        TextView tv_wordTranslation = (TextView) v.findViewById(R.id._wordTranslation);   //单词翻译
        TextView tv_example = (TextView) v.findViewById(R.id._example);               //例子
        TextView tv_exampleTranslation = (TextView) v.findViewById(R.id._exampleTranslation);   //例子翻译

        final String word = WordList.get(position).getWord();
        final String example = WordList.get(position).getExample();

        //声明单词音频播放器
        final MediaPlayer mMediaPlayer = MediaPlayer.create(getContext(),
                Uri.parse("android.resource://com.example.dictionary/raw/"+word));

        //声明例子音频播放器
        final MediaPlayer examplePlayer = MediaPlayer.create(getContext(),
                Uri.parse("android.resource://com.example.dictionary/raw/"+word+"_exp"));

        //备胎单词播放器
        final MediaPlayer newMedia = new MediaPlayer();
        try {
            newMedia.setDataSource("http://dict.youdao.com/dictvoice?audio="+word);
            newMedia.prepareAsync();
        } catch (IOException e) {
            Toast.makeText(getContext(),"暂无该单词音频，且获取网络资源失败。",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //备胎句子播放器
        final MediaPlayer newMeidaExample = new MediaPlayer();
        try {
            newMeidaExample.setDataSource("http://dict.youdao.com/dictvoice?audio="+example);
            newMeidaExample.prepareAsync();
        } catch (IOException e) {
            Toast.makeText(getContext(),"暂无该例句音频，且获取网络资源失败。",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        //给文本框等等赋值
        ImageButton imageButton = (ImageButton) v.findViewById(R.id._sound);  //点击播放单词音效
        final ImageButton exampleSound = (ImageButton) v.findViewById(R.id._exampleSound);  //点击播放音效

        tv_word.setText(WordList.get(position).getWord());
        tv_wordTranslation.setText(WordList.get(position).getWordTranslation());
        tv_example.setText(WordList.get(position).getExample());
        tv_exampleTranslation.setText(WordList.get(position).getExampleTranslation());

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击播放单词音源
                //音源文件命名： 以单词命名
                if(mMediaPlayer!=null){
                    mMediaPlayer.start();
                }
                else{
                    //从网络中播放对应单词读音
                    newMedia.start();
                }
            }
        });

        exampleSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击播放单词音源
                //音源文件命名： 以单词命名
                if(examplePlayer!=null){
                    examplePlayer.start();
                }
                else{
                    //Toast.makeText(getContext(),"暂无该例句音频资源",Toast.LENGTH_SHORT).show();
                    newMeidaExample.start();
                }

            }
        });
        return v;
    }

}
