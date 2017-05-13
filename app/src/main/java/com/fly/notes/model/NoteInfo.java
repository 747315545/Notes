package com.fly.notes.model;

import android.content.ContentValues;
import com.fly.notes.db.NoteInfoColumns;
import com.fly.notes.db.NotesUser;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by huangfei on 2016/11/9.
 */
public class NoteInfo extends BmobObject {
    public long id;
    public String body;
    public String firstPicPath;
    public long modifiedTime;
    public String title;
    public String summary;
    public NotesUser author;
    public List<String> imageList;
    public List<String> imageUrls;
    public NoteInfo() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFirstPicPath() {
        return firstPicPath;
    }

    public void setFirstPicPath(String firstPicPath) {
        this.firstPicPath = firstPicPath;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public NotesUser getAuthor() {
        return author;
    }

    public void setAuthor(NotesUser author) {
        this.author = author;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteInfoColumns._ID, id);
        contentValues.put(NoteInfoColumns.MODIFIED_TIME, modifiedTime);
        contentValues.put(NoteInfoColumns.BODY, body);
        contentValues.put(NoteInfoColumns.TITLE, title);
        contentValues.put(NoteInfoColumns.SUMMARY, summary);
        contentValues.put(NoteInfoColumns.FIRST_PIC_PATH, firstPicPath);
        return contentValues;
    }

}
