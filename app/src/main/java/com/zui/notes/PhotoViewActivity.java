package com.zui.notes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zui.notes.widget.ZoomImageView;

/**
 * Created by huangfei on 2016/11/30.
 */

public class PhotoViewActivity extends Activity {
    private ImageView imageView;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photeview);
        path = getIntent().getStringExtra("pic_path");
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView= (ZoomImageView) findViewById(R.id.iv_activity_photoview);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(PhotoViewActivity.this,EditActivity.class);
            startActivity(intent);
            PhotoViewActivity.this.overridePendingTransition(0,R.anim.center_zoom_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
