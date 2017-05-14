package com.fly.notes;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Message;

import com.fly.notes.db.Config;
import com.fly.notes.model.Setting;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

/**
 * Created by huangfei on 2016/11/17.
 */

public class NotesApplication extends Application {
    private static NotesApplication mInstance;
    private static final String ApplicationID = "f7df8e86df28f29176bc3888d9419879";
    private List<Activity> activityList;
    private long lockTime = 0;
    private Setting settings;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Config.init(getApplicationContext());
        Bmob.initialize(this, ApplicationID);
        activityList = new ArrayList<>();
        lockTime = 0;
        settings = Config.getGestureSettings();
    }


    public static NotesApplication getInstance() {
        return mInstance;
    }

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

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public void exit() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                for (Activity activity : activityList) {
                    activity.finish();
                }
                System.exit(0);
                super.handleMessage(msg);
            }
        };
        handler.sendMessageDelayed(Message.obtain(), 3 * 1000);
    }

}
