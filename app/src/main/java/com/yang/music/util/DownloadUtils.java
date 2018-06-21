package com.yang.music.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yang.music.base.Constant;
import com.yang.music.bean.SearchResultBean;
import com.yang.music.util.data.ScreenUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.yang.music.activity.HomeActivity.mImageUrl;
import static com.yang.music.base.Constant.DIR_MUSIC_IMAGE;


public class DownloadUtils {

    private static final String DOWNLOAD_URL = "/download?_o=%2Fsearch%2Fsong";
    private static final int SUCCESS_LRC = 1;//下载歌词成功
    private static final int FAILED_LRC = 2;//下载歌词失败
    private static final int SUCCESS_MP3 = 3;//下载歌曲成功
    private static final int FAILED_MP3 = 4;//下载歌曲失败
    private static final int GET_MP3_URL = 5;//获取音乐下载地址成功
    private static final int GET_FAILED_MP3_URL = 6;//获取音乐下载地址失败
    private static final int MUSIC_EXISTS = 7;//下载时,音乐已存在
    private static final int GET_MP3_IMAGE = 8;//封面下载地址
    private static DownloadUtils sInstance;
    private OnDownloadListener mListener;
    private String urlImage;
    private ExecutorService mThreadPool;

    /**
     *设置回调监听器对象
     * @param mListener
     * @return
     */
    public DownloadUtils setListener(OnDownloadListener mListener){
        this.mListener = mListener;
        return this;
    }

    //获取下载工具的实例
    public synchronized static DownloadUtils getsInstance(){
        if (sInstance == null){
            try {
                sInstance = new DownloadUtils();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return  sInstance;
    }

    /**
     * 下载的具体业务方法
     * @throws ParserConfigurationException
     */
    private DownloadUtils() throws ParserConfigurationException{
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public void download(final SearchResultBean searchResult){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SUCCESS_LRC:
//                        if (mListener != null) mListener.onDowload(searchResult.getMusicName()+".mp3");
                        break;
                    case FAILED_LRC:
                        if (mListener != null) mListener.onFailed(searchResult.getMusicName()+"的歌词下载失败");
                        break;
                    case GET_MP3_URL:
                        System.out.println("GET_MP3_URL:"+msg.obj);
                        downloadMusic(searchResult,(String)msg.obj,this);
                        break;
                    case GET_FAILED_MP3_URL:
                        if (mListener != null) mListener.onFailed(searchResult.getMusicName()+"的MP3下载失败");
                        break;
                    case SUCCESS_MP3:
                        //if (mListener != null) mListener.onDowload(Environment.getExternalStorageDirectory()+Constant.DIR_MUSIC + "/" + searchResult.getMusicName()+".mp3");
                        if (mListener != null) mListener.onDowload(searchResult.getMusicName()+".mp3");
                        String url = Constant.MIGU_URL + searchResult.getUrl();
                        System.out.println("download lrc:"+url);
                        downloadLRC(searchResult.getMusicName(),searchResult.getArtist(),this);
                        break;
                    case FAILED_MP3:
                        if (mListener != null) mListener.onFailed(searchResult.getMusicName()+"的MP3下载失败");
                        break;
                    case MUSIC_EXISTS:
                        if (mListener != null) mListener.onFailed(searchResult.getMusicName()+"已存在");
                        break;
                    case GET_MP3_IMAGE:
                        downloadImage(searchResult,urlImage);
                        break;
                }
            }
        };
        getDownloadMusicURL(searchResult,handler);
    }

