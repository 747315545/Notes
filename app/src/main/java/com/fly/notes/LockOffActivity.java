package com.fly.notes;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.fly.notes.model.Setting;
import com.fly.notes.widget.GestureLockViewGroup;

/**
 * 取消手势密码
 */
public class LockOffActivity extends BaseActivity {
    private static final String TAG = LockOffActivity.class.getSimpleName();

    private Context mContext;
    private NotesApplication myApp;
    private TextView mTextView;
    private GestureLockViewGroup mGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_off);
        setTitle("取消手势密码");
        mContext = this;

        myApp = NotesApplication.getInstance();

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_prompt_lock_off);
        mTextView.setText("请绘制手势密码");

        mGesture = (GestureLockViewGroup) findViewById(R.id.gesture_lock_view_group_lock_off);
        mGesture.setAnswer(myApp.getSettings().getGesture());
        mGesture.setShowPath(Setting.SHOW_PATH.equals(myApp.getSettings().getShowPath()));
        mGesture.setOnGestureLockViewListener(mListener);
    }

    private void gestureEvent(boolean matched) {
        if (matched) {
            mTextView.setText("输入正确，手势关闭");
            NotesApplication.getInstance().setSettings(new Setting("", Setting.SHOW_PATH));
            setResult(RESULT_OK);
            finish();
        } else {
            if (mGesture.getTryTimes() > 0) {
                mTextView.setText("手势错误，还剩" + mGesture.getTryTimes() + "次");
            } else {
                mTextView.setText("错误次数已达上限");
            }
        }
    }

    private void unmatchedExceedBoundary() {
        // 正常情况这里需要做处理（如退出或重登）
        Toast.makeText(mContext, "错误次数太多，即将退出！！！", Toast.LENGTH_SHORT).show();
        NotesApplication.getInstance().exit();
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
                    unmatchedExceedBoundary();
                }

                @Override
                public void onFirstSetPattern(boolean patternOk) {
                }
            };
}
