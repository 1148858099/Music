package com.yang.music.util.data;

/**

 */

public class StringUtils {

    public static boolean isNotEmpty(String string){

        return string != null && !string.trim().isEmpty()?true:false;
    }
}
