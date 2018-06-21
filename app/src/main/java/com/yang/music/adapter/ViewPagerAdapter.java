package com.yang.music.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;



public class ViewPagerAdapter extends PagerAdapter{

    private View[] mList;

    public ViewPagerAdapter(View[] list){
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try{
            ((ViewPager)container).removeView((View)object);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try{
            ((ViewPager)container).addView(mList[position]);
        }catch (Exception e){
            e.printStackTrace();
        }
        return mList[position];
    }
}
