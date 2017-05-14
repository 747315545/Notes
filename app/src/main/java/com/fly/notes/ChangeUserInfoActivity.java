package com.fly.notes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.fly.notes.db.NotesUser;
import com.fly.notes.util.ErrorCode;
import com.fly.notes.util.ImageUtils;
import com.fly.notes.util.ToastUtil;
import com.fly.notes.widget.CircleImageView;
import java.io.File;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by huangfei on 2017/5/14.
 */

public class ChangeUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private CircleImageView userico;
    private EditText edUsername;
    private EditText edRawpassword;
    private EditText edNewPassword1;
    private EditText edNewPassword2;
    private Button change;
    private RelativeLayout rlLoading;
    private AlertDialog photoDialog;
    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 10;
    private File tempFile = null;
    private Bitmap bitmap;
    BmobFile bmobFile;
    private String username;
    private String rawpassword;
    private String newpassword1;
    private String newpassword2;
    private NotesUser user;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeuserinfo);
        initView();
        initAction();
        Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + PHOTO_IMAGE_FILE_NAME);
        if (bitmap != null) {
            userico.setImageBitmap(bitmap);
        } else {
            userico.setImageResource(R.drawable.userico);
        }
        handler = new MyHandler();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.ima_change_back);
        userico = (CircleImageView) findViewById(R.id.changeuserico);
        edUsername = (EditText) findViewById(R.id.ed_changeusername);
        edRawpassword = (EditText) findViewById(R.id.ed_rawpassword);
        edNewPassword1 = (EditText) findViewById(R.id.ed_newpassword1);
        edNewPassword2 = (EditText) findViewById(R.id.ed_newpassword2);
        change = (Button) findViewById(R.id.btn_change);
        rlLoading = (RelativeLayout) findViewById(R.id.rl_change_loading);
    }

    private void initAction() {
        back.setOnClickListener(this);
        userico.setOnClickListener(this);
        change.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ima_change_back:
                finish();
                break;
            case R.id.changeuserico:
                showDialog();
                break;
            case R.id.btn_change:
                updateUser();
                break;
        }
    }

    private void showDialog() {
        photoDialog = new AlertDialog.Builder(ChangeUserInfoActivity.this).create();
        photoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        photoDialog.show();
        Window window = photoDialog.getWindow();
        window.setContentView(R.layout.dialog_photo);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        window.setWindowAnimations(R.style.popupWindowAnim);
        WindowManager.LayoutParams lp = photoDialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;
        photoDialog.getWindow().setAttributes(lp);

        photoDialog.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCamera();
            }
        });
        photoDialog.findViewById(R.id.btn_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPicture();
            }
        });
        photoDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialog.dismiss();
            }
        });
    }

    public void toCamera() {
        requestWESPermission();
        photoDialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void requestWESPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(ChangeUserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ChangeUserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    Toast.makeText(ChangeUserInfoActivity.this, "Need write external storage permission.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(ChangeUserInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            } else {
            }
        } else {
        }
    }

    /**
     * 跳转相册
     */
    private void toPicture() {
        photoDialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    /**
     * 裁剪
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.e("", "裁剪uri == null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }


    private void updateUser() {
        rlLoading.setVisibility(View.VISIBLE);
        user = NotesUser.getCurrentUser(NotesUser.class);
        handler.init();
        username = edUsername.getText().toString().trim();
        rawpassword = edRawpassword.getText().toString().trim();
        newpassword1 = edNewPassword1.getText().toString().trim();
        newpassword2 = edNewPassword2.getText().toString().trim();
        updateUserinfo();
    }

    private void updateUserinfo(){
        if (!TextUtils.isEmpty(rawpassword)) {
            boolean b = true;
            if (TextUtils.isEmpty(newpassword1)) {
                ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.toastchangepassword1));
                b = false;
            }
            if (TextUtils.isEmpty(newpassword2)) {
                ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.toastchangepassword1));
                b = false;
            }
            if (!newpassword1.equals(newpassword2)) {
                ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.toastchangepassword2));
                b = false;
            }
            if (b) {
                user.updateCurrentUserPassword(rawpassword, newpassword1, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            handler.sendEmptyMessage(0);
                            updateUserico();
                        } else {
                            if (e.getErrorCode() == ErrorCode.NETWORK_DISABLE) {
                                ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.error9016));
                            } else {
                                ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.toastchangeerror));
                            }
                        }
                    }
                });
            }
        } else {
            handler.sendEmptyMessage(0);
            updateUserico();
        }

    }


    private void updateUserico() {
        if(bitmap!=null) {
            tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), PHOTO_IMAGE_FILE_NAME);
            if(tempFile.exists()){
                tempFile.delete();
            }
            ImageUtils.compressImage(bitmap, tempFile);
            bmobFile = new BmobFile(tempFile);
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        user.setAvatar(bmobFile);
                    }
                    updateUsername();
                    handler.sendEmptyMessage(0);
                }
            });
        }else {
            handler.sendEmptyMessage(0);
            updateUsername();
        }
    }

    private void updateUsername() {
        if(!TextUtils.isEmpty(username)){
            user.setUsername(username);
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    handler.sendEmptyMessage(0);
                }
            });
        }else {
            handler.sendEmptyMessage(0);
        }

    }

    private void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            final Bitmap bitmap = bundle.getParcelable("data");
            tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), PHOTO_IMAGE_FILE_NAME);
            ImageUtils.compressImage(bitmap, tempFile);
            final BmobFile bmobFile = new BmobFile(tempFile);
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        NotesUser user = BmobUser.getCurrentUser(NotesUser.class);
                        user.setAvatar(bmobFile);
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.avatar_editor_success));
                                } else {
                                    ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.avatar_editor_failure));
                                }
                            }
                        });
                    } else {
                        ToastUtil.INSTANCE.makeToast(ChangeUserInfoActivity.this, getResources().getText(R.string.avatar_editor_failure));
                    }
                }

                @Override
                public void onProgress(Integer value) {
                    super.onProgress(value);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            case CAMERA_REQUEST_CODE:
                tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), PHOTO_IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(tempFile));
                break;
            case RESULT_REQUEST_CODE:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        bitmap = bundle.getParcelable("data");
                        if (bitmap != null) {
                            userico.setImageBitmap(bitmap);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private class MyHandler extends Handler{
            int max =3;
            int i=0;
        public void init(){
            i=0;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            i++;
            if(i==max){
                rlLoading.setVisibility(View.GONE);
                Intent intent = new Intent(ChangeUserInfoActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
