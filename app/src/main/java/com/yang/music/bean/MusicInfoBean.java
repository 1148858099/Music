package com.yang.music.bean;

import android.os.Parcel;
import android.os.Parcelable;



public class MusicInfoBean implements Parcelable {
    private long id;
    private String title;
    private String album;
    private long albumId;
    private int duration;
    private long size;
    private String artist;
    private String url;
    private String picUri;

    public MusicInfoBean(){

    }

    public MusicInfoBean(long pId, String pTitle){
        id = pId;
        title = pTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId){
        this.albumId =albumId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(url);
        dest.writeLong(albumId);
        dest.writeInt(duration);
        dest.writeLong(size);
        dest.writeString(picUri);
    }

    public static final Parcelable.Creator<MusicInfoBean>
            CREATOR = new Creator<MusicInfoBean>() {

        @Override
        public MusicInfoBean[] newArray(int size) {
            return new MusicInfoBean[size];
        }

        @Override
        public MusicInfoBean createFromParcel(Parcel source) {
            MusicInfoBean musicInfo = new MusicInfoBean();
            musicInfo.setId(source.readLong());
            musicInfo.setTitle(source.readString());
            musicInfo.setAlbum(source.readString());
            musicInfo.setArtist(source.readString());
            musicInfo.setUrl(source.readString());
            musicInfo.setAlbumId(source.readLong());
            musicInfo.setDuration(source.readInt());
            musicInfo.setSize(source.readLong());
            musicInfo.setPicUri(source.readString());
            return musicInfo;
        }
    };

    @Override
    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                ", picUri='" + picUri + '\'' +
                '}';
    }
}
