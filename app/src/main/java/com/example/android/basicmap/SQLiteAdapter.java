package com.example.android.basicmap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAdapter
{
    //声明数据库的基本信息
    private static final int DB_VERSION=1;
    private static final String TABLE_NAME="users"; //记录用户信息的表名
    private static final String _ID="_id"; //保存ID值
    private static final String NAME="name"; //用户名
    private static final String PWD="pwd"; //密码

    //声明操作Sqlite数据库的实例
    private SQLiteDatabase sqliteDb;
    private DBOpenHelper sqliteHelper;

    //构造方法
    public SQLiteAdapter(Context context,String dbname)
    {
        sqliteHelper=new DBOpenHelper(context,dbname,null,DB_VERSION);
        sqliteDb=sqliteHelper.getWritableDatabase(); //获得可写的数据库
    }

    //自定义的帮助类
    private static class DBOpenHelper extends SQLiteOpenHelper
    {
        public DBOpenHelper(Context context,String dbname,SQLiteDatabase.CursorFactory factory,int version)
        {
            super(context,dbname,factory,version);
        }

        private static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME
                +" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +NAME+" TEXT NOT NULL,"
                +PWD+" TEXT NOT NULL );"; //预定义创建表的SQL语句

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE); //创建表
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME); //删除旧表
            onCreate(db); //创建新表
        }
    }
    //获取SQLite数据库实例
    public SQLiteDatabase getSqliteDb()
    {
        return sqliteDb;
    }
}
