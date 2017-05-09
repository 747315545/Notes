package com.zui.notes;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.zui.notes.model.Setting;
import com.zui.notes.widget.GestureLockViewGroup;

public class LockActivity extends BaseActivity {
    private static final String TAG = LockActivity.class.getSimpleName();

    private Context mContext;
    private NotesApplication myApp;
    private TextView mTextView;
    private GestureLockViewGroup mGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        setTitle("手势密码");
        mContext = this;

        myApp = NotesApplication.getInstance();

        // 禁止唤起手势页
        disablePatternLock(false);

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_prompt_lock);
        mTextView.setText("请绘制手势密码");

        mGesture = (GestureLockViewGroup) findViewById(R.id.gesture_lock_view_group_lock);
        mGesture.setAnswer(myApp.getSettings().getGesture());
        mGesture.setShowPath(Setting.SHOW_PATH.equals(myApp.getSettings().getShowPath()));
        mGesture.setOnGestureLockViewListener(mListener);
    }

    @Override
    public void onBackPressed() {
        // 阻止Lock页面的返回事件
        moveTaskToBack(true);
    }

    /**
     * 处理手势图案的输入结果
     * @param matched
     */
    private void gestureEvent(boolean matched) {
        if (matched) {
            mTextView.setText("输入正确");
            finish();
        } else {
            mTextView.setText("手势错误，还剩"+ mGesture.getTryTimes() + "次");
        }
    }

    /**
     * 处理输错次数超限的情况
     */
    private void unmatchedExceedBoundary() {
        // 正常情况这里需要做处理（如退出或重登）
        Toast.makeText(mContext, "错误次数太多，请重新登录", Toast.LENGTH_SHORT).show();
    }

    // 手势操作的回调监听
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
