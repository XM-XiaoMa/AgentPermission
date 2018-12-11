package com.xm.permission.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.xm.permission.R;
import com.xm.permission.api.RationalExecutor;
import com.xm.permission.api.Rationale;
import com.xm.permission.entity.PermissionParam;

import java.util.List;

public final class RationaleDialogImpl implements Rationale {

    @Override
    public void showRationale(final Context context, final List<PermissionParam> permissions, final RationalExecutor executor) {
        StringBuilder message = new StringBuilder();
        for (PermissionParam permission : permissions) {
            message.append(permission.getShouldShowText())
                    .append("\n");
        }
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.junhai_rational_title)
                .setMessage(message.toString())
                .setPositiveButton(R.string.junhai_resume, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 之前取消过的,重新申请
                        dialog.dismiss();
                        executor.execute(context);
                    }
                })
                .setNegativeButton(R.string.junhai_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 如果取消了,就只申请那些之前没有拒绝过的
                        dialog.dismiss();
                        executor.cancel(context);
                    }
                })
                .show();
    }
}