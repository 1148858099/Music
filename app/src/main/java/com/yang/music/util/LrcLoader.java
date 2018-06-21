package com.yang.music.util;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class LrcLoader {

    private final static String SDPATH = Environment.getExternalStorageDirectory()+ File.separator; // 目录的名称
    private static List<String> LrcList = new ArrayList<>();
    private static String nowPath;
    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    LrcList.add(msg.getData().getString("path"));
                    break;
            }
        }
    };

    public static void getLrcFilePath(){
        LrcList.clear();
        nowPath = SDPATH;
        ExecutorService service = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getLrcFile();
            }
        });
        service.execute(thread);
    }

    private static void getLrcFile() {

        File file = new File(nowPath);
        if (!file.exists()) {
            return;
        }

        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    nowPath = file1.getAbsolutePath();
                    getLrcFile();
                } else {
                    sendMessage(file1);
                }
            }
        } else {
            sendMessage(file);
        }
    }

    private static void sendMessage(File file){

        String path = file.getAbsolutePath();
        if(path.endsWith(".lrc")){
            Message msg = new Message();
            msg.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("path",path);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    public static String getLrcPath(String title){

        if(LrcList.size() > 0){
            for(String path:LrcList){
                if(path.contains(title)){
                    return path;
                }
            }
        }
        return "";
    }

    public static void addLrcList(String lrc) {
        LrcList.add(lrc);
    }

    public static List<String> getLrcList() {
        return LrcList;
    }
}
