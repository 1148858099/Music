package com.yang.music.myEnum;

import com.yang.music.bean.MusicInfoBean;

import java.util.ArrayList;
import java.util.List;

import static com.yang.music.activity.HomeActivity.mMusicList;



public enum ChangeFragment {
    HOME_FRAGMENT(0),
    MY_MUSIC_FRAGMENT(1),
    ARTIST_FRAGMENT(2),
    ARTIST_LIST_FRAGMENT(3),
    ALBUM_FRAGMENT(4),
    ALBUM_LIST_FRAGMENT(5),
    FOLDER_FRAGMENT(6),
    FOLDER_LIST_FRAGMENT(7),
    LIKE_FRAGMENT(8),
    LATELY_PLAY_FRAGMENT(9);
    private int value;
    private static ChangeFragment mChangeFragment;

    private ChangeFragment(int value) {
        this.value = value;
    }

    public static ChangeFragment getChangeFragment() {
        return mChangeFragment;
    }

    public static void setChangeFragment(ChangeFragment changeFragment) {
        mChangeFragment = changeFragment;
    }

    public int getValue() {
        return value;
    }

    private static List<MusicInfoBean> getMyMusicMusicInfos() {
        List<MusicInfoBean> musicInfos = new ArrayList<>();
        musicInfos.addAll(mMusicList);
        return musicInfos;
    }

    public static String getUrl(String url){
        int i = url.lastIndexOf("/");
        String string = url.substring(0,i);
        return string;
    }

}
