package com.yang.music.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yang.music.R;
import com.yang.music.util.CheckPermission;

import java.util.Timer;
import java.util.TimerTask;




public class StartActivity extends AppCompatActivity {

 /*   private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        initPermission();
    }

    private void initPermission() {
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                initTimer();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            initTimer();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                initTimer();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                mPermissionHelper.applyPermissions();
            }
        }
    }

    private Handler handler = new Handler();

    private void initTimer() {
        handler.postDelayed(runnable,10);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            goNext();
        }
    };

    private void goNext(){
        LrcLoader.getLrcFilePath();
        CoverUrlLoad.loadCoverUrl();
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
*/

    private final int START_LOGIN = 1;
    private boolean toSettingPermission = false;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE = 1;
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_LOGIN:
                    login();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_start);
        if (CheckPermission.requestPermission(this, "android.permission.READ_EXTERNAL_STORAGE", REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE)) {
            initTimer();
        }
    }

    private void initTimer() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(START_LOGIN);
            }
        }, 2000);
    }

    private void login() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toSettingPermission) {
            toSettingPermission = false;
            if (CheckPermission.requestPermission(this, "android.permission.READ_EXTERNAL_STORAGE", REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE)) {
                initTimer();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initTimer();
                } else {
                    showMissingPermissionDialog(this, "存储");
                }

            }
            break;
        }
    }

    private void showMissingPermissionDialog(final Activity context, String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请允许获取设备信息");
        builder.setMessage("由于" + getResources().getString(R.string.app_name) + "不能获取设备信息的权限，不能正常运行，请设置" + permission + "权限后使用。" + "\n" + "\n" + "设置路径：系统设置->" + getResources().getString(R.string.app_name) + "->权限");
        builder.setCancelable(false);
        // 拒绝, 退出应用
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
                context.startActivity(intent);
                toSettingPermission = true;
            }
        });

        builder.show();
    }
}

