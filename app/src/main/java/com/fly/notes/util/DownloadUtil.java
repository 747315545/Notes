package com.fly.notes.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.fly.notes.R;
import com.fly.notes.db.NoteChangeType;
import com.fly.notes.db.NoteInfoColumns;
import com.fly.notes.db.NotesUser;
import com.fly.notes.model.NoteInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by huangfei on 2017/5/13.
 */

public class DownloadUtil {
    private Context mcontext;
    private Handler mhandler;
    private final static Uri uri = Uri.parse("content://com.fly.notes/notes");

    public DownloadUtil(Context context, Handler handler) {
        mcontext = context;
        mhandler = handler;
    }

    public void downloadBatch() {
        BmobQuery bmobQuery = new BmobQuery();
        bmobQuery.addWhereEqualTo("author", NotesUser.getCurrentUser(NotesUser.class));
        bmobQuery.findObjects(new FindListener<NoteInfo>() {
            @Override
            public void done(List<NoteInfo> list, BmobException e) {
                if (e == null) {
                    swtichDownload(list);
                } else {
                    ToastUtil.INSTANCE.makeToast(mcontext, mcontext.getResources().getText(R.string.toastuploaderror));
                    mhandler.sendEmptyMessage(-1);
                }
            }

        });
    }

    private void swtichDownload(List<NoteInfo> list) {
        List idlist = getRawIdList();
        Message m = Message.obtain();
        m.arg1 = list.size();
        m.what = -2;
        mhandler.sendMessage(m);
        for (NoteInfo n : list) {
            if (idlist.contains(n.getId())) {
                download(n, true);
            } else {
                download(n, false);
            }
        }

    }

    public List getRawIdList() {
        List idList = new ArrayList<>();
        Cursor cursor = mcontext.getContentResolver().query(uri, new String[]{NoteInfoColumns._ID}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(NoteInfoColumns._ID));
                idList.add(id);
            } while (cursor.moveToNext());
        }
        return idList;
    }


    private void download(NoteInfo noteInfo, final boolean exists) {

        final long id = noteInfo.getId();
        List<String> imageurls = noteInfo.getImageUrls();
        List<String> imageList = noteInfo.getImageList();
        final int size = imageurls.size();
        final ContentValues contentValues = noteInfo.getContentValues();
        final Handler handler = new Handler() {
            int i = 0;
            int max = size;

            @Override
            public void handleMessage(Message msg) {
                i++;
                if (i == max) {
                    if (exists) {
                        mcontext.getContentResolver().update(uri, contentValues, "id=?", new String[]{id + ""});
                    } else {
                        mcontext.getContentResolver().insert(uri, contentValues);
                    }
                    mhandler.sendEmptyMessage(NoteChangeType.ADD);
                }
                super.handleMessage(msg);
            }
        };
        for (int i = 0; i < imageList.size(); i++) {
            String url = imageurls.get(i);
            String path = imageList.get(i);
            if (url != null) {
                File file = new File(path);
                if (!file.exists()) {
                    BmobFile bmobFile = new BmobFile("huangfei.jpg", null, url);
                    bmobFile.download(file, new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            handler.sendEmptyMessage(0);
                        }

                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });
                } else {
                    handler.sendEmptyMessage(0);
                }
            }
        }

    }
}
