package com.xm.permission.api;

import android.content.Context;

public interface RationalExecutor {

    /**
     * 处理权限申请
     */
    void execute(Context context);

    /**
     * 取消权限申请
     */
    void cancel(Context context);

}
