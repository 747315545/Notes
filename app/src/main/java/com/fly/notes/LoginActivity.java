package com.fly.notes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fly.notes.widget.MultiShapeView;

/**
 * Created by huangfei on 2017/5/7.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private MultiShapeView ico;
    private EditText userName;
    private EditText passWord;
    private Button login;
    private Button regist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        disablePatternLock(false);
        initView();
        ico.setImageResource(R.drawable.userico);
    }

    private void initView(){
        ico = (MultiShapeView) findViewById(R.id.login_ico);
        userName = (EditText) findViewById(R.id.ed_username);
        passWord = (EditText) findViewById(R.id.ed_password);
        login = (Button) findViewById(R.id.btn_login);
        regist = (Button) findViewById(R.id.btn_regist);
    }

    @Override
    public void onClick(View v) {

    }
}
