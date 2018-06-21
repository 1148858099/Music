package com.yang.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.yang.music.R;
import com.yang.music.adapter.HomeAdapter;
import com.yang.music.base.BaseFragment;
import com.yang.music.bean.HomeListBean;
import com.yang.music.eventModel.MusicListCheckedEvent;
import com.yang.music.myEnum.ChangeFragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/12/13.
 */

public class HomeFragment extends BaseFragment{
    private static final String TAG = "HomeFragment";
    private GridView mGridView;
    private HomeAdapter homeAdapter;
    private List<HomeListBean> mList = new ArrayList<>();

    private static final int GRID_POSITION_ZERO = 0;
    private static final int GRID_POSITION_ONE = 1;
    private static final int GRID_POSITION_TWO = 2;
    private static final int GRID_POSITION_THREE = 3;
    private static final int GRID_POSITION_FOUR = 4;
    private static final int GRID_POSITION_FIVE = 5;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: " );
        return inflater.inflate(R.layout.frag_home, container, false);
    }

    @Override
    public void initUi(View view) {
        super.initUi(view);
        mGridView = (GridView) view.findViewById(R.id.home_grid);
        initGridView();
        setData();
    }

    public void refresh(){
        setData();
    }

    private void initGridView() {
        homeAdapter = new HomeAdapter(getActivity(), mList);
        mGridView.setAdapter(homeAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case GRID_POSITION_ZERO:
                        Log.e(TAG, "onItemClick: " );
                        EventBus.getDefault().post(new MusicListCheckedEvent(ChangeFragment.MY_MUSIC_FRAGMENT));
                        break;
                    case GRID_POSITION_ONE:
                        EventBus.getDefault().post(new MusicListCheckedEvent(ChangeFragment.ARTIST_FRAGMENT));
                        break;
                    case GRID_POSITION_TWO:
                        EventBus.getDefault().post(new MusicListCheckedEvent(ChangeFragment.ALBUM_FRAGMENT));
                        break;
                    case GRID_POSITION_THREE:
                        EventBus.getDefault().post(new MusicListCheckedEvent(ChangeFragment.FOLDER_FRAGMENT));
                        break;
                    case GRID_POSITION_FOUR:
                        EventBus.getDefault().post(new MusicListCheckedEvent(ChangeFragment.LIKE_FRAGMENT));
                        break;
                    case GRID_POSITION_FIVE:
                        EventBus.getDefault().post(new MusicListCheckedEvent(ChangeFragment.LATELY_PLAY_FRAGMENT));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setData() {

        if(mList.size() > 0){
            mList.clear();
        }

        for (int i = 0; i < 6; i++) {
            HomeListBean homeListModel = new HomeListBean();
            switch (i){
                case GRID_POSITION_ZERO:
                    setModleData(homeListModel,R.drawable.ic_music_all,R.color.transparent,"全部歌曲");
                    break;
                case GRID_POSITION_ONE:
                    setModleData(homeListModel,R.drawable.ic_music_artist,R.color.transparent,"艺术家");
                    break;
                case GRID_POSITION_TWO:
                    setModleData(homeListModel,R.drawable.ic_music_album,R.color.transparent,"专辑");
                    break;
                case GRID_POSITION_THREE:
                    setModleData(homeListModel,R.drawable.ic_music_file,R.color.transparent,"文件夹");
                    break;
                case GRID_POSITION_FOUR:
                    setModleData(homeListModel,R.drawable.ic_music_like,R.color.transparent,"喜欢");
                    break;
                case GRID_POSITION_FIVE:
                    setModleData(homeListModel,R.drawable.ic_music_nearly,R.color.transparent,"最近播放");
                    break;
            }
        }
        homeAdapter.notifyDataSetChanged();
    }

    private void setModleData(HomeListBean homeListModel, int picId, int bgColorId, String title){
        homeListModel.picId = picId;
        homeListModel.title = title;
        homeListModel.bgColorId = bgColorId;
        mList.add(homeListModel);
    }

}
