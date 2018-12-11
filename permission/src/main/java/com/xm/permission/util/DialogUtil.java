package com.xm.permission.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.xm.permission.R;

public class DialogUtil {

    private static DialogUtil instance = null;

    private DialogUtil() {
    }

    public static synchronized DialogUtil getInstance() {
        if (instance == null) {
            synchronized (DialogUtil.class) {
                instance = new DialogUtil();
            }
        }
        return instance;
    }

    public void showYesOrNoDialog(Context context, String title, String message, final OnDialogClickListener onDialogClickListener) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.junhai_resume, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onDialogClickListener.confirm();
                    }
                })
                .setNegativeButton(R.string.junhai_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onDialogClickListener.quit();
                    }
                })
                .show();
    }

    public interface OnDialogClickListener {
        void confirm();

        void quit();
    }
}
