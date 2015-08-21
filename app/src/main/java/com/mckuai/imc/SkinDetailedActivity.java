package com.mckuai.imc;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mckuai.bean.SkinItem;
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.MCSkinManager;
import com.mckuai.mctools.WorldUtil.OptionUntil;
import com.mckuai.service_and_recevier.DownloadProgressRecevier;
import com.mckuai.widget.ProgressButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.media.UMImage;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

public class SkinDetailedActivity extends BaseActivity implements View.OnClickListener {
    private SkinItem item;

    private ProgressButton btn_operation;
    private TextView tv_skinNmae;
    private TextView tv_skinOwner;
    private TextView tv_skinRank;
    private TextView tv_desc;
    private ImageView iv_skinCover;
    private LinearLayout ll_pics;
    private ImageView btn_return;
    private ImageView btn_share;

    private ImageLoader mLoader;
    private DisplayImageOptions mOptions;

    private DownloadProgressRecevier recevier;
    private com.umeng.socialize.controller.UMSocialService mShareService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_detailed);
        mShareService = UMServiceFactory.getUMSocialService("com.umeng.share");
        setTitle("皮肤详情");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("皮肤详情");
        Intent intent = getIntent();
        item = (SkinItem) intent.getSerializableExtra("SKIN_ITEM");
        if (null != item) {
            showData();
        }
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd("皮肤详情");
        super.onPause();
    }

    private void showData() {
        if (null == mLoader) {
            mLoader = ImageLoader.getInstance();
            mOptions = MCkuai.getInstance().getNormalOption();
            initView();
        }
        initReciver();
        showPics();
        mLoader.displayImage(item.getIcon(), iv_skinCover);
        tv_skinNmae.setText(item.getViewName() + "");
        tv_skinOwner.setText(item.getUploadMan() + "");
        tv_skinRank.setText("下载：0");
        tv_desc.setText(item.getDesc() + "");
        updateProgress();

    }

    private void showPics() {
        if (null != item.getPics() && 10 < item.getPics().length()) {
            String[] pics = item.getPics().split(",");
            if (1 == pics.length) {
                //只有一张图
                ImageView imageView = new ImageView(this);
                mLoader.displayImage(item.getPics(), imageView, mOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
                ll_pics.removeAllViews();
                ll_pics.addView(imageView);
            } else {
                //有多张图

            }
        }
    }

    private void initView() {
        btn_operation = (ProgressButton) findViewById(R.id.btn_operation);
        tv_skinNmae = (TextView) findViewById(R.id.tv_skinname);
        tv_skinRank = (TextView) findViewById(R.id.tv_skinrank);
        tv_skinOwner = (TextView) findViewById(R.id.tv_skinowner);
        tv_desc = (TextView) findViewById(R.id.tv_skindes);
        iv_skinCover = (ImageView) findViewById(R.id.iv_skincover);
        ll_pics = (LinearLayout) findViewById(R.id.ll_skinpics);
        btn_return = (ImageView) findViewById(R.id.btn_left);
        btn_share = (ImageView) findViewById(R.id.btn_right);
        btn_share.setImageResource(R.drawable.btn_titlebar_share);
        btn_share.setVisibility(View.VISIBLE);

        btn_return.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_operation.setOnClickListener(this);
    }

    private void initReciver() {
        if (null == recevier) {
            recevier = new DownloadProgressRecevier() {
                @Override
                public void onProgress(int resType,String resId,final int progress) {
                    if (2 != resType){
                        return;
                    }
                    if (resId.equals((item.getId()+""))) {
                        item.setProgress(progress);
                        updateProgress();
                        if (100 == progress) {
                            updateDownloadCount();
                            unregisterReceiver(recevier);
                            recevier = null;
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter("com.mckuai.imc.downloadprogress");
            registerReceiver(recevier, filter);
        }
    }

    private void updateProgress(){
        switch (item.getProgress()){
            case -1:
                btn_operation.setText("下载皮肤");
                break;
            case 100:
                btn_operation.updateProgress(item.getProgress());
                btn_operation.setText("启动游戏");
                break;
            default:
                btn_operation.updateProgress(item.getProgress());
                btn_operation.setText("正在下载");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_left:
                this.finish();
                break;
            case R.id.btn_right:
                shareSkin();
                break;
            case R.id.btn_operation:
                switch (item.getProgress()) {
                    case -1:
                        MobclickAgent.onEvent(SkinDetailedActivity.this, "downloadSkin_skinDetail");
                        Intent intent = new Intent();
                        intent.setAction("com.mckuai.downloadservice");
                        intent.setPackage(getPackageName());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("SKIN", item);
                        intent.putExtras(bundle);
                        startService(intent);
                        break;
                    case 100:
                        OptionUntil.setSkin(2);//配置成自定义皮肤
                        MCSkinManager manager = MCkuai.getInstance().getSkinManager();
                        manager.moveToGame(item);
                        MobclickAgent.onEvent(SkinDetailedActivity.this, "startGame_skinDetail");
                        if(!GameUntil.startGame(SkinDetailedActivity.this,11)){
                            downloadGame(11);
                        }
                        break;
                    default:

                        break;
                }
                break;
        }
    }

    private void shareSkin(){
        if (null != item){
            mShareService.setShareContent(item.getViewName());
            if (null != item.getIcon() && 10 < item.getIcon().length()){
                mShareService.setShareMedia(new UMImage(this,item.getIcon()));
            }
            mShareService.openShare(this, false);
        }
    }

    private void updateDownloadCount(){
        String url = getString(R.string.interface_domainName)+getString(R.string.interface_skinupdatecount)+"&id="+item.getId();
        AsyncHttpClient client = MCkuai.getInstance().mClient;
        client.post(url,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("","更新下载计数失败，原因："+throwable.getLocalizedMessage());
            }
        });

    }

    private void downloadGame(final int version){
        MobclickAgent.onEvent(SkinDetailedActivity.this,"showDownloadGame");
        String url = "";
        String msgText = null;
        switch (version){
            case 10:
                url = "http://softdown.mckuai.com:8081/mcpe0.10.5.apk";
                msgText = "此服务器需要安装0.10版我的世界。\n是否下载安装？";
                break;
            case 11:
                url = "http://softdown.mckuai.com:8081/mcpe0.11.1.apk";
                msgText = "此服务器需要安装0.11版我的世界。\n是否下载安装？";
                break;
        }
        final String downloadUrl = url;
        showAlert("提示", msgText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(SkinDetailedActivity.this,"downloadGame");
                DLManager.getInstance(SkinDetailedActivity.this).dlStart(downloadUrl, MCkuai.getInstance().getGameDownloadDir(), new DLTaskListener() {
                    @Override
                    public void onStart(String fileName, String url) {
                        super.onStart(fileName, url);
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        installGame(file);
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);

                        showError(version, error);
                    }
                });
            }
        });
    }

    private void showError(final int version,String msg){
        showAlert("下载失败", "下载游戏失败，原因：" + msg + "\n是否重新下载？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadGame(version);
            }
        });
    }

    private void installGame(final File file){
        showAlert("安装游戏", "游戏下载完成，是否立即安装？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameUntil.installGame(SkinDetailedActivity.this, file.getPath());
            }
        });
    }
}
