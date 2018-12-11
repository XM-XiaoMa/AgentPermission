package com.xm.permission.api;

import android.content.Context;

import com.xm.permission.entity.PermissionParam;

import java.util.List;

public interface DeniedDialog {
    void showDialog(Context context, List<PermissionParam> permissions, DeniedExecutor executor);
}
