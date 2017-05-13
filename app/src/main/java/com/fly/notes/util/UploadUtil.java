package com.fly.notes.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.fly.notes.db.DataBaseHelper;
import com.fly.notes.db.NoteChangeType;
import com.fly.notes.db.NoteInfoColumns;
import com.fly.notes.db.NotesUser;
import com.fly.notes.model.NoteInfo;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by huangfei on 2017/5/13.
 */

public class UploadUtil {

    private final static String NOTESCHANGETABLENAME = "notesChange";
    private final static Uri uri = Uri.parse("content://com.fly.notes/notes");
    private Context mcontext;
    private Handler mhandler;


    public UploadUtil(Context context, Handler handler) {
        mcontext = context;
        mhandler = handler;
    }

    public List<BmobObject> getChangeList(int type) {
        List<BmobObject> list = null;
        if (type == NoteChangeType.DELETE) {
            list = new ArrayList<>();
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mcontext);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(NOTESCHANGETABLENAME, null, "changeType=?", new String[]{String.valueOf(type)}, null, null, null);
        List<String> idList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(NoteInfoColumns._ID));
                if (type == NoteChangeType.DELETE) {
                    NoteInfo n = new NoteInfo();
                    n.setId(id);
                    list.add(n);
                }
                idList.add(String.valueOf(id));
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        if (type == NoteChangeType.DELETE) {
            return list;
        } else {
            return getNoteList(idList);
        }
    }

    private List<BmobObject> getNoteList(List<String> idList) {
        List<BmobObject> list = new ArrayList<>();
        for (String a : idList) {
            Cursor cursor = mcontext.getContentResolver().query(uri, null, "id=?", new String[]{a}, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    NoteInfo noteInfo = new NoteInfo();
                    noteInfo.id = cursor.getLong(cursor.getColumnIndex(NoteInfoColumns._ID));
                    noteInfo.body = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.BODY));
                    noteInfo.modifiedTime = cursor.getLong(cursor.getColumnIndex(NoteInfoColumns.MODIFIED_TIME));
                    noteInfo.title = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.TITLE));
                    noteInfo.summary = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.SUMMARY));
                    noteInfo.firstPicPath = cursor.getString(cursor.getColumnIndex(NoteInfoColumns.FIRST_PIC_PATH));
                    noteInfo.setAuthor(NotesUser.getCurrentUser(NotesUser.class));
                    noteInfo.setImageList(ImageUtils.getImageList(noteInfo.body));
                    list.add(noteInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public void insertBatch(List<BmobObject> noteInfos) {
        final List<BmobObject> noteInfostemp = noteInfos;
        if (noteInfostemp != null && noteInfostemp.size() > 0) {
            for (BmobObject b : noteInfostemp) {
                List<String> imagelist = ((NoteInfo) b).getImageList();
                insertFilesBatch(b, imagelist);
            }
        }
    }


    public void deleteBatch(List<BmobObject> noteInfos) {
        final List<BmobObject> noteInfostemp = noteInfos;
        if (noteInfostemp != null && noteInfostemp.size() > 0) {
            for (final BmobObject b : noteInfostemp) {
                BmobQuery query0 = new BmobQuery();
                query0.addWhereEqualTo(NoteInfoColumns._ID, ((NoteInfo) b).getId());
                BmobQuery query1 = new BmobQuery();
                query1.addWhereEqualTo("author",NotesUser.getCurrentUser());
                List<BmobQuery<NoteInfo>> andQuerys = new ArrayList<BmobQuery<NoteInfo>>();
                andQuerys.add(query0);
                andQuerys.add(query1);
                BmobQuery query = new BmobQuery();
                query.and(andQuerys);
                query.findObjects(new FindListener<NoteInfo>() {
                    @Override
                    public void done(List<NoteInfo> list, BmobException e) {
                        if (e == null) {
                            if (list.size() > 0) {
                                b.setObjectId(list.get(0).getObjectId());
                                List<String> urls = list.get(0).getImageUrls();
                                deleteFilesBatch(b, urls);
                            }
                            DataBaseHelper database = new DataBaseHelper(mcontext);
                            final SQLiteDatabase db = database.getWritableDatabase();
                            db.delete(NOTESCHANGETABLENAME, "id=?", new String[]{((NoteInfo) b).getId() + ""});
                            db.close();
                        } else {
                            Log.i("huangfei", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
            }
        }
    }

    public void updateBatch(List<BmobObject> noteInfos) {
        final List<BmobObject> noteInfostemp = noteInfos;
        for (final BmobObject b : noteInfostemp) {
            BmobQuery query0 = new BmobQuery();
            query0.addWhereEqualTo(NoteInfoColumns._ID, ((NoteInfo) b).getId());
            BmobQuery query1 = new BmobQuery();
            query1.addWhereEqualTo("author",NotesUser.getCurrentUser());
            List<BmobQuery<NoteInfo>> andQuerys = new ArrayList<BmobQuery<NoteInfo>>();
            andQuerys.add(query0);
            andQuerys.add(query1);
            BmobQuery query = new BmobQuery();
            query.and(andQuerys);
            query.findObjects(new FindListener<NoteInfo>() {
                @Override
                public void done(List<NoteInfo> list, BmobException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            b.setObjectId(list.get(0).getObjectId());
                            List<String> urls = list.get(0).imageUrls;
                            updateFilesBatch(b, urls);
                        } else {
                            insert((NoteInfo) b);
                        }
                    } else {
                        Log.i("huangfei", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }
    }

    private void updateFilesBatch(final BmobObject b, List<String> urls) {
        if (urls != null && urls.size() > 0) {
            BmobFile.deleteBatch(urls.toArray(new String[urls.size()]), new DeleteBatchListener() {
                @Override
                public void done(String[] strings, BmobException e) {
                    uploadAfterFilesBatch(b);
                }
            });
        } else {
            uploadAfterFilesBatch(b);
        }
    }

    private void uploadAfterFilesBatch(final BmobObject b) {
        List<String> list = ((NoteInfo) b).getImageList();
        BmobFile.uploadBatch(list.toArray(new String[list.size()]), new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list.size() == list1.size()) {
                    ((NoteInfo) b).setImageUrls(list1);
                    update((NoteInfo) b);
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    private void deleteFilesBatch(final BmobObject b, List<String> urls) {
        if (urls != null && urls.size() > 0) {
            BmobFile.deleteBatch(urls.toArray(new String[urls.size()]), new DeleteBatchListener() {
                @Override
                public void done(String[] strings, BmobException e) {
                    delete((NoteInfo) b);
                }
            });
        } else {
            delete((NoteInfo) b);
        }
    }

    private void insertFilesBatch(final BmobObject b, List<String> list) {
        if (list != null && list.size() > 0) {
            BmobFile.uploadBatch(list.toArray(new String[list.size()]), new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list.size() == list1.size()) {
                        ((NoteInfo) b).setImageUrls(list1);
                        insert((NoteInfo) b);
                    }
                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {

                }

                @Override
                public void onError(int i, String s) {

                }
            });
        } else {
            insert((NoteInfo) b);
        }
    }

    private void insert(final NoteInfo noteinfo) {
        noteinfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    DataBaseHelper database = new DataBaseHelper(mcontext);
                    final SQLiteDatabase db = database.getWritableDatabase();
                    db.delete(NOTESCHANGETABLENAME, "id=?", new String[]{noteinfo.getId() + ""});
                    db.close();
                } else {
                    Log.i("huangfei", "添加单条数据失败！" + e.getMessage() + "," + e.getErrorCode());
                }
                mhandler.sendEmptyMessage(NoteChangeType.ADD);
            }
        });
    }

    private void update(final NoteInfo noteinfo) {
        noteinfo.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    DataBaseHelper database = new DataBaseHelper(mcontext);
                    final SQLiteDatabase db = database.getWritableDatabase();
                    db.delete(NOTESCHANGETABLENAME, "id=?", new String[]{noteinfo.getId() + ""});
                    db.close();
                } else {
                    Log.i("huangfei", "更新单条数据失败！" + e.getMessage() + "," + e.getErrorCode());
                }
                mhandler.sendEmptyMessage(NoteChangeType.UPDATE);
            }
        });
    }


    private void delete(final NoteInfo noteinfo) {
        noteinfo.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("huangfei", "删除单条数据成功！");
                } else {
                    Log.i("huangfei", "删除单条数据失败！" + e.getMessage() + "," + e.getErrorCode());
                }
                mhandler.sendEmptyMessage(NoteChangeType.DELETE);
            }
        });
    }

}
