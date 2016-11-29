package com.zui.notes;

import android.app.Application;

/**
 * Created by huangfei on 2016/11/17.
 */

public class NotesApplication extends Application {
    private static NotesApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static NotesApplication getInstance() {
        return mInstance;
    }
}
