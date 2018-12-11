package com.xm.permission.api;

import com.xm.permission.entity.PermissionParam;

import java.util.List;

public interface Action {

    /**
     * @param type              权限请求返回类型
     * @param grantPermissions  经用户同意的权限
     * @param deniedPermissions 经用户拒绝的权限
     */
    void onAction(int type, List<PermissionParam> grantPermissions, List<PermissionParam> deniedPermissions);
}
