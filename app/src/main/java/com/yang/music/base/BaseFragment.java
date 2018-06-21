package com.yang.music.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yang.music.R;
import com.yang.music.combination.TopView;


public class BaseFragment extends Fragment implements BaseInterface{

    public ListView mListView;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initConfig();
        initgetIntent();
        initUi(view);
        initListener();
        initData();
        initTitle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void initConfig() {

    }

    @Override
    public void initgetIntent() {

    }

    @Override
    public void initUi() {
        TopView.getTopView(getActivity(), R.id.top_view);
    }

    @Override
    public void initUi(View view) {
        mListView = (ListView) view.findViewById(R.id.my_music_listview);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initTitle() {

    }
}
