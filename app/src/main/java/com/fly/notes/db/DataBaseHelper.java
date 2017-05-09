package com.fly.notes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by huangfei on 2016/11/14.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private String CREATE_NOTE_TABLE = "CREATE TABLE notes (_id LONG NOT NULL UNIQUE,title TEXT,summary TEXT,body TEXT,modifiedTime LONG,firstPicPath TEXT);";
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db,0,DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i=oldVersion+1;i<=newVersion;i++){
            upgradeTo(db,i);
        }
    }

    private void upgradeTo(SQLiteDatabase db,int version){
        switch (version){
            case 1:
                db.execSQL(CREATE_NOTE_TABLE);
                break;
            default:
                throw new IllegalStateException("Don't know how to upgrade to " + version);
        }
    }
}
