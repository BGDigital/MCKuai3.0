package com.mckuai.imc;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.mckuai.bean.MCUser;
import com.mckuai.until.JsonCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

/**
 * Created by kyly on 2015/6/23.
 */
public class MCkuai  extends Application{


    private static  MCkuai  instance;
    private String mCacheDir;
    private Spinner spinner;

    public MCUser mUser;
    public AsyncHttpClient mClient;
    public JsonCache mCache;
    public int fragmentIndex = 0;

    private static final int MEM_CACHE_SIZE = 8 * 1024 * 1024;// 内存缓存大小
    private static final int CONNECT_TIME = 15 * 1000;// 连接时间
    private static final int TIME_OUT = 30 * 1000;// 超时时间
    private static final int IMAGE_POOL_SIZE = 3;// 线程池数量


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mCache = new JsonCache(this);
        mClient = new AsyncHttpClient();
        initImageLoader();
    }

    @Override
    public void onTerminate() {
        Log.e("MCkuai","onTerminate");
        super.onTerminate();
    }

    public AsyncHttpClient getHttpClient(){
        if (null == mClient){
            mClient = new AsyncHttpClient();
        }
        return mClient;
    }

    private void initImageLoader(){

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(750, 480)
                .threadPoolSize(IMAGE_POOL_SIZE)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                        // 对于同一url只缓存一个图
                .memoryCache(new UsingFreqLimitedMemoryCache(MEM_CACHE_SIZE)).memoryCacheSize(MEM_CACHE_SIZE)
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.FIFO)
                .discCache(new UnlimitedDiskCache(new File(getImageCacheDir())))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), CONNECT_TIME, TIME_OUT))
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(configuration);
    }

    public static MCkuai getInstance() {
        return instance;
    }



    public String getJsonFile(){
        return  getCacheRoot() + File.separator + getString(R.string.jsoncache_dir) + File.separator + getString(R.string.jsoncache_file);
    }

    public String getImageCacheDir() {
        return getCacheRoot() + File.separator + getString(R.string.imagecache_dir);
    }

    public String getMapDownloadDir(){
        String path = getSDPath();
        if (null != path){
            return path + "/Download/MCPEMaps/";
        }
    else {
        return getFilesDir() + File.separator + "MCPEMaps" + File.separator;
    }
}

    public String getGameProfileDir(){
        String dir = getSDPath();
        if (null != dir){
            dir += "/games/com.mojang/";
        }
        return  dir;
    }

    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    public boolean isFirstBoot()
    {
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.preferences_file), 0);
        return preferences.getBoolean("FirstBoot", true);
    }

    public void setFirstBoot()
    {
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.preferences_file), 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FirstBoot", false);
        editor.commit();
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    private String getCacheRoot()
    {
        if (null == mCacheDir)
        {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable())
            {
                if (null != getExternalCacheDir())
                {
                    mCacheDir = getExternalCacheDir().getPath() + File.separator + getString(R.string.cache_root);
                } else
                {
                    mCacheDir = getCacheDir().getPath() + File.separator
                            + getString(R.string.cache_root);
                }
            } else
            {
                mCacheDir = getCacheDir().getPath() + File.separator
                        + getString(R.string.cache_root);
            }
        }
        return mCacheDir;


    }
}
