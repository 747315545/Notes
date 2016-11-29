package com.zui.notes.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by huangfei on 2016/11/11.
 */

public class NotesContentProvider extends ContentProvider {
    private final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final static int CRUD = 1;
    private final static int DELETEITEM = 2;
    private final static String TABLENAME = "notes";
    SQLiteDatabase db;

    static {
        uriMatcher.addURI("com.zui.notes", "notes", CRUD);
        uriMatcher.addURI("com.zui.notes","notes_id",DELETEITEM);
    }

    @Override
    public boolean onCreate() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        db = dataBaseHelper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case CRUD:
                Cursor cursor = db.query(TABLENAME, projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CRUD:
                long result = db.insert(TABLENAME, null, values);
                if (result != -1) {
                    this.getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CRUD:
                boolean bool = false;
                db.beginTransaction();
                try {
                    for (String a : selectionArgs) {
                        db.delete(TABLENAME, selection, new String[]{a});
                    }
                    db.setTransactionSuccessful();
                    bool = true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                if (bool) {
                    this.getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case DELETEITEM:
                long result = db.delete(TABLENAME,selection,selectionArgs);
                if(result==-1){
                    this.getContext().getContentResolver().notifyChange(Uri.parse("content://com.zui.notes/notes"),null);
                }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CRUD:
                long result = db.update(TABLENAME, values, selection, selectionArgs);
                if (result != -1) {
                    this.getContext().getContentResolver().notifyChange(uri, null);
                    return 1;
                }
                break;
        }
        return 0;
    }
}
