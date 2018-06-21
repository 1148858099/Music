package com.yang.music.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yang.music.R;


/**
 * 正在加载的对话框

 */
public class LoadingDialog extends Dialog{

    private LayoutInflater inflater;
    private WindowManager.LayoutParams lp;
    private TextView titleTv;

    public LoadingDialog(Context context) {
        this(context, null);
    }

    public LoadingDialog(Context context, View layout) {
        super(context, R.style.loading_dialog);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.loading_dialog, null);
        titleTv = (TextView) layout.findViewById(R.id.loading_text);
        titleTv.setVisibility(View.GONE);
        setContentView(layout);
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    public void showLoading() {

        if (!isShowing()) {
            setCancelable(false);
            show();
        }
    }

    public void dismissLoading() {
        dismiss();
    }

    public void setMessage(String message) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(message);
    }

    public void setTitleSize(float size) {
        titleTv.setTextSize(size);
    }

    public void setTitleColor(int color) {
        titleTv.setTextColor(color);
    }

}
