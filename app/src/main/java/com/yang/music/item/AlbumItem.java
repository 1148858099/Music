package com.yang.music.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.music.R;
import com.yang.music.bean.MusicInfoBean;

public class AlbumItem extends RelativeLayout {

    private TextView albumName,musicNum;
    private Context context;

    public AlbumItem(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater.from(context).inflate(R.layout.item_album, this, true);
        albumName = (TextView) findViewById(R.id.item_albun_name);
        musicNum = (TextView) findViewById(R.id.item_music_num);
    }

    public void initData(MusicInfoBean musicInfo, int num) {
        albumName.setText(musicInfo.getAlbum());
        musicNum.setText(num + "首");
    }
}