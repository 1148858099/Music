package com.yang.music.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;



public class PlayPagerAdapter extends PagerAdapter{
    private List<View>mView;
    public PlayPagerAdapter(List<View>views){

        mView = views;
    }
    @Override
    public int getCount() {
        return mView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mView.get(position));
        return mView.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mView.get(position));
    }
}
