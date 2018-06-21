package com.yang.music.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.yang.music.MusicService;
import com.yang.music.R;
import com.yang.music.base.BaseActivity;
import com.yang.music.base.MusicLoader;
import com.yang.music.base.OnPlayerListener;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.combination.BottomView;
import com.yang.music.combination.TopView;
import com.yang.music.eventModel.MusicCheckedEvent;
import com.yang.music.eventModel.MusicListCheckedEvent;
import com.yang.music.fragment.AlbumFragment;
import com.yang.music.fragment.AlbumListFragment;
import com.yang.music.fragment.ArtistFragment;
import com.yang.music.fragment.ArtistListFragment;
import com.yang.music.fragment.FolderFragment;
import com.yang.music.fragment.FolderListFragment;
import com.yang.music.fragment.HomeFragment;
import com.yang.music.fragment.LatelyPlayFragment;
import com.yang.music.fragment.LikeFragment;
import com.yang.music.fragment.MyMusicFragment;
import com.yang.music.myEnum.ChangeFragment;
import com.yang.music.util.CoverLoader;
import com.yang.music.util.CoverUrlLoad;
import com.yang.music.util.data.LatelyPlayMusic;
import com.yang.music.util.data.LikeMusic;
import com.yang.music.util.data.StringUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;

import static com.yang.music.MusicService.musicInfoList;
import static com.yang.music.myEnum.ChangeFragment.ALBUM_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.ALBUM_LIST_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.ARTIST_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.ARTIST_LIST_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.FOLDER_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.FOLDER_LIST_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.HOME_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.LATELY_PLAY_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.LIKE_FRAGMENT;
import static com.yang.music.myEnum.ChangeFragment.MY_MUSIC_FRAGMENT;


public class HomeActivity extends BaseActivity implements View.OnClickListener,OnPlayerListener {
    private static final String TAG = "HomeActivity";
    public static List<MusicInfoBean> mMusicList = new ArrayList<>();
    public static List<String>mImageUrl = new ArrayList<>();
    public static MusicService musicService;

    private HomeFragment homeFragment;
    private MyMusicFragment myMusicFragment;
    private ArtistFragment artistFragment;
    private ArtistListFragment artistListFragment;
    private AlbumFragment albumFragment;
    private AlbumListFragment albumListFragment;
    private FolderFragment folderFragment;
    private FolderListFragment folderListFragment;
    private LikeFragment likeFragment;
    private LatelyPlayFragment latelyPlayFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private View flView;

