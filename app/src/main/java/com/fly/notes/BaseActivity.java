package com.fly.notes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fly.notes.db.Config;


public class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getSimpleName();

    private Context mContext;
    private NotesApplication myApp;

    // 页面是否允许唤起手势密码
    private boolean enableLock = true;
    // 下一个页面是否唤起手势密码
    private boolean nextShowLock = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        myApp = NotesApplication.getInstance();
        myApp.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (enableLock) {
            // 减得当前APP在后台滞留的时间 durTime
            if (myApp.getLockTime() == 0) {
                showLockActivity();
            } else {
                long durTime = System.currentTimeMillis() - myApp.getLockTime();
                if (durTime > Config.LOCK_TIME) {
                    // 显示手势密码页面
                    showLockActivity();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (enableLock || !nextShowLock) {
            // 更新 lockTime
            myApp.setLockTime(System.currentTimeMillis());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 跳转至手势密码页面
     */
    private void showLockActivity() {
        if (myApp.getSettings() != null
                && myApp.getSettings().getGesture() != null
                && !myApp.getSettings().getGesture().isEmpty()) {
            Intent intent = new Intent(mContext, LockActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 部分页面禁用手势密码需要调用该方法，例如启动页、注册登录页、解锁页（LockActivity）等
     * 在这些页面如果停留时间较久后，如果想进入下一个页面时不弹出手势，需要在finish前手动添加
     * myApp.setLockTime(System.currentTimeMillis());
     * 或者传入新的参数进行标识，在onPause中根据标识判断是否setLockTime
     * 本例选择传入参数
     * nextShowLock 为false 表示onPause()会调用setLockTime()，则下一个页面不会唤起手势
     *
     * @param nextShowLock
     */
    protected void disablePatternLock(boolean nextShowLock) {
        enableLock = false;
        this.nextShowLock = nextShowLock;
    }

    @Override
    protected void onDestroy() {
        myApp.removeActivity(this);
        super.onDestroy();
    }
}
