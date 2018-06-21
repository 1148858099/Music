package com.yang.music.fragment;

import android.view.View;
import android.widget.AdapterView;

import com.yang.music.BaseApp;
import com.yang.music.adapter.MusicListAdapter;
import com.yang.music.base.BaseFragment;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;

import de.greenrobot.event.EventBus;

import static com.yang.music.activity.HomeActivity.mMusicList;

public class MyMusicFragment extends BaseFragment  {

    private MusicListAdapter musicListAdapter;

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
                EventBus.getDefault().post(new MusicCheckedEvent(position,mMusicList,null));
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        initTitle();
        MusicListAdapter musicListAdapter =  new MusicListAdapter(getActivity(), mMusicList);
        mListView.setAdapter(musicListAdapter);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("音乐列表");
    }
}
