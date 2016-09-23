package com.example.hungcv.test.picker;

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

}
