package com.example.hungcv.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * @author HUNGCV
 *         This class support quickly way to show simple dialog.
 */
public class DialogUtils {

    @NonNull
    public static ProgressDialog progressDialogDefaultConfigure(@NonNull final ProgressDialog progressDialog, final boolean cancelable, String message) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }


    public interface OnDialogListSelected {
        void onDialogListSelected(int index);
    }


    public static void showConfirmDialog(Context context, String title, String message, String strPositiveButton,
                                         String strNegativeButton, DialogInterface.OnClickListener positiveOnclick,
                                         DialogInterface.OnClickListener negativeOnClick, boolean cancelAble,
                                         DialogInterface.OnCancelListener cancelListener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) {
            builder.setTitle(title);
        }

        builder.setMessage(message);
        if (TextUtils.isEmpty(strPositiveButton)) {
            builder.setPositiveButton(strPositiveButton, positiveOnclick);
        } else {
            builder.setPositiveButton(strPositiveButton, positiveOnclick);
        }
        if (!TextUtils.isEmpty(strNegativeButton)) {
            builder.setNegativeButton(strNegativeButton, negativeOnClick);
        }
        builder.setCancelable(cancelAble);
        builder.setOnCancelListener(cancelListener);
        builder.create().show();
    }

    public static void showConfirmDialog(Context context, int titleResId, int messageResId, int strPositiveButtonResId,
                                         int strNegativeButtonResId, DialogInterface.OnClickListener positiveOnclick,
                                         DialogInterface.OnClickListener negativeOnClick, boolean cancelAble,
                                         DialogInterface.OnCancelListener cancelListener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (titleResId != 0) {
            builder.setTitle(titleResId);
        }

        builder.setMessage(messageResId);
        if (strPositiveButtonResId > 0) {
            builder.setPositiveButton(strPositiveButtonResId, positiveOnclick);
        } else {
            builder.setPositiveButton("OK", positiveOnclick);
        }
        if (strNegativeButtonResId > 0) {
            builder.setNegativeButton(strNegativeButtonResId, negativeOnClick);
        }

        builder.setCancelable(cancelAble);
        builder.setOnCancelListener(cancelListener);
        builder.create().show();
    }

    public static void showErrorApiDialog(Context context, String message) {
        showConfirmDialog(context, null, message, null, null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }, null, true, null);
    }

    public static void showDismissDialog(Context context, String message, DialogInterface.OnClickListener onOK, DialogInterface.OnCancelListener cancelListener) {
        showConfirmDialog(context, null, message, "OK", null, onOK, null, true, cancelListener);
    }

    public static void showDismissDialog(Context context, String message) {
        showConfirmDialog(context, null, message, "OK", null, null, null, true, null);
    }

    public static void showDissmissDialogWithTitle(Context context, String title, String message, DialogInterface.OnClickListener onOK, DialogInterface.OnCancelListener cancelListener) {
        showConfirmDialog(context, title, message, "閉じる", null, onOK, null, true, cancelListener);
    }

    public static void showDismissDialog(Context context, int message_res_id) {
        showConfirmDialog(context, null, context.getString(message_res_id), "OK", null, null, null, true, null);
    }

}
