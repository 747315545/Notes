package com.zui.notes.model;

import java.io.Serializable;

public class NoteInfo
        implements Serializable {
    public long _id;
    public String body;
    public String firstPicPath;
    public long modifiedTime;
    public String title;
    public String summary;

    public NoteInfo() {
    }
}
