package com.mckuai.imc;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.mckuai.bean.ForumInfo;
import com.mckuai.bean.MCUser;
import com.mckuai.until.JsonCache;
import com.mckuai.until.MCDTListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.aigestudio.downloader.interfaces.DLTaskListener;

/**
 * Created by kyly on 2015/6/23.
 */
public class MCkuai  extends Application{


    private static  MCkuai  instance;
    private String mCacheDir;
    private Spinner spinner;
    private ImageView btn_publish;

    public MCUser mUser;
    public JsonCache mCache;
    public int fragmentIndex = 0;
    public ArrayList<ForumInfo> forumList;

    public AsyncHttpClient mClient;
    public Tencent tencent;

    private static final int MEM_CACHE_SIZE = 8 * 1024 * 1024;// 内存缓存大小
    private static final int CONNECT_TIME = 15 * 1000;// 连接时间
    private static final int TIME_OUT = 30 * 1000;// 超时时间
    private static final int IMAGE_POOL_SIZE = 3;// 线程池数量

    private HashMap<String,MCDTListener> downloadTask;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mCache = new JsonCache(this);
        mClient = new AsyncHttpClient();
        initImageLoader();
        initTencent();
//        initUM();
        getProfile();//获取保存的用户信息
    }

    @Override
    public void onTerminate() {
        Log.e("MCkuai", "onTerminate");
        super.onTerminate();
    }

    public AsyncHttpClient getHttpClient(){
        if (null == mClient){
            mClient = new AsyncHttpClient();
        }
        return mClient;
    }

    public ArrayList<ForumInfo> getForumList() {
        return forumList;
    }

    public void setForumList(ArrayList<ForumInfo> forumList) {
        this.forumList = forumList;
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

    private void initUM()
    {
        MobclickAgent.openActivityDurationTrack(false);// 禁止默认的页面统计方式，不会再自动统计Activity
    }

    private void initTencent(){
        tencent = Tencent.createInstance("101155101", getApplicationContext());
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

    public boolean isLogin(){
        if (mUser != null && 0 < mUser.getId()){
            return  true;
        }
        else {
            return  false;
        }
    }

    public boolean LogOut(){
        this.mUser = null;
        return true;
    }

    public void  setUser(MCUser user){
        this.mUser = user;
    }

    public MCUser getUser(){
        return  mUser;
    }

    public void getProfile()
    {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), 0);
        long mQQToken_Birthday = preferences.getLong(getString(R.string.tokenTime), 0);
        long mQQToken_Expires = preferences.getLong(getString(R.string.tokenExpires), 0);
        // 检查qq的token是否有效如果在有效期内则获取qqtoken
        if (verificationTokenLife(mQQToken_Birthday, mQQToken_Expires))
        {
            mUser = new MCUser();
            mUser.setLevel(preferences.getInt("MC_LEVEL", 0));
            mUser.setProcess(preferences.getFloat("MC_PROCESS", 0f));
            mUser.setAddr(preferences.getString("MC_ADDR", null));
            mUser.setId(preferences.getInt(getString(R.string.mc_id), 0));
//            mUser.setName(preferences.getString(getString(R.string.qq_OpenId), null));// qq_OpenId用于融云的用户名
            mUser.setHeadImg(preferences.getString(getString(R.string.mc_userFace), null));// 用户头像
            mUser.setNike(preferences.getString(getString(R.string.mc_nick), null));// 显示名
            mUser.setGender(preferences.getString(getString(R.string.mc_gender), null));// 性别
//            mUser.setToken(preferences.getString(getString(R.string.rcToken), null));// 融云令牌
            mUser.setProcess(preferences.getFloat("USER_PROCESS",0f));
        }
    }

    private boolean verificationTokenLife(Long birthday, long expires)
    {
        return (System.currentTimeMillis() - birthday) < expires * 1000;
    }

    public ImageView getBtn_publish() {
        return btn_publish;
    }

    public void setBtn_publish(ImageView btn_publish) {
        this.btn_publish = btn_publish;
    }

    public HashMap<String,MCDTListener> getDownloadTask(){
        if (null == downloadTask){
            downloadTask = new HashMap<>();
        }
        return  downloadTask;
    }

    public void addDownloadTask(String resId,MCDTListener listener){
        if (null == downloadTask){
            downloadTask = new HashMap<>();
        }
        downloadTask.put(resId,listener);
    }

    public MCDTListener getDownloadTask(String resId){
        if (null != downloadTask){
            return  downloadTask.get(resId);
        }
    }

    public void deleteDownloadTask(String resId){
        if (null != downloadTask){
            DLTaskListener listener = downloadTask.get(resId);
            if (null != listener){
                downloadTask.remove(listener);
            }
        }
    }
}
