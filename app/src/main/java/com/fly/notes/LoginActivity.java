package com.fly.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eftimoff.androipathview.PathView;
import com.fly.notes.db.NotesUser;
import com.fly.notes.util.ErrorCode;
import com.fly.notes.util.ImageUtils;
import com.fly.notes.util.ToastUtil;
import com.fly.notes.widget.CircleImageView;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by huangfei on 2017/5/7.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private PathView ico;
    private ImageView back;
    private EditText userEmail;
    private EditText passWord;
    private Button login;
    private Button regist;
    private RelativeLayout rlLoading;
    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        disablePatternLock(false);
        initView();
        initAction();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.ima_login_back);
        ico = (PathView) findViewById(R.id.login_ico);
        userEmail = (EditText) findViewById(R.id.ed_username);
        passWord = (EditText) findViewById(R.id.ed_password);
        login = (Button) findViewById(R.id.btn_login);
        regist = (Button) findViewById(R.id.btn_regist);
        rlLoading = (RelativeLayout) findViewById(R.id.rl_login_loading);
        ico.getPathAnimator().delay(500)
                .duration(1000)
                .start();
    }

    private void initAction() {
        back.setOnClickListener(this);
        login.setOnClickListener(this);
        regist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ima_login_back:
                finish();
                break;
            case R.id.btn_login:
                toLogin();
                break;
            case R.id.btn_regist:
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void toLogin() {
        String useremail = userEmail.getText().toString().trim();
        String password = passWord.getText().toString().trim();
        if (TextUtils.isEmpty(useremail) || TextUtils.isEmpty(password)) {
            ToastUtil.INSTANCE.makeToast(LoginActivity.this, getResources().getText(R.string.toastemailpasswordnotnull));
            return;
        }
        rlLoading.setVisibility(View.VISIBLE);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                rlLoading.setVisibility(View.GONE);
                super.handleMessage(msg);
            }
        };
        NotesUser.loginByAccount(useremail, password, new LogInListener<NotesUser>() {
            @Override
            public void done(NotesUser notesUser, BmobException e) {
                if (e == null) {
                    BmobFile file = notesUser.getAvatar();
                    if (file != null) {
                        file.download(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + PHOTO_IMAGE_FILE_NAME), new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                handler.sendEmptyMessage(0);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
                    } else {
                        handler.sendEmptyMessage(0);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    handler.sendEmptyMessage(0);
                    int errorcode = e.getErrorCode();
                    switch (errorcode) {
                        case ErrorCode.LOGIN_INCORRECT:
                            ToastUtil.INSTANCE.makeToast(LoginActivity.this, getResources().getText(R.string.error101));
                            break;
                        case ErrorCode.NETWORK_DISABLE:
                            ToastUtil.INSTANCE.makeToast(LoginActivity.this, getResources().getText(R.string.error9016));
                            break;
                        default:
                            ToastUtil.INSTANCE.makeToast(LoginActivity.this, getResources().getText(R.string.loginerror));
                    }
                }
            }
        });
    }
}
