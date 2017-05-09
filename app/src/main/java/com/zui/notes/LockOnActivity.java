package com.zui.notes;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.zui.notes.model.Setting;
import com.zui.notes.widget.GestureLockViewGroup;


/**
 * 设置手势密码
 */
public class LockOnActivity extends BaseActivity {
    private static final String TAG = LockOnActivity.class.getSimpleName();

    private TextView mTextView;
    private GestureLockViewGroup mGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_on);
        setTitle("设置手势密码");

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_prompt_lock_on);
        mTextView.setText("请绘制手势密码");

        mGesture = (GestureLockViewGroup) findViewById(R.id.gesture_lock_view_group_lock_on);
        mGesture.isFirstSet(true);
        mGesture.setUnMatchExceedBoundary(10000);
        mGesture.setOnGestureLockViewListener(mListener);
    }

    private void gestureEvent(boolean matched) {
        if (matched) {
            mTextView.setText("设置成功");
            Setting setting = new Setting(mGesture.getChooseStr(),Setting.SHOW_PATH);
            NotesApplication.getInstance().setSettings(setting);
            setResult(RESULT_OK);
            finish();
        } else {
            mTextView.setText("手势不一致，请重试");
        }
    }

    private void firstSetPattern(boolean patternOk) {
        if (patternOk) {
            mTextView.setText("请再次输入以确认");
        } else {
            mTextView.setText("需要四个点以上");
        }
    }

    // 回调监听
    private GestureLockViewGroup.OnGestureLockViewListener mListener = new
            GestureLockViewGroup.OnGestureLockViewListener() {
        @Override
        public void onGestureEvent(boolean matched) {
            gestureEvent(matched);
        }

        @Override
        public void onUnmatchedExceedBoundary() {
        }

        @Override
        public void onFirstSetPattern(boolean patternOk) {
            firstSetPattern(patternOk);
        }
    };
}
