package com.yang.music.fragment;

import android.view.View;
import android.widget.AdapterView;

import com.yang.music.adapter.ArtistListAdapter;
import com.yang.music.base.BaseFragment;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.yang.music.activity.HomeActivity.mMusicList;


public class ArtistFragment extends BaseFragment {

    private static final String TAG = "ArtistFragment";
    private List<MusicInfoBean> artistMusicList = new ArrayList<>();
    List<Integer>sizeList = new ArrayList<>();
    @Override
    public void initUi(View view) {
        super.initUi(view);
    }

    @Override
    public void initListener() {
        super.initListener();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new MusicCheckedEvent(position,null,artistMusicList.get(position).getArtist()));
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        initTitle();
        artistMusicList.addAll((List<MusicInfoBean>)getArtistMusicInfos(0));
        sizeList.addAll((List<Integer>)getArtistMusicInfos(1));

        ArtistListAdapter artistFragment = new ArtistListAdapter(getActivity(), artistMusicList,sizeList);
        mListView.setAdapter(artistFragment);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("艺术家");
    }

    private List<?> getArtistMusicInfos(int flag) {
        List<MusicInfoBean> artistMusicList = new ArrayList<>();
        List<Integer> sizeList = new ArrayList<>();
        artistMusicList.addAll(mMusicList);
        boolean isFirst = false;
        String artist = null;
        int size = 1;

        for (int i = 0; i < artistMusicList.size(); i++) {
            artist = artistMusicList.get(i).getArtist();
            size = 1;
            isFirst = false;
            Iterator iterator = artistMusicList.iterator();
            while (iterator.hasNext()) {
                MusicInfoBean musicInfo = (MusicInfoBean) iterator.next();
                if (musicInfo.getArtist().equals(artist)) {
                    if (isFirst) {
                        iterator.remove();
                        size = size + 1;
                    }
                    isFirst = true;
                }
            }
            sizeList.add(size);
        }
        if (flag == 0) {
            return artistMusicList;
        } else {
            return sizeList;
        }
    }
}
