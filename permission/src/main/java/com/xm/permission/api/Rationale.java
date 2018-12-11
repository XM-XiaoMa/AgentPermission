package com.xm.permission.api;

import android.content.Context;

import com.xm.permission.entity.PermissionParam;

import java.util.List;

public interface Rationale {

    void showRationale(Context context, List<PermissionParam> permission, RationalExecutor executor);
}