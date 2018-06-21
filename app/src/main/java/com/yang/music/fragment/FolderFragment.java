package com.yang.music.fragment;

import android.view.View;
import android.widget.AdapterView;

import com.yang.music.adapter.FolderListAdapter;
import com.yang.music.base.BaseFragment;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.yang.music.activity.HomeActivity.mMusicList;
import static com.yang.music.myEnum.ChangeFragment.getUrl;



public class FolderFragment extends BaseFragment {

    private static final String TAG = "ArtistFragment";
    private List<MusicInfoBean> folderMusicList = new ArrayList<>();
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
                EventBus.getDefault().post(new MusicCheckedEvent(position,null, getUrl(folderMusicList.get(position).getUrl())));
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        initTitle();
        folderMusicList.addAll((List<MusicInfoBean>)getFolderMusicInfos(0));
        sizeList.addAll((List<Integer>)getFolderMusicInfos(1));

        FolderListAdapter folderListAdapter = new FolderListAdapter(getActivity(), folderMusicList,sizeList);
        mListView.setAdapter(folderListAdapter);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("文件夹");
    }

    private static List<?> getFolderMusicInfos(int flag) {
        List<MusicInfoBean> folderMusicList = new ArrayList<>();
        List<Integer> sizeList = new ArrayList<>();
        folderMusicList.addAll(mMusicList);
        boolean isFirst = false;
        String folder = null;
        int size = 1;

        for (int i = 0; i < folderMusicList.size(); i++) {
            folder = getUrl(folderMusicList.get(i).getUrl());
            size = 1;
            isFirst = false;
            Iterator iterator = folderMusicList.iterator();
            while (iterator.hasNext()) {
                MusicInfoBean musicInfo = (MusicInfoBean) iterator.next();
                if (getUrl(musicInfo.getUrl()).equals(folder)) {
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
            return folderMusicList;
        } else {
            return sizeList;
        }
    }
}
