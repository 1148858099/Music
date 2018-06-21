package com.yang.music.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.music.R;


public class ItemMusicControl extends RelativeLayout{
    private Context context;
    private TextView mTv;
    public ItemMusicControl(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {

        LayoutInflater.from(context).inflate(R.layout.item_music_control,this,true);
        mTv = (TextView) findViewById(R.id.item_music_control_tv);
    }

    public void setData(String control){
        mTv.setText(control);
    }
}