    //获取下载歌词的URL
    private void getDownloadLrcURL(final SearchResultBean searchResult, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                //http://music.baidu.com/search/lrc?key=%E6%9C%89%E5%BF%83%E4%BA%BA%20G.E.M.%E9%82%93%E7%B4%AB%E6%A3%8B
                //"http://music.baidu.com/search/lrc?key=" + 歌名 + " " + 歌手
                //System.out.println("searchResult.getUrl() = " + searchResult.getUrl());
                //String[] aa = searchResult.getUrl().split("/");
                //String sn = aa[5];
                //System.out.println("歌曲编号 = " + sn);

                //从浏览器复制出来的Url是这样的,"http://music.baidu.com/search?key=%E6%B2%A1%E6%9C%89";
                //汉字经过utf8编码,比如 冰雨 == %E5%86%B0%E9%9B%A8;
                //经过测试 获取页面 使用"http://music.baidu.com/search?key=冰雨";无法打开正确连接
                //比如使用URLEncoder.encode转码,转为utf8
                //实际使用 获取页面 使用"http://music.baidu.com/search?key=%E6%B2%A1%E6%9C%89";
                try {
                    String musicName = URLEncoder.encode(searchResult.getMusicName(), "utf8");
                    String artistName = URLEncoder.encode(searchResult.getArtist(), "utf8");
                    String url = Constant.BAIDU_LRC_SEARCH_HEAD + musicName + "+" + artistName;

                    Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6000).get();
                    Elements lrcUrls = doc.select("span.lyric-action");

                    for (int i=0;i<lrcUrls.size();i++) {
                        Elements urlsa = lrcUrls.get(i).getElementsByTag("a");
                        System.out.println("tag a urlsa : " + urlsa);
                        for (int a=0;i<urlsa.size();a++) {
                            System.out.println("----" + urlsa.get(a).toString());
                            String urla = urlsa.get(a).toString();
                            System.out.println("-----" + urla);
                            //-----<a class="down-lrc-btn { 'href':'/data2/lrc/14488216/14488216.lrc' }" href="#">下载LRC歌词</a>
                            if (urla.indexOf("'href':'")>0){
                                String[] uu = urla.split("'href':'");
                                System.out.println("uu1 : " + uu[1]);
                                //uu1 : /data2/lrc/14488216/14488216.lrc' }" href="#">下载LRC歌词</a>
                                String[] uuu = uu[1].split(".lrc");
                                System.out.println("uuu0 : " + uuu[0]);
                                //uuu0 : /data2/lrc/14488216/14488216
                                String result = "http://music.baidu.com" + uuu[0] + ".lrc";
                                System.out.println("result : " + result);
                                //result :  http://music.baidu.com/data2/lrc/14488216/14488216.lrc
                                Message msg = handler.obtainMessage(SUCCESS_LRC,result);
                                msg.sendToTarget();
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.obtainMessage(FAILED_LRC).sendToTarget();
                }

            }
        });
    }


    //获取下载音乐的URL
    private void getDownloadMusicURL(final SearchResultBean searchResult, final Handler handler) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String[] aa = searchResult.getUrl().split("/");
                String sn = aa[5];
                System.out.println("歌曲编号 = " + sn);
                String url = Constant.MIGU_DOWN_HEAD + sn + Constant.MIGU_DOWN_FOOT;
                System.out.println("歌曲下载页面url = " + url);

                try {
                    Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6000).get();

                    Log.e("Document",doc.toString());
                    String[]images = doc.toString().split("src");
                    String image = images[2];
                    String[] arrhttps = image.split("http");
                    String[] arrJpg = arrhttps[1].split("jpg");
                    urlImage = "http" + arrJpg[0] + "jpg";//封面图片的地址
                    handler.sendEmptyMessage(GET_MP3_IMAGE);
                    String[] bb = doc.toString().split("song");//把 下载页面源码 按照"song"分割
                    for (int i=0;i<bb.length;i++){
                        if (bb[i].indexOf("mp3?msisdn")>0){
                            System.out.println("mp3?msisdn = " + bb[i]);
                            String initMp3Url = bb[i];//initMp3Url 初始Mp3下载链接,如下

                            String[] arrayHttp = initMp3Url.split("http");//把 初始Mp3下载链接 按照"http"分割
                            String[] arrayMp3 = arrayHttp[1].split(".mp3");//把 arrayHttp 按照".mp3"分割
                            String result = "http" + arrayMp3[0] + ".mp3";//把分割去掉的"http"和".mp3",组合回来
                            System.out.println("DownloadUtils.getDownloadMusicURL.result = " + result);

                            Message msg = handler.obtainMessage(GET_MP3_URL,result);

                            msg.sendToTarget();
                            break;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    handler.obtainMessage(GET_FAILED_MP3_URL).sendToTarget();
                }
            }
        });
    }


    //下载歌词
    public void downloadLRC(final String musicName, final String artistName, final Handler handler){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                //从浏览器复制出来的Url是这样的,"http://music.baidu.com/search?key=%E6%B2%A1%E6%9C%89";
                //汉字经过utf8编码,比如 冰雨 == %E5%86%B0%E9%9B%A8;
                //经过测试 获取页面 使用"http://music.baidu.com/search?key=冰雨";无法打开正确连接
                //比如使用URLEncoder.encode转码,转为utf8
                //实际使用 获取页面 使用"http://music.baidu.com/search?key=%E6%B2%A1%E6%9C%89";
                try {
                    String musicNameEn = URLEncoder.encode(musicName, "utf8");
                    String artistNameEn = URLEncoder.encode(artistName, "utf8");
                    //String url = Constant.BAIDU_LRC_SEARCH_HEAD + searchResult.getMusicName() + " " + searchResult.getArtist();
                    String url = Constant.BAIDU_LRC_SEARCH_HEAD + musicNameEn + "+" + artistNameEn;
                    System.out.println("歌词下载页面url = " + url);

                    Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6000).get();
                    //System.out.println("歌词下载页面 doc : " + doc);

                    Elements lrcUrls = doc.select("span.lyric-action");
                    System.out.println(lrcUrls);

                    for (int i = 0; i < lrcUrls.size(); i++) {
                        Elements urlsa = lrcUrls.get(i).getElementsByTag("a");
                        System.out.println("tag a urlsa : " + urlsa);
                        for (int a = 0; a < urlsa.size(); a++) {
                            //System.out.println("----" + urlsa.get(a).toString());
                            String urla = urlsa.get(a).toString();
                            System.out.println("-----" + urla);
                            //-----<a class="down-lrc-btn { 'href':'/data2/lrc/14488216/14488216.lrc' }" href="#">下载LRC歌词</a>
                            if (urla.indexOf("'href':'") > 0) {
                                String[] uu = urla.split("'href':'");
                                System.out.println("uu1 : " + uu[1]);
                                //uu1 : /data2/lrc/14488216/14488216.lrc' }" href="#">下载LRC歌词</a>
                                String[] uuu = uu[1].split(".lrc'");
                                System.out.println("uuu0 : " + uuu[0]);
                                //uuu0 : /data2/lrc/246970367/246970367.lrc
                                String lrcDwonUrl = "http://music.baidu.com" + uuu[0] + ".lrc";
                                System.out.println("lrcDwonUrl : " + lrcDwonUrl);
                                //result :  http://music.baidu.com/data2/lrc/14488216/14488216.lrc
                                //File LrcDirFile = new File(Environment.getExternalStorageDirectory() + "/drm_music");
                                File LrcDirFile = new File(Environment.getExternalStorageDirectory() + Constant.DIR_LRC);
                                System.out.println("LrcDirFile : " + LrcDirFile);
                                if (!LrcDirFile.exists()) {
                                    LrcDirFile.mkdirs();
                                }
                                String target = LrcDirFile + "/" + musicName + ".lrc";
                                System.out.println("lrcDwonUrl : " + lrcDwonUrl);
                                System.out.println("target : " + target);
                                File fileTarget = new File(target);
                                if (fileTarget.exists()) {
                                    handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                                    return;
                                } else {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url(lrcDwonUrl).build();
                                    Response response = client.newCall(request).execute();
                                    if (response.isSuccessful()) {
                                        PrintStream ps = new PrintStream(new File(target));
                                        byte[] bytes = response.body().bytes();
                                        ps.write(bytes, 0, bytes.length);
                                        ps.close();
                                        Log.e("target111",target);
                                        LrcLoader.addLrcList(target);
                                        handler.obtainMessage(SUCCESS_LRC, target).sendToTarget();
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    private void downloadImage(final SearchResultBean bean,final String imageUrl){

        //使用OkHttpClient组件
        Log.e("imageUrl",imageUrl);
        try{
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().get().url(imageUrl).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    byte[] image = response.body().bytes();
                    Log.e("response",response.message() + "" + image.length);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    saveImage(bitmap,DIR_MUSIC_IMAGE,bean.getMusicName() + ".jpg");
                }
            });
        }catch (Exception e){

        }

    }

    //下载MP3
    private void downloadMusic(final SearchResultBean searchResult, final String url, final Handler handler){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File musicDirFile = new File(Environment.getExternalStorageDirectory()+Constant.DIR_MUSIC);
                if (!musicDirFile.exists()){
                    musicDirFile.mkdirs();
                }

                String mp3url = url;
                String target = musicDirFile + "/" + searchResult.getMusicName() + ".mp3";
                File fileTarget = new File(target);
                if (fileTarget.exists()){
                    handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                    return;
                }else {
                    //使用OkHttpClient组件
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(mp3url).build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()){
                            PrintStream ps = new PrintStream(fileTarget);
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes,0,bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_MP3).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAILED_MP3).sendToTarget();
                    }
                }
            }
        });
    }

    public static String getMusicRootPath(){
        return Environment.getExternalStorageDirectory()+Constant.DIR_MUSIC + "/";
    }

    //自定义下载事件监听器
    public interface OnDownloadListener {
        public void onDowload(String mp3Url);
        public void onFailed(String error);
    }

    private void saveImage(Bitmap bm,String path,String fileName){

        bm = compress(bm);
        int width =  ScreenUtils.getScreenWidth()/2;
        bm = zoomImage(bm,width,width);
        String subForder = getMusicRootPath() + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            try {
                myCaptureFile.createNewFile();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                mImageUrl.add(myCaptureFile.getAbsolutePath());
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap compress(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            if (options <= 0) {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        // 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,(int) height, matrix, true);
        return bitmap;
    }
}
