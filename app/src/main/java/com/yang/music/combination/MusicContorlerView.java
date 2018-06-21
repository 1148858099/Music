package com.yang.music.combination;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.yang.music.R;
import com.yang.music.adapter.MusicControlAdapter;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.fragment.LikeFragment;
import com.yang.music.util.data.LikeMusic;

import java.util.ArrayList;
import java.util.List;

import static com.yang.music.BaseApp.mApp;



public class MusicContorlerView {

    private PopupWindow popupWindow;
    private View contentView;
    private ListView mListView;
    private Context mContext;
    private View mView;
    private List<String> mList = new ArrayList<>();
    private final int MUSIC_CONTROL_ZERO = 0;
    private final int MUSIC_CONTROL_ONE = 1;
    private final int MUSIC_CONTROL_TWO = 2;
    private final int MUSIC_CONTROL_THREE = 3;

    private MusicInfoBean musicInfo;
    private MusicControlAdapter musicControladapter;
    private boolean isLike;

    public MusicContorlerView(){

    }

    public void controlMyMusic(Context context, View view, boolean isLike) {
        mContext = context;
        mView = view;
        this.isLike = isLike;
        initView();
        initListener();
        initPopupWindow();
    }

    private void initView() {

        contentView = LayoutInflater.from(mApp).inflate(R.layout.popup_control_music, null);
        mListView = (ListView) contentView.findViewById(R.id.music_control_listview);

        musicControladapter = new MusicControlAdapter(mContext, mList);
        mListView.setAdapter(musicControladapter);
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case MUSIC_CONTROL_ZERO:
                        if(!LikeMusic.getRecordMessage(mContext).toString().contains(musicInfo.toString())){
                            LikeMusic.setRecordMessage(mContext,musicInfo,true);
                        }else{
                            LikeMusic.setRecordMessage(mContext,musicInfo,false);
                            if (isLike){
                                ((ListView)mView).setAdapter(new LikeFragment.MusicLikeListAdapter(mContext));
                            }
                        }

                        popupWindow.dismiss();
                        break;
                    case MUSIC_CONTROL_ONE:

                        break;
                }

            }
        });
    }

    private void initPopupWindow() {

        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.color.transparent));

    }

    private void setData() {

        if(mList.size() > 0){
            mList.clear();
        }

        for (int i = 0; i < 1; i++) {
            switch (i) {
                case MUSIC_CONTROL_ZERO:
                    Log.d("Xian","MusicControllerView = " + LikeMusic.getRecordMessage(mContext).toString());
                    if(!LikeMusic.getRecordMessage(mContext).toString().contains(musicInfo.toString())){
                        mList.add("喜欢");
                    }else{
                        mList.add("不喜欢");
                    }

                    break;
            }
        }
    }

    public void showPopup(MusicInfoBean musicInfo){
        this.musicInfo = musicInfo;
        setData();
        // 设置好参数之后再show
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
        }
    }
}