    private ArrayList<MusicInfoBean> artistList = new ArrayList<>();
    private ArrayList<MusicInfoBean> albumList = new ArrayList<>();
    private ArrayList<MusicInfoBean> folderList = new ArrayList<>();
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBinder) service).getMusicService();
            musicService.addPlayerListener(HomeActivity.this);
            if (musicInfoList.size() == 0) {
                return;
            }
            if (musicInfoList.size() >= musicService.getCurrentMusic()) {
                String url = "";
                for (int i = 0; i < mImageUrl.size(); i++) {
                    if (mImageUrl.get(i).contains(musicInfoList.get(musicService.getCurrentMusic()).getTitle())) {
                        url = mImageUrl.get(i);
                    }
                }
                if (StringUtils.isNotEmpty(url)) {
                    BottomView.getMusicImage().setImageBitmap(CoverLoader.loadNormal(musicService.getCurrentMusic(), url));
                } else {
                    BottomView.getMusicImage().setImageBitmap(CoverLoader.loadNormal(musicService.getCurrentMusic(), musicInfoList.get(musicService.getCurrentMusic()).getPicUri()));
                }
                BottomView.getTopTv().setText(musicInfoList.get(musicService.getCurrentMusic()).getTitle());
                BottomView.getBottomTv().setText(musicInfoList.get(musicService.getCurrentMusic()).getArtist());
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);

    }

    @Override
    public void initConfig() {
        super.initConfig();
        isEvent = true;
    }

    @Override
    public void initUi() {
        super.initUi();
        BottomView.getBottomView(this, R.id.bottom_view);

/*
   flView = findViewById(R.id.fl_home_time);
*/
        mImageUrl.addAll(CoverUrlLoad.getCoverList());
        fragmentManager = getSupportFragmentManager();

    }



    @Override
    public void initListener() {
        super.initListener();
        BottomView.getPrevBtn().setOnClickListener(this);
        BottomView.getNextBtn().setOnClickListener(this);
        BottomView.getSwitchBtn().setOnClickListener(this);
        TopView.getTopRight().setOnClickListener(this);
        TopView.getTopLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChangeFragment.getChangeFragment().equals(ARTIST_LIST_FRAGMENT)) {
                    select(ARTIST_FRAGMENT.getValue());
                }else if (ChangeFragment.getChangeFragment().equals(ALBUM_LIST_FRAGMENT)) {
                    select(ALBUM_FRAGMENT.getValue());
                } else if(ChangeFragment.getChangeFragment().equals(FOLDER_LIST_FRAGMENT)){
                    select(FOLDER_FRAGMENT.getValue());
                }else {
                    select(HOME_FRAGMENT.getValue());
                }
            }
        });

        BottomView.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,PlayMusicActivity.class));
            }
        });
    }

    @Override
    public void initData() {
        Log.d("Xian","HomeActivity initData()");
        super.initData();
        initTitle();
        mMusicList.addAll(MusicLoader.instance(getContentResolver()).getAllMusicList());
        select(HOME_FRAGMENT.getValue());
        connectToMusicService();
    }

    @Override
    public void initTitle() {
        super.initTitle();
        TopView.getTopTitle().setText("本地音乐");
    }

    private void connectToMusicService() {

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        if (musicInfoList != null) {
            musicInfoList.clear();
            musicInfoList.addAll(mMusicList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_btn:
                musicService.playPause();
                break;
            case R.id.next_btn:
                musicService.toNext();
                break;
            case R.id.prev_btn:
                musicService.toPrevious();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Xian","HomeActivity onResume()");
        if (musicService != null) {
            if (musicService.isPlaying()) {
                BottomView.getSwitchBtn().setSelected(true);
            } else {
                BottomView.getSwitchBtn().setSelected(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicService != null) {
            unbindService(serviceConnection);
        }
    }

    private void select(int position) {
        fragmentTransaction = fragmentManager.beginTransaction();
        hide();

        if (HOME_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.GONE);
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.home_frag, homeFragment);
            } else {
                homeFragment.refresh();
                fragmentTransaction.show(homeFragment);
            }
            ChangeFragment.setChangeFragment(HOME_FRAGMENT);
            homeFragment.initTitle();
        }

        if (MY_MUSIC_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);
            if (myMusicFragment == null) {
                myMusicFragment = new MyMusicFragment();
                fragmentTransaction.add(R.id.home_frag, myMusicFragment);
            } else {
                fragmentTransaction.show(myMusicFragment);
            }
            ChangeFragment.setChangeFragment(MY_MUSIC_FRAGMENT);
            myMusicFragment.initTitle();
        }

        if (ARTIST_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);
            if (artistFragment == null) {
                artistFragment = new ArtistFragment();
                fragmentTransaction.add(R.id.home_frag, artistFragment);
            } else {
                fragmentTransaction.show(artistFragment);
            }
            ChangeFragment.setChangeFragment(ARTIST_FRAGMENT);
            artistFragment.initTitle();
        }
        if (ARTIST_LIST_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);

            if (artistListFragment == null) {
                artistListFragment = new ArtistListFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("artist", artistList);
                artistListFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.home_frag, artistListFragment);
            } else {
                fragmentTransaction.show(artistListFragment);
                artistListFragment.setArtistList(artistList);
            }
            ChangeFragment.setChangeFragment(ARTIST_LIST_FRAGMENT);
            artistListFragment.initTitle();
        }

        if (ALBUM_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);
            if (albumFragment == null) {
                albumFragment = new AlbumFragment();
                fragmentTransaction.add(R.id.home_frag, albumFragment);
            } else {
                fragmentTransaction.show(albumFragment);
            }
            ChangeFragment.setChangeFragment(ALBUM_FRAGMENT);
            albumFragment.initTitle();
        }
        if (ALBUM_LIST_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);

            if (albumListFragment == null) {
                albumListFragment = new AlbumListFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("album", albumList);
                albumListFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.home_frag, albumListFragment);
            } else {
                fragmentTransaction.show(albumListFragment);
                albumListFragment.setAlbumList(albumList);
            }
            ChangeFragment.setChangeFragment(ALBUM_LIST_FRAGMENT);
            albumListFragment.initTitle();
        }

        if (FOLDER_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);
            if (folderFragment == null) {
                folderFragment = new FolderFragment();
                fragmentTransaction.add(R.id.home_frag, folderFragment);
            } else {
                fragmentTransaction.show(folderFragment);
            }
            ChangeFragment.setChangeFragment(FOLDER_FRAGMENT);
            folderFragment.initTitle();
        }
        if ( FOLDER_LIST_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);

            if (folderListFragment == null) {
                folderListFragment = new FolderListFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("folder", folderList);
                folderListFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.home_frag, folderListFragment);
            } else {
                fragmentTransaction.show(folderListFragment);
                folderListFragment.setFolderList(folderList);
            }
            ChangeFragment.setChangeFragment(FOLDER_LIST_FRAGMENT);
            folderListFragment.initTitle();
        }

        if ( LIKE_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);

            likeFragment = new LikeFragment();
            Bundle bundle = new Bundle();
            ArrayList<MusicInfoBean>likeList = new ArrayList<>();
            likeList.addAll(LikeMusic.getRecordMessage(this));
            bundle.putParcelableArrayList("like", likeList);
            likeFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.home_frag, likeFragment);

