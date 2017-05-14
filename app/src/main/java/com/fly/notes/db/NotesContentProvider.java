package com.fly.notes.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by huangfei on 2016/11/11.
 */

public class NotesContentProvider extends ContentProvider {
    private final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final static int CRUD = 1;
    private final static int DELETEITEM = 2;
    private final static String NOTESTABLENAME = "notes";
    private final static String NOTESCHANGETABLENAME = "notesChange";
    SQLiteDatabase db;

    static {
        uriMatcher.addURI("com.fly.notes", "notes", CRUD);
        uriMatcher.addURI("com.fly.notes", "notes_id", DELETEITEM);
    }

    @Override
    public boolean onCreate() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        db = dataBaseHelper.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case CRUD:
                Cursor cursor = db.query(NOTESTABLENAME, projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CRUD:
                long result = db.insert(NOTESTABLENAME, null, values);
                long id = values.getAsLong(NoteInfoColumns._ID);
                insertToChange(id, NoteChangeType.ADD);
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
                        db.delete(NOTESTABLENAME, selection, new String[]{a});
                        insertToChange(Long.parseLong(a), NoteChangeType.DELETE);
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
                long result = db.delete(NOTESTABLENAME, selection, selectionArgs);
                insertToChange(Long.parseLong(selectionArgs[0]), NoteChangeType.DELETE);
                if (result == -1) {
                    this.getContext().getContentResolver().notifyChange(Uri.parse("content://com.fly.notes/notes"), null);
                }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CRUD:
                long result = db.update(NOTESTABLENAME, values, selection, selectionArgs);
                long id = Long.parseLong(selectionArgs[0]);
                insertToChange(id, NoteChangeType.UPDATE);
                if (result != -1) {
                    this.getContext().getContentResolver().notifyChange(uri, null);
                    return 1;
                }
                break;
        }
        return 0;
    }

    public void insertToChange(long id, int type) {
        Log.d("huangfei insertToChang:", "type:" + type);
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteInfoColumns._ID, id);
        contentValues.put(NoteInfoColumns.CHANGETYPE, type);
        long result = db.insert(NOTESCHANGETABLENAME, null, contentValues);
        if (result == -1) {
            result = db.update(NOTESCHANGETABLENAME, contentValues, "id=?", new String[]{id + ""});
        }
        Log.d("huangfei insertToChang:", "type:" + type + "  result: " + result);
    }
}
