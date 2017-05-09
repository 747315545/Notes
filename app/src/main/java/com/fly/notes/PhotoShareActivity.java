package com.fly.notes;

import android.content.ComponentName;
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
import com.fly.notes.util.Utils;
import com.fly.notes.widget.LongPhotoView;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by huangfei on 2016/12/1.
 */

public class PhotoShareActivity extends BaseActivity implements View.OnClickListener {
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
    private Toast toast;
    private Uri uri = null;
    private static final String TAG = "PhotoShareActivity" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_photo);
        toast = null;
        initView();
        initAction();
        initBottomBar();
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
        int viewId = view.getId();
        Bitmap image = loadBitmapFromView(mLongPhotoView);
        Intent shareIntent;
        boolean needShare = true;
        ComponentName componentName = null;
        if(viewId != R.id.exit_share_cancel && viewId != R.id.share_photo_save){
            uri = Uri.parse(MediaStore.Images.Media.insertImage(PhotoShareActivity.this.getContentResolver(), image, null, null));
        }
        switch (viewId){
            case R.id.exit_share_cancel:
                needShare = false;
                Intent intent = new Intent(PhotoShareActivity.this, EditActivity.class);
                PhotoShareActivity.this.startActivity(intent);
                PhotoShareActivity.this.overridePendingTransition(R.anim.fake_anim, R.anim.activity_push_out);
                break;
            case R.id.share_photo_save:
                needShare = false;
                try {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            .toString()
                            + File.separator
                            +"cache"
                            + File.separator
                            +getIntent().getLongExtra("id",0)
                            + ".jpg");
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    if(toast == null){
                        toast = Toast.makeText(PhotoShareActivity.this,PhotoShareActivity.this.getResources().getText(R.string.save_success)+file.getAbsolutePath(),Toast.LENGTH_LONG);
                    }else {
                        toast.setText(PhotoShareActivity.this.getResources().getText(R.string.save_success)+file.getAbsolutePath());
                    }
                    toast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    if(toast == null){
                        toast = Toast.makeText(PhotoShareActivity.this,PhotoShareActivity.this.getResources().getText(R.string.save_failed),Toast.LENGTH_LONG);
                    }else {
                        toast.setText(PhotoShareActivity.this.getResources().getText(R.string.save_failed));
                    }
                    toast.show();
                }
                break;
            case R.id.pengyouquan:
                componentName = new ComponentName(
                        "com.tencent.mm",
                        "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                break;
            case R.id.weibo:
                componentName = new ComponentName(
                        "com.sina.weibo",
                        "com.sina.weibo.composerinde.ComposerDispatchActivity");
                break;
            case R.id.weixin:
                componentName = new ComponentName(
                        "com.tencent.mm",
                        "com.tencent.mm.ui.tools.ShareImgUI");
                break;
            case R.id.qq:
                componentName = new ComponentName(
                        "com.tencent.mobileqq",
                        "com.tencent.mobileqq.activity.JumpActivity");
                Log.e(TAG,componentName+"");
                break;
            case R.id.more:
                break;
        }
        if(needShare) {
            shareIntent = new Intent(Intent.ACTION_SEND);
            if (componentName != null) {
                shareIntent.setComponent(componentName);
            }
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            startActivity(shareIntent);
        }
        if(image !=null && !image.isRecycled()){
            image.recycle();
        }
    }

    public  Bitmap loadBitmapFromView(View v) {
        int i = v.getWidth();
        int j = v.getHeight();
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

    @Override
    protected void onResume() {
        if(uri != null) {
            PhotoShareActivity.this.getContentResolver().delete(uri, null, null);
            uri = null;
        }
        super.onResume();
    }
}
