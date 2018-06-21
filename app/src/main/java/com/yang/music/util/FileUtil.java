package com.yang.music.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class FileUtil {

    /*删除文件夾下的所以文件或删除文件*/
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; files != null && i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
        }
    }

    public static void writeMemoreyFile(String fileName,String string,Context context) {

        try {
            //已追加模式打开文件输出流
            FileOutputStream fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            //将FileOutputStream封装成PrintStream
            PrintStream ps = new PrintStream(fos);
            //输出文件内容
            ps.println(string);
            //关闭文件输出流
            ps.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static String readMemoreyFile(String fileName,Context context) {
        StringBuilder sb = null;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[]buff = new byte[1024];
            int hasRead = 0;
            sb = new StringBuilder("");
            try {
                while((hasRead = fis.read(buff)) > 0){
                    sb.append(new String(buff,0,hasRead));
                }
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public static String getFileRoot(){
        return Environment.getExternalStorageDirectory()+ File.separator;
    }
}
