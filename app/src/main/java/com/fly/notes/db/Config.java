package com.fly.notes.db;

import android.content.Context;

import com.google.gson.Gson;
import com.fly.notes.model.Setting;
import com.fly.notes.util.SPUtil;

/**
 * Created by huangfei on 2017/5/4.
 */
public class Config {
    private static final String TAG = Config.class.getSimpleName();

    private static Context mContext;

    //在application中调用init方法进行初始化，传入全局context
    public final static void init(Context context) {
        mContext = context;
    }

    /**
     * ---------------------------------------变量---------------------------------------------
     */
    // 设置存储的文件名
    public static final String FILE_NAME = "shared_info";

    // 手势密码唤起时间间隔（1分钟）
    public static final int LOCK_TIME = 60 * 1000;

    // 保存手势设置信息
    public static final String KEY_GESTURE_SETTINGS = "Key_GestureSettings";


    /**
     * ---------------------------------------方法---------------------------------------------
     */
    // 保存手势设置信息
    public static void setGestureSettings(Setting settings) {
        String settingsStr = new Gson().toJson(settings);
        SPUtil.put(mContext, KEY_GESTURE_SETTINGS, settingsStr);
    }

    // 读取手势设置信息
    public static Setting getGestureSettings() {
        if (SPUtil.contains(mContext, KEY_GESTURE_SETTINGS)) {
            String settingsStr = (String) SPUtil.get(mContext, KEY_GESTURE_SETTINGS, SPUtil.SHARE_STRING);
            Setting settings = new Gson().fromJson(settingsStr, Setting.class);
            return settings;
        }
        return null;
    }

}

