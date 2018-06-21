package com.yang.music.fragment;

import android.view.View;
import android.widget.AdapterView;

import com.yang.music.adapter.AlbumListAdapter;
import com.yang.music.base.BaseFragment;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.yang.music.activity.HomeActivity.mMusicList;


public class AlbumFragment extends BaseFragment {

    private static final String TAG = "AlbumFragment";
    private List<MusicInfoBean> albumMusicList = new ArrayList<>();
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
                EventBus.getDefault().post(new MusicCheckedEvent(position,null,albumMusicList.get(position).getAlbum()));
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        initTitle();
        albumMusicList.addAll((List<MusicInfoBean>)getAlbumMusicInfos(0));
        sizeList.addAll((List<Integer>)getAlbumMusicInfos(1));

        AlbumListAdapter albumListAdapter = new AlbumListAdapter(getActivity(), albumMusicList,sizeList);
        mListView.setAdapter(albumListAdapter);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("专辑");
    }

    private List<?> getAlbumMusicInfos(int flag) {
        List<MusicInfoBean> albumMusicList = new ArrayList<>();
        List<Integer> sizeList = new ArrayList<>();
        albumMusicList.addAll(mMusicList);
        boolean isFirst = false;
        String album = null;
        int size = 1;

        for (int i = 0; i < albumMusicList.size(); i++) {
            album = albumMusicList.get(i).getAlbum();
            size = 1;
            isFirst = false;
            Iterator iterator = albumMusicList.iterator();
            while (iterator.hasNext()) {
                MusicInfoBean musicInfo = (MusicInfoBean) iterator.next();
                if (musicInfo.getAlbum().equals(album)) {
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
            return albumMusicList;
        } else {
            return sizeList;
        }
    }
}
