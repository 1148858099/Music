package com.yang.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.yang.music.MusicService;
import com.yang.music.base.Constant;



public class HeadSetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null || event.getAction() != KeyEvent.ACTION_UP) {
            return;
        }
        Intent serviceIntent;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                serviceIntent = new Intent(context, MusicService.class);
                serviceIntent.setAction(Constant.ACTION_MEDIA_PLAY_PAUSE);
                context.startService(serviceIntent);
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                serviceIntent = new Intent(context, MusicService.class);
                serviceIntent.setAction(Constant.ACTION_MEDIA_NEXT);
                context.startService(serviceIntent);
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                serviceIntent = new Intent(context, MusicService.class);
                serviceIntent.setAction(Constant.ACTION_MEDIA_PREVIOUS);
                context.startService(serviceIntent);
                break;
        }
    }
}
