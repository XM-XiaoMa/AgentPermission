package com.xm.permission.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.xm.permission.R;
import com.xm.permission.api.SettingPageExecutor;
import com.xm.permission.api.SettingPageDialog;
import com.xm.permission.entity.PermissionParam;

import java.util.List;

public class NecessaryPermissionDialog implements SettingPageDialog {


    private static NecessaryPermissionDialog instance = null;

    private NecessaryPermissionDialog() {
    }

    public static synchronized NecessaryPermissionDialog getInstance() {
        if (instance == null) {
            synchronized (NecessaryPermissionDialog.class) {
                instance = new NecessaryPermissionDialog();
            }
        }
        return instance;
    }


    @Override
    public void showDialog(Context context, List<PermissionParam> permissions, final SettingPageExecutor executor) {
        StringBuilder message = new StringBuilder();
        for (PermissionParam permission : permissions) {
            message.append(permission.getNecessaryText())
                    .append("\n");
        }
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.junhai_necessary_permission_title)
                .setMessage(message.toString())
                .setPositiveButton(R.string.junhai_resume, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 之前取消过的,重新申请
                        dialog.dismiss();
                        executor.resume();
                    }
                })
                .setNegativeButton(R.string.junhai_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 如果取消了,就只申请那些之前没有拒绝过的
                        dialog.dismiss();
                        executor.cancel();
                    }
                })
                .show();
    }
}
