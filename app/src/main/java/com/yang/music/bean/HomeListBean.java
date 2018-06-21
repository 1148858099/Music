package com.yang.music.bean;

import java.io.Serializable;



public class HomeListBean implements Serializable{

    public int picId;
    public String title;
    public int bgColorId;

    @Override
    public String toString() {
        return "HomeListModel{" +
                ", picId=" + picId +
                ", title='" + title + '\'' +
                ", bgColorId=" + bgColorId +
                '}';
    }
}
