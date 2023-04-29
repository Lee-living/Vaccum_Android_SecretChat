package com.lee.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.lee.domain.ReceiveMsg;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "my_database.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // 创建好友公钥表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS friend_keys ("
                + "user TEXT PRIMARY KEY,"
                + "public_key TEXT"
                + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库
    }

    // 插入或更新好友公钥
    public void insertOrUpdateFriendKey(String user, String publicKey) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM friend_keys WHERE user = ?", new String[]{user});

        if (cursor.getCount() > 0) {
            db.execSQL("UPDATE friend_keys SET public_key = ? WHERE user = ?", new String[]{publicKey, user});
        } else {
            db.execSQL("INSERT INTO friend_keys (user, public_key) VALUES (?, ?)", new String[]{user, publicKey});
        }

        cursor.close();
        db.close();
    }

    // 获取好友公钥
    @SuppressLint("Range")
    public String getFriendPublicKey(String user) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM friend_keys WHERE user = ?", new String[]{user});
        String publicKey = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            publicKey = cursor.getString(cursor.getColumnIndex("public_key"));
        }
        cursor.close();
        db.close();
        return publicKey;
    }


    // 创建好友聊天记录表
    public void createFriendMessagesTable(String tableName) {
        String tableFullName = "user" + tableName;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableFullName + " ("
                + "flag INTEGER,"
                + "message TEXT"
                + ")");
        db.close();
    }

    // 插入聊天记录
    public void insertMessage(String tableName, int flag, String message) {
        String tableFullName = "user" + tableName;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + tableFullName + " ( flag, message) VALUES (?, ?)", new String[]{ String.valueOf(flag), message});
        db.close();
    }

    // 获取聊天记录
    @SuppressLint("Range")
    public List<ReceiveMsg> getFriendMessages(String tableName) {
        List<ReceiveMsg> messages = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String tableFullName = "user" + tableName;
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableFullName, null);
        while (cursor.moveToNext()) {
            String message = cursor.getString(cursor.getColumnIndex("message"));
             int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            ReceiveMsg receiveMsg = new ReceiveMsg(message, flag == 1);
            messages.add(receiveMsg);
        }
        cursor.close();
        db.close();
        return messages;
    }

    /*public Cursor getMessages(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        return cursor;
    }*/

    // 清空聊天记录
    public void clearMessages(String tableName) {
        String tableFullName = "user" + tableName;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tableFullName);
        db.close();
    }

}
