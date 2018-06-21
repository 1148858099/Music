package com.yang.music.fragment;

import android.view.View;
import android.widget.AdapterView;

import com.yang.music.BaseApp;
import com.yang.music.adapter.MusicListAdapter;
import com.yang.music.base.BaseFragment;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/11.
 */

public class LatelyPlayFragment extends BaseFragment {

    private List<MusicInfoBean> mLatelyPlayList = new ArrayList<>();
    private MusicListAdapter musicListAdapter;

    public void setlatelyPlayList(List<MusicInfoBean>mLatelyPlayList){
        this.mLatelyPlayList.clear();
        this.mLatelyPlayList.addAll(mLatelyPlayList);
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
                EventBus.getDefault().post(new MusicCheckedEvent(position,mLatelyPlayList,null));
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        initTitle();
        mLatelyPlayList.addAll((ArrayList) getArguments().getParcelableArrayList("lately_play"));
        musicListAdapter = new MusicListAdapter(getActivity(), mLatelyPlayList);
        mListView.setAdapter(musicListAdapter);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("音乐列表");
    }
}
