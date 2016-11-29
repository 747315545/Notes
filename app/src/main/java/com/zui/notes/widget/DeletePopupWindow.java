package com.zui.notes.widget;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zui.notes.R;

/**
 * Created by huangfei on 2016/11/18.
 */

public class DeletePopupWindow extends PopupWindow {
    private TextView tvTitle;
    private TextView tvDelete;
    private TextView tvCancel;
    private View menuView;

    public DeletePopupWindow(final Activity context, View.OnClickListener onClickListener,int count) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        menuView = layoutInflater.inflate(R.layout.popupwindow_delete,null);
        tvTitle = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tvDelete = (TextView) menuView.findViewById(R.id.tv_pop_delete);
        tvCancel = (TextView) menuView.findViewById(R.id.tv_pop_cancel);
        if(count==1){
            tvTitle.setText(R.string.confirm_delete_file);
        }else {
            String s = context.getResources().getString(R.string.confirm_delete_items);
            s = String.format(s, count);
            tvTitle.setText(s);
        }
        tvDelete.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setContentView(menuView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.popupWindowAnim);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        backgroundAlpha(context,0.7f);
        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = menuView.findViewById(R.id.ll_popup_window).getTop();
                int y = (int)event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y>height){
                        dismiss();
                    }
                }
                return false;
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(context,1f);
            }
        });
    }

    public void backgroundAlpha(Activity context, float bgAlpha)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
