package com.zui.notes.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zui.notes.EditActivity;
import com.zui.notes.R;
import com.zui.notes.listener.ItemSlideHelper;
import com.zui.notes.model.NoteInfo;
import com.zui.notes.util.ImageUtils;
import com.zui.notes.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by huangfei on 2016/11/10.
 */
public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemSlideHelper.Callback {

    private RecyclerView mRecyclerView;
    private Context context;
    private boolean isEditMode = false;
    private HashMap<Integer, Boolean> isSelected;
    private List<NoteInfo> list;
    private ItemSlideHelper itemSlideHelper;
    private CallBack mCallBack;
    private int selectCount = 0;

    public NotesAdapter(CallBack callBack) {
        this.mCallBack = callBack;
        isSelected = new HashMap<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_activity, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        if (isEditMode) {
            itemViewHolder.rlCheckBox.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.rlCheckBox.setVisibility(View.GONE);
        }
        itemViewHolder.checkBox.setChecked(isSelected.get(position));
        itemViewHolder.itemTitle.setText(list.get(position).title);
        itemViewHolder.tvSummary.setText(list.get(position).summary);
        itemViewHolder.tvModifiedDate.setText(list.get(position).modifiedTime + "");
        Bitmap bitmap = BitmapFactory.decodeFile(list.get(position).firstPicPath);
        if (bitmap != null) {
            itemViewHolder.iv.setImageBitmap(bitmap);
            itemViewHolder.iv.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.iv.setVisibility(View.GONE);
        }
        if (Utils.getYearMonthDay(System.currentTimeMillis()).equals(Utils.getYearMonthDay(list.get(position).modifiedTime))) {
            itemViewHolder.tvModifiedDate.setText(Utils.getHourMinute(list.get(position).modifiedTime));
        } else if (Utils.getYear(System.currentTimeMillis()).equals(Utils.getYear(list.get(position).modifiedTime))) {
            itemViewHolder.tvModifiedDate.setText(Utils.getMonthDay(list.get(position).modifiedTime));
        } else {
            itemViewHolder.tvModifiedDate.setText(Utils.getYearMonthDay(list.get(position).modifiedTime));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (itemSlideHelper.isExpanded())
                            return true;
                }
                return false;
            }
        });
        itemSlideHelper = new ItemSlideHelper(mRecyclerView.getContext(), this, mRecyclerView);
        mRecyclerView.addOnItemTouchListener(itemSlideHelper);
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }


    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {

        if (holder.itemView instanceof LinearLayout) {
            ViewGroup viewGroup = (ViewGroup) holder.itemView;
            if (viewGroup.getChildCount() == 2) {
                return viewGroup.getChildAt(1).getLayoutParams().width;
            }
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        return mRecyclerView.getChildViewHolder(childView);
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    @Override
    public void onMenuClick(int position) {
        context.getContentResolver().delete(Uri.parse("content://com.zui.notes/notes_id"), "_id=?", new String[]{list.get(position)._id + ""});
        ImageUtils.deleteImagePath(Environment.getExternalStorageDirectory().toString() + File.separator + "Notes" + File.separator + list.get(position)._id);
        list.remove(position);
        notifyItemRemoved(position);
        if (list == null || list.isEmpty()) {
            mCallBack.showNoData();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (position != -1) {
            if (isEditMode) {
                isSelected.put(position, !isSelected.get(position));
                if (isSelected.get(position)) {
                    selectCount++;
                } else {
                    selectCount--;
                }
                mCallBack.setSelectCount(selectCount);
                notifyItemChanged(position);
            } else {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("noteInfo", list.get(position));
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void onItemLongClick(int position) {
        setEditMode(true);
        isSelected.put(position, true);
        selectCount++;
        mCallBack.setSelectCount(selectCount);
    }

    private void initHashMapData() {
        selectCount = 0;
        isSelected.clear();
        for (int i = 0; i < getItemCount(); i++)
            isSelected.put(i, false);
    }

    public void selectAll(boolean select) {
        if (select) {
            selectCount = 0;
            for (int i = 0; i < getItemCount(); i++) {
                isSelected.put(i, true);
                selectCount++;
            }
        } else {
            initHashMapData();
        }
        mCallBack.setSelectCount(selectCount);
        notifyDataSetChanged();
    }

    public void setEditMode(boolean editMode) {
        if (editMode) {
            isEditMode = true;
            notifyDataSetChanged();
        } else {
            isEditMode = false;
            initHashMapData();
            itemSlideHelper.setEditMode(false);
            notifyDataSetChanged();
        }
        if (list != null) {
            mCallBack.showSelectTitle(isEditMode);
        }
    }

    public void deleteSelected() {
        List<String> strings = new ArrayList<>();
        for (int i = (isSelected.size() - 1); i >= 0; i--) {
            if (isSelected.get(i)) {
                strings.add(list.get(i)._id + "");
                ImageUtils.deleteImagePath(Environment.getExternalStorageDirectory().toString() + File.separator + "Notes" + File.separator + list.get(i)._id);
                list.remove(i);
            }
        }
        context.getContentResolver().delete(Uri.parse("content://com.zui.notes/notes"), "_id=?", strings.toArray(new String[strings.size()]));
        setEditMode(false);
        if (list == null || list.isEmpty()) {
            mCallBack.showNoData();
        }
    }

    public void setList(List<NoteInfo> list) {
        this.list = list;
        initHashMapData();
    }

    public interface CallBack {
        void showSelectTitle(boolean show);

        void showNoData();

        void setSelectCount(int count);
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        RelativeLayout rlCheckBox;
        TextView itemTitle;
        ImageView iv;
        TextView tvModifiedDate;
        TextView tvSummary;

        private ItemViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
            rlCheckBox = (RelativeLayout) itemView.findViewById(R.id.item_rl_checkbox);
            itemTitle = (TextView) itemView.findViewById(R.id.item_tv_title);
            iv = (ImageView) itemView.findViewById(R.id.item_iv);
            tvModifiedDate = (TextView) itemView.findViewById(R.id.item_tv_modified_date);
            tvSummary = (TextView) itemView.findViewById(R.id.item_tv_summary);
        }
    }
}
