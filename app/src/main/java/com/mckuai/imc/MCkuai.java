package com.mckuai.imc;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.mckuai.bean.MCUser;
import com.mckuai.until.JsonCache;

import java.io.File;

/**
 * Created by kyly on 2015/6/23.
 */
public class MCkuai  extends Application{


    private static  MCkuai  instance;
    private String mCacheDir;

    public MCUser mUser;
    public AsyncHttpClient mClient;
    public JsonCache mCache;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mCache = new JsonCache(this);
        mClient = new AsyncHttpClient();
    }

    @Override
    public void onTerminate() {
        Log.e("MCkuai","onTerminate");
        super.onTerminate();
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
        return  getFilesDir() + File.separator + "maps";
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
