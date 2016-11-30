package com.zui.notes.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by huangfei on 2016/11/24.
 */

public class CheckboxLayout extends LinearLayout {
    CheckBox checkBox;
    EditText editText;

    public CheckboxLayout(Context context) {
        super(context);
    }

    public CheckboxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckboxLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckboxLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public String toString() {
        checkBox = (CheckBox) this.getChildAt(0);
        editText = (EditText) this.getChildAt(1);
        StringBuilder string = new StringBuilder();
        if (checkBox.getVisibility() == VISIBLE) {
            if (checkBox.isChecked()) {
                string.append("011");
            } else {
                string.append("010");
            }
        } else {
            string.append("000");
        }
        string.append(editText.getText().toString());
        return string.toString();
    }
}