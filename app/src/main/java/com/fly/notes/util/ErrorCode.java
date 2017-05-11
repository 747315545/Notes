package com.fly.notes.util;

/**
 * Created by huangfei on 2017/5/11.
 */

public abstract interface ErrorCode {
     /**网络不可用*/
    public static final int NETWORK_DISABLE = 9016;
    /**注册邮箱格式不合法*/
    public static final int EMAIL_ADDRESS_INVALID = 301;
    /**登录信息出错*/
    public static final int LOGIN_INCORRECT = 101;
    /**用户名已注册*/
    public static final int EMAIL_EMAIL_USED = 202;
    /**邮箱已注册*/
    public static final int EMAIL_ADDRESS_USED = 203;
}
