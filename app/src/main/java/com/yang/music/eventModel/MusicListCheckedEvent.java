package com.yang.music.eventModel;

import com.yang.music.myEnum.ChangeFragment;


public class MusicListCheckedEvent {
    private ChangeFragment changeFragment;
    public MusicListCheckedEvent(ChangeFragment changeFragment) {
        this.changeFragment = changeFragment;
    }

    public ChangeFragment getChangeFragment() {
        return changeFragment;
    }
}
