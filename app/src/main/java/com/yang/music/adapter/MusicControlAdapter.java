package com.yang.music.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yang.music.item.ItemMusicControl;

import java.util.List;



public class MusicControlAdapter extends BaseAdapter{

    private Context mContext;
    private List<String> mList;
    public MusicControlAdapter(Context context,List<String>list){
        mContext = context;
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
        ItemMusicControl view = (ItemMusicControl)convertView;
        if(view == null){
            view =new ItemMusicControl(mContext);
            view.setData(mList.get(position));
        }else{
            view.setData(mList.get(position));
        }
        return view;
    }
}
