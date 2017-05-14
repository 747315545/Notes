package com.fly.notes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fly.notes.widget.ZoomImageView;

/**
 * Created by huangfei on 2016/11/30.
 */

public class PhotoViewActivity extends BaseActivity {
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
        if(bitmap==null){
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.nopic);
        }
        imageView = (ZoomImageView) findViewById(R.id.iv_activity_photoview);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(PhotoViewActivity.this, EditActivity.class);
            startActivity(intent);
            PhotoViewActivity.this.overridePendingTransition(0, R.anim.center_zoom_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
