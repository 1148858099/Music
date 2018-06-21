package com.yang.music.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.util.Log;

import com.yang.music.BaseApp;
import com.yang.music.R;
import com.yang.music.util.data.ScreenUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 专辑封面图片加载器

 */
public class CoverLoader {

    private static Map<Integer,Bitmap> localImages = new HashMap<>();
    private static Map<Integer,Bitmap>roundImages = new HashMap<>();
    private static Map<Integer,Bitmap>blurImages = new HashMap();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

    public static Bitmap loadRound(int position,String uri) {

        Bitmap bitmap = null;
        if(roundImages.size() > 0){
            for (Map.Entry entry:roundImages.entrySet()){
                if((int)entry.getKey() == position){
                    bitmap = roundImages.get(position);
                    if(bitmap != null){
                        return bitmap;
                    }
                }
            }
        }

        bitmap = getLocalCover(position,uri);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(BaseApp.mApp.getResources(), R.drawable.play_page_default_cover);
        }

        int width = ScreenUtils.getScreenWidth() / 2;
        bitmap = resizeImage(bitmap, width, width);
        bitmap = createCircleImage(bitmap);
        roundImages.put(position,bitmap);
        return bitmap;
    }

    public static Bitmap loadBlur(int position,String uri) {
        Bitmap bitmap = null;
        if(blurImages.size() > 0){
            for (Map.Entry entry:blurImages.entrySet()){
                if((int)entry.getKey() == position){
                    bitmap = blurImages.get(position);
                    if(bitmap != null){
                        return bitmap;
                    }
                }
            }
        }

        bitmap = getLocalCover(position,uri);
        if (bitmap == null) {
            Log.e("loadBlur","loadBlur");
            bitmap = BitmapFactory.decodeResource(BaseApp.mApp.getResources(), R.drawable.play_page_default_bg);
        }
        bitmap = ImageUtils.blur(bitmap, ImageUtils.BLUR_RADIUS);
        blurImages.put(position,bitmap);
        return bitmap;
    }

    public static Bitmap loadNormal(int position,String uri) {
        Bitmap bitmap = getLocalCover(position,uri);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(BaseApp.mApp.getResources(), R.drawable.default_cover);
        }
        return bitmap;
    }

    private static Bitmap getLocalCover(int position,String uri) {
        if (uri == null) {
            return null;
        }
        Bitmap bitmap = null;
        if(localImages.size() > 0){
            for (Map.Entry entry:localImages.entrySet()){
                if((int)entry.getKey() == position){
                    bitmap = localImages.get(position);
                    if(bitmap != null){
                        return bitmap;
                    }
                }
            }
        }

        ContentResolver res = BaseApp.mApp.getContentResolver();
        try {
            InputStream in = res.openInputStream(Uri.parse(uri));
            bitmap = BitmapFactory.decodeStream(in, null, sBitmapOptions);
            localImages.put(position,bitmap);
            in.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        if(bitmap == null){
            bitmap = BitmapFactory.decodeFile(uri);
            localImages.put(position,bitmap);
        }

        return bitmap;
    }


    public static Bitmap resizeImage(Bitmap source, int w, int h) {
        int width = source.getWidth();
        int height = source.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
    }

    public static Bitmap createCircleImage(Bitmap source) {
        int length = source.getWidth() < source.getHeight() ? source.getWidth() : source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(length / 2, length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

}
