package com.yang.music.base;

import com.yang.music.bean.MusicInfoBean;


public interface OnPlayerListener {
    //更新进度
    void onProgressChange(int progress);
    //切换歌曲
    void onMusicChange(MusicInfoBean musicInfon,int duration);
    //暂停播放
    void onPlayPause();
    //继续播放
    void onPlayResume();
    //定时播放
    void onTimer(long time);
}
