package com.yang.music.combination;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yang.music.R;


public class BottomView {

    private static SeekBar durationSeekBar;
    private static ImageView musicImage;
    private static TextView topTv,bottomTv;
    private static ImageView switchBtn,nextBtn,prevBtn;

    public static View getView() {
        return view;
    }

    private static View view;

    public static void getBottomView(Activity context, int viewId){

        view = context.findViewById(viewId);
        durationSeekBar = (SeekBar) view.findViewById(R.id.duration_seekbar);
        musicImage = (ImageView) view.findViewById(R.id.music_image);
        topTv = (TextView) view.findViewById(R.id.current_music_top_tv);
        bottomTv = (TextView) view.findViewById(R.id.current_music_bottom_tv);
        switchBtn = (ImageView) view.findViewById(R.id.switch_btn);
        nextBtn = (ImageView) view.findViewById(R.id.next_btn);
        prevBtn = (ImageView) view.findViewById(R.id.prev_btn);
    }

    public static SeekBar getDurationSeekBar() {
        return durationSeekBar;
    }

    public static ImageView getMusicImage() {
        return musicImage;
    }

    public static TextView getTopTv() {
        return topTv;
    }

    public static TextView getBottomTv() {
        return bottomTv;
    }

    public static ImageView getSwitchBtn() {
        return switchBtn;
    }

    public static ImageView getNextBtn() {
        return nextBtn;
    }
    public static ImageView getPrevBtn() {
        return prevBtn;
    }
}
