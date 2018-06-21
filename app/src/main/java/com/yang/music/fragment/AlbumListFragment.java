package com.yang.music.fragment;

import android.view.View;
import android.widget.AdapterView;

import com.yang.music.BaseApp;
import com.yang.music.adapter.MusicListAdapter;
import com.yang.music.base.BaseFragment;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;
import com.yang.music.bean.MusicInfoBean;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;



public class AlbumListFragment extends BaseFragment{

    private List<MusicInfoBean> mAlbumList = new ArrayList<>();
    private MusicListAdapter musicListAdapter;

    public void setAlbumList(List<MusicInfoBean>mAlbumList){
        this.mAlbumList.clear();
        this.mAlbumList.addAll(mAlbumList);
        musicListAdapter.notifyDataSetChanged();

    }

    @Override
    public void initUi(View view) {
        super.initUi(view);
        BaseApp.musicContorlerView.controlMyMusic(getActivity(),mListView,false);
    }

    @Override
    public void initListener() {
        super.initListener();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new MusicCheckedEvent(position,mAlbumList,null));
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        initTitle();
        mAlbumList.addAll((ArrayList) getArguments().getParcelableArrayList("album"));
        musicListAdapter = new MusicListAdapter(getActivity(), mAlbumList);
        mListView.setAdapter(musicListAdapter);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("音乐列表");
    }
}
