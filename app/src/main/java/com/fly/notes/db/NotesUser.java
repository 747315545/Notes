package com.fly.notes.db;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by huangfei on 2017/5/9.
 */

public class NotesUser extends BmobUser {
    private BmobFile avatar;

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }
}
