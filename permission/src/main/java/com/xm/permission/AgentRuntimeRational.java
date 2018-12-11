package com.xm.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.Toast;


import com.xm.permission.activity.PermissionAty;
import com.xm.permission.api.Action;
import com.xm.permission.api.DeniedExecutor;
import com.xm.permission.api.PermissionRequestCallback;
import com.xm.permission.api.PermissionSettingCallback;
import com.xm.permission.api.RationalExecutor;
import com.xm.permission.api.SettingPageExecutor;
import com.xm.permission.checker.PermissionChecker;
import com.xm.permission.entity.PermissionParam;
import com.xm.permission.log.Log;
import com.xm.permission.widget.DeniedDialogImpl;
import com.xm.permission.widget.NecessaryPermissionDialog;
import com.xm.permission.widget.RationaleDialogImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AgentRuntimeRational implements RationalExecutor {
    private Action requestCallback;
    private RationaleDialogImpl rationaleDialogImpl;
    private PermissionChecker permissionChecker;
    private List<PermissionParam> mAllPermissions; // 传入的全部权限
    private List<PermissionParam> mDeniedPermissions; // 未经同意的权限
    private List<String> manifestPermissions;

    private static final int AGENT_PERMISSION_REQUEST_CODE = 9191;
    public static final List<PermissionParam> EMPTY_PERMISSIONS = new ArrayList<>();

    public AgentRuntimeRational() {
    }

    public void reset() {
        requestCallback = null;
        rationaleDialogImpl = null;
        permissionChecker = null;
        mAllPermissions = null;
        mDeniedPermissions = null;
        manifestPermissions = null;
    }

    public void setPermission(List<PermissionParam> permissionParamList) {
        this.mAllPermissions = permissionParamList;
        permissionChecker = PermissionChecker.getInstance();
        rationaleDialogImpl = new RationaleDialogImpl();
    }

    public void setRequestCallback(Action requestCallback) {
        this.requestCallback = requestCallback;
    }

    /**
     * 检查权限是否被申请
     *
     * @param context
     * @param permissions
     */
    private void checkPermissionInManifest(Context context, List<PermissionParam> permissions) {
        if (manifestPermissions == null)
            manifestPermissions = getManifestPermissions(context);
        if (permissions == null || permissions.size() == 0) {
            throw new IllegalArgumentException("传入的权限申请列表为空，请检查！");
        }
        for (PermissionParam permission : permissions) {
            if (!manifestPermissions.contains(permission.getPermission())) {
                throw new IllegalStateException(String.format("清单文件没有该权限的声明，请添加： <uses-setPermission android:name=\"%1$s\"/>", permission.getPermission()));
            }
        }
    }

    /**
     * 获取清单文件中的权限声明
     *
     * @param context
     * @return
     */
    private static List<String> getManifestPermissions(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions == null || permissions.length == 0) {
                throw new IllegalStateException("清单文件中没有任何权限的声明，请检查！");
            }
            return Collections.unmodifiableList(Arrays.asList(permissions));
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Package name cannot be found.");
        }
    }

    /**
     * 开始权限申请
     */
    public void start(Context context) {
        // checkPermission判断权限
        checkPermissionInManifest(context, mAllPermissions);
        mDeniedPermissions = getDeniedPermissions(permissionChecker, context, mAllPermissions);
        if (mDeniedPermissions.size() > 0) {
            // shouldShowRequestPermissionRationale判断某权限之前是否拒绝过
            List<PermissionParam> rationalePermission = getRationalePermissions(context, mDeniedPermissions);
            if (rationalePermission.size() > 0) {
                handleRational(context, rationalePermission);
            } else {
                execute(context);
            }
        } else {
            callbackAllGranted(context);
        }
    }


    @Override
    public void execute(Context context) {
        // 权限准备
        String[] permissions = new String[mDeniedPermissions.size()];
        for (int i = 0; i < mDeniedPermissions.size(); i++) {
            // 之前被拒绝过的,用户同意之后,一同加入权限申请集合中
            PermissionParam permissionParam = mDeniedPermissions.get(i);
            permissions[i] = permissionParam.getPermission();
        }
        // 权限请求
        requestPermission(context, permissions);
    }

    @Override
    public void cancel(Context context) {
        // 权限准备
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < mDeniedPermissions.size(); i++) {
            PermissionParam permissionParam = mDeniedPermissions.get(i);
            if (permissionParam.isRational() && !permissionParam.isNecessary())
                continue;// 被拒绝且非必要权限的,将不加入权限申请集合中
            permissionList.add(permissionParam.getPermission());
        }
        // 权限请求
        requestPermission(context, permissionList.toArray(new String[permissionList.size()]));
    }

    private void requestPermission(final Context context, String[] deniedPermissions) {
        if (deniedPermissions.length <= 0) {
            Log.d("requestPermission == permission is empty");
            callbackAuto(context);
            return;
        }
        PermissionAty.requestPermission((Activity) context, deniedPermissions, new PermissionRequestCallback() {
            @Override
            public void callback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (requestCode != AGENT_PERMISSION_REQUEST_CODE)
                    return;
                if (grantResults.length <= 0)
                    return;
                // 重新检查全部权限申请情况
                List<PermissionParam> deniedPermissions = getDeniedPermissions(permissionChecker, context, mAllPermissions);
                if (deniedPermissions.size() == 0) {
                    // 都是同意的
                    callbackAllGranted(context);
                } else {
                    showDeniedDialog(context, PermissionSetting.RequestCode.SETTING_UNNECESSARY_PERMISSION, deniedPermissions);
                }
            }
        });
    }

    /**
     * 权限全部被同意的
     */
    private void callbackAllGranted(Context context) {
        if (requestCallback == null) {
            Log.e(" error--requestCallback is null ");
            return;
        }
        requestCallback.onAction(PermissionConst.ResultCode.REQUEST_PERMISSION_ALL_GRANTED, mAllPermissions, EMPTY_PERMISSIONS);
        reset();
    }

    /**
     * 权限全部被拒绝的
     */
    private void callbackAllDenied(Context context) {
        if (requestCallback == null) {
            Log.e("error -- requestCallback is null");
            return;
        }
        requestCallback.onAction(PermissionConst.ResultCode.REQUEST_PERMISSION_ALL_DENIED, EMPTY_PERMISSIONS,
                getDeniedPermissions(permissionChecker, context, mAllPermissions));
        reset();
    }

    /**
     * 有被同意的，也有被拒绝的
     */
    private void callbackBoth(Context context) {
        if (requestCallback == null) {
            Log.e("error -- requestCallback is null");
            return;
        }
        requestCallback.onAction(PermissionConst.ResultCode.REQUEST_PERMISSION_BOTH_HAS,
                getGrantedPermissions(permissionChecker, context, mAllPermissions),
                getDeniedPermissions(permissionChecker, context, mAllPermissions));
        reset();
    }

    /**
     * 自动判定是哪种返回模式
     */
    private void callbackAuto(Context context) {
        if (requestCallback == null) {
            Log.e("error -- requestCallback is null");
            return;
        }

        List<PermissionParam> deniedPermissions = getDeniedPermissions(permissionChecker, context, mAllPermissions);
        List<PermissionParam> grantedPermissions = getGrantedPermissions(permissionChecker, context, mAllPermissions);
        if (deniedPermissions.size() == 0) {
            callbackAllGranted(context);
        } else if (grantedPermissions.size() == 0) {
            callbackAllDenied(context);
        } else {
            callbackBoth(context);
        }
    }

    /**
     * 获取被拒绝的权限
     *
     * @param checker
     * @param context
     * @param permissions
     * @return
     */
    public static List<PermissionParam> getDeniedPermissions(PermissionChecker checker, Context context, List<PermissionParam> permissions) {
        List<PermissionParam> deniedList = new ArrayList<>();
        for (PermissionParam permission : permissions) {
            if (!checker.hasPermission(context, permission.getPermission())) {
                deniedList.add(permission);
            }
        }
        return deniedList;
    }

    /**
     * 获取被同意的权限
     *
     * @param checker
     * @param context
     * @param permissions
     * @return
     */
    public static List<PermissionParam> getGrantedPermissions(PermissionChecker checker, Context context, List<PermissionParam> permissions) {
        List<PermissionParam> grantedList = new ArrayList<>();
        for (PermissionParam permission : permissions) {
            if (checker.hasPermission(context, permission.getPermission())) {
                grantedList.add(permission);
            }
        }
        return grantedList;
    }

    /**
     * 获取之前被拒绝过的权限
     *
     * @param context
     * @param permissions
     * @return
     */
    private List<PermissionParam> getRationalePermissions(Context context, List<PermissionParam> permissions) {
        List<PermissionParam> rationaleList = new ArrayList<>();
        for (PermissionParam permission : permissions) {
            if (isShowRationalePermission(context, permission)) {
                permission.setRational(true);
                rationaleList.add(permission);
            }
        }
        return rationaleList;
    }

    /**
     * 反射调用shouldShowRequestPermissionRationale
     *
     * @param context
     * @param permission
     * @return
     */
    private boolean isShowRationalePermission(Context context, PermissionParam permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return activity.shouldShowRequestPermissionRationale(permission.getPermission());
        }

        PackageManager packageManager = context.getPackageManager();
        Class<?> pkManagerClass = packageManager.getClass();
        try {
            Method method = pkManagerClass.getMethod("shouldShowRequestPermissionRationale", String.class);
            if (!method.isAccessible()) method.setAccessible(true);
            return (boolean) method.invoke(packageManager, permission.getPermission());
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 处理授权失败之后相关权限的提示，并引导用户去往权限设置页面设置权限
     *
     * @param deniedPermissions
     */
    private void showDeniedDialog(final Context context, final int requestCode, final List<PermissionParam> deniedPermissions) {
        DeniedDialogImpl.getInstance().showDialog(context, deniedPermissions, new DeniedExecutor() {
            @Override
            public void resume() {
                goToSettingPage(context, requestCode, deniedPermissions);
            }

            @Override
            public void cancel() {
                // 最后判断在被拒绝的权限中是否具有必要权限，有的话还需要走一下流程
                List<PermissionParam> necessaryPermission = getNecessaryPermission(deniedPermissions);
                if (necessaryPermission.size() > 0) {
                    showNecessaryPermissionDialog(context, necessaryPermission);
                } else {
                    callbackAuto(context);
                }
            }
        });
    }

    private void goToSettingPage(final Context context, int requestCode, List<PermissionParam> deniedPermissions) {
        PermissionAty.openPermissionSetting((Activity) context, requestCode, deniedPermissions, new PermissionSettingCallback() {
            @Override
            public void callback(int requestCode, int resultCode, List<PermissionParam> grantedPermissions, List<PermissionParam> deniedPermissions) {
                switch (resultCode) {
                    case PermissionSetting.ResultCode.UNNECESSARY_PERMISSION_ALL_GRANTED:
                        callbackAllGranted(context);
                        break;
                    case PermissionSetting.ResultCode.UNNECESSARY_PERMISSION_ALL_DENIED:
                        // 最后判断在被拒绝的权限中是否具有必要权限
                        List<PermissionParam> necessaryPermission = getNecessaryPermission(deniedPermissions);
                        if (necessaryPermission.size() > 0) {
                            showNecessaryPermissionDialog(context, necessaryPermission);
                        } else {
                            callbackAuto(context);
                        }
                        break;
                    case PermissionSetting.ResultCode.UNNECESSARY_PERMISSION_BOTH_HAS:
                        // 最后判断在被拒绝的权限中是否具有必要权限
                        List<PermissionParam> necessaryPermission1 = getNecessaryPermission(deniedPermissions);
                        if (necessaryPermission1.size() > 0) {
                            showNecessaryPermissionDialog(context, necessaryPermission1);
                        } else {
                            callbackBoth(context);
                        }
                        break;
                    case PermissionSetting.ResultCode.NECESSARY_PERMISSION_ALL_GRANTED:
                        callbackAuto(context);
                        break;
                    case PermissionSetting.ResultCode.NECESSARY_PERMISSION_ALL_DENIED:
                        ((Activity) context).finish();
                        System.exit(0);
                        break;
                }
            }
        });
    }

    /**
     * 判断拒绝的权限中是否存在必要的权限 - 有的话需要弹出退出框
     *
     * @param permissions
     * @return
     */
    private List<PermissionParam> getNecessaryPermission(List<PermissionParam> permissions) {
        List<PermissionParam> necessaryPermission = new ArrayList<>();
        for (PermissionParam permission : permissions) {
            if (permission.isNecessary())
                necessaryPermission.add(permission);
        }
        return necessaryPermission;
    }

    /**
     * 展示必要权限提示框
     *
     * @param necessaryPermissions
     */
    private void showNecessaryPermissionDialog(final Context context,
                                               final List<PermissionParam> necessaryPermissions) {
        NecessaryPermissionDialog.getInstance().showDialog(context, necessaryPermissions, new SettingPageExecutor() {
            @Override
            public void resume() {
                // 重新去往设置页面进行设置
                goToSettingPage(context, PermissionSetting.RequestCode.SETTING_NECESSARY_PERMISSION, necessaryPermissions);
            }

            @Override
            public void cancel() {
                Toast.makeText(context, "退出游戏", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        });
    }

    /**
     * 处理shouldShowRequestPermissionRationale 返回为 true 的权限
     *
     * @param permissions
     */
    private void handleRational(Context context, List<PermissionParam> permissions) {
        rationaleDialogImpl.showRationale(context, permissions, AgentRuntimeRational.this);
    }
}
