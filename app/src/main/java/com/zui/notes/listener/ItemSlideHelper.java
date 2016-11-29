package com.zui.notes.listener;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by huangfei on 2016/11/10.
 */
public class ItemSlideHelper implements RecyclerView.OnItemTouchListener, GestureDetector.OnGestureListener {
    private final int DEFAULT_DURATION = 250;
    private View mTargetView;
    private int mTouchSlop;
    private int mMaxVelocity;
    private int mMinVelocity;
    private int mLastX;
    private int mLastY;
    private boolean mIsDragging;
    private boolean isEditMode = false;
    private boolean canClick = true;
    private Animator mExpandAndCollapseAnim;
    private GestureDetectorCompat mGestureDetector;
    private Callback mCallback;
    private RecyclerView mRecyclerView;


    public ItemSlideHelper(Context context, Callback callback, RecyclerView recyclerView) {
        this.mCallback = callback;
        this.mRecyclerView = recyclerView;
        mGestureDetector = new GestureDetectorCompat(context, this);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        int action = MotionEventCompat.getActionMasked(e);
        int x = (int) e.getX();
        int y = (int) e.getY();
        mGestureDetector.onTouchEvent(e);
        if (rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
            if (mTargetView != null) {
                smoothHorizontalExpandOrCollapse(DEFAULT_DURATION / 2);
                mTargetView = null;
            }
            return false;
        }
        if (mExpandAndCollapseAnim != null && mExpandAndCollapseAnim.isRunning()) {
            return true;
        }
        boolean needIntercept = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) e.getX();
                mLastY = (int) e.getY();
                if (mTargetView != null) {
                    return !inView(x, y);
                }
                mTargetView = mCallback.findTargetView(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (x - mLastX);
                int deltaY = (y - mLastY);
                if (Math.abs(deltaY) > Math.abs(deltaX))
                    return false;
                needIntercept = mIsDragging = mTargetView != null && Math.abs(deltaX) >= mTouchSlop;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isExpanded()) {

                    if (inView(x, y)) {
                        mCallback.onMenuClick(rv.getChildLayoutPosition(mTargetView));
                    } else {
                        needIntercept = true;
                    }
                    smoothHorizontalExpandOrCollapse(DEFAULT_DURATION / 2);
                } else if (canClick) {
                    mCallback.onItemClick(rv.getChildLayoutPosition(mTargetView));
                }
                canClick = true;
                mTargetView = null;
                break;
        }
        return needIntercept;
    }

    public boolean isExpanded() {
        return mTargetView != null && mTargetView.getScrollX() == getHorizontalRange();
    }

    private boolean isCollapsed() {

        return mTargetView != null && mTargetView.getScrollX() == 0;
    }

    private boolean inView(int x, int y) {

        if (mTargetView == null)
            return false;
        int scrollX = mTargetView.getScrollX();
        int left = mTargetView.getWidth() - scrollX;
        int top = mTargetView.getTop();
        int right = left + getHorizontalRange();
        int bottom = mTargetView.getBottom();
        Rect rect = new Rect(left, top, right, bottom);
        return rect.contains(x, y);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (mExpandAndCollapseAnim != null && mExpandAndCollapseAnim.isRunning() || mTargetView == null)
            return;
        if (mGestureDetector.onTouchEvent(e)) {
            mIsDragging = false;
            return;
        }
        int x = (int) e.getX();
        int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastX - e.getX());
                if (mIsDragging && !isEditMode) {
                    horizontalDrag(deltaX);
                }
                mLastX = x;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    if (!smoothHorizontalExpandOrCollapse(0) && isCollapsed())
                        mTargetView = null;

                    mIsDragging = false;
                }

                break;
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void horizontalDrag(int delta) {
        int scrollX = mTargetView.getScrollX();
        int scrollY = mTargetView.getScrollY();
        if ((scrollX + delta) <= 0) {
            mTargetView.scrollTo(0, scrollY);
            return;
        }
        int horRange = getHorizontalRange();
        scrollX += delta;
        if (Math.abs(scrollX) < horRange) {
            mTargetView.scrollTo(scrollX, scrollY);
        } else {
            mTargetView.scrollTo(horRange, scrollY);
        }
    }

    private boolean smoothHorizontalExpandOrCollapse(float velocityX) {
        int scrollX;
        if (mTargetView == null || isEditMode)
            return false;
        else
            scrollX = mTargetView.getScrollX();
        int scrollRange = getHorizontalRange();
        if (mExpandAndCollapseAnim != null)
            return false;
        int to = 0;
        int duration = DEFAULT_DURATION;
        if (velocityX == 0) {
            if (scrollX > scrollRange / 2) {
                to = scrollRange;
            }
        } else {
            if (velocityX > 0)
                to = 0;
            else
                to = scrollRange;

            duration = (int) ((1.f - Math.abs(velocityX) / mMaxVelocity) * DEFAULT_DURATION);
        }

        if (to == scrollX)
            return false;
        mExpandAndCollapseAnim = ObjectAnimator.ofInt(mTargetView, "scrollX", to);
        mExpandAndCollapseAnim.setDuration(duration);
        mExpandAndCollapseAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mExpandAndCollapseAnim = null;
                if (isCollapsed())
                    mTargetView = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
                mExpandAndCollapseAnim = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mExpandAndCollapseAnim.start();

        return true;
    }


    private int getHorizontalRange() {
        RecyclerView.ViewHolder viewHolder = mCallback.getChildViewHolder(mTargetView);
        return mCallback.getHorizontalRange(viewHolder);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        View view = mCallback.findTargetView(x, y);
        if (isCollapsed() && !isEditMode) {
            mCallback.onItemLongClick(mRecyclerView.getChildLayoutPosition(view));
            isEditMode = true;
            canClick = false;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > mMinVelocity && Math.abs(velocityX) < mMaxVelocity) {
            if (!smoothHorizontalExpandOrCollapse(velocityX)) {
                if (isCollapsed())
                    mTargetView = null;
                return true;
            }
        }
        return false;
    }


    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public interface Callback {

        int getHorizontalRange(RecyclerView.ViewHolder holder);

        RecyclerView.ViewHolder getChildViewHolder(View childView);

        View findTargetView(float x, float y);

        void onMenuClick(int position);

        void onItemClick(int position);

        void onItemLongClick(int position);

    }
}
