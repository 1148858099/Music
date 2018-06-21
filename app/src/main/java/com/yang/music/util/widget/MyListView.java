package com.yang.music.util.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;

import com.yang.music.R;



public class MyListView extends ListView implements AbsListView.OnScrollListener{

    private LoadNextListener loadNextListener;

    public MyListView(Context context) {
        super(context);
        init();
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 设置滚动监听
     * footerview 为1dp高度
     */
    private void init() {
        setOnScrollListener(this);
        addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.listview_loading_footer, null));
    }

    /**
     * 滚动监听
     *
     * @param view        The view whose scroll state is being reported
     * @param scrollState The current scroll state. One of
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

            if (view.getLastVisiblePosition() == view.getCount() - 1 && LFStatus.getBLKLFStatus().equals(LFStatus.THEND)) {

                loadNextListener.loadNext();
            }
        }
    }

    public void BLKListViewCall(LoadNextListener loadNextListener) {

        this.loadNextListener = loadNextListener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public interface LoadNextListener {

        void loadNext();
    }

}
