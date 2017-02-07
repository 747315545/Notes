package com.zui.notes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zui.notes.util.Utils;
import com.zui.notes.widget.LongPhotoView;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by huangfei on 2016/12/1.
 */

public class PhotoShareActivity extends Activity implements View.OnClickListener {
    private TextView tvShareCancel;
    private TextView tvPhotoSave;
    private LinearLayout llPengYouQuan;
    private LinearLayout llWeiBo;
    private LinearLayout llWeiXin;
    private LinearLayout llQQ;
    private ImageView ivPengYouQuan;
    private ImageView ivWeiBo;
    private ImageView ivWeiXin;
    private ImageView ivQQ;
    private ImageView ivMore;
    private LongPhotoView mLongPhotoView;
    private Bitmap image;
    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_photo);
        initView();
        initAction();
        initBottomBar();
        image = loadBitmapFromView(mLongPhotoView);
    }

    private void initView(){
        tvShareCancel = (TextView) findViewById(R.id.exit_share_cancel);
        tvPhotoSave = (TextView) findViewById(R.id.share_photo_save);
        llPengYouQuan = (LinearLayout) findViewById(R.id.pengyouquan_layout);
        llWeiBo = (LinearLayout) findViewById(R.id.weibo_layout);
        llWeiXin = (LinearLayout) findViewById(R.id.weixin_layout);
        llQQ = (LinearLayout) findViewById(R.id.qq_layout);
        ivPengYouQuan = (ImageView) findViewById(R.id.pengyouquan);
        ivWeiBo = (ImageView) findViewById(R.id.weibo);
        ivWeiXin = (ImageView) findViewById(R.id.weixin);
        ivQQ = (ImageView) findViewById(R.id.qq);
        ivMore = (ImageView) findViewById(R.id.more);
        LinearLayout localLinearLayout = (LinearLayout)findViewById(R.id.share_content_container);
        int i = localLinearLayout.getPaddingLeft();
        int j = localLinearLayout.getPaddingRight();
        this.mLongPhotoView = new LongPhotoView(this, getIntent().getStringExtra("data"), ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getWidth() - i - j);
        localLinearLayout.addView(this.mLongPhotoView);
    }


    private void initAction(){
        tvShareCancel.setOnClickListener(this);
        tvPhotoSave.setOnClickListener(this);
        ivPengYouQuan.setOnClickListener(this);
        ivWeiBo.setOnClickListener(this);
        ivWeiXin.setOnClickListener(this);
        ivQQ.setOnClickListener(this);
        ivMore.setOnClickListener(this);
    }

    private void initBottomBar(){
        PackageInfo packageInfo;
        packageInfo = Utils.getPackageInfo(PhotoShareActivity.this,"com.tencent.mm");
        if(packageInfo!=null){
            llPengYouQuan.setVisibility(View.VISIBLE);
            llWeiXin.setVisibility(View.VISIBLE);
            ivWeiXin.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
        }
        packageInfo=Utils.getPackageInfo(PhotoShareActivity.this,"com.sina.weibo");
        if(packageInfo!=null){
            llWeiBo.setVisibility(View.VISIBLE);
            ivWeiBo.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
        }
        packageInfo = Utils.getPackageInfo(PhotoShareActivity.this,"com.tencent.mobileqq");
        if(packageInfo!=null){
            llQQ.setVisibility(View.VISIBLE);
            ivQQ.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.exit_share_cancel:
                Intent intent = new Intent(PhotoShareActivity.this, EditActivity.class);
                PhotoShareActivity.this.startActivity(intent);
                PhotoShareActivity.this.overridePendingTransition(R.anim.fake_anim, R.anim.activity_push_out);
                break;
            case R.id.share_photo_save:
                try {
                    FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory()
                            .toString()
                            + File.separator
                            + "Notes"
                            + File.separator
                            +"cache"
                            + File.separator
                            +getIntent().getLongExtra("id",0)
                            + ".jpg"));
                    image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    if(toast!=null){

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pengyouquan:
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), image, null,null));
                break;
            case R.id.weibo:
                break;
            case R.id.weixin:
                break;
            case R.id.qq:
                break;
            case R.id.more:
                break;
        }
    }

    public  Bitmap loadBitmapFromView(View v) {
        v.measure(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        int i = v.getMeasuredWidth();
        int j = v.getMeasuredHeight();
        Log.e("huangfei",""+i+"da"+j);
        v.setDrawingCacheEnabled(true);
        v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        v.setDrawingCacheBackgroundColor(-1);
        Bitmap bmp = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(bmp);
        localCanvas.drawColor(-1);
        v.draw(localCanvas);
        return bmp;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = new Intent(PhotoShareActivity.this, EditActivity.class);
            PhotoShareActivity.this.startActivity(intent);
            PhotoShareActivity.this.overridePendingTransition(R.anim.fake_anim, R.anim.activity_push_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
