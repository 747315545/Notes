package com.fly.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fly.notes.db.NotesUser;
import com.fly.notes.util.ErrorCode;
import com.fly.notes.util.ToastUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by huangfei on 2017/5/9.
 */

public class RegistrationActivity extends BaseActivity implements View.OnClickListener {
    protected static final String TAG = RegistrationActivity.class.getSimpleName();
    private EditText registUsername;
    private EditText registEmail;
    private EditText registPassword1;
    private EditText registPassword2;
    private RelativeLayout rlLoading;
    private Button regist;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disablePatternLock(false);
        setContentView(R.layout.activity_registration);
        initView();
        initAction();
    }

    private void initView() {
        registUsername = (EditText) findViewById(R.id.ed_registusername);
        registEmail = (EditText) findViewById(R.id.ed_registemail);
        registPassword1 = (EditText) findViewById(R.id.ed_registpassword1);
        registPassword2 = (EditText) findViewById(R.id.ed_registpassword2);
        regist = (Button) findViewById(R.id.btn_regist);
        back = (ImageView) findViewById(R.id.ima_regist_back);
        rlLoading = (RelativeLayout) findViewById(R.id.rl_regist_loading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist:
                toRegist();
                break;
            case R.id.ima_regist_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void initAction() {
        regist.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void toRegist() {
        String username = registUsername.getText().toString().trim();
        String email = registEmail.getText().toString().trim();
        String password1 = registPassword1.getText().toString().trim();
        String password2 = registPassword2.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.toasteusername));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.toastemail));
            return;
        }
        if (TextUtils.isEmpty(password1)) {
            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.toastpassword1));
            return;
        }
        if (!password1.equals(password2)) {
            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.toastpassword2));
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
        NotesUser user = new NotesUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password1);
        user.signUp(new SaveListener<NotesUser>() {

            @Override
            public void done(NotesUser notesUser, BmobException e) {
                handler.sendEmptyMessage(0);
                if (e == null) {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    int code = e.getErrorCode();
                    switch (code) {
                        case ErrorCode.EMAIL_ADDRESS_INVALID:
                            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.error301));
                            break;
                        case ErrorCode.NETWORK_DISABLE:
                            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.error9016));
                            break;
                        case ErrorCode.EMAIL_EMAIL_USED:
                            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.error202));
                            break;
                        case ErrorCode.EMAIL_ADDRESS_USED:
                            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.error203));
                            break;
                        default:
                            ToastUtil.INSTANCE.makeToast(RegistrationActivity.this, getResources().getText(R.string.registerror));
                    }
                }
            }

        });
    }

}
