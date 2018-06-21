package com.yang.music.util.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yang.music.base.Constant;
import com.yang.music.bean.MusicInfoBean;
import com.yang.music.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;



public class LatelyPlayMusic {

    public static void setRecordMessage(Context context, MusicInfoBean musicInfo){

        List<MusicInfoBean> musicInfos;
        musicInfos = getRecordMessage(context);

        if(!(musicInfos.toString().contains(musicInfo.toString()))){
            musicInfos.add(musicInfo);
            Log.e("musicInfos",musicInfos.toString());
            saveMessage(musicInfos,context);
        }
    }

    private static void saveMessage(List<MusicInfoBean> nearAutomatModels, Context context){

        Gson gson = new Gson();
        String string = gson.toJson(nearAutomatModels);
        FileUtil.writeMemoreyFile(Constant.SHARE_LATELY_PLAY_MUSIC, string, context);

    }

    public static List<MusicInfoBean> getRecordMessage(Context context){

        List<MusicInfoBean> modelList = new ArrayList<>();
        String string = FileUtil.readMemoreyFile(Constant.SHARE_LATELY_PLAY_MUSIC,context);

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
