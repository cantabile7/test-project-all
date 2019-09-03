package com.example.dictionary;

public class Item {
    //Part,PartName,Chapter,ChapterName
    int Part;         //章
    String PartName;  //章名
    int Chapter;      //节
    String ChapterName;   //节名
    String Word;         //单词
    String WordTranslation;  //单词翻译
    String Example;          //单词例子
    String ExampleTranslation;  //例子翻译

    //主界面-Part   2个参数
    public Item(int Part, String PartName){
        this.Part=Part;
        this.PartName=PartName;
    }

    //次界面-Chapter   3个参数
    public Item(int Part, String PartName, int Chapter, String ChapterNmae){
        this.Part=Part;
        this.PartName = PartName;
        this.Chapter=Chapter;
        this.ChapterName=ChapterNmae;
    }

    //次次界面-Word    3个参数
    public Item(int Chapter, String Word, String WordTranslation){
        //this.Part = Part;
        this.Chapter=Chapter;
        this.Word=Word;
        this.WordTranslation=WordTranslation;
    }

    //最后界面-example 4个参数
    public Item(String Word, String WordTranslation, String Example, String ExampleTranslation){
        this.Word = Word;
        this.WordTranslation = WordTranslation;
        this.Example = Example;
        this.ExampleTranslation = ExampleTranslation;
    }

    public int getPart(){
        return Part;
    }
    public String getPartName(){
        return PartName;
    }

    public int getChapter(){
        return Chapter;
    }
    public String getChapterName(){
        return ChapterName;
    }

    public String getWord(){
        return Word;
    }
    public String getWordTranslation(){
        return WordTranslation;
    }

    public String getExample(){
        return Example;
    }
    public String getExampleTranslation(){
        return ExampleTranslation;
    }
}
