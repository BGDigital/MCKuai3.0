package com.mckuai.imc;

import android.content.ClipboardManager;
import android.content.Context;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mckuai.bean.GameServerInfo;
import com.mckuai.until.GameUntil;
import com.mckuai.until.ServerEditer;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ServerDetailsActivity extends BaseActivity implements View.OnClickListener {
    private GameServerInfo serverInfo;
    private final String TAG = "ServerDetailsActivity";
    private final String mTitle = "服务器详情";

    private ImageView iv_cover;
    private TextView tv_name;
    private TextView tv_type;
    private TextView tv_owner;
    private TextView tv_state;
    private TextView tv_playCount;
    private TextView tv_ip;
    private TextView tv_port;
    private TextView tv_qqGroup;
    private TextView tv_des;
   //private ImageView btn_left;
    private ImageView btn_right;
    private TextView tv_title;
    private TextView tv_version;
    private LinearLayout ll_pics;

    private ImageLoader imageLoader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_details);
        serverInfo = (GameServerInfo) getIntent().getSerializableExtra("SERVER_INFO");
        imageLoader = ImageLoader.getInstance();
        setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void initView(){
        iv_cover = (ImageView) findViewById(R.id.iv_serverCover);
        //btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_serverName);
        tv_type = (TextView) findViewById(R.id.tv_server_type);
        tv_owner = (TextView) findViewById(R.id.tv_serverOwner);
        tv_state = (TextView) findViewById(R.id.tv_serverState);
        tv_playCount = (TextView) findViewById(R.id.tv_serverPlayerCount);
        tv_ip = (TextView) findViewById(R.id.tv_serverIp);
        tv_port = (TextView) findViewById(R.id.tv_serverPort);
        tv_qqGroup = (TextView) findViewById(R.id.tv_serverQQGroup);
        tv_des = (TextView) findViewById(R.id.tv_serverDes);
        tv_version = (TextView) findViewById(R.id.tv_serverVersion);
        ll_pics = (LinearLayout) findViewById(R.id.ll_serverPic);

        //btn_left.setImageResource(R.drawable.btn_back);
        btn_right.setImageResource(R.drawable.btn_titlebar_share);

        btn_right.setOnClickListener(this);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_copyQQGroup).setOnClickListener(this);
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
            String tag[] = serverInfo.getServerTag().split("|");
            tv_type.setText("类型："+tag[0]);
        }


        tv_owner.setText("腐竹：" + (null == serverInfo.getUserName() ? "麦友" : serverInfo.getUserName()));
        tv_state.setText(true == serverInfo.getIsOnline() ? "在线" : "离线");
        tv_playCount.setText("人数：" + serverInfo.getOnlineNum() + "/" + serverInfo.getPeopleNum());
        //tv_qqGroup.setText(serverInfo.get);
        tv_ip.setText("服务器地址：" + serverInfo.getResIp());
        tv_port.setText("服务器端口：" + serverInfo.getServerPort());
        tv_des.setText(Html.fromHtml(serverInfo.getDres() + ""));
    }

    private void showPics(){
        if (null != serverInfo.getPictures() && 1 < serverInfo.getPictures().length()){
            String[] pic = serverInfo.getPictures().split(",");
            ll_pics.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);
            for (int i = 0;i < 5;i++) {
                for (String curpic : pic) {
                    ImageView imageView = (ImageView) inflater.inflate(R.layout.item_pic, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(213),dp2px(120));
                    params.setMargins(dp2px(2),dp2px(10),dp2px(2),dp2px(10));

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

    private void showShareView(){

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
        showNotification(1,"已复制!",R.id.rl_serverBasicInfo);
    }

    private void addAndRunGame(){
        ServerEditer editer = new ServerEditer();
        editer.addServer(serverInfo);
        editer.save();
        GameUntil.startGame(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                showShareView();
                break;
            case R.id.btn_copyQQGroup:
                copyServerInfoToClip(0);
                break;
            case R.id.btn_copyServerIp:
                copyServerInfoToClip(1);
                break;
            case R.id.btn_copyServerPort:
                copyServerInfoToClip(2);
                break;
            case R.id.btn_addServer:
                addAndRunGame();
                break;
        }
    }
}
