package com.example.wordbook;//数据访问对象。SQL查询到方法的映射。

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//dao层用注解定义了增删改查方法,它是一个接口，里面主要是对数据的操作方法
//对于复杂查询，再注解中定义了sql语句：
@Dao
public interface WordDao {
    @Insert //增加
        //void insertWords(Word word);传入一个参数
    void insertWords(Word... words);//传入多个参数
    @Update
    void updateWords(Word... words);
    @Delete
    void deleteWords(Word... words);

//LiveData是 lifecycle library 中，用于数据观察的类。
// 在你的方法中使用LiveData为返回值。这样Room将会为你生成所有必须的代码，当数据库更新时，自动去更新LiveData

    //改变deleteAllWords()方法的返回值
    @Query("delete from word")
    void deleteAllWords();
//改变getAllWords()方法的返回值
    @Query("select * from word order by id desc")
    LiveData<List<Word>> getAllWordsLive();

    //模糊查询并按倒叙排序，按照英文查询
    @Query("select * from word where english_word like :patten order by id desc ")
    LiveData<List<Word>> findWordsWithPattern(String patten);

}


//后面会在MainActivity的onCreate()方法中创建一个Observer对象，并覆盖其onChanged()方法。
// 当LiveData改变时，观察者会被通知然后onChanged()会被回调。这时你可以更新适配器中的缓存数据，然后在适配器中更新UI。