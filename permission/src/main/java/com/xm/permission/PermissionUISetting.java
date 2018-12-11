package com.xm.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.xm.permission.api.PermissionSettingCallback;
import com.xm.permission.entity.PermissionParam;
import com.xm.permission.exce.AgentPermissionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class PermissionUISetting {

    private int mRequestCode = 0; // 辨别设置页面是由哪种权限定位开启的
    private List<PermissionParam> mDeniedPermissions;
    private PermissionSettingCallback mPermissionSettingCallback;
    private static final int REQUEST_CODE = 9191; // 权限请求页面返回请求码
    private static volatile PermissionUISetting instance = null;
    private static final String MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String PHONE_MESSAGE = Build.MANUFACTURER.toLowerCase(); // 获取设备信息

    private PermissionUISetting() {
    }

    public static synchronized PermissionUISetting getInstance() {
        if (instance == null) {
            synchronized (PermissionUISetting.class) {
                instance = new PermissionUISetting();
            }
        }
        return instance;
    }

    public void goToSettingPage(Context context, int requestCode, List<PermissionParam> deniedPermissions, PermissionSettingCallback permissionSettingCallback) {
        this.mRequestCode = requestCode;
        this.mDeniedPermissions = deniedPermissions;
        this.mPermissionSettingCallback = permissionSettingCallback;
        start(context);
    }

    public void start(Context context) {
        if (!(context instanceof Activity))
            throw new AgentPermissionException("context is not instance of Activity");
        Intent intent;
        if (PHONE_MESSAGE.contains("huawei")) {
            intent = huaweiApi(context);
        } else if (PHONE_MESSAGE.contains("xiaomi")) {
            intent = xiaomiApi(context);
        } else if (PHONE_MESSAGE.contains("oppo")) {
            intent = oppoApi(context);
        } else if (PHONE_MESSAGE.contains("vivo")) {
            intent = vivoApi(context);
        } else if (PHONE_MESSAGE.contains("meizu")) {
            intent = meizuApi(context);
        } else {
            intent = defaultApi(context);
        }
        try {
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception e) {
            intent = defaultApi(context);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private static Intent defaultApi(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private static Intent huaweiApi(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return defaultApi(context);
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        return intent;
    }

    private static Intent xiaomiApi(Context context) {
        String version = getSystemProperty(MIUI_VERSION_NAME);
        if (TextUtils.isEmpty(version) || version.contains("7") || version.contains("8")) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.putExtra("extra_pkgname", context.getPackageName());
            return intent;
        }
        return defaultApi(context);
    }

    private static Intent vivoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"));
        if (hasActivity(context, intent)) return intent;

        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        return intent;
    }

    private static Intent oppoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.setPermission.PermissionManagerActivity"));
        return intent;
    }

    private static Intent meizuApi(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return defaultApi(context);
        }
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    private static boolean hasActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    private static String getSystemProperty(String propName) {
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            return input.readLine();
        } catch (IOException ex) {
            return "";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
