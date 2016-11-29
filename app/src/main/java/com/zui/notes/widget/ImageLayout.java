package com.zui.notes.widget;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Created by huangfei on 2016/11/24.
 */

public class ImageLayout extends RelativeLayout {
    StrokeImageView imageView;

    public ImageLayout(Context context) {
        super(context);
    }

    @Override
    public String toString() {
        imageView = (StrokeImageView) this.getChildAt(0);
        return "1" + imageView.picFolderAndName;
    }
}
