package com.yang.music.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.yang.music.BaseApp;
import com.yang.music.base.BaseFragment;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;
import com.yang.music.item.MusicListItem;
import com.yang.music.util.data.LikeMusic;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/11.
 */

public class LikeFragment extends BaseFragment{

    private List<MusicInfoBean> mLikeList = new ArrayList<>();
    public MusicLikeListAdapter musicListAdapter;

    public void setLikeList(List<MusicInfoBean>mLikeList){
        this.mLikeList.clear();
        this.mLikeList.addAll(mLikeList);
        musicListAdapter.notifyDataSetChanged();

    }

    @Override
    public void initUi(View view) {
        super.initUi(view);
        BaseApp.musicContorlerView.controlMyMusic(getActivity(),mListView,true);
    }

    @Override
    public void initListener() {
        super.initListener();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new MusicCheckedEvent(position,mLikeList,null));
            }
        });
    }


    @Override
    public void initData() {
        super.initData();
        initTitle();
        mLikeList.addAll((ArrayList) getArguments().getParcelableArrayList("like"));
        musicListAdapter = new MusicLikeListAdapter(getActivity());
        mListView.setAdapter(musicListAdapter);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("音乐列表");
    }

    public static class MusicLikeListAdapter extends BaseAdapter {
        List<MusicInfoBean> mList;
        private Context context;

        public MusicLikeListAdapter(Context context) {
            this.context = context;
            mList = LikeMusic.getRecordMessage(context);
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
            view.initData(mList.get(position));
            return view;
        }
    }
}
