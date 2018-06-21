package com.yang.music.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yang.music.item.ArtistItem;
import com.yang.music.bean.MusicInfoBean;

import java.util.List;



public class ArtistListAdapter extends BaseAdapter {
    List<MusicInfoBean> mList;
    List<Integer> sizeList;
    private Context context;

    public ArtistListAdapter(Context context, List<MusicInfoBean> list, List<Integer> sizeList) {
        this.context = context;
        this.sizeList = sizeList;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArtistItem view = (ArtistItem) convertView;
        if (view == null) {
            view = new ArtistItem(context);
        }
        view.initData(mList.get(position),sizeList.get(position));

        return view;
    }
}