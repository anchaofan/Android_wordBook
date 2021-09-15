package com.example.wordbook;
//ViewModel的作用是向UI提供数据，并保存配置更改。充当Repository和UI之间的通信中心。


//通过调用word repository类的方法完成增删改查
import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;



//创建WordViewModel类，使其继承AndroidViewModel
public class WordViewModel extends AndroidViewModel {
    // ViewModel 没有很好的方法来获得一个Context 需要继承AndroidViewModel
    private WordRepository wordRepository;//添加一个私有成员变量来保存对存储库的引用

    //添加一个构造函数，该构造函数获取对存储库的引用，并从存储库获取单词列表
    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }


//主要功能是处理数据相关功能，返回的LiveData可以监控数据变化，并再变化时调用相应方法来处理视图等。

    //添加一个构造函数，该构造函数获取对存储库的引用，并从存储库获取单词列表
    public LiveData<List<Word>> getAllWordsLive() {
        return wordRepository.getAllWordsLive();
    }
    public LiveData<List<Word>> findWordsWithPattern(String patten){
        return wordRepository.findWordsWithPattern(patten);
    }

//通过调用WordRepository类的方法完成增删改查

    //添加一个构造函数，该构造函数获取对存储库的引用，并从存储库获取单词列表
    void insertWords(Word... words){
        wordRepository.insertWords(words);
    }
    void updateWords(Word... words){
        wordRepository.updateWords(words);
    }
    void deleteWords(Word... words){
        wordRepository.deleteWords(words);
    }
    void deleteAllWords(){
        wordRepository.deleteAllWords();
    }



}
