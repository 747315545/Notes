package com.zui.notes.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zui.notes.R;
import com.zui.notes.util.Utils;

/**
 * Created by huangfei on 2016/12/5.
 */

public class LongPhotoView extends LinearLayout {
    private float mContentWidth = 0.0F;
    private Context mContext;
    private float mLineSpacingExtra;
    private float mLineSpacingMult;
    private int mWidth = 0;

    public LongPhotoView(Context paramContext, String paramString, int paramInt) {
        super(paramContext);
        this.mContext = paramContext;
        this.mWidth = paramInt;
        this.mLineSpacingMult = 1.0F;
        this.mLineSpacingExtra = this.mContext.getResources().getDimension(R.dimen.share_photo_line_space);
        initParams();
        layoutContent(paramString);
        layoutLogo();
    }

    private void initParams() {
        int contentPaddingLeft = ((int) this.mContext.getResources().getDimension(R.dimen.content_padding_left));
        int contentPaddingRight = ((int) this.mContext.getResources().getDimension(R.dimen.content_padding_right));
        int contentPaddingTop = ((int) this.mContext.getResources().getDimension(R.dimen.content_padding_top));
        setBackground(this.mContext.getResources().getDrawable(R.drawable.long_photo_view_bg));
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) getLayoutParams();
        if (localLayoutParams == null) {
            localLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        setOrientation(VERTICAL);
        setPadding(contentPaddingLeft, contentPaddingTop, contentPaddingRight, 0);
        setLayoutParams(localLayoutParams);
        this.mContentWidth = (this.mWidth - contentPaddingLeft - contentPaddingRight);
    }


    private void layoutContent(String data) {

        if (data.length() > 2) {
            String[] str = data.split(":");
            for (int i = 0; i < str.length; i++) {
                if (str[i].charAt(0) == '0') {
                    boolean a = false;
                    boolean b = false;
                    if (str[i].charAt(1) == '1') {
                        a = true;
                    }
                    if (str[i].charAt(2) == '1') {
                        b = true;
                    }
                    layoutRadioLayout(a, b, str[i].substring(3));
                } else {
                    layoutPicture(str[i].substring(1));
                }
            }
        }


    }


    private void layoutPicture(String paramString) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString, localOptions);
        float f = localOptions.outWidth / this.mContentWidth;
        int j = (int) (localOptions.outHeight / f);
        ImageView localImageView = new ImageView(this.mContext);
        int k = (int) this.mContext.getResources().getDimension(R.dimen.share_photo_segment_space);
        localImageView.setLayoutParams(new ViewGroup.LayoutParams((int) this.mContentWidth, j + k));
        localImageView.setPadding(0, 0, 0, k);
        localOptions.inJustDecodeBounds = false;
        if (f > 1.0F) {
            int i = (int) Math.ceil(f);
            localOptions.inSampleSize = i;
        }
        localImageView.setImageBitmap(BitmapFactory.decodeFile(paramString, localOptions));
        addView(localImageView);
    }


    private void layoutRadioLayout(boolean isShow, boolean isChecked,String paramString) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this.mContext).inflate(R.layout.item_radio_layout, null);
        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.iv_radio_btn);
        if(isShow){
            imageView.setVisibility(VISIBLE);
            if (isChecked) {
                imageView.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.checkbox_pressed));
            }else {
                imageView.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.checkbox_normal));
            }
        }else {
            imageView.setVisibility(GONE);
        }
        TextView textView = (TextView) linearLayout.findViewById(R.id.radio_textview);
        textView.setText(paramString);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setTextColor(this.mContext.getResources().getColor(R.color.share_photo_text_color));
        textView.setLineSpacing(this.mLineSpacingExtra, this.mLineSpacingMult);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setPadding((int) this.mContext.getResources().getDimension(R.dimen.radio_text_padding_bottom),(int) this.mContext.getResources().getDimension(R.dimen.radio_text_padding_top),0,0);
        addView(linearLayout);
    }


    private void layoutLogo() {
        View view = new View(this.mContext);
        view.setLayoutParams(new ViewGroup.LayoutParams(100, Utils.dp2px(this.mContext, 18.0F)));
        addView(view);
        TextView textView = new TextView(this.mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(this.mContext.getResources().getColor(R.color.share_photo_slogan_color));
        textView.setTextSize(1, 10.0F);
        textView.setText(this.mContext.getString(R.string.share_slogan));
        addView(textView);
        View view1 = new View(this.mContext);
        view1.setLayoutParams(new ViewGroup.LayoutParams(100, Utils.dp2px(this.mContext, 30.0F)));
        addView(view1);
    }
}
