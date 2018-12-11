package com.xm.permission.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.xm.permission.AgentRuntimeRational;
import com.xm.permission.PermissionSetting;
import com.xm.permission.PermissionUISetting;
import com.xm.permission.api.PermissionRequestCallback;
import com.xm.permission.api.PermissionSettingCallback;
import com.xm.permission.checker.PermissionChecker;
import com.xm.permission.entity.PermissionParam;

import java.util.List;

public final class PermissionAty extends Activity {

    private static final String KEY_INPUT_OPERATION = "KEY_INPUT_OPERATION";
    private static final int PERMISSION_REQUEST = 1;
    private static final int PERMISSION_SETTING = 2;
    private static final int AGENT_PERMISSION_REQUEST_CODE = 9191;
    private static final int REQUEST_CODE = 9191; // 权限请求页面返回请求码


    private static int settingRequestCode;
    private static PermissionSettingCallback settingCallback;
    private static PermissionRequestCallback requestCallback;
    private static List<PermissionParam> settingPermissions;
    private static String[] requestPermissions;

    /**
     * Request for permissions.
     */
    public static void requestPermission(Activity activity, String[] deniedPermissions, PermissionRequestCallback requestCallback) {
        PermissionAty.requestCallback = requestCallback;
        PermissionAty.requestPermissions = deniedPermissions;
        Intent intent = new Intent(activity, PermissionAty.class);
        intent.putExtra(KEY_INPUT_OPERATION, PERMISSION_REQUEST);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * Request for setting.
     */
    public static void openPermissionSetting(Activity activity, int requestCode, List<PermissionParam> deniedPermissions, PermissionSettingCallback settingCallback) {
        PermissionAty.settingCallback = settingCallback;
        PermissionAty.settingPermissions = deniedPermissions;
        PermissionAty.settingRequestCode = requestCode;
        Intent intent = new Intent(activity, PermissionAty.class);
        intent.putExtra(KEY_INPUT_OPERATION, PERMISSION_SETTING);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int operation = intent.getIntExtra(KEY_INPUT_OPERATION, 0);
        switch (operation) {
            case PERMISSION_REQUEST: {
                if (requestPermissions != null && requestCallback != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(requestPermissions, AGENT_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    finish();
                }
                break;
            }
            case PERMISSION_SETTING: {
                if (settingCallback != null) {
                    PermissionUISetting.getInstance().goToSettingPage(PermissionAty.this, settingRequestCode, settingPermissions, settingCallback);
                } else {
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCallback != null) {
            requestCallback.callback(requestCode, permissions, grantResults);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE)
            return;
        List<PermissionParam> deniedPermissions = AgentRuntimeRational.getDeniedPermissions(PermissionChecker.getInstance(), PermissionAty.this, settingPermissions);
        List<PermissionParam> grantedPermissions = AgentRuntimeRational.getGrantedPermissions(PermissionChecker.getInstance(), PermissionAty.this, settingPermissions);

        if (deniedPermissions.size() == 0 && settingRequestCode == PermissionSetting.RequestCode.SETTING_UNNECESSARY_PERMISSION) {
            settingCallback.callback(settingRequestCode, PermissionSetting.ResultCode.UNNECESSARY_PERMISSION_ALL_GRANTED, grantedPermissions, AgentRuntimeRational.EMPTY_PERMISSIONS);
            // 请求非必要权限 - 全部同意
        } else if (deniedPermissions.size() == 0 && settingRequestCode == PermissionSetting.RequestCode.SETTING_NECESSARY_PERMISSION) {
            settingCallback.callback(settingRequestCode, PermissionSetting.ResultCode.NECESSARY_PERMISSION_ALL_GRANTED, grantedPermissions, AgentRuntimeRational.EMPTY_PERMISSIONS);
            // 请求必要权限 - 全部同意
        } else if (grantedPermissions.size() == 0 && settingRequestCode == PermissionSetting.RequestCode.SETTING_UNNECESSARY_PERMISSION) {
            settingCallback.callback(settingRequestCode, PermissionSetting.ResultCode.UNNECESSARY_PERMISSION_ALL_DENIED, AgentRuntimeRational.EMPTY_PERMISSIONS, deniedPermissions);
            // 请求非必要权限 - 全部拒绝
        } else if (grantedPermissions.size() == 0 && settingRequestCode == PermissionSetting.RequestCode.SETTING_NECESSARY_PERMISSION) {
            settingCallback.callback(settingRequestCode, PermissionSetting.ResultCode.NECESSARY_PERMISSION_ALL_DENIED, AgentRuntimeRational.EMPTY_PERMISSIONS, deniedPermissions);
            // 请求必要权限 - 全部拒绝
        } else if (grantedPermissions.size() > 0 && deniedPermissions.size() > 0 && settingRequestCode == PermissionSetting.RequestCode.SETTING_UNNECESSARY_PERMISSION) {
            settingCallback.callback(settingRequestCode, PermissionSetting.ResultCode.UNNECESSARY_PERMISSION_BOTH_HAS, grantedPermissions, deniedPermissions);
            // 请求非必要权限 - 部分拒绝部分同意
        } else if (deniedPermissions.size() > 0 && settingRequestCode == PermissionSetting.RequestCode.SETTING_NECESSARY_PERMISSION) {
            settingCallback.callback(settingRequestCode, PermissionSetting.ResultCode.NECESSARY_PERMISSION_ALL_DENIED, grantedPermissions, deniedPermissions);
            // 请求必要权限 - 部分拒绝部分同意 - 统一当不给于权限处理
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        settingRequestCode = 0;
        settingCallback = null;
        requestCallback = null;
        settingPermissions = null;
        requestPermissions = null;
        settingCallback = null;
        requestCallback = null;
        super.finish();
    }
}