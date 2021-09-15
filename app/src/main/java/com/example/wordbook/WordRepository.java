package com.example.wordbook;//数据仓库，用来做数据的获取，用于管理数据源

//多线程调用WordDao的方法完成增删改查
//建立仓库类Repository将ViewModel中的数据直接操作的信息提取出来
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
//创建一个公共类WordRepository
//添加两个成员变量
public class WordRepository {
    private LiveData<List<Word>> allWordsLive;

    private WordDao wordDao;//用来调用数据库操作

   // 添加一个构造函数，该构造函数获取数据库的句柄并初始化成员变量。
    public WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        wordDao = wordDatabase. getWordDao();
        allWordsLive = wordDao.getAllWordsLive();
    }

    //多线程调用WordDao的方法完成增删改查

    //为getAllWords()添加一个包装器。Room在单独的线程上执行所有查询。观察到LiveData数据更改时，将通知观察者
    public LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }
    public LiveData<List<Word>> findWordsWithPattern(String patten){
        //通配符保证模糊匹配,通配符%
        return wordDao.findWordsWithPattern("%" + patten + "%");
    }


    //为insert()方法添加一个包装器。使用AsyncTask来执行，确保其是在非UI线程中执行
    //写四个接口来使用这几个类
    void insertWords(Word... words){
        new InsertAsyncTask(wordDao).execute(words);
    }
    void updateWords(Word... words){
        new UpdateAsyncTask(wordDao).execute(words);
    }
    void deleteWords(Word... words){
        new DeleteAsyncTask(wordDao).execute(words);
    }
    void deleteAllWords(Word... words){
        new DeleteAllAsyncTask(wordDao).execute();
    }


    //主要功能是实现多线程
    //InsertAsyncTask的实现,借助AsyncTask将数据库脱离主线程并放入正确的工作线程中：
    //AsyncTsak为抽象类，每个操作都要生成单独的子类
    static class InsertAsyncTask extends AsyncTask<Word,Void,Void> {
        private WordDao wordDao;
        public InsertAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }
        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        public UpdateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        public DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private WordDao wordDao;

        public DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}
