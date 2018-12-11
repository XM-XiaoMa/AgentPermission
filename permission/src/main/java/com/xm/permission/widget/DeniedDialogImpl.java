package com.xm.permission.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.xm.permission.R;
import com.xm.permission.api.DeniedExecutor;
import com.xm.permission.api.DeniedDialog;
import com.xm.permission.entity.PermissionParam;
import com.xm.permission.util.DialogUtil;

import java.util.List;

public class DeniedDialogImpl implements DeniedDialog {

    private static DeniedDialogImpl instance = null;

    private DeniedDialogImpl() {
    }

    public static synchronized DeniedDialogImpl getInstance() {
        if (instance == null) {
            synchronized (DeniedDialogImpl.class) {
                instance = new DeniedDialogImpl();
            }
        }
        return instance;
    }

    @Override
    public void showDialog(Context context, List<PermissionParam> permissions, final DeniedExecutor executor) {
        StringBuilder message = new StringBuilder();
        for (PermissionParam permission : permissions) {
            message.append(permission.getDeniedText())
                    .append("\n");
        }

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.junhai_dangerous_permission_title)
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
