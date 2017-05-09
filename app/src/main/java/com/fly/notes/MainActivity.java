package com.fly.notes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fly.notes.adapter.NotesAdapter;
import com.fly.notes.db.Config;
import com.fly.notes.db.NoteInfoColumns;
import com.fly.notes.model.NoteInfo;
import com.fly.notes.widget.CustomDrawerLayout;
import com.fly.notes.widget.DeletePopupWindow;

import java.util.LinkedList;
import java.util.List;

import android.widget.Toast;

import at.markushi.ui.ActionView;
import at.markushi.ui.action.BackAction;
import at.markushi.ui.action.DrawerAction;

import com.nineoldandroids.view.ViewHelper;
import com.fly.notes.widget.MultiShapeView;

/**
 * Created by huangfei on 2016/11/9.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvEdit;
    private TextView titleCancel;
    private TextView selectTitle;
    private TextView titleSelectAll;
    private TextView textLock;
    private TextView textLogin;
    private ImageView imageLock;
    private ImageView imageLogin;
    private RelativeLayout rlEditModeTitle;
    private RelativeLayout rlBottomDelete;
    private ImageView ivBottomDelete;
    private RelativeLayout rlNoData;
    private LinearLayout llLock;
    private LinearLayout llLogin;
    private LinearLayout llupload;
    private LinearLayout lldownload;
    private LinearLayout llabout;
    private RecyclerView notesList;
    private Button addButton;
    private ActionView actionView;
    private CustomDrawerLayout customDrawerLayout;
    private NotesAdapter notesAdapter;
    private MultiShapeView userIcon;
    private List<NoteInfo> list;
    private Uri uri;
    private DataFailedObserver observer;
    private DeletePopupWindow deletePopupWindow;
    private int itemCount;
    private boolean selectAll = false;
    public static final int REQUEST_EXTERNAL_STORAGE_CODE = 1;
    private static final int REQUEST_CODE_LOCK = 2;
    private static final int REQUEST_CODE_LOGIN = 3;
    private NotesApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp = NotesApplication.getInstance();
        initView();
        initAction();
        initData();
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_CODE);
            } else if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_CODE);
            } else if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_CODE);
            }
        }
        super.onResume();
    }

    public void initData() {
        uri = Uri.parse("content://com.zui.notes/notes");
        list = new LinkedList<>();
        observer = new DataFailedObserver(new Handler());
        MainActivity.this.getContentResolver().registerContentObserver(uri, true, observer);
        notesList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        notesAdapter = new NotesAdapter(new NotesAdapter.CallBack() {
            @Override
            public void showSelectTitle(boolean show) {
                if (show) {
                    rlBottomDelete.setVisibility(View.VISIBLE);
                    rlEditModeTitle.setVisibility(View.VISIBLE);
                    addButton.setVisibility(View.GONE);
                } else {
                    rlBottomDelete.setVisibility(View.GONE);
                    rlEditModeTitle.setVisibility(View.GONE);
                    addButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void showNoData() {
                rlNoData.setVisibility(View.VISIBLE);
                tvEdit.setClickable(false);
                tvEdit.setTextColor(MainActivity.this.getResources().getColor(R.color.tv_main_activity_edit_text_color_text_color_enabled_false));
            }

            @Override
            public void setSelectCount(int count) {
                if (count > 0) {
                    itemCount = count;
                    selectTitle.setText(MainActivity.this.getResources().getString(R.string.choose_item) + count + MainActivity.this.getResources().getString(R.string.items));
                    rlBottomDelete.setClickable(true);
                    ivBottomDelete.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.delete));
                } else {
                    selectTitle.setText(MainActivity.this.getResources().getString(R.string.choose_item));
                    rlBottomDelete.setClickable(false);
                    ivBottomDelete.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.delete_disable));
                }
            }
        });
        notesList.setAdapter(notesAdapter);
        tvEdit.setClickable(false);
        tvEdit.setTextColor(MainActivity.this.getResources().getColor(R.color.tv_main_activity_edit_text_color_text_color_enabled_false));
        fillDataForDatabase();
        userIcon.setImageResource(R.drawable.userico);
        changeStatus();
    }

    public void initView() {
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        titleCancel = (TextView) findViewById(R.id.title_tv_cancel);
        selectTitle = (TextView) findViewById(R.id.title_edit_mode_title);
        titleSelectAll = (TextView) findViewById(R.id.title_edit_mode_select_all);
        rlEditModeTitle = (RelativeLayout) findViewById(R.id.title_edit_mode);
        rlBottomDelete = (RelativeLayout) findViewById(R.id.rl_bottom_delete);
        ivBottomDelete = (ImageView) findViewById(R.id.iv_bottom_delete);
        rlNoData = (RelativeLayout) findViewById(R.id.rl_activity_main_no_data);
        addButton = (Button) findViewById(R.id.btn_add);
        actionView = (ActionView) findViewById(R.id.action);
        customDrawerLayout = (CustomDrawerLayout) findViewById(R.id.drawerlayout);
        notesList = (RecyclerView) findViewById(R.id.notes_list);
        userIcon = (MultiShapeView) findViewById(R.id.user_ico);
        llLock = (LinearLayout) findViewById(R.id.ll_lock);
        textLock = (TextView) findViewById(R.id.tv_lock);
        imageLock = (ImageView) findViewById(R.id.ima_lock);
        llLogin = (LinearLayout) findViewById(R.id.ll_login_logout);
        textLogin = (TextView) findViewById(R.id.tv_login);
        imageLogin = (ImageView) findViewById(R.id.ima_login);
        llupload = (LinearLayout) findViewById(R.id.ll_upload);
        lldownload = (LinearLayout) findViewById(R.id.ll_download);
        llabout = (LinearLayout) findViewById(R.id.ll_about);
    }


    public void initAction() {
        tvEdit.setOnClickListener(this);
        titleCancel.setOnClickListener(this);
        titleSelectAll.setOnClickListener(this);
        rlBottomDelete.setOnClickListener(this);
        addButton.setOnClickListener(this);
        llLock.setOnClickListener(this);
        llLogin.setOnClickListener(this);
        llupload.setOnClickListener(this);
        lldownload.setOnClickListener(this);
        llabout.setOnClickListener(this);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = actionView.getAction() instanceof BackAction ? 1 : 0;
                switch (type) {
                    case 1:
                        customDrawerLayout.closeDrawers();
                        break;
                    case 0:
                        customDrawerLayout.openDrawer(Gravity.LEFT);
                        break;
                    default:
                        break;
                }
            }
        });
        customDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = customDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 1 - 0.3f * scale;
                ViewHelper.setScaleX(mMenu, leftScale);
                ViewHelper.setScaleY(mMenu, leftScale);
                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent,
                        mMenu.getMeasuredWidth() * (1 - scale));
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent,
                        mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                actionView.setAction(new BackAction(), ActionView.ROTATE_COUNTER_CLOCKWISE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                actionView.setAction(new DrawerAction(), ActionView.ROTATE_COUNTER_CLOCKWISE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                if (list != null || !list.isEmpty()) {
                    notesAdapter.setEditMode(true);
                }
                break;
            case R.id.title_tv_cancel:
                selectAll = false;
                titleSelectAll.setText(R.string.select_all);
                notesAdapter.setEditMode(false);
                break;
            case R.id.title_edit_mode_select_all:
                selectAll = !selectAll;
                if (selectAll) {
                    titleSelectAll.setText(R.string.deselect);
                } else {
                    titleSelectAll.setText(R.string.select_all);
                }
                notesAdapter.selectAll(selectAll);
                break;
            case R.id.rl_bottom_delete:
                deletePopupWindow = new DeletePopupWindow(MainActivity.this, this, itemCount);
                deletePopupWindow.showAtLocation(MainActivity.this.findViewById(R.id.activity_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_add:
                startActivity(new Intent(MainActivity.this, EditActivity.class));
                break;
            case R.id.tv_pop_delete:
                deletePopupWindow.dismiss();
                deletePopupWindow = null;
                notesAdapter.deleteSelected();
                break;
            case R.id.ll_lock:
                toSetLock();
                break;
            case R.id.ll_login_logout:
                toLogin();
                break;
            case R.id.ll_upload:
                break;
            case R.id.ll_download:
                break;
            case R.id.ll_about:
                break;
            default:
                break;
        }

    }

    private void toSetLock() {
        Intent intent;
        if (myApp.getSettings() == null || "".equals(myApp.getSettings().getGesture())) {
            intent = new Intent(MainActivity.this, LockOnActivity.class);
        } else {
            intent = new Intent(MainActivity.this, LockOffActivity.class);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCK);
    }

    private void changeStatus() {
        if (myApp.getSettings() == null || "".equals(myApp.getSettings().getGesture())) {
            textLock.setText(R.string.lockon);
            imageLock.setImageResource(R.drawable.lockon);
        } else {
            textLock.setText(R.string.lockoff);
            imageLock.setImageResource(R.drawable.lockoff);
        }
    }

    /**
     * 存手势设置
     */
    private void savePattern() {
        Config.setGestureSettings(myApp.getSettings());
    }

    private void toLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent,REQUEST_CODE_LOGIN);
    }

    private void fillDataForDatabase() {
        list.clear();
        Cursor cursor = MainActivity.this.getContentResolver().query(uri, null, null, null, NoteInfoColumns.MODIFIED_TIME + " DESC");
        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                NoteInfo noteInfo = new NoteInfo();
                noteInfo._id = cursor.getLong(cursor.getColumnIndex(NoteInfoColumns._ID));
                noteInfo.body = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.BODY));
                noteInfo.modifiedTime = cursor.getLong(cursor.getColumnIndex(NoteInfoColumns.MODIFIED_TIME));
                noteInfo.title = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.TITLE));
                noteInfo.summary = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.SUMMARY));
                noteInfo.firstPicPath = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.FIRST_PIC_PATH));
                list.add(noteInfo);
            }
            cursor.close();
        }
        notesAdapter.setList(list);
        notesAdapter.notifyDataSetChanged();
        if (list.isEmpty()) {
            rlNoData.setVisibility(View.VISIBLE);
            tvEdit.setClickable(false);
            tvEdit.setTextColor(MainActivity.this.getResources().getColor(R.color.tv_main_activity_edit_text_color_text_color_enabled_false));
        } else {
            rlNoData.setVisibility(View.GONE);
            tvEdit.setClickable(true);
            tvEdit.setTextColor(MainActivity.this.getResources().getColor(R.color.tv_main_activity_edit_text_color));
        }
    }

    private class DataFailedObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DataFailedObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            fillDataForDatabase();
        }
    }

    @Override
    protected void onDestroy() {
        MainActivity.this.getContentResolver().unregisterContentObserver(observer);
        myApp.setLockTime(0);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (rlBottomDelete.getVisibility() == View.VISIBLE) {
                notesAdapter.setEditMode(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
            notesAdapter.notifyDataSetChanged();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_LOCK:
                if (resultCode == RESULT_OK) {
                    changeStatus();
                    savePattern();
                }
                break;
            case REQUEST_CODE_LOGIN:
                break;
            default:
                break;
        }
    }

}
