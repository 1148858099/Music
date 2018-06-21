package com.yang.music.combination;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.yang.music.R;



public class TopView {

    private static TextView topTitle;
    private static TextView topLeft;
    private static TextView topRight;
    public static void getTopView(Activity context, int viewId){
        View view = context.findViewById(viewId);
        topTitle = (TextView) view.findViewById(R.id.top_middle_tv);
        topLeft = (TextView) view.findViewById(R.id.top_left_tv);
        topRight = (TextView) view.findViewById(R.id.top_right_tv);
    }

    public static TextView getTopTitle() {
        return topTitle;
    }

    public static TextView getTopLeft() {
        return topLeft;
    }

    public static TextView getTopRight() {
        return topRight;
    }
}
