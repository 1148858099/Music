package com.yang.music.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.music.BaseApp;
import com.yang.music.R;
import com.yang.music.bean.MusicInfoBean;



public class MusicListItem extends RelativeLayout {

    private TextView mLeftTop, mLeftBottom, mRightBtn;
    private Context context;
    private MusicInfoBean musicInfo;
    public MusicListItem(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater.from(context).inflate(R.layout.item_music, this, true);
        mLeftTop = (TextView) findViewById(R.id.item_left_top);
        mLeftBottom = (TextView) findViewById(R.id.item_left_bottom);
        mRightBtn = (TextView) findViewById(R.id.item_right_tv);

        initListener();
    }

    private void initListener() {

        mRightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseApp.musicContorlerView.showPopup(musicInfo);
            }
        });
    }

    public void initData(MusicInfoBean musicInfo) {
        this.musicInfo = musicInfo;
        if (musicInfo != null) {
            mLeftTop.setText(musicInfo.getTitle());
            mLeftBottom.setText(musicInfo.getArtist() + "/" + musicInfo.getAlbum());
        }
    }
}
