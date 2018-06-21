package com.yang.music.util.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;



public class MytextView extends TextView{

    public MytextView(Context context) {
        super(context);
    }

    public MytextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MytextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
