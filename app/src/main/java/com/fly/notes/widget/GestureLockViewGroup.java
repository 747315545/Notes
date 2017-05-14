package com.fly.notes.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.fly.notes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfei on 2017/5/4.
 */
public class GestureLockViewGroup extends RelativeLayout {
    private static final String TAG = "GestureLockViewGroup";
    /**
     * 保存所有的GestureLockView
     */
    private GestureLockView[] mGestureLockViews;
    /**
     * 每个边上的GestureLockView的个数
     */
    private int mCount = 4;
    /**
     * 存储答案
     */
    private List<Integer> mAnswer = new ArrayList<>();
    /**
     * 保存用户选中的GestureLockView的id
     */
    private List<Integer> mChoose = new ArrayList<Integer>();

    private String firstSetAnswerStr = "";

    private Paint mPaint;

    // 圈/间距
    private int marginRate = 2;
    /**
     * 每个GestureLockView中间的间距 设置为：mGestureLockViewWidth * 25%
     */
    private int mMarginBetweenLockView = 30;
    /**
     * GestureLockView的边长 4 * mWidth / ( 5 * mCount + 1 )
     */
    private int mGestureLockViewWidth;

    /**
     * GestureLockView无手指触摸的状态下内圆的颜色
     */
    private int mNoFingerInnerCircleColor = 0xFF939090;
    /**
     * GestureLockView无手指触摸的状态下外圆的颜色
     */
    private int mNoFingerOuterCircleColor = 0xFFE0DBDB;
    /**
     * GestureLockView手指触摸的状态下内圆和外圆的颜色
     */
    private int mFingerOnColor = 0xFF378FC9;
    /**
     * GestureLockView手指抬起的状态下内圆和外圆的颜色
     */
    private int mFingerUpColor = 0xFFFF0000;
    /**
     * 错误状态下的颜色
     */
    private int mFingerWrongColor = 0xffff4444;


    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;

    private Path mPath;
    /**
     * 指引线的开始位置x
     */
    private int mLastPathX;
    /**
     * 指引线的开始位置y
     */
    private int mLastPathY;
    /**
     * 指引下的结束位置
     */
    private Point mTmpTarget = new Point();

    /**
     * 最大尝试次数
     */
    private int mTryTimes = 4;
    /**
     * 回调接口
     */
    private OnGestureLockViewListener mOnGestureLockViewListener;

