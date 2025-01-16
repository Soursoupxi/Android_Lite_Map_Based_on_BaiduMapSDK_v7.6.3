package com.example.android.basicmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable
{
    protected int _id; //保存用户的ID（若计划使用ContentProvider来共享表，则必须具有唯一ID字段
    private String name;
    private String pwd;

    //声明一个SQLiteAdapter对象作为访问SQLite数据库中介
    private SQLiteAdapter sqliteAdapter=null;
    private SQLiteDatabase sqliteDb=null; //数据库实例
    private static final String SQLITE_NAME="SqliteUserInfo"; //数据库名称
    private static final String TABLE_NAME="users"; //记录用户信息的表名

    public User(){}
    public User(String name,String pwd)
    {
        this.name=name;
        this.pwd=pwd;
    }

    public Long saveUser(Context context)
    {
        sqliteAdapter=new SQLiteAdapter(context,SQLITE_NAME); //创建SQLiteAdapter对象
        sqliteDb=sqliteAdapter.getSqliteDb(); //得到SQLite实例
        ContentValues values=new ContentValues(); //构造ContentValues实例
        //保存数据
        values.put("name",name);
        values.put("pwd",pwd);
        Long id=sqliteDb.insert(TABLE_NAME,null,values);
        return id;
    }

    public void getUserData(Context context)
    { //获取注册的用户信息
        sqliteAdapter=new SQLiteAdapter(context,SQLITE_NAME); //创建SQLiteAdapter对象
        sqliteDb=sqliteAdapter.getSqliteDb(); //得到SQLite实例
        Cursor cursor=sqliteDb.query(TABLE_NAME,new String[]{"_id","name","pwd"},null,null,null,null,null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            _id=cursor.getInt(0);
            name=cursor.getString(1);
            pwd=cursor.getString(2);
        }
        cursor.close();
    }
}






