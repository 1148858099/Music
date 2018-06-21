package com.yang.music.util.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yang.music.base.Constant;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.fragment.LikeFragment;
import com.yang.music.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LikeMusic{

    private LikeFragment mLikeFragment;

    public static void setRecordMessage(Context context, MusicInfoBean musicInfo, boolean addLike){

        List<MusicInfoBean> musicInfos;
        musicInfos = getRecordMessage(context);

        if(addLike){
            musicInfos.add(musicInfo);
        }else{
            Iterator<MusicInfoBean> iterator = musicInfos.iterator();
            while (iterator.hasNext()){
                MusicInfoBean musicInfo1 = iterator.next();
                if(musicInfo1.toString().equals(musicInfo.toString())){
                    iterator.remove();
                }
            }
        }
        Log.e("musicInfos",musicInfos.toString());
        saveMessage(musicInfos,context);
    }


    private static void saveMessage(List<MusicInfoBean> nearAutomatModels, Context context){

        Gson gson = new Gson();
        String string = gson.toJson(nearAutomatModels);
        FileUtil.writeMemoreyFile(Constant.SHARE_LIKE_MUSIC, string, context);

    }

    public static List<MusicInfoBean> getRecordMessage(Context context){

        List<MusicInfoBean> modelList = new ArrayList<>();
        String string = FileUtil.readMemoreyFile(Constant.SHARE_LIKE_MUSIC,context);

        if(!StringUtils.isNotEmpty(string)){
            return modelList;
        }
        try {
            JSONArray array = new JSONArray(string);
            try {
                modelList = (ArrayList) JsonUtil.parseJsonToList(array.toString(), new TypeToken<ArrayList<MusicInfoBean>>() {
                }.getType());

            } catch (Exception e) {

                return modelList;
            }

        } catch (JSONException e) {

            return modelList;
        }

        return modelList;
    }
}
