package com.yang.music.util;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class FadeController extends Handler {
    private static final int FADE_DOWN = 2;

    private static final int FADE_UP = 3;

    private static final float MAX_VOLUME = 1.0f;

    private static final float VOLUME_CHANGE_DIFF = .01f;

    private static final int UPDATE_INTERVAL_MS = 100;

    private MediaPlayer mMediaPlayer;

    float mCurrentVolume = MAX_VOLUME;

    float mTargetVolume;

    private OnFadeFinishedListener mOnFadeFinishedListener;


    public interface OnFadeFinishedListener {
        void onFadeFinished(float volume);
    }

    public FadeController(Looper looper, MediaPlayer player) {
        super(looper);
        mMediaPlayer = player;
    }

    public void setOnFadeFinishedListener(OnFadeFinishedListener onFadeFinishedListener) {
        mOnFadeFinishedListener = onFadeFinishedListener;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case FADE_DOWN:
                mCurrentVolume -= VOLUME_CHANGE_DIFF;
                if (mCurrentVolume > mTargetVolume) {
                    sendEmptyMessageDelayed(FADE_DOWN, UPDATE_INTERVAL_MS);
                } else {
                    // Finish fade down.
                    mCurrentVolume = mTargetVolume;
                    if (mOnFadeFinishedListener != null) {
                        mOnFadeFinishedListener.onFadeFinished(mCurrentVolume);
                    }
                }
                mMediaPlayer.setVolume(mCurrentVolume, mCurrentVolume);
                break;
            case FADE_UP:
                mCurrentVolume += VOLUME_CHANGE_DIFF;
                if (mCurrentVolume < mTargetVolume) {
                    sendEmptyMessageDelayed(FADE_UP, UPDATE_INTERVAL_MS);
                } else {
                    // Finish fade down.
                    mCurrentVolume = mTargetVolume;
                    if (mOnFadeFinishedListener != null) {
                        mOnFadeFinishedListener.onFadeFinished(mCurrentVolume);
                    }
                }
                mMediaPlayer.setVolume(mCurrentVolume, mCurrentVolume);
                break;
            default:
        }
    }

    public void setCurrentVolume(float currentVolume) {
        mMediaPlayer.setVolume(currentVolume, mCurrentVolume);
        mCurrentVolume = currentVolume;
    }

    public void fadeUp(float targetVolume) {
        mTargetVolume = targetVolume;
        // make sure we fade up, in case a previous fade down was stopped
        // because of another focus loss
        removeMessages(FADE_DOWN);
        sendEmptyMessage(FADE_UP);
        //sendMessage(obtainMessage(FADE_UP, targetVolume));
        Log.d("FadeController","fadeup");
    }



    public void fadeDown(float targetVolume) {
        mTargetVolume = targetVolume;
        // make sure we fade up, in case a previous fade down was stopped
        // because of another focus loss
        removeMessages(FADE_UP);
        sendEmptyMessage(FADE_DOWN);
        //sendMessage(obtainMessage(FADE_DOWN, targetVolume));
        Log.d("FadeController","fadedown");
    }

}