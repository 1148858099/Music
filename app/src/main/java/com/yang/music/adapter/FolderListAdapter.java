package com.yang.music.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yang.music.item.FolderItem;
import com.yang.music.bean.MusicInfoBean;

import java.util.List;



public class FolderListAdapter extends BaseAdapter{
    private List<MusicInfoBean>musicInfos;
    private List<Integer>integers;
    private Context context;
    public FolderListAdapter(Context context, List<MusicInfoBean>musicInfos, List<Integer>integers){
        this.context = context;
        this.musicInfos = musicInfos;
        this.integers = integers;
    }

    @Override
    public int getCount() {
        return musicInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return musicInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FolderItem view = (FolderItem)convertView;
        if(view == null){
            view = new FolderItem(context);
        }
        view.initData(musicInfos.get(position),integers.get(position));
        return view;
    }
}
