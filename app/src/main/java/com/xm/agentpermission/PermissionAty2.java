package com.xm.agentpermission;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xm.permission.log.Log;

import android.view.View;

import com.xm.permission.AgentPermission;
import com.xm.permission.PermissionConst;
import com.xm.permission.api.Action;
import com.xm.permission.entity.PermissionParam;

import java.util.ArrayList;
import java.util.List;

public class PermissionAty2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_aty2);
    }

    public void external(View view) {
        List<PermissionParam> permissionList = new ArrayList<>();
        PermissionParam permissionParam1 = new PermissionParam();
        permissionParam1.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionParam1.setDeniedText("访问存储空间 - 用于历史账号的记录");
        permissionParam1.setShouldShowText("访问存储空间 - 用于历史账号的记录");
        permissionParam1.setNecessary(false);
        permissionParam1.setNecessaryText("访问存储空间 - 用于历史账号的记录");

        PermissionParam permissionParam5 = new PermissionParam();
        permissionParam5.setPermission(Manifest.permission.READ_CALENDAR);
        permissionParam5.setDeniedText("访问日历 - 用于系统日历获取");
        permissionParam5.setShouldShowText("访问日历 - 用于系统日历获取");
        permissionParam5.setNecessary(false);
        permissionParam5.setNecessaryText("访问相机 - 用于系统日历获取");

        permissionList.add(permissionParam1);
        permissionList.add(permissionParam5);

        AgentPermission.getInstance()
                .build()
                .setPermissionList(permissionList) // 设置全部要申请的权限
                .setRequestCallback(new Action() {
                    @Override
                    public void onAction(int resuleCode, List<PermissionParam> grantPermissions, List<PermissionParam> deniedPermissions) {
                        Log.d("PermissionAty2 resultCode == " + resuleCode);
                        switch (resuleCode) {
                            case PermissionConst.ResultCode.REQUEST_PERMISSION_ALL_GRANTED:
                                Log.d("全部同意");
                                break;
                            case PermissionConst.ResultCode.REQUEST_PERMISSION_ALL_DENIED:
                                Log.d("全部拒绝");
                                break;
                            case PermissionConst.ResultCode.REQUEST_PERMISSION_BOTH_HAS:
                                Log.d("部分同意");
                                break;
                        }
                    }
                })
                .start(this);
    }
}