//            if (likeFragment == null) {
//                likeFragment = new LikeFragment();
//                Bundle bundle = new Bundle();
//                ArrayList<MusicInfoBean>likeList = new ArrayList<>();
//                likeList.addAll(LikeMusic.getRecordMessage(this));
//                bundle.putParcelableArrayList("like", likeList);
//                likeFragment.setArguments(bundle);
//                fragmentTransaction.add(R.id.home_frag, likeFragment);
//            } else {
//                ArrayList<MusicInfoBean>likeList = new ArrayList<>();
//                likeList.addAll(LikeMusic.getRecordMessage(this));
//                likeFragment.mListView.setAdapter(new LikeFragment.MusicLikeListAdapter(getApplicationContext()));
//                fragmentTransaction.show(likeFragment);
//                likeFragment.setLikeList(likeList);
//            }
            ChangeFragment.setChangeFragment(LIKE_FRAGMENT);
            likeFragment.initTitle();
        }

        if ( LATELY_PLAY_FRAGMENT.getValue() == position) {
            TopView.getTopLeft().setVisibility(View.VISIBLE);

            if (latelyPlayFragment == null) {
                latelyPlayFragment = new LatelyPlayFragment();
                Bundle bundle = new Bundle();
                ArrayList<MusicInfoBean>latelyPlayList = new ArrayList<>();
                latelyPlayList.addAll(LatelyPlayMusic.getRecordMessage(this));
                bundle.putParcelableArrayList("lately_play", latelyPlayList);
                latelyPlayFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.home_frag, latelyPlayFragment);
            } else {
                ArrayList<MusicInfoBean>latelyPlayList = new ArrayList<>();
                latelyPlayList.addAll(LatelyPlayMusic.getRecordMessage(this));

                fragmentTransaction.show(latelyPlayFragment);
                latelyPlayFragment.setlatelyPlayList(latelyPlayList);
            }
            ChangeFragment.setChangeFragment(LATELY_PLAY_FRAGMENT);
            latelyPlayFragment.initTitle();
        }

        fragmentTransaction.commit();
    }

    private void hide() {
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        }
        if (myMusicFragment != null) {
            fragmentTransaction.hide(myMusicFragment);
        }

        if (artistFragment != null) {
            fragmentTransaction.hide(artistFragment);
        }

        if (artistListFragment != null) {
            fragmentTransaction.hide(artistListFragment);
        }

        if (albumFragment != null) {
            fragmentTransaction.hide(albumFragment);
        }

        if (albumListFragment != null) {
            fragmentTransaction.hide(albumListFragment);
        }

        if(folderFragment != null){
            fragmentTransaction.hide(folderFragment);
        }

        if(folderListFragment != null){
            fragmentTransaction.hide(folderListFragment);
        }

        if(likeFragment != null){
            fragmentTransaction.hide(likeFragment);
        }

        if(latelyPlayFragment != null){
            fragmentTransaction.hide(latelyPlayFragment);
        }
        initTitle();
    }

     //选着的那首音乐
   @Subscribe
    public void onEventMainThread(MusicCheckedEvent musicCheckedEvent) {

        if (musicCheckedEvent.getmMusicList() != null) {
            musicInfoList.clear();
            musicInfoList.addAll(musicCheckedEvent.getmMusicList());
            musicService.play(musicCheckedEvent.getPosition());
        } else if (ChangeFragment.getChangeFragment().equals(ARTIST_FRAGMENT)) {
            artistList.clear();
            for (int i = 0; i < mMusicList.size(); i++) {
                if (mMusicList.get(i).getArtist().equals(musicCheckedEvent.getFlag())) {
                    artistList.add(mMusicList.get(i));
                }
            }
            select(ARTIST_LIST_FRAGMENT.getValue());
            Log.e("checkedmusic","adadadadada");
        } else if (ChangeFragment.getChangeFragment().equals(ALBUM_FRAGMENT)) {
            albumList.clear();
            for (int i = 0; i < mMusicList.size(); i++) {
                if (mMusicList.get(i).getAlbum().equals(musicCheckedEvent.getFlag())) {
                    albumList.add(mMusicList.get(i));
                }
            }
            select(ALBUM_LIST_FRAGMENT.getValue());
        }else if(ChangeFragment.getChangeFragment().equals(FOLDER_FRAGMENT)){
            folderList.clear();
            for (int i = 0; i < mMusicList.size(); i++) {
                if (ChangeFragment.getUrl(mMusicList.get(i).getUrl()).equals(musicCheckedEvent.getFlag())) {
                    folderList.add(mMusicList.get(i));
                }
            }
            select(FOLDER_LIST_FRAGMENT.getValue());
        }

        if (musicService.isPlaying()) {
            BottomView.getSwitchBtn().setSelected(true);
        }else{
            BottomView.getSwitchBtn().setSelected(false);
        }

    }

    @Subscribe
    public void onEventMainThread(MusicListCheckedEvent musicListCheckedEvent) {
        Log.e(TAG, "onEventMainThread: ");
        if (musicListCheckedEvent.getChangeFragment().equals(ChangeFragment.MY_MUSIC_FRAGMENT)) {
            Log.e(TAG, "onEventMainThread: ");
            select(MY_MUSIC_FRAGMENT.getValue());
        }
        if (musicListCheckedEvent.getChangeFragment().equals(ChangeFragment.ARTIST_FRAGMENT)) {
            select(ARTIST_FRAGMENT.getValue());
        }

        if (musicListCheckedEvent.getChangeFragment().equals(ChangeFragment.ALBUM_FRAGMENT)) {
            select(ALBUM_FRAGMENT.getValue());
        }

        if (musicListCheckedEvent.getChangeFragment().equals(ChangeFragment.FOLDER_FRAGMENT)) {
            select(FOLDER_FRAGMENT.getValue());
        }

        if(musicListCheckedEvent.getChangeFragment().equals(ChangeFragment.LIKE_FRAGMENT)){
            select(LIKE_FRAGMENT.getValue());
        }

        if(musicListCheckedEvent.getChangeFragment().equals(ChangeFragment.LATELY_PLAY_FRAGMENT)){
            select(LATELY_PLAY_FRAGMENT.getValue());
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (ChangeFragment.getChangeFragment().equals(HOME_FRAGMENT)) {
                    moveTaskToBack(false);
                } else if (ChangeFragment.getChangeFragment().equals(ARTIST_LIST_FRAGMENT)) {
                    select(ARTIST_FRAGMENT.getValue());
                } else if (ChangeFragment.getChangeFragment().equals(ALBUM_LIST_FRAGMENT)) {
                    select(ALBUM_FRAGMENT.getValue());
                } else if(ChangeFragment.getChangeFragment().equals(FOLDER_LIST_FRAGMENT)){
                    select(FOLDER_FRAGMENT.getValue());
                }else{
                    select(HOME_FRAGMENT.getValue());
                }
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onProgressChange(int progress) {
        BottomView.getDurationSeekBar().setProgress(progress / 1000);
    }

    @Override
    public void onMusicChange(MusicInfoBean musicInfo,int duration) {

        BottomView.getTopTv().setText(musicInfo.getTitle());
        BottomView.getBottomTv().setText(musicInfo.getArtist());

        String url = "";
        for(int i = 0;i < mImageUrl.size();i++){
            if(mImageUrl.get(i).contains(musicInfoList.get(musicService.getCurrentMusic()).getTitle())){
                url = mImageUrl.get(i);
            }
        }
        if(StringUtils.isNotEmpty(url)){
            BottomView.getMusicImage().setImageBitmap(CoverLoader.loadNormal(musicService.getCurrentMusic(),url));
        }else{
            BottomView.getMusicImage().setImageBitmap(CoverLoader.loadNormal(musicService.getCurrentMusic(),musicInfo.getPicUri()));
        }

        LatelyPlayMusic.setRecordMessage(HomeActivity.this, musicInfo);
        Log.e("UPDATE_CURRENT_MUSIC",musicInfo.getTitle());

        BottomView.getSwitchBtn().setSelected(true);
        int max = duration / 1000;
        BottomView.getDurationSeekBar().setMax(max);
    }

    @Override
    public void onPlayPause() {
        BottomView.getSwitchBtn().setSelected(false);
    }

    @Override
    public void onPlayResume() {
        BottomView.getSwitchBtn().setSelected(true);
    }

    @Override
    public void onTimer(long time) {

        long second= time/1000;

    }

}
