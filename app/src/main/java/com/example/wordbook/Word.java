package com.example.wordbook;
//首先创建一个Word类，并为其创建构造函数与必要的get方法。这样Room才可以实例化对象

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
//创建一个Entity的Java类
//个这个类定义了单词条目的属性：id word chineseWord chineseInvisible 并同时映射到数据库对应的属性字段；


@Entity//标识
public class Word {
    @PrimaryKey(autoGenerate = true)
    //设置常数参量,每一个Entity至少定义一个主键
    private int id;

     @ColumnInfo(name = "english_word")//定义数据表中的字段名
    private String word;
    @ColumnInfo(name = "chinese_meaning")
    private String chineseMeaning;
    @ColumnInfo(name = "chinese_invisible")
    private boolean chineseInvisible;


    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }


    //定义了set/get方法用于存取数据
    public boolean isChineseInvisible() {
        return chineseInvisible;
    }

    public void setChineseInvisible(boolean chineseInvisible) {
        this.chineseInvisible = chineseInvisible;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}
