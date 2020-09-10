package com.ddhuy4298.chatapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PermissionUtils {

    /**
     * check user permissions is granted
     * @param context current activity of fragment
     * @param permissions list permissions need checking
     *
     * @return true if permissions is granted else denied
     * */
    public static boolean checkPermission(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p:permissions) {
                int result = ContextCompat.checkSelfPermission(context, p);
                if (result == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * request for acceptance of permissions
     * @param activity current activity context
     * @param permissions list permissions need requesting for acceptance
     * */
    public static void requestPermissions(FragmentActivity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, requestCode);
        }
    }
}
