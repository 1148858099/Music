package com.yang.music.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.yang.music.R;


public class CheckPermission {

    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
    /**
     * 6.0以上：activity权限申请
     * @param context
     * @param permissionName
     * @param requstCode
     * @return
     */
    public static boolean requestPermission(Activity context, String permissionName, int requstCode) {

        if (ContextCompat.checkSelfPermission(context, permissionName) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context, new String[]{permissionName}, requstCode);
            return false;
        } else {

            return true;
        }

    }

    /**
     *:6.0以上：fragment的权限申请
     * @param activity
     * @param fragment
     * @param permissionName
     * @param requstCode
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean requestPermissionFragment(Activity activity, Fragment fragment, String permissionName, int requstCode) {

        if (ContextCompat.checkSelfPermission(activity, permissionName) != PackageManager.PERMISSION_GRANTED) {

            fragment.requestPermissions(new String[]{permissionName}, requstCode);
            return false;
        } else {

            return true;
        }
    }

    // 显示缺失权限提示
    public static void showMissingPermissionDialog(final Activity context, String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请允许获取设备信息");
        builder.setMessage("由于"+context.getResources().getString(R.string.app_name)+"不能获取设备信息的权限，不能正常运行，请设置" + permission + "权限后使用。"+"\n"+"\n" + "设置路径：系统设置-> "+context.getResources().getString(R.string.app_name)+"->权限");
        builder.setCancelable(false);
        // 拒绝, 退出应用
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
                context.startActivity(intent);
            }
        });

        builder.show();
    }
}
