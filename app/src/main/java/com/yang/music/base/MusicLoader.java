package com.yang.music.base;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import com.yang.music.bean.MusicInfoBean;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentUris.withAppendedId;

public class MusicLoader {

    private static final String TAG = "MusicLoader";
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static List<MusicInfoBean> musicList = new ArrayList<MusicInfoBean>();

    private static MusicLoader musicLoader;

    private static ContentResolver contentResolver;

    private Uri contentUri = Media.EXTERNAL_CONTENT_URI;

    public static MusicLoader instance(ContentResolver pContentResolver) {
        contentResolver = pContentResolver;
        musicLoader = new MusicLoader();
        return musicLoader;
    }

    private MusicLoader() {
        Cursor cursor = contentResolver.query(contentUri, null, null, null, null);
        if (cursor == null) {
            Log.v(TAG, "Line(37	)	Music Loader cursor == null.");
        } else if (!cursor.moveToFirst()) {
            Log.v(TAG, "Line(39	)	Music Loader cursor.moveToFirst() returns false.");
        } else {
            int displayNameCol = cursor.getColumnIndex(Media.TITLE);
            int albumCol = cursor.getColumnIndex(Media.ALBUM);
            int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int idCol = cursor.getColumnIndex(Media._ID);
            int durationCol = cursor.getColumnIndex(Media.DURATION);
            int sizeCol = cursor.getColumnIndex(Media.SIZE);
            int artistCol = cursor.getColumnIndex(Media.ARTIST);
            int urlCol = cursor.getColumnIndex(Media.DATA);
            Log.e("displayNameCol", displayNameCol + "albumCol" + albumCol + "idCol" + idCol + "durationCol" + durationCol + "sizeCol" + sizeCol);
            do {
                String title = cursor.getString(displayNameCol);
                String album = cursor.getString(albumCol);
                long albumId = cursor.getLong(album_id);
                long id = cursor.getLong(idCol);
                int duration = cursor.getInt(durationCol);
                long size = cursor.getLong(sizeCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);

                String picUri = ContentUris.withAppendedId(sArtworkUri, albumId).toString();

                MusicInfoBean musicInfo = new MusicInfoBean(id, title);
                musicInfo.setAlbumId(albumId);
                musicInfo.setAlbum(album);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                musicInfo.setArtist(artist);
                musicInfo.setUrl(url);
                musicInfo.setPicUri(picUri);
                musicList.add(musicInfo);

            } while (cursor.moveToNext());
        }
    }

/*    public void insert(SearchResultBean bean, String url) {
        if (contentResolver != null) {
            ContentValues values = new ContentValues();
            values.put(Media.TITLE, bean.getMusicName());
            values.put(Media.ALBUM, bean.getAlbum());
            values.put(Media.ARTIST, bean.getArtist());
            values.put(Media.DATA, url);
            contentResolver.insert(contentUri, values);
        }
    }*/

    public List<MusicInfoBean> getAllMusicList() {
        return musicList;
    }

    public Uri getMusicUriById(long id) {
        Uri uri = withAppendedId(contentUri, id);
        return uri;
    }
}
