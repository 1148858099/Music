package com.yang.music.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.music.R;
import com.yang.music.bean.MusicInfoBean;



public class ArtistItem extends RelativeLayout {

    private TextView artistName, musicNum;
    private Context context;

    public ArtistItem(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater.from(context).inflate(R.layout.item_artist, this, true);
        artistName = (TextView) findViewById(R.id.item_artist_name);
        musicNum = (TextView) findViewById(R.id.item_music_num);
    }

    public void initData(MusicInfoBean musicInfo, int size) {
        artistName.setText(musicInfo.getArtist());
        musicNum.setText(size + "首");
    }
}
