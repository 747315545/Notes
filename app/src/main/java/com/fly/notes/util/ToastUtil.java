package com.fly.notes.util;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by huangfei on 2017/5/12.
 */

 public enum ToastUtil {
    INSTANCE;
    private Toast toast;
    public void makeToast(Context context,CharSequence s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(s);
            toast.show();
        }
    }
}
