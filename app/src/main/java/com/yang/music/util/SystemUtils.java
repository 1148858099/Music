package com.yang.music.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yang.music.BaseApp;
import com.yang.music.R;
import com.yang.music.activity.PlayMusicActivity;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.constants.Extras;
import com.yang.music.util.data.StringUtils;

import java.util.List;
import java.util.Locale;

import static com.yang.music.MusicService.musicInfoList;
import static com.yang.music.activity.HomeActivity.mImageUrl;
import static com.yang.music.activity.HomeActivity.musicService;



public class SystemUtils {

    /**
     * 判断是否有Activity在运行
     */
    public static boolean isStackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
        return runningTaskInfo.numActivities > 1;
    }

    /**
     * 判断Service是否在运行
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Notification createNotification(Context context, MusicInfoBean music) {
        String title = music.getTitle();
        Bitmap cover;
        String url = "";
        for(int i = 0;i < mImageUrl.size();i++){
            if(mImageUrl.get(i).contains(musicInfoList.get(musicService.getCurrentMusic()).getTitle())){
                url = mImageUrl.get(i);
            }
        }
        if(StringUtils.isNotEmpty(url)){
            cover = CoverLoader.loadNormal(musicService.getCurrentMusic(),url);
        }else{
            cover = CoverLoader.loadNormal(musicService.getCurrentMusic(),music.getPicUri());
        }

        Intent intent = new Intent(context, PlayMusicActivity.class);
        intent.putExtra(Extras.FROM_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(music.getArtist())
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(cover);
        return builder.getNotification();
    }

    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }

    //隐藏键盘
    public static void hideInputMethod(View view){
        InputMethodManager imm = (InputMethodManager) BaseApp.mApp.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){//判断键盘是否经激活(弹出)
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);//隐藏键盘
        }
    }

}
