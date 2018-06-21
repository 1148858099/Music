package com.yang.music.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.music.R;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.myEnum.ChangeFragment;



public class FolderItem extends RelativeLayout{
    private TextView folderName, musicNum;
    private Context context;
    public FolderItem(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    private void initUi() {
        LayoutInflater.from(context).inflate(R.layout.item_folder, this, true);
        folderName = (TextView) findViewById(R.id.item_folder_name);
        musicNum = (TextView) findViewById(R.id.item_music_num);
    }

    public void initData(MusicInfoBean musicInfo, int size) {
        folderName.setText(ChangeFragment.getUrl(musicInfo.getUrl()));
        musicNum.setText(size + "首");
    }
}
