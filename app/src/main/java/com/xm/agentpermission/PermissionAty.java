package com.xm.agentpermission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xm.permission.PermissionConst;
import com.xm.permission.api.Action;
import com.xm.permission.AgentPermission;
import com.xm.permission.entity.PermissionParam;

import java.util.ArrayList;
import java.util.List;

public class PermissionAty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_aty);
    }

    public void external(View view) {
        List<PermissionParam> permissionList = new ArrayList<>();
        PermissionParam permissionParam1 = new PermissionParam();
        permissionParam1.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionParam1.setDeniedText("访问存储空间 - 用于历史账号的记录");
        permissionParam1.setShouldShowText("访问存储空间 - 用于历史账号的记录");
        permissionParam1.setNecessary(false);
        permissionParam1.setNecessaryText("访问存储空间 - 用于历史账号的记录");

        PermissionParam permissionParam3 = new PermissionParam();
        permissionParam3.setPermission(Manifest.permission.READ_PHONE_STATE);
        permissionParam3.setDeniedText("访问电话 - 用于设备型号获取");
        permissionParam3.setShouldShowText("访问电话 - 用于设备型号获取" + "\n" + "访问电话 - 用于设备型号获取" + "\n" + "访问电话 - 用于设备型号获取");
        permissionParam3.setNecessary(true);
        permissionParam3.setNecessaryText("访问电话 - 用于设备型号获取");

        PermissionParam permissionParam4 = new PermissionParam();
        permissionParam4.setPermission(Manifest.permission.CAMERA);
        permissionParam4.setDeniedText("访问相机 - 用于游戏中摄像功能");
        permissionParam4.setShouldShowText("访问相机 - 用于游戏中摄像功能");
        permissionParam4.setNecessary(true);
        permissionParam4.setNecessaryText("访问相机 - 用于游戏中摄像功能");



        permissionList.add(permissionParam1);
        permissionList.add(permissionParam3);
        permissionList.add(permissionParam4);

        AgentPermission.getInstance()
                .build()
                .setPermissionList(permissionList) // 设置全部要申请的权限
                .setRequestCallback(new Action() {
                    @Override
                    public void onAction(int resuleCode, List<PermissionParam> grantPermissions, List<PermissionParam> deniedPermissions) {
                        Log.d("JUNHAI_OVERSEA", "PermissionAty resultCode == " + resuleCode);
                        switch (resuleCode) {
                            case PermissionConst.ResultCode.REQUEST_PERMISSION_ALL_GRANTED:
                                Log.d("JUNHAI_OVERSEA", "全部同意");
                                break;
                            case PermissionConst.ResultCode.REQUEST_PERMISSION_ALL_DENIED:
                                Log.d("JUNHAI_OVERSEA", "全部拒绝");
                                break;
                            case PermissionConst.ResultCode.REQUEST_PERMISSION_BOTH_HAS:
                                Log.d("JUNHAI_OVERSEA", "部分同意");
                                break;
                        }
                    }
                })
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void second(View view) {
        Intent intent = new Intent(PermissionAty.this, PermissionAty2.class);
        PermissionAty.this.startActivity(intent);
    }
}
