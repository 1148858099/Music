package com.yang.music.util;

import java.io.File;
import java.util.ArrayList;

import static com.yang.music.base.Constant.DIR_MUSIC_IMAGE;



public class CoverUrlLoad {

    private static final String COVER_PATH = DownloadUtils.getMusicRootPath() + DIR_MUSIC_IMAGE;
    private static ArrayList<String>coverList = new ArrayList<>();

    public static void loadCoverUrl(){

        File file = new File(COVER_PATH);
        if(!file.exists()){
            return;
        }
        File[] files = file.listFiles();
        if(files != null && files.length > 0) {
            for (File file1 : files) {
                coverList.add(file1.getAbsolutePath());
            }
        }
    }

    public static ArrayList<String> getCoverList() {
        return coverList;
    }
}
