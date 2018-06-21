package com.yang.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yang.music.MusicService;
import com.yang.music.base.Constant;



public class NoisyAudioStreamReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.setAction(Constant.ACTION_MEDIA_PLAY_PAUSE);
        context.startService(serviceIntent);
    }
}
