package com.example.android.basicmap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SQLiteProvider extends ContentProvider
{
    //声明一个SQLiteAdapter对象作为访问SQLite数据库的中介
    private SQLiteAdapter sqliteAdapter;
    private SQLiteDatabase sqliteDb; //数据库实例
    private static final String SQLITE_NAME="SqliteUserInfo"; //数据库名称
    private static final String TABLE_NAME="users"; //记录用户信息的表名

    private static final int USERS=1;
    private static final int USER=2;
    private static final UriMatcher MATCHER;

    static
    {
        MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI("com.example.android.basicmap.SQLiteProvider","users",USERS); //不带主键编号的URI
        MATCHER.addURI("com.example.android.basicmap.SQLiteProvider","users/#",USER); //带主键编号的URI
    }

    @Override
    public boolean onCreate()
    {
        sqliteAdapter=new SQLiteAdapter(getContext(),SQLITE_NAME);
        sqliteDb=sqliteAdapter.getSqliteDb();
        if(sqliteDb==null)
        {
            System.err.println("sqliteDb实例为空");
            return false;
        }
        else
        {
            return true;
        }
    }

    /*
    返回当前URI所代表数据的MIME类型。
    若操作的数据属于集合类型，则MIME类型字符串应以vnd.android.cursor.dir/开头
    若操作的数据属于非集合类型，则MIME类型字符串应以vnd.android.cursor.item/开头
     */
    @Override
    public String getType(Uri uri)
    {
        switch(MATCHER.match(uri))
        {
            case USERS:
                return "vnd.android.cursor.dir/vnd.example.users";
            case USER:
                return "vnd.android.cursor.item/vnd.example.users";
            default:
                throw new IllegalArgumentException("Failed to getType:"+uri.toString());
        }
    }

    //供外部应用从ContentProvider添加数据
    @Override
    public Uri insert(Uri uri,ContentValues values)
    {
        switch(MATCHER.match(uri))
        {
            case USERS:
                Long userId=sqliteDb.insert(TABLE_NAME,null,values);
                Uri insertUri=ContentUris.withAppendedId(uri,userId);
                getContext().getContentResolver().notifyChange(insertUri,null);
                return Uri.parse(userId.toString());
            default:
                throw new IllegalArgumentException("Failed to insert:"+uri.toString());
        }
    }

    //供外部应用从ContentProvider删除数据
    @Override
    public int delete(Uri uri,String selection,String[] selectionArgs)
    {
        int count=0;
        switch(MATCHER.match(uri))
        {
            case USERS:
                count=sqliteDb.delete(TABLE_NAME,selection,selectionArgs);
                break;
            case USER:
                String segment=uri.getPathSegments().get(1);
                count=sqliteDb.delete(TABLE_NAME,"_id="+segment,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed to delete:"+uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    //供外部应用更新ContentProvider中的数据
    @Override
    public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs)
    {
        int count=0;
        switch(MATCHER.match(uri))
        {
            case USERS:
                count=sqliteDb.update(TABLE_NAME,values,selection,selectionArgs);
                break;
            case USER:
                String segment=uri.getPathSegments().get(1);
                count=sqliteDb.update(TABLE_NAME,values,"_id="+segment,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed to update:"+uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    //供外部应用从ContentProvider中获取数据
    @Override
    public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder)
    {
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        switch(MATCHER.match(uri))
        {
            case USER:
                qb.appendWhere("_id="+uri.getPathSegments().get(1));
                break;
            default:
                break;
        }
        Cursor cursor=qb.query(sqliteDb,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
}








