package com.xm.permission.api;

import com.xm.permission.entity.PermissionParam;

import java.util.List;

public interface PermissionSettingCallback {

    void callback(int requestCode, int resultCode, List<PermissionParam> grantedPermissions, List<PermissionParam> deniedPermissions);
}
