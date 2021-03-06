package com.example.hungcv.test.picker;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

/**
 * Created by usr0200475 on 16/01/21.
 */
public class PermissionUtil {

    public static boolean checkPermission(final FragmentActivity activity, @NonNull final String permission, String explainText, @NonNull final int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                DialogUtils.showConfirmDialog(activity, null, explainText, "Ok", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission(activity, permission, requestCode);
                    }
                }, null, false, null);
            } else {
                requestPermission(activity, permission, requestCode);
            }
            return false;
        }
    }

    public static boolean checkPermission(final Fragment fragment, @NonNull final String permission, String explainText, @NonNull final int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(fragment.getContext(), permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(),
                    permission)) {
                DialogUtils.showConfirmDialog(fragment.getContext(), null, explainText, "Ok", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission(fragment, permission, requestCode);
                    }
                }, null, false, null);
            } else {
                requestPermission(fragment, permission, requestCode);
            }
            return false;
        }
    }



    private static void requestPermission(FragmentActivity fragmentActivity, @NonNull String permission, @NonNull int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragmentActivity.requestPermissions(new String[]{
                    permission}, requestCode);
        }
    }

    private static void requestPermission(Fragment fragment, @NonNull String permission, @NonNull int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragment.requestPermissions(new String[]{
                    permission}, requestCode);
        }
    }

}
