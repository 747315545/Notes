package com.fly.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fly.notes.db.NoteInfoColumns;
import com.fly.notes.model.MyList;
import com.fly.notes.model.NoteInfo;
import com.fly.notes.util.ImageUtils;
import com.fly.notes.util.Utils;
import com.fly.notes.widget.CheckboxLayout;
import com.fly.notes.widget.DeletePopupWindow;
import com.fly.notes.widget.FireworkView;
import com.fly.notes.widget.ImageLayout;
import com.fly.notes.widget.StrokeImageView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by huangfei on 2016/11/9.
 */

public class EditActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, View.OnLayoutChangeListener {
    private NoteInfo note;
    private ImageView ivEditBack;
    private TextView tvTitle;
    private TextView tvTitleFinish;
    private ImageView ivShowBack;
    private TextView tvTitleDate;
    private TextView tvTitleTime;
    private ImageView ivShare;
    private RelativeLayout rlNewTitle;
    private RelativeLayout rlShowTitle;
    private LinearLayout llEditContent;
    private LinearLayout llContentParent;
    private LinearLayout llActivityBottom;
    private ImageView ivDelete;
    private ImageView ivPic;
    private ImageView ivCamera;
    private ImageView ivCheck;
    private FireworkView fireworkView;
    private static final int NEW_MODE = 1;
    private static final int SHOW_MODE = 2;
    private static final int RESULT_CODE = 1;
    private static final int CODE_CAMERA = 2;
    private int mode;
    private boolean isKeyboardShowed = false;
    private InputMethodManager inputMethodManager;
    private int keyHeight = 0;
    private View activityRootView;
    private DeletePopupWindow deletePopupWindow;
    private boolean isDeleted = false;
    private boolean fillCompleted = false;
    private File file = null;
    List<View> viewList = new MyList();
    List<String> picPath = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initView();
        initAction();
        keyHeight = this.getWindowManager().getDefaultDisplay().getHeight() / 3;
        note = (NoteInfo) getIntent().getSerializableExtra("noteInfo");
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (note == null) {
            mode = NEW_MODE;
            fillNote();
            llActivityBottom.setVisibility(View.VISIBLE);
            rlShowTitle.setVisibility(View.GONE);
        } else {
            mode = SHOW_MODE;
            rlNewTitle.setVisibility(View.GONE);
            rlShowTitle.setVisibility(View.VISIBLE);
            llActivityBottom.setVisibility(View.GONE);
        }
        addView();
        fillCompleted = true;
    }

    private void addView() {
        if (mode == NEW_MODE) {
            tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color_enabled_false));
            tvTitleFinish.setClickable(false);
            addCheckBoxLayout(false, false, null);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            tvTitleDate.setText(Utils.getYearMonthDay(note.modifiedTime));
            tvTitleTime.setText(Utils.getHourMinute(note.modifiedTime));
            fillViewByBody(note.body);
            llEditContent.requestFocus();
        }
        EditText editText = ((EditText) ((ViewGroup) viewList.get(0)).getChildAt(1));
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    fireworkView.bindEditText((EditText) view);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0 && viewList.toString().equals("000")) {
                    tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color_enabled_false));
                    tvTitleFinish.setClickable(false);
                } else {
                    tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color));
                    tvTitleFinish.setClickable(true);
                }
                if (fillCompleted) {
                    float[] coordinate = fireworkView.getCursorCoordinate();
                    fireworkView.launch(coordinate[0], coordinate[1], i1 == 0 ? -1 : 1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initView() {
        ivEditBack = (ImageView) findViewById(R.id.edit_activity_iv_title_new_edit_back_arrow);
        tvTitle = (TextView) findViewById(R.id.tv_title_edit_or_new);
        tvTitleFinish = (TextView) findViewById(R.id.tv_edit_activity_title_finish);
        ivShowBack = (ImageView) findViewById(R.id.edit_activity_iv_title_show_back_arrow);
        tvTitleDate = (TextView) findViewById(R.id.edit_activity_tv_title_date);
        tvTitleTime = (TextView) findViewById(R.id.edit_activity_tv_title_time);
        rlNewTitle = (RelativeLayout) findViewById(R.id.edit_activity_new_edit);
        rlShowTitle = (RelativeLayout) findViewById(R.id.edit_activity_title_show);
        ivShare = (ImageView) findViewById(R.id.edit_activity_iv_share);
        llEditContent = (LinearLayout) findViewById(R.id.ll_edit_content);
        llContentParent = (LinearLayout) findViewById(R.id.ll_content_parent);
        llActivityBottom = (LinearLayout) findViewById(R.id.edit_activity_bottom_ll);
        activityRootView = findViewById(R.id.rl_edit_activity);
        ivDelete = (ImageView) findViewById(R.id.edit_activity_iv_delete);
        ivPic = (ImageView) findViewById(R.id.edit_activity_iv_pic);
        ivCamera = (ImageView) findViewById(R.id.edit_activity_iv_camera);
        ivCheck = (ImageView) findViewById(R.id.edit_activity_iv_check);
        fireworkView = (FireworkView) findViewById(R.id.fire_work);
    }

    private void initAction() {
        ivEditBack.setOnClickListener(this);
        tvTitleFinish.setOnClickListener(this);
        ivShowBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        llContentParent.setOnTouchListener(this);
        activityRootView.addOnLayoutChangeListener(this);
        ivDelete.setOnClickListener(this);
        ivPic.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBoxShowOrAdd(getFocusedPosition());
                tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color));
                tvTitleFinish.setClickable(true);
            }
        });
    }

    private void fillNote() {
        note = new NoteInfo();
        note._id = System.currentTimeMillis();
        note.modifiedTime = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        if (isKeyboardShowed)
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        switch (v.getId()) {
            case R.id.edit_activity_iv_title_new_edit_back_arrow:
                finish();
                break;
            case R.id.tv_edit_activity_title_finish:
                ((ViewGroup) viewList.get(0)).getChildAt(1).requestFocus();
                llEditContent.requestFocus();
                rlNewTitle.setVisibility(View.GONE);
                rlShowTitle.setVisibility(View.VISIBLE);
                llActivityBottom.setVisibility(View.GONE);
                if (mode == NEW_MODE) {
                    tvTitleDate.setText(Utils.getYearMonthDay(System.currentTimeMillis()));
                    tvTitleTime.setText(Utils.getHourMinute(System.currentTimeMillis()));
                } else {
                    if (!note.body.equals(viewList.toString())) {
                        tvTitleDate.setText(Utils.getYearMonthDay(System.currentTimeMillis()));
                        tvTitleTime.setText(Utils.getHourMinute(System.currentTimeMillis()));
                    }
                }
                break;
            case R.id.edit_activity_iv_share:
                Intent intent = new Intent(EditActivity.this, PhotoShareActivity.class);
                intent.putExtra("data", viewList.toString());
                intent.putExtra("id", note._id);
                EditActivity.this.startActivity(intent);
                EditActivity.this.overridePendingTransition(R.anim.activity_push_in, R.anim.fake_anim);
                break;
            case R.id.edit_activity_iv_title_show_back_arrow:
                finish();
                break;
            case R.id.edit_activity_iv_delete:
                deletePopupWindow = new DeletePopupWindow(EditActivity.this, this, 1);
                deletePopupWindow.showAtLocation(EditActivity.this.findViewById(R.id.rl_edit_activity), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.edit_activity_iv_pic:
                Intent intentForPic = new Intent();
                intentForPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intentForPic.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentForPic, RESULT_CODE);
                break;
            case R.id.edit_activity_iv_camera:
                file = new File(getSavePath());
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                Intent intentForCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentForCamera.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentForCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                Uri uri = FileProvider.getUriForFile(EditActivity.this, "com.zui.notes.fileProvider", file);
                intentForCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intentForCamera, CODE_CAMERA);
                break;
            case R.id.tv_pop_delete:
                deletePopupWindow.dismiss();
                deletePopupWindow = null;
                getContentResolver().delete(Uri.parse("content://com.zui.notes/notes"), "_id=?", new String[]{note._id + ""});
                ImageUtils.deleteImagePath(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + note._id);
                isDeleted = true;
                finish();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getRawX();
                float y = event.getRawY();
                fireworkView.launch(x, y, 1);
                if (getFocusedPosition() == -1) {
                    ((ViewGroup) viewList.get(viewList.size() - 1)).getChildAt(1).requestFocus();
                }
                if (!isKeyboardShowed) {
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
        }
        return true;
    }

    private void saveData() {
        if (mode == NEW_MODE) {
            if (!viewList.toString().equals("000")) {
                note.body = viewList.toString();
                if (((MyList) viewList).getList().get(0).length() == 3)
                    note.title = EditActivity.this.getResources().getString(R.string.no_title);
                else
                    note.title = ((MyList) viewList).getList().get(0).substring(3);
                if (((MyList) viewList).getList().size() == 1) {
                    note.summary = "";
                } else {
                    note.summary = ((MyList) viewList).getList().get(1);
                }
                if (!picPath.isEmpty())
                    note.firstPicPath = picPath.get(0);
                else
                    note.firstPicPath = "";
                insertOrUpdate(note);
            } else {
                ImageUtils.deleteImagePath(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + note._id);
            }
        } else {
            if (viewList.toString().equals("000")) {
                getContentResolver().delete(Uri.parse("content://com.zui.notes/notes"), "_id=?", new String[]{note._id + ""});
                ImageUtils.deleteImagePath(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + note._id);
            } else if (!note.body.equals(viewList.toString())) {
                note.body = viewList.toString();
                if (((MyList) viewList).getList().get(0).length() == 3)
                    note.title = EditActivity.this.getResources().getString(R.string.no_title);
                else
                    note.title = ((MyList) viewList).getList().get(0).substring(3);
                if (((MyList) viewList).getList().size() == 1)
                    note.summary = "";
                else
                    note.summary = ((MyList) viewList).getList().get(1);
                note.modifiedTime = System.currentTimeMillis();
                if (!picPath.isEmpty())
                    note.firstPicPath = picPath.get(0);
                else
                    note.firstPicPath = "";
                insertOrUpdate(note);
            }
        }
    }

    private void insertOrUpdate(NoteInfo note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteInfoColumns._ID, note._id);
        contentValues.put(NoteInfoColumns.MODIFIED_TIME, note.modifiedTime);
        contentValues.put(NoteInfoColumns.BODY, note.body);
        contentValues.put(NoteInfoColumns.TITLE, note.title);
        contentValues.put(NoteInfoColumns.SUMMARY, note.summary);
        contentValues.put(NoteInfoColumns.FIRST_PIC_PATH, note.firstPicPath);
        if (mode == NEW_MODE) {
            getContentResolver().insert(Uri.parse("content://com.zui.notes/notes"), contentValues);
        } else {
            getContentResolver().update(Uri.parse("content://com.zui.notes/notes"), contentValues, "_id=?", new String[]{note._id + ""});
        }
    }

    @Override
    protected void onDestroy() {
        if (!isDeleted) {
            saveData();
        }
        super.onDestroy();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            isKeyboardShowed = true;
            if (llActivityBottom.getVisibility() != View.VISIBLE)
                llActivityBottom.setVisibility(View.VISIBLE);
            rlNewTitle.setVisibility(View.VISIBLE);
            rlShowTitle.setVisibility(View.GONE);
            if (mode == SHOW_MODE) {
                tvTitle.setText(EditActivity.this.getResources().getString(R.string.edit_note));
            } else {
                tvTitle.setText(EditActivity.this.getResources().getString(R.string.new_note));
            }
            if (viewList.toString().equals("000")) {
                tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color_enabled_false));
                tvTitleFinish.setClickable(false);
            } else {
                tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color));
                tvTitleFinish.setClickable(true);
            }
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            isKeyboardShowed = false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int k = getResources().getDimensionPixelSize(R.dimen.iv_edit_activity_width);
        if (requestCode == RESULT_CODE && data != null) {
            Uri uri = data.getData();
            try {
                file = new File(getSavePath());
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (ImageUtils.SmallBitmap(getRealPath(uri), k, file)) {
                    addPicLayout(getSavePath());
                    addCheckBoxLayout(false, false, null);
                    if (mode == NEW_MODE) {
                        tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color));
                        tvTitleFinish.setClickable(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CODE_CAMERA) {
            if (ImageUtils.SmallBitmap(getSavePath(), k, file)) {
                addPicLayout(getSavePath());
                addCheckBoxLayout(false, false, null);
                if (mode == NEW_MODE) {
                    tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color));
                    tvTitleFinish.setClickable(true);
                }
            }
        }
        file = null;
    }


    private void addPicLayout(String path) {
        Bitmap paramBitmap = BitmapFactory.decodeFile(path);
        final ImageLayout relativeLayout = new ImageLayout(this);
        int n = getResources().getDimensionPixelSize(R.dimen.ev_edit_activity_padding);
        int m = getResources().getDimensionPixelSize(R.dimen.ev_edit_activity_padding_bottom);
        relativeLayout.setPadding(0, n, 0, 0);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.title_bg));
        int k = getResources().getDimensionPixelSize(R.dimen.iv_edit_activity_width);
        int l = getResources().getDimensionPixelSize(R.dimen.iv_edit_activity_pic_width);
        float f = l * 1.0F / paramBitmap.getWidth();
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        if (i / j > 20) f = l * 1.0F / i;
        i = (int) (paramBitmap.getHeight() * 1.0F * f);
        j = getResources().getDimensionPixelSize(R.dimen.add_pic_stroke_width);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(n * 2 + k, i + n + m));
        final StrokeImageView imageView;
        imageView = new StrokeImageView(this);
        i += j * 2;
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(k, i));
        imageView.setBackgroundColor(getResources().getColor(R.color.pic_bg_color));
        imageView.setImageBitmap(paramBitmap);
        imageView.picFolderAndName = path;
        picPath.add(path);
        relativeLayout.addView(imageView);
        final EditText editText = new EditText(this);
        editText.setTextSize(i);
        editText.setBackgroundColor(getResources().getColor(R.color.pic_bg_color));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.add_pic_edit_text_width), i);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        editText.setLayoutParams(layoutParams);
        editText.setBackgroundColor(getResources().getColor(R.color.pic_line_color));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(0)});
        relativeLayout.addView(editText);
        int position = getFocusedPosition();
        viewList.add(position + 1, relativeLayout);
        llEditContent.addView(relativeLayout, position + 1);
        editText.requestFocus();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, PhotoViewActivity.class);
                intent.putExtra("pic_path", imageView.picFolderAndName);
                EditActivity.this.startActivity(intent);
                EditActivity.this.overridePendingTransition(R.anim.center_zoom_in, 0);
            }
        });
    }

    private String getSavePath() {
        if (file != null) {
            return file.getAbsolutePath();
        } else
            return getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString()
                    + File.separator
                    + note._id
                    + File.separator
                    + System.currentTimeMillis()
                    + ".jpg";
    }

    private void addCheckBoxLayout(boolean isCheckBoxShow, boolean isChecked, String text) {
        final CheckboxLayout relativeLayout = (CheckboxLayout) LayoutInflater.from(EditActivity.this).inflate(R.layout.check_layout, null);
        final CheckBox checkBox = (CheckBox) relativeLayout.findViewById(R.id.cb_edit_activity);
        final EditText editText = (EditText) relativeLayout.findViewById(R.id.ev_check_edit_activity);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editText.setTextColor(EditActivity.this.getResources().getColor(R.color.ev_edit_activity_check_text_color));
                } else {
                    editText.setTextColor(EditActivity.this.getResources().getColor(R.color.ev_edit_activity_text_color));
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (fillCompleted) {
                    float[] coordinate = fireworkView.getCursorCoordinate();
                    fireworkView.launch(coordinate[0], coordinate[1], i1 == 0 ? -1 : 1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    fireworkView.bindEditText((EditText) view);
                }
            }
        });
        if (isCheckBoxShow) {
            checkBox.setChecked(isChecked);
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        if (text != null) {
            editText.setText(text);
        }
        int i = getFocusedPosition();
        if (i != -1) {
            viewList.add(i + 1, relativeLayout);
            llEditContent.addView(relativeLayout, i + 1);
        } else {
            viewList.add(relativeLayout);
            llEditContent.addView(relativeLayout);
        }
        editText.requestFocus();
    }

    private void fillViewByBody(String string) {
        if (string.length() > 2) {
            String[] str = string.split(":");
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
                    addCheckBoxLayout(a, b, str[i].substring(3));
                } else {
                    addPicLayout(str[i].substring(1));
                }
            }
        }
    }

    private String getRealPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String path = "";
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }

    private int getFocusedPosition() {
        for (int i = 0; i < viewList.size(); i++) {
            if (((ViewGroup) viewList.get(i)).getChildAt(1).isFocused()) {
                return i;
            }
        }
        return -1;
    }

    private void CheckBoxShowOrAdd(int i) {
        View view = viewList.get(i);
        if (view instanceof CheckboxLayout && ((CheckboxLayout) view).getChildAt(0).getVisibility() == View.GONE) {
            ((CheckboxLayout) view).getChildAt(0).setVisibility(View.VISIBLE);
        } else {
            addCheckBoxLayout(true, false, null);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            if (((EditText) ((ViewGroup) llEditContent.getFocusedChild()).getChildAt(1)).getText().length() == 0) {
                removeLayoutOrCheckbox();
                if (viewList.toString().equals("000")) {
                    tvTitleFinish.setTextColor(EditActivity.this.getResources().getColor(R.color.tv_edit_activity_title_finish_text_color_enabled_false));
                    tvTitleFinish.setClickable(false);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void removeLayoutOrCheckbox() {
        int position = getFocusedPosition();
        if (position != -1) {
            View view = ((ViewGroup) viewList.get(position)).getChildAt(0);
            if (view instanceof StrokeImageView) {
                picPath.remove(((StrokeImageView) view).picFolderAndName);
                ImageUtils.deleteImagePath(((StrokeImageView) view).picFolderAndName);
                llEditContent.removeViewAt(position);
                viewList.remove(position);
                ((ViewGroup) viewList.get(position - 1)).getChildAt(1).requestFocus();
            } else {
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                } else {
                    if (position != 0) {
                        llEditContent.removeViewAt(position);
                        viewList.remove(position);
                        ((ViewGroup) viewList.get(position - 1)).getChildAt(1).requestFocus();
                    }
                }
            }
        }
    }
}