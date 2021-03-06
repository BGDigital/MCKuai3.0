package com.mckuai.imc;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mckuai.bean.GameServerInfo;
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.ServerEditer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.media.UMImage;

import java.io.File;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;


public class ServerDetailsActivity extends BaseActivity implements View.OnClickListener {
    private GameServerInfo serverInfo;
    private final String TAG = "ServerDetailsActivity";
    private final String mTitle = "服务器详情";

    private ImageView iv_cover;
    private TextView tv_name;
    private TextView tv_type;
    private TextView tv_owner;
    private TextView tv_ip;
    private TextView tv_port;
    private TextView tv_qqGroup;
    private TextView tv_des;
    private ImageView btn_right;
    private TextView tv_title;
    private TextView tv_version;
    private LinearLayout ll_pics;
    private ImageView iv_serverPic;//只有一张图时显示
    private Button btn_qqGroup;

    private ImageLoader imageLoader;
    private com.umeng.socialize.controller.UMSocialService mShareService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_details);
        serverInfo = (GameServerInfo) getIntent().getSerializableExtra("SERVER_INFO");
        imageLoader = ImageLoader.getInstance();
        setTitle(mTitle);
        mShareService = UMServiceFactory.getUMSocialService("com.umeng.share");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("联机详情");
        if (null == iv_cover){
            initView();
        }
        if (null != serverInfo){
            showData();
        }
        else {
            showNotification(3,"未获取到服务器信息,请返回!",R.id.rl_serverBasicInfo);
        }
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd("联机详情");
        super.onPause();
    }

    private void initView(){
        iv_cover = (ImageView) findViewById(R.id.iv_serverCover);
        //btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_serverName);
        tv_type = (TextView) findViewById(R.id.tv_server_type);
//        tv_state = (TextView) findViewById(R.id.tv_serverState);
        tv_owner = (TextView) findViewById(R.id.tv_serverOwner);
//        tv_playCount = (TextView) findViewById(R.id.tv_serverPlayerCount);
        tv_ip = (TextView) findViewById(R.id.tv_serverIp);
        tv_port = (TextView) findViewById(R.id.tv_serverPort);
        tv_qqGroup = (TextView) findViewById(R.id.tv_serverQQGroup);
        tv_des = (TextView) findViewById(R.id.tv_serverDes);
        tv_version = (TextView) findViewById(R.id.tv_serverVersion);
        ll_pics = (LinearLayout) findViewById(R.id.ll_serverPic);
        iv_serverPic = (ImageView) findViewById(R.id.iv_pic);
        btn_qqGroup = (Button)findViewById(R.id.btn_copyQQGroup);

        btn_right.setImageResource(R.drawable.btn_titlebar_share);
        btn_right.setVisibility(View.VISIBLE);

        btn_right.setOnClickListener(this);
        btn_qqGroup.setOnClickListener(this);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_copyServerIp).setOnClickListener(this);
        findViewById(R.id.btn_copyServerPort).setOnClickListener(this);
        findViewById(R.id.btn_addServer).setOnClickListener(this);
    }

    private void showData(){
        if (null != serverInfo.getIcon() && 10 < serverInfo.getIcon().length()){
            imageLoader.displayImage(serverInfo.getIcon()+"",iv_cover);
        }

        showPics();

        tv_title.setText(mTitle);
        tv_version.setText(serverInfo.getResVersion()+"");
        tv_name.setText(serverInfo.getViewName() + "");

        if (null != serverInfo.getServerTag() && 1 < serverInfo.getServerTag().length()){
            String tag[] = serverInfo.getServerTag().split("\\|");
            tv_type.setText("类型："+tag[0]);
        }

        if (null != serverInfo.getExchangeQQ()){
            tv_qqGroup.setText("服务器QQ群："+serverInfo.getExchangeQQ());
            tv_qqGroup.setVisibility(View.VISIBLE);
            btn_qqGroup.setVisibility(View.VISIBLE);
        }
        else {
            tv_qqGroup.setVisibility(View.GONE);
            btn_qqGroup.setVisibility(View.GONE);
        }
        tv_owner.setText("腐竹：" + (null == serverInfo.getUserName() ? "麦友" : serverInfo.getUserName()));
        tv_ip.setText("服务器地址：" + serverInfo.getResIp());
        tv_port.setText("服务器端口：" + serverInfo.getServerPort());
        tv_des.setText(Html.fromHtml(serverInfo.getDres() + ""));
    }

    private void showPics(){
        if (null != serverInfo.getPictures() && 1 < serverInfo.getPictures().length()){
            String[] pic = serverInfo.getPictures().split(",");
            if (pic.length == 1){
                imageLoader.displayImage(pic[0], iv_serverPic, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                        float scale= loadedImage.getWidth()*1.0f / screenWidth;
                        int height = (int)(loadedImage.getHeight() / scale);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth,height);
                        params.setMargins(0,20,0,20);
                        iv_serverPic.setLayoutParams(params);
                        iv_serverPic.setScaleType(ImageView.ScaleType.FIT_XY);
                        iv_serverPic.setImageBitmap(loadedImage);

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
            else {
                ll_pics.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(this);
                for (String curpic : pic) {
                    ImageView imageView = (ImageView) inflater.inflate(R.layout.item_pic, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(213), dp2px(120));
                    params.setMargins(dp2px(2), dp2px(10), dp2px(2), dp2px(10));

                    imageView.setLayoutParams(params);
                    imageLoader.displayImage(curpic, imageView);
                    imageView.setTag(curpic);
                    ll_pics.addView(imageView);
                }
            }
        }
    }



    private int dp2px(int dp){
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }


    private void copyServerInfoToClip(int type){
        ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        switch (type){
            case 0:
                clip.setText(serverInfo.getQQ());   //QQ group
                break;
            default:
                clip.setText("服务器名称："+serverInfo.getViewName()+"\n服务器IP:"+serverInfo.getResIp()+"\n服务器端口:"+serverInfo.getServerPort()+"\n更多精彩尽在《麦块我的世界盒子》马上登录www.mckuai.com感受吧！");
                break;
        }
        showNotification(1, "已复制!", R.id.rl_serverBasicInfo);
    }

    private void addAndRunGame(){
        ServerEditer editer = new ServerEditer();
        editer.addServer(serverInfo);
        editer.save();
        if (!GameUntil.startGame(this,serverInfo.getResVersionEx())){
            downloadGame(serverInfo.getResVersionEx());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                shareService();
                break;
            case R.id.btn_copyQQGroup:
                MobclickAgent.onEvent(this,"copyServerGroup");
                copyServerInfoToClip(0);
                break;
            case R.id.btn_copyServerIp:
                MobclickAgent.onEvent(this,"copyServerInfo");
                copyServerInfoToClip(1);
                break;
            case R.id.btn_copyServerPort:
                MobclickAgent.onEvent(this,"copyServerInfo");
                copyServerInfoToClip(2);
                break;
            case R.id.btn_addServer:
                MobclickAgent.onEvent(this,"startGame_serverDetail");
                addAndRunGame();
                break;
        }
    }

    protected void shareService()
    {
        if (null == serverInfo)
        {
            return;
        }
        mShareService.setShareContent(serverInfo.getViewName());
        if (null != serverInfo.getIcon() || 10 < serverInfo.getIcon().length())
        {
            mShareService.setShareMedia(new UMImage(this,serverInfo.getIcon()));
        }
        mShareService.openShare(this, false);
    }

    private void downloadGame(final int version){
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
                DLManager.getInstance(ServerDetailsActivity.this).dlStart(downloadUrl, MCkuai.getInstance().getGameDownloadDir(), new DLTaskListener() {
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
                GameUntil.installGame(ServerDetailsActivity.this, file.getPath());
            }
        });

    }
}
