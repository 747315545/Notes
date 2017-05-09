package com.fly.notes.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.fly.notes.NotesApplication;
import com.fly.notes.R;
import com.fly.notes.util.ImageUtils;

/**
 * Created by huangfei on 2016/11/21.
 */

public class StrokeImageView
        extends ImageView
{
    public String picFolderAndName;

    public StrokeImageView(Context paramContext)
    {
        super(paramContext);
    }

    public StrokeImageView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    public StrokeImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public StrokeImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    }

    protected void onDraw(Canvas paramCanvas)
    {
        Rect localRect = new Rect(0, 0, paramCanvas.getWidth(), paramCanvas.getHeight());
        int i = NotesApplication.getInstance().getResources().getDimensionPixelOffset(R.dimen.add_pic_stroke_width);
        localRect.left += i / 2;
        localRect.top += i / 2;
        localRect.right -= i / 2;
        localRect.bottom -= i / 2;
        Object localObject = new Paint();
        ((Paint)localObject).setStrokeWidth(i);
        ((Paint)localObject).setColor(getResources().getColor(R.color.pic_line_color));
        ((Paint)localObject).setStyle(Paint.Style.STROKE);
        paramCanvas.drawRect(localRect, (Paint)localObject);
        localObject = new Rect(localRect.left + i * 2, localRect.top + i * 2, localRect.right - i * 2, localRect.bottom - i * 2);
        paramCanvas.drawBitmap(ImageUtils.drawableToBitmap(getDrawable()), null, (Rect)localObject, null);
    }
}