package com.xm.permission.api;

public interface DeniedExecutor {

    /**
     * 打开权限设置页面
     */
    void resume();

    /**
     * 取消打开权限设置页面
     */
    void cancel();

}
