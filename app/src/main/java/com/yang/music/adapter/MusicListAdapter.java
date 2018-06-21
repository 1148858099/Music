package com.yang.music.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yang.music.bean.MusicInfoBean;
import com.yang.music.item.MusicListItem;

import java.util.List;



public class MusicListAdapter extends BaseAdapter {
    List<MusicInfoBean> mList;
    private Context context;

    public MusicListAdapter(Context context, List<MusicInfoBean> list) {
        this.context = context;
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
        MusicListItem view = (MusicListItem) convertView;
        if (view == null) {
            view = new MusicListItem(context);
        }
        Log.d("Xian", "getView: "+11);
        view.initData(mList.get(position));

        return view;
    }
}
