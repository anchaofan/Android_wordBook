package com.example.wordbook;

//Room是在SQLite之上的数据库层。Room用于处理我们曾经用SQLiteOpenHelper来处理任务
//创建抽象类Database继承于RoomDatabase并声明为Abstract——WordDatabase
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//1、创建继承RoomDatabase的抽象类。
//2、在继承的类前使用注解@Database。
//3、申明数据库结构的Entity，并且设置数据库的版本号。




//其中@Database是声明，里面的entities是对应的Entity，如果有多个的话，使用如下方式：entities={StudentEntity.clss,ClassEntity.class}，version是数据库版本，
//exportSchema：存储展示数据库的结构信息，如果不设置的话，需要再database类上配置exportSchema = false，要不然编译的时候会出现警告。

@Database(entities = {Word.class}, version = 5,  exportSchema = false)


//利用单例工厂模式，new了一个WordDataBase类，这个类继承了RoomDatabase，建立了数据库，用来通过getDatabase来实例化WordDao
//用一个继承了RoomDatabase的抽象类WordDatabase来获得Dao中的方法



//singleton 单例模式,保证只有一个实例
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;
    static synchronized WordDatabase getDatabase(Context context){
        if(INSTANCE == null ){
            //添加到migration列表
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"words_database")
                    .addMigrations(MIGRATION_4_5)//指定版本升级时的升级策略
                    .build();
        }
        return INSTANCE;
    }


//数据库版本迁移


    public abstract WordDao getWordDao();
//添加一个version 2到 version 3的Migration
    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table word add column bar_data integer not null default 1");
        }
    };

    //使用Room，Migration的实现是这样的：
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("create table word_temp (id integer primary key not null, english_word text, " +
                    "chinese_meaning text)");// 创建临时表
            database.execSQL("insert into word_temp (id,english_word,chinese_meaning) " +
                    "select id, word, chinese_meaning from word");//  拷贝数据
            database.execSQL("drop table word");// 删除老的表
            database.execSQL("alter table word_temp rename to word");//改名

        }
    };


    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table word add column chinese_invisible integer not null default 0");
        }
    };

}
//Static 可以不需要new 就可以调用方法
//Synchronized同步块只有一个再执行，反正多线程而new了多个实例
//此方法构建并返回一个wordDao。同此此方法中还包含数据库的升级及修改方法