package com.xm.permission.api;

import android.content.Context;
import android.support.annotation.NonNull;

public interface PermissionRequestCallback {

    void callback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
