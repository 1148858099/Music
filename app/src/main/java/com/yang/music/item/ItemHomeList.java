package com.yang.music.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.music.R;
import com.yang.music.bean.HomeListBean;


public class ItemHomeList extends RelativeLayout{
    private Context context;
    private TextView mMusicTitle;
    private ImageView mMusicPic;
    private View view;
    public ItemHomeList(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater.from(context).inflate(R.layout.item_grid_home,this,true);
        view = findViewById(R.id.item_home_view);
        mMusicPic = (ImageView) findViewById(R.id.item_grid_middle_imag);
        mMusicTitle = (TextView) findViewById(R.id.item_grid_bottom_tv);
;    }

    public void setData(HomeListBean home){
        view.setBackgroundResource(home.bgColorId);
        mMusicPic.setImageResource(home.picId);
        mMusicTitle.setText(home.title);
    }
}
