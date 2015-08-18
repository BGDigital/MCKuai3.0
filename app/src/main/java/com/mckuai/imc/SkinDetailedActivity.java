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
import org.json.JSONArray;
import org.json.JSONObject;

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
        Intent intent = getIntent();
        item = (SkinItem) intent.getSerializableExtra("SKIN_ITEM");
        if (null != item) {
            showData();
        }
    }

    @Override
    protected void onPause() {
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
                        MobclickAgent.onEvent(SkinDetailedActivity.this, "downloadSkin");
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
                        MobclickAgent.onEvent(SkinDetailedActivity.this, "startGame_skin");
                        GameUntil.startGame(SkinDetailedActivity.this);
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
            mShareService.openShare(this,false);
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
}
