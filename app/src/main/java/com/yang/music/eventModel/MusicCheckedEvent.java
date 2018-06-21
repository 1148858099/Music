package com.yang.music.eventModel;

import com.yang.music.bean.MusicInfoBean;

import java.util.List;



public class MusicCheckedEvent {

    private int position;
    private List<MusicInfoBean>mMusicList;
    private String flag;

    public MusicCheckedEvent(int position, List<MusicInfoBean>mMusicList, String flag){
        this.position = position;
        this.mMusicList = mMusicList;
        this.flag = flag;
    }

    public int getPosition() {
        return position;
    }

    public List<MusicInfoBean> getmMusicList() {
        return mMusicList;
    }

    public String getFlag() {
        return flag;
    }
}
