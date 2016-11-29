package com.zui.notes;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zui.notes.adapter.NotesAdapter;
import com.zui.notes.db.NoteInfoColumns;
import com.zui.notes.model.NoteInfo;
import com.zui.notes.widget.DeletePopupWindow;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tvEdit;
    private TextView titleCancel;
    private TextView selectTitle;
    private TextView titleSelectAll;
    private RelativeLayout rlEditModeTitle;
    private RelativeLayout rlBottomDelete;
    private ImageView ivBottomDelete;
    private RelativeLayout rlNoData;
    private RecyclerView notesList;
    private Button addButton;
    private NotesAdapter notesAdapter;
    private List<NoteInfo> list;
    private Uri uri;
    private DataFailedObserver observer;
    private DeletePopupWindow deletePopupWindow;
    private int itemCount;
    private boolean selectAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAction();
        initData();
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
        notesList = (RecyclerView) findViewById(R.id.notes_list);
    }


    public void initAction() {
        tvEdit.setOnClickListener(this);
        titleCancel.setOnClickListener(this);
        titleSelectAll.setOnClickListener(this);
        rlBottomDelete.setOnClickListener(this);
        addButton.setOnClickListener(this);
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
                deletePopupWindow = new DeletePopupWindow(MainActivity.this,this,itemCount);
                deletePopupWindow.showAtLocation(MainActivity.this.findViewById(R.id.activity_main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.btn_add:
                startActivity(new Intent(MainActivity.this,EditActivity.class));
                break;
            case R.id.tv_pop_delete:
                deletePopupWindow.dismiss();
                deletePopupWindow=null;
                notesAdapter.deleteSelected();
            default:
                break;
        }

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
        }else {
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
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(rlBottomDelete.getVisibility()==View.VISIBLE){
                notesAdapter.setEditMode(false);
                return true;
            }
        }
            return super.onKeyDown(keyCode, event);
    }
}