    public GestureLockViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        /**
         * 获得所有自定义的参数的值
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.GestureLockViewGroup, defStyle, 0);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.GestureLockViewGroup_color_no_finger_inner_circle:
                    mNoFingerInnerCircleColor = a.getColor(attr,
                            mNoFingerInnerCircleColor);
                    break;
                case R.styleable.GestureLockViewGroup_color_no_finger_outer_circle:
                    mNoFingerOuterCircleColor = a.getColor(attr,
                            mNoFingerOuterCircleColor);
                    break;
                case R.styleable.GestureLockViewGroup_color_finger_on:
                    mFingerOnColor = a.getColor(attr, mFingerOnColor);
                    break;
                case R.styleable.GestureLockViewGroup_color_finger_wrong:
                    mFingerWrongColor = a.getColor(attr, mFingerWrongColor);
                    break;
                case R.styleable.GestureLockViewGroup_count:
                    mCount = a.getInt(attr, 3);
                    break;
                case R.styleable.GestureLockViewGroup_tryTimes:
                    mTryTimes = a.getInt(attr, 5);
                default:
                    break;
            }
        }

        a.recycle();

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAlpha(255);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Log.e(TAG, mWidth + "");
        // Log.e(TAG, mHeight + "");

        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;

        // setMeasuredDimension(mWidth, mHeight);

        // 初始化mGestureLockViews
        if (mGestureLockViews == null) {
            mGestureLockViews = new GestureLockView[mCount * mCount];
            // 计算每个GestureLockView的宽度
            mGestureLockViewWidth = (int) (marginRate * mWidth * 1.0f / ((marginRate + 1) * mCount + 1));
            // 计算每个GestureLockView的间距
            mMarginBetweenLockView = mGestureLockViewWidth / marginRate;
            // 设置画笔的宽度为GestureLockView的内圆直径稍微小点（不喜欢的话，随便设）
            mPaint.setStrokeWidth(mGestureLockViewWidth * 0.01f);

            for (int i = 0; i < mGestureLockViews.length; i++) {
                //初始化每个GestureLockView
                mGestureLockViews[i] = new GestureLockView(getContext(),
                        mNoFingerInnerCircleColor, mNoFingerOuterCircleColor,
                        mFingerOnColor, mFingerUpColor);
                mGestureLockViews[i].setId(i + 1);
                //设置参数，主要是定位GestureLockView间的位置
                LayoutParams lockerParams = new LayoutParams(
                        mGestureLockViewWidth, mGestureLockViewWidth);

                // 不是每行的第一个，则设置位置为前一个的右边
                if (i % mCount != 0) {
                    lockerParams.addRule(RelativeLayout.RIGHT_OF,
                            mGestureLockViews[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面
                if (i > mCount - 1) {
                    lockerParams.addRule(RelativeLayout.BELOW,
                            mGestureLockViews[i - mCount].getId());
                }
                //设置右下左上的边距
                int rightMargin = mMarginBetweenLockView;
                int bottomMargin = mMarginBetweenLockView;
                int leftMagin = 0;
                int topMargin = 0;
                /**
                 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
                if (i >= 0 && i < mCount)// 第一行
                {
                    topMargin = mMarginBetweenLockView;
                }
                if (i % mCount == 0)// 第一列
                {
                    leftMagin = mMarginBetweenLockView;
                }

                lockerParams.setMargins(leftMagin, topMargin, rightMargin,
                        bottomMargin);
                mGestureLockViews[i].setMode(GestureLockView.Mode.STATUS_NO_FINGER, showPath);
                addView(mGestureLockViews[i], lockerParams);
            }

            Log.e(TAG, "mWidth = " + mWidth + " ,  mGestureViewWidth = "
                    + mGestureLockViewWidth + " , mMarginBetweenLockView = "
                    + mMarginBetweenLockView);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 重置
                reset();
            case MotionEvent.ACTION_MOVE:
                // 初始化画笔为蓝色
                setViewColor(true);
                GestureLockView child = getChildIdByPos(x, y);

                if (child != null) {
                    int cId = child.getId();
                    if (!mChoose.contains(cId)) {
                        // 循环加入中间点
                        int subId = checkChoose(cId);
                        Log.e(TAG, "SubId:" + subId);
                        while (subId != -1) {
                            // 1、这部分代码和以下 2 部分基本一样，可以抽离出一个方法
                            mChoose.add(subId);
                            GestureLockView subChild = mGestureLockViews[subId - 1];
                            subChild.setMode(GestureLockView.Mode.STATUS_FINGER_ON, showPath);
                            // 设置指引线的起点
                            mLastPathX = subChild.getLeft() / 2 + subChild.getRight() / 2;
                            mLastPathY = subChild.getTop() / 2 + subChild.getBottom() / 2;
                            // 非第一个，将两者使用线连上
                            mPath.lineTo(mLastPathX, mLastPathY);
                            // 继续循环
                            subId = checkChoose(cId);
                        }

                        // 2、中间点加入完成，继续加入当前选择的点
                        mChoose.add(cId);
                        child.setMode(GestureLockView.Mode.STATUS_FINGER_ON, showPath);
                        // 设置指引线的起点
                        mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
                        mLastPathY = child.getTop() / 2 + child.getBottom() / 2;

                        if (mChoose.size() == 1) {
                            // 当前添加为第一个
                            mPath.moveTo(mLastPathX, mLastPathY);
                        } else {
                            // 非第一个，将两者使用线连上
                            mPath.lineTo(mLastPathX, mLastPathY);
                        }
                    }
                }
                // 指引线的终点
                mTmpTarget.x = x;
                mTmpTarget.y = y;
                break;
            case MotionEvent.ACTION_UP:
                // 回调是否成功
                if (mOnGestureLockViewListener != null && mChoose.size() > 0) {
                    //如果是初次设置图案，不需要checkAnswer()，但需要setAnswer()
                    if (isFirstSet) {
                        boolean patternOk = !(mChoose.size() < 4);
                        if (patternOk) {
                            setAnswer(mChoose);
                            isFirstSet = false;
                            firstSetAnswerStr = listToStr(mChoose);
                        }
                        setViewColor(patternOk);
                        mOnGestureLockViewListener.onFirstSetPattern(patternOk);
                    } else {
                        isAnswerRight = checkAnswer();
                        if (!isAnswerRight) {
                            this.mTryTimes--;
                        }
                        setViewColor(isAnswerRight);
                        mOnGestureLockViewListener.onGestureEvent(isAnswerRight);
                        if (this.mTryTimes == 0) {
                            // 剩余次数为0时进行一些处理（在使用该手势密码的activity中覆写）
                            mOnGestureLockViewListener.onUnmatchedExceedBoundary();
                        }
                    }
                }

                Log.e(TAG, "mChoose = " + mChoose);
                // 将终点设置位置为起点，即取消指引线
                mTmpTarget.x = mLastPathX;
                mTmpTarget.y = mLastPathY;

                // 改变子元素的状态为UP
                changeItemMode();

                // 计算每个元素中箭头需要旋转的角度
                for (int i = 0; i + 1 < mChoose.size(); i++) {
                    int childId = mChoose.get(i);
                    int nextChildId = mChoose.get(i + 1);

                    GestureLockView startChild = (GestureLockView) findViewById(childId);
                    GestureLockView nextChild = (GestureLockView) findViewById(nextChildId);

                    int dx = nextChild.getLeft() - startChild.getLeft();
                    int dy = nextChild.getTop() - startChild.getTop();
                    // 计算角度
                    int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
                    startChild.setArrowDegree(angle);
                }
                delayReset();// 调用
                break;

        }
        invalidate();
        return true;
    }

    private void changeItemMode() {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (mChoose.contains(gestureLockView.getId())) {
                gestureLockView.setViewColor(mFingerUpColor);
                gestureLockView.setIsAnswerRight(isAnswerRight);// 调用
                gestureLockView.setMode(GestureLockView.Mode.STATUS_FINGER_UP, showPath);
            }
        }
    }

    /**
     * 做一些必要的重置
     */
    private void reset() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mChoose.clear();
        mPath.reset();
        isAnswerRight = true;// 重置
        for (GestureLockView gestureLockView : mGestureLockViews) {
            gestureLockView.setMode(GestureLockView.Mode.STATUS_NO_FINGER, showPath);
            gestureLockView.setArrowDegree(-1);
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            reset();
            invalidate();
        }
    };

    private void delayReset() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    /**
     * 检查用户绘制的手势是否正确
     *
     * @return
     */
    private boolean checkAnswer() {
        return mChoose.equals(mAnswer);
    }

    /**
     * 检查当前左边是否在child中
     *
     * @param child
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(View child, int x, int y) {

        //设置了内边距，即x,y必须落入下GestureLockView的内部中间的小区域中，可以通过调整padding使得x,y落入范围不变大，或者不设置padding
        int padding = (int) (mGestureLockViewWidth * 0.15);

        if (x >= child.getLeft() + padding && x <= child.getRight() - padding
                && y >= child.getTop() + padding
                && y <= child.getBottom() - padding) {
            return true;
        }
        return false;
    }

    /**
     * 通过x,y获得落入的GestureLockView
     *
     * @param x
     * @param y
     * @return
     */
    private GestureLockView getChildIdByPos(int x, int y) {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (checkPositionInChild(gestureLockView, x, y)) {
                return gestureLockView;
            }
        }

        return null;

    }

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public void setOnGestureLockViewListener(OnGestureLockViewListener listener) {
        this.mOnGestureLockViewListener = listener;
    }

    /**
     * 设置答案
     *
     * @param answer
     */
    private void setAnswer(List<Integer> answer) {
        // 直接用赋值语句传递的是内存地址，会导致mChoose一改mAnswer就跟着改了
        this.mAnswer.clear();
        for (int i = 0; i < answer.size(); i++) {
            this.mAnswer.add(answer.get(i));
        }
    }

    /**
     * 对外公布设置答案的方法
     *
     * @param answer
     */
    public void setAnswer(String answer) {
        mAnswer.clear();
        for (int i = 0; i < answer.length(); i++) {
            mAnswer.add((int) answer.charAt(i) - 48);
        }
    }

    public String getChooseStr() {
        return firstSetAnswerStr;
    }

    // 处理手势密码的字符串转换
    private String listToStr(List<Integer> list) {
        String str = "";
        if (list == null || list.size() <= 0) {
            return str;
        }
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i) + "";
        }
        return str;
    }

    public int getTryTimes() {
        return mTryTimes;// 返回剩余次数
    }

    /**
     * 设置最大实验次数
     *
     * @param boundary
     */
    public void setUnMatchExceedBoundary(int boundary) {
        this.mTryTimes = boundary;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!showPath && isAnswerRight) {
            return;
        }
        //绘制GestureLockView间的连线
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChoose.size() > 0) {
            if (mLastPathX != 0 && mLastPathY != 0)
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x,
                        mTmpTarget.y, mPaint);
        }

    }

    public interface OnGestureLockViewListener {
        /**
         * 是否匹配
         *
         * @param matched
         */
        void onGestureEvent(boolean matched);

        /**
         * 超过尝试次数
         */
        void onUnmatchedExceedBoundary();

        /**
         * 是否初始设置密码
         *
         * @param patternOk
         */
        void onFirstSetPattern(boolean patternOk);
    }

    // 传入true，设为蓝色，传入false，设为红色
    private void setViewColor(boolean isOk) {
        if (isOk) {
            mFingerUpColor = mFingerOnColor;
        } else {
            mFingerUpColor = mFingerWrongColor;
        }
        mPaint.setColor(mFingerUpColor);
        mPaint.setAlpha(255);
    }

    // 记录答案是否正确
    private boolean isAnswerRight = true;
    // 默认显示轨迹
    private boolean showPath = true;

    // 对外公开的set方法
    public void setShowPath(boolean showPath) {
        this.showPath = showPath || isFirstSet;
    }

    // 默认false
    private boolean isFirstSet = false;

    // 是否是初次设置密码
    public void isFirstSet(boolean isFirstSet) {
        this.isFirstSet = isFirstSet;
        setShowPath(true);// 强制显示路径
    }

    // n * n的阵列，首位从0起算，计算公式：cId = x + n*y + 1
    private int checkChoose(int cId) {
        if (mChoose == null || mChoose.size() < 1) {
            return -1;
        }
        int lastX, lastY;
        int nowX, nowY;
        int lastId = mChoose.get(mChoose.size() - 1);

        lastX = (lastId - 1) % mCount;
        lastY = (lastId - 1) / mCount;

        nowX = (cId - 1) % mCount;
        nowY = (cId - 1) / mCount;

        int signX = compare(lastX, nowX);
        int signY = compare(lastY, nowY);
        // 比较x轴y轴间距
        int copiesX = (nowX - lastX) * signX;
        int copiesY = (nowY - lastY) * signY;
        int copies = copiesX > copiesY ? copiesY : copiesX;

        if (copiesX == 1 || copiesY == 1) {
            return -1;
        }

        if (signX == 0 || signY == 0) {
            return lastX + signX + (lastY + signY) * mCount + 1;
        }

        if (copies > 1 && copiesX % copies == 0 && copiesY % copies == 0) {
            return lastX + copiesX / copies * signX
                    + (lastY + copiesY / copies * signY) * mCount + 1;
        }

        return -1;
    }

    private int compare(int last, int now) {
        if (now > last) {
            return 1;
        } else if (now < last) {
            return -1;
        } else {
            return 0;
        }
    }
}
