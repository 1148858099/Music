package com.yang.music.util.data;

import android.content.Context;
import android.view.WindowManager;

import com.yang.music.BaseApp;

/**
 * 工具类

 */
public class ScreenUtils {
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) BaseApp.mApp.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取状态栏高度
     */
    public static int getSystemBarHeight() {
        int result = 0;
        int resourceId = BaseApp.mApp.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = BaseApp.mApp.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dp2px(float dpValue) {
        final float scale = BaseApp.mApp.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        final float scale = BaseApp.mApp.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = BaseApp.mApp.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
