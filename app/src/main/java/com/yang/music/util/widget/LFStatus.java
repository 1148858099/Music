package com.yang.music.util.widget;

/**
 * 上拉加载的状态

 */
public enum LFStatus {

    NULL,LOADING,THEND;
    //默认状态,加载中，加载完。

    private static LFStatus blklfStatus;

    private LFStatus(){
    }

    public static LFStatus getBLKLFStatus(){

        if(blklfStatus != null){
            return blklfStatus;
        }else{
            return NULL;
        }
    }

    public static void setBLKLFStatus(LFStatus status){

        blklfStatus = status;
    }
}
