package com.yang.music;

import android.app.Application;
import android.widget.Toast;

import com.yang.music.combination.MusicContorlerView;
import com.yang.music.util.CoverUrlLoad;
import com.yang.music.util.LrcLoader;
import com.yang.music.util.data.KV;



public class BaseApp extends Application{
    public static BaseApp mApp;
    public static MusicContorlerView musicContorlerView;
    private KV kv;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        musicContorlerView = new MusicContorlerView();
        LrcLoader.getLrcFilePath();
        CoverUrlLoad.loadCoverUrl();
    }

    public KV getKv(){
        if(kv == null){
            kv = new KV(this);
        }
        return kv;
    }

    public static void showMessage(String message){
        Toast.makeText(mApp,message,Toast.LENGTH_SHORT).show();
    }
}
