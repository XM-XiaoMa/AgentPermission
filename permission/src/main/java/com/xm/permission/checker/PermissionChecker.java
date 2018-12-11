package com.xm.permission.checker;

import android.content.Context;
import android.content.pm.PackageManager;

import com.xm.permission.entity.PermissionParam;


public class PermissionChecker {

    private static volatile PermissionChecker instance = null;

    private PermissionChecker() {
    }

    public static synchronized PermissionChecker getInstance() {
        if (instance == null) {
            synchronized (PermissionChecker.class) {
                instance = new PermissionChecker();
            }
        }
        return instance;
    }

    // 对于targetSDKVersion <=23 并在6.0以下的手机，只要权限是在清单文件中申请过的，都会返回已同意，即使你在设置中将权限去除
    public boolean hasPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(permission, context.getPackageName());
    }
}