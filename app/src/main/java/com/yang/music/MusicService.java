package com.yang.music;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.yang.music.base.Constant;
import com.yang.music.base.OnPlayerListener;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.receiver.NoisyAudioStreamReceiver;
import com.yang.music.util.FadeController;
import com.yang.music.util.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "MusicService";
    private static final int NOTIFICATION_ID = 0x111;
    private int currentMusic;
    //    private int currentPosition;
    private MediaPlayer mediaPlayer;
    private boolean isPause = false;
    public static List<MusicInfoBean> musicInfoList = new ArrayList<>();

    private NotificationManager mNotificationManager;
    private int currentMode;//默认播放模式
    private AudioManager mAudioManager;
    private FadeController mFadeController;

    public static final int MODE_ONE_LOOP = 0;
    public static final int MODE_ALL_LOOP = 1;
    public static final int MODE_RANDOM = 2;
    public static final int MODE_SEQUENCE = 3;

    public static final String[] MODE_DESC = {"单曲循环", "列表循环", "随机播放", "顺序播放"};

    List<OnPlayerListener> listeners = new ArrayList<>();

    private IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();

    private long quitTimerRemain;
    private Notification notification;
    private Handler handler = new Handler();

    private Runnable changeProgress = new Runnable() {
        @Override
        public void run() {
            if (handler != null && listeners.size() > 0 && isPlaying()) {
                int progress = mediaPlayer.getCurrentPosition();
                for (OnPlayerListener listener : listeners) {
                    listener.onProgressChange(progress);
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        currentMode = getCurrentMode();
        currentMusic = BaseApp.mApp.getKv().getInt(Constant.SHARE_MUSIC_POSITION, 0);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initMediaPlayer();
        mFadeController = new FadeController(getMainLooper(), mediaPlayer);
    }

    public void addPlayerListener(OnPlayerListener listener) {
        listeners.add(listener);
    }

    public void removePlayerListener(OnPlayerListener listener) {
        for (OnPlayerListener listener1 : listeners) {
            if (listener1.toString().equals(listener.toString())) {
                listeners.remove(listener);
                return;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constant.ACTION_MEDIA_PLAY_PAUSE:
                    pause();
                    break;
                case Constant.ACTION_MEDIA_NEXT:
                    playNext();
                    break;
                case Constant.ACTION_MEDIA_PREVIOUS:
                    playPrevious();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void initMediaPlayer() {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (currentMode) {
                        case MODE_ONE_LOOP:
                            mediaPlayer.start();
                            break;
                        case MODE_ALL_LOOP:
                            play((currentMusic + 1) % musicInfoList.size());
                            break;
                        case MODE_RANDOM:
                            play(getRandomPosition());
                            break;
                        case MODE_SEQUENCE:
                            if (currentMusic < musicInfoList.size()) {
                                playNext();
                            }
                            break;
                    }
                }
            });
        } else {
            Log.e(TAG, "initMediaPlayer: ");
        }
    }

    public void play(int currentMusic) {
        if(musicInfoList.size() == 0){
            return;
        }

        this.currentMusic = currentMusic;
        try {
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(musicInfoList.get(currentMusic).getUrl());
            mediaPlayer.prepare();
            BaseApp.mApp.getKv().put(Constant.SHARE_MUSIC_POSITION, currentMusic).commit();
            start();
            if (!isPlaying()) {
                mFadeController.setCurrentVolume(0f);
            } else {
                mFadeController.fadeUp(1.0f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listeners.size() > 0) {
            for (OnPlayerListener listener : listeners) {
                listener.onMusicChange(musicInfoList.get(currentMusic),getDurationTime());
            }
        }
        Log.d("MusicService","play");
    }


    private void start() {
        Log.d("Xian","MusicService start()");
        mediaPlayer.start();
        handler.post(changeProgress);
        isPause = false;
        updateNotification(musicInfoList.get(getCurrentMusic()));
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        Log.e("playPause3", "playPauseregister");
        Log.d("Xian","MusicService start() registerReceiver");
        registerReceiver(mNoisyReceiver, mNoisyFilter);
        Log.d("Xian","MusicService start mNoisyReceiver = " + mNoisyReceiver);

    }

    public void playPause() {
        if (isPlaying()) {
            Log.e("playPause1", "playPause");
            pause();
        } else if (isPause()) {
            Log.e("playPause2", "playPause");
            resume();
        } else {
            Log.e("playPause3", "playPause");
            play(getCurrentMusic());
        }
    }

    private int pause() {
        Log.d("Xian","MusicService pause()");
        if (!isPlaying()) {
            return -1;
        } else {
            mFadeController.fadeDown(0.1f);
            mFadeController.setOnFadeFinishedListener(
                new FadeController.OnFadeFinishedListener() {
                    @Override
                    public void onFadeFinished(float volume) {
                        mFadeController.setOnFadeFinishedListener(null);
                    }
                });
        }
        mediaPlayer.pause();
        isPause = true;
        handler.removeCallbacks(changeProgress);
        cancelNotification(musicInfoList.get(getCurrentMusic()));
        mAudioManager.abandonAudioFocus(this);
        Log.d("Xian","MusicService pause mNoisyReceiver = " + mNoisyReceiver);
        unregisterReceiver(mNoisyReceiver);
        if (listeners.size() > 0) {
            for (OnPlayerListener listener : listeners) {
                listener.onPlayPause();
            }
        }
        return currentMusic;
    }

    public int resume() {
        if (isPlaying()) {
            return -1;
        }
        start();
        if (listeners.size() > 0) {
            for (OnPlayerListener listener : listeners) {
                listener.onPlayResume();
            }
        }
        return currentMusic;
    }

    public void stopPlay() {
        stop();
    }

    public void toNext() {
        playNext();
    }

    public void toPrevious() {
        playPrevious();
    }

    private void playNext() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                play(currentMusic);
                break;
            case MODE_ALL_LOOP:
                if (currentMusic + 1 == musicInfoList.size()) {
                    play(0);
                } else {
                    play(currentMusic + 1);
                }
                break;
            case MODE_SEQUENCE:
                if (currentMusic + 1 == musicInfoList.size()) {
                    Toast.makeText(this, "没有更多音乐", Toast.LENGTH_SHORT).show();
                } else {
                    play(currentMusic + 1);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition());
                break;
        }
    }

    private void playPrevious() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                play(currentMusic);
                break;
            case MODE_ALL_LOOP:
                if (currentMusic - 1 < 0) {
                    play(musicInfoList.size() - 1);
                } else {
                    play(currentMusic - 1);
                }
                break;
            case MODE_SEQUENCE:
                if (currentMusic - 1 < 0) {
                    Toast.makeText(this, "没有上一首歌曲", Toast.LENGTH_SHORT).show();
                } else {
                    play(currentMusic - 1);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition());
                break;
        }
    }

    private int getRandomPosition() {
        int random = (int) (Math.random() * (musicInfoList.size() - 1));
        return random;
    }

    public void saveMode(int mode) {
        BaseApp.mApp.getKv().put(Constant.SHARE_MODE, mode).commit();
        currentMode = mode;
        Toast.makeText(MusicService.this, MODE_DESC[currentMode], Toast.LENGTH_LONG).show();
    }

    public int getCurrentMode() {
        return BaseApp.mApp.getKv().getInt(Constant.SHARE_MODE, MODE_ALL_LOOP);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isPause() {
        return mediaPlayer != null && isPause;
    }

    public void stop() {
        pause();
        stopQuitTimer();
    }

    public void changeProgress(int progress) {
        if (isPlaying()) {
            mediaPlayer.seekTo(progress * 1000);
        } else {
            play(currentMusic);
        }
    }

    public int getCurrentMusic() {
        if(currentMusic > musicInfoList.size()){
            currentMusic = 0;
        }
        return currentMusic;
    }

    /**
     * 更新通知栏
     */
    private void updateNotification(MusicInfoBean music) {
        mNotificationManager.cancel(NOTIFICATION_ID);
        startForeground(NOTIFICATION_ID, SystemUtils.createNotification(this, music));
    }

    private void cancelNotification(MusicInfoBean music) {
        stopForeground(true);
        mNotificationManager.notify(NOTIFICATION_ID, SystemUtils.createNotification(this, music));
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying()) {
                    pause();
                }
                break;
        }
    }

    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    public void setTime(final long milli) {
        stopQuitTimer();
        if (milli > 0) {
            quitTimerRemain = milli + DateUtils.SECOND_IN_MILLIS;
            handler.post(mQuitRunnable);
        } else {
            quitTimerRemain = 0;
            for (OnPlayerListener listener : listeners) {
                listener.onTimer(milli);
                Log.e("setTime", milli + "");
            }
        }

    }

    private void stopQuitTimer() {
        handler.removeCallbacks(mQuitRunnable);
    }

    private Runnable mQuitRunnable = new Runnable() {
        @Override
        public void run() {
            quitTimerRemain -= DateUtils.SECOND_IN_MILLIS;
            if (quitTimerRemain > 0) {
                for (OnPlayerListener listener : listeners) {
                    listener.onTimer(quitTimerRemain);
                    Log.e("setTime", quitTimerRemain + "");
                }
                handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
            } else {
                stop();
            }
        }
    };

    public int getDurationTime(){
        if(mediaPlayer != null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }
}
