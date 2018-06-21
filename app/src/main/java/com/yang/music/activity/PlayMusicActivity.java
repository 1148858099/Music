package com.yang.music.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yang.music.MusicService;
import com.yang.music.R;
import com.yang.music.adapter.PlayPagerAdapter;
import com.yang.music.base.BaseActivity;
import com.yang.music.base.OnPlayerListener;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.util.CoverLoader;
import com.yang.music.util.FormatHelper;
import com.yang.music.util.LrcLoader;
import com.yang.music.util.data.StringUtils;
import com.yang.music.util.lrcview.LrcView;
import com.yang.music.util.widget.AlbumCoverView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.yang.music.MusicService.musicInfoList;
import static com.yang.music.activity.HomeActivity.mImageUrl;

/**
 * 正在播放界面
 */

public class PlayMusicActivity extends BaseActivity implements View.OnClickListener
        , ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener, OnPlayerListener {

    private MusicService musicService;

    private LinearLayout llContent, mDotContainer;
    private ImageView ivPlayingBg, ivBack, ivMode, ivPlay, ivNext, ivPrev;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private ViewPager vpPlay;
    private SeekBar sbProgress;

    private AlbumCoverView mAlbumCoverView;
    private LrcView mLrcViewFull;
    private SeekBar sbVolume;
    private AudioManager mAudioManager;
    private List<View> mViewPagerContent;
    private int mPreviousPosition;
    private static final int LRC_LOAD = 1;
    private static final int IMAGE_LOAD = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LRC_LOAD:
                    File file = new File(msg.getData().getString("path"));
                    mLrcViewFull.loadLrc(file);
                    break;
                case IMAGE_LOAD:
                    mAlbumCoverView.setCoverBitmap(bitmapRound);
                    ivPlayingBg.setImageBitmap(bitmapBlur);
                    break;
            }
        }
    };
    private Bitmap bitmapRound;
    private Bitmap bitmapBlur;
    private String musicImageUri;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_play_music);
    }

    @Override
    public void initConfig() {
        super.initConfig();
    }

    @Override
    public void initgetIntent() {
        super.initgetIntent();
    }

    @Override
    public void initUi() {
        musicService = HomeActivity.musicService;
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        mDotContainer = (LinearLayout) findViewById(R.id.dot_container);
        ivPlayingBg = (ImageView) findViewById(R.id.iv_play_page_bg);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivMode = (ImageView) findViewById(R.id.iv_mode);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        ivPrev = (ImageView) findViewById(R.id.iv_prev);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        vpPlay = (ViewPager) findViewById(R.id.vp_play_page);
        sbProgress = (SeekBar) findViewById(R.id.sb_progress);
        executor = Executors.newSingleThreadExecutor();
        initViewPager();
        setDuration();
    }

    @Override
    public void initListener() {
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
        vpPlay.setOnPageChangeListener(this);
        musicService.addPlayerListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        initMode();
    }

    private void initMode() {
        int mode = musicService.getCurrentMode();
        switch (mode) {
            case MusicService.MODE_ONE_LOOP:
                ivMode.setImageLevel(MusicService.MODE_ONE_LOOP);
                break;
            case MusicService.MODE_ALL_LOOP:
                ivMode.setImageLevel(MusicService.MODE_ALL_LOOP);
                break;
            case MusicService.MODE_RANDOM:
                ivMode.setImageLevel(MusicService.MODE_RANDOM);
                break;
            case MusicService.MODE_SEQUENCE:
                ivMode.setImageLevel(MusicService.MODE_SEQUENCE);
                break;
        }
    }

    private void initViewPager() {
        View coverView = LayoutInflater.from(this).inflate(R.layout.viewpager_play_page_cover, null);
        View lrcView = LayoutInflater.from(this).inflate(R.layout.viewpager_play_page_lrc, null);
        mAlbumCoverView = (AlbumCoverView) coverView.findViewById(R.id.album_cover_view);
        mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
        sbVolume = (SeekBar) lrcView.findViewById(R.id.sb_volume);
        mAlbumCoverView.initNeedle(HomeActivity.musicService.isPlaying());
        setCoverCircle();
        mViewPagerContent = new ArrayList<>(2);
        mViewPagerContent.add(coverView);
        mViewPagerContent.add(lrcView);
        vpPlay.setAdapter(new PlayPagerAdapter(mViewPagerContent));

        initVolume();
        for (int i = 0; i < mViewPagerContent.size(); i++) {
            TextView textview = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
            if (i == 0) {
                textview.setSelected(true);
            } else {
                lp.leftMargin = 10;
            }
            textview.setLayoutParams(lp);
            textview.setBackgroundResource(R.drawable.play_music_dot);
            mDotContainer.addView(textview);
        }
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void setCoverCircle() {
        if(musicInfoList.size() == 0){
            return;
        }
        String title = musicInfoList.get(musicService.getCurrentMusic()).getTitle();
        mLrcViewFull.setLabel("正在加载");
        loadLrc(title);

        tvTitle.setText(title);
        setMusicImageUri(musicInfoList.get(musicService.getCurrentMusic()).getPicUri());
        setBitMap();
    }

    private void setMusicImageUri(String imageUri){
        musicImageUri = imageUri;
    }

    private void setBitMap() {
        executor.execute(loadImageRunnable);
    }

    private Runnable loadImageRunnable = new Runnable() {
        @Override
        public void run() {
            String url = "";
            for(int i = 0;i < mImageUrl.size();i++){
               if(mImageUrl.get(i).contains(musicInfoList.get(musicService.getCurrentMusic()).getTitle())){
                   url = mImageUrl.get(i);
               }
            }
            if(StringUtils.isNotEmpty(url)){
                bitmapRound = CoverLoader.loadRound(musicService.getCurrentMusic(),url);
                bitmapBlur = CoverLoader.loadBlur(musicService.getCurrentMusic(),url);
            }else{
                bitmapRound = CoverLoader.loadRound(musicService.getCurrentMusic(),musicImageUri);
                bitmapBlur = CoverLoader.loadBlur(musicService.getCurrentMusic(),musicImageUri);
            }
            handler.sendEmptyMessage(IMAGE_LOAD);
        }
    };

    private void loadLrc(final String title) {
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                if (LrcLoader.getLrcList().size() > 0) {

                    if (StringUtils.isNotEmpty(lrcPath(title))) {

                        Message message = new Message();
                        message.what = LRC_LOAD;
                        Bundle bundle = new Bundle();
                        bundle.putString("path", lrcPath(title));
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }else{
                        mLrcViewFull.loadLrc("");
                        mLrcViewFull.setLabel("加载失败");
                    }
                    executorService.shutdown();
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private String lrcPath(String title) {
        return LrcLoader.getLrcPath(title);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_play:
                musicService.playPause();
                break;
            case R.id.iv_next:
                musicService.toNext();
                break;
            case R.id.iv_prev:
                musicService.toPrevious();
                break;
            case R.id.iv_mode:
                changeMode();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void changeMode() {

        int mode = musicService.getCurrentMode();
        switch (mode) {
            case MusicService.MODE_ONE_LOOP:
                ivMode.setImageLevel(MusicService.MODE_SEQUENCE);
                musicService.saveMode(MusicService.MODE_SEQUENCE);
                break;
            case MusicService.MODE_ALL_LOOP:
                ivMode.setImageLevel(MusicService.MODE_ONE_LOOP);
                musicService.saveMode(MusicService.MODE_ONE_LOOP);
                break;
            case MusicService.MODE_RANDOM:
                ivMode.setImageLevel(MusicService.MODE_ALL_LOOP);
                musicService.saveMode(MusicService.MODE_ALL_LOOP);
                break;
            case MusicService.MODE_SEQUENCE:
                ivMode.setImageLevel(MusicService.MODE_RANDOM);
                musicService.saveMode(MusicService.MODE_RANDOM);
                break;
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (mPreviousPosition != position) {
            mDotContainer.getChildAt(mPreviousPosition).setSelected(false);
            mDotContainer.getChildAt(position).setSelected(true);
            mPreviousPosition = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        if (seekBar == sbProgress && fromUser) {
            musicService.changeProgress(progress);
            Log.e("progerss", progress + "");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, filter);

        if (musicService.isPlaying()) {
            ivPlay.setSelected(true);
            mAlbumCoverView.start();
        } else {
            ivPlay.setSelected(false);
            mAlbumCoverView.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mVolumeReceiver);
        musicService.removePlayerListener(this);
    }

    @Override
    public void onProgressChange(int progress) {

        sbProgress.setProgress(progress / 1000);
        tvCurrentTime.setText(FormatHelper.formatDuration(progress));

        mLrcViewFull.updateTime(progress);
    }

    @Override
    public void onMusicChange(MusicInfoBean musicInfo,int duratime) {
        ivPlay.setSelected(true);
        setDuration();
        setMusicImageUri(musicInfo.getPicUri());
        setCoverCircle();
        mAlbumCoverView.start();
    }

    @Override
    public void onPlayPause() {
        mAlbumCoverView.pause();
        ivPlay.setSelected(false);
    }

    @Override
    public void onPlayResume() {
        mAlbumCoverView.start();
        ivPlay.setSelected(true);
    }

    @Override
    public void onTimer(long time) {

    }

    private void setDuration() {
        sbProgress.setMax(musicService.getDurationTime() / 1000);
        tvTotalTime.setText(FormatHelper.formatDuration(musicService.getDurationTime()));
    }

}
