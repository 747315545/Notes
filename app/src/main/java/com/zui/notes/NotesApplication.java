package com.zui.notes;

import android.app.Application;

import com.zui.notes.db.Config;
import com.zui.notes.model.Setting;

import cn.bmob.v3.Bmob;

/**
 * Created by huangfei on 2016/11/17.
 */

public class NotesApplication extends Application {
    private static NotesApplication mInstance;
    private static final String ApplicationID = "f7df8e86df28f29176bc3888d9419879";
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Config.init(getApplicationContext());
        Bmob.initialize(this,ApplicationID);
    }

    public static NotesApplication getInstance() {
        return mInstance;
    }
    /** ----------------------- 一些公共的变量 ------------------------- */
    private long lockTime = 0; // 保存的是最近一次调用onPause()的系统时间
    private Setting settings;  // 手势设置

    /** ----------------------- 一些set/get方法 ------------------------- */
    public long getLockTime() {
        return lockTime;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }

    public Setting getSettings() {
        return settings;
    }

    public void setSettings(Setting settings) {
        this.settings = settings;
    }
}
