package com.yang.music.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yang.music.item.ItemHomeList;
import com.yang.music.bean.HomeListBean;

import java.util.List;


public class HomeAdapter extends BaseAdapter{

    private Context context;
    private List<HomeListBean>mList;
    public HomeAdapter(Context context, List<HomeListBean>list){
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
        ItemHomeList view = (ItemHomeList)convertView;
        if(view == null){
            view = new ItemHomeList(context);
            view.setData(mList.get(position));
        }else{
            view.setData(mList.get(position));
        }
        return view;
    }
}
