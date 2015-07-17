package com.mckuai.imc;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.until.MCDTListener;
import com.mckuai.until.MCMapManager;
import com.mckuai.widget.CustomShareBoard;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.aigestudio.downloader.bizs.DLManager;

/**
 * Created by Zzz on 2015/6/25.
 */
public class Map_detailsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title, tv_name, tv_nm, tv_category, tx_times, tv_tx;
    private ImageView btn_left, imag, btn_right;
    private com.mckuai.widget.ProgressButton dl;
    private Context mContext;
    private ScrollView sv_v;
    private HorizontalScrollView sv_h;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private ImageLoader mLoader;
    private MCDTListener taskListener;
    private Map map;
    private LinearLayout sv_lh;
    private DLManager manager;
    private MCMapManager mapManager;
    private int downloadstate;//0未下载，1正在下载，2已下载
    private DisplayImageOptions options;
    private com.umeng.socialize.controller.UMSocialService mShareService;
    private CustomShareBoard shareBoard;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_details);
        map = (Map) getIntent().getSerializableExtra(getString(R.string.Details));
        mapManager = MCkuai.getInstance().getMapManager();
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.background_user_cover_default)
                .showImageForEmptyUri(R.drawable.background_user_cover_default)
                .showImageOnFail(R.drawable.background_user_cover_default)
                .build();
        handler.sendMessageDelayed(handler.obtainMessage(5), 1000);
        mShareService = UMServiceFactory.getUMSocialService("com.umeng.share");
//        shareBoard = new CustomShareBoard(this, mShareService, map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkState();
        mLoader = ImageLoader.getInstance();

        dl = (com.mckuai.widget.ProgressButton) findViewById(R.id.dl);
        taskListener = new MCDTListener() {
            @Override
            public void onProgress(int progress) {
                super.onProgress(progress);
                dl.updateProgress(progress);
            }
        };

        if (null == imag) {
            initview();
        }
        if (null != map) {
            showData();
            loadData();
        } else {
            showNotification(3, "未获取到地图信息,请返回!", R.id.details);
        }
    }

    public void checkState() {
        ArrayList<Map> curDownloadedMaps = mapManager.getDownloadMaps();
        if (null != curDownloadedMaps) {
            for (Map curMap : curDownloadedMaps) {
                if (curMap.getResId().equalsIgnoreCase(map.getResId())) {
                    downloadstate = 2;
                    return;
                }
            }
        }
        taskListener = MCkuai.getInstance().getDownloadTask(map.getResId());
        if (null == taskListener) {
            downloadstate = 0;
        } else {
            downloadstate = 1;
        }
    }

    public void initview() {
//        iv_cover = (ImageView) findViewById(R.id.iv_serverCover);
        imag = (ImageView) findViewById(R.id.image);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("地图详情");
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_nm = (TextView) findViewById(R.id.tv_nm);
        tv_category = (TextView) findViewById(R.id.tv_category);
        tx_times = (TextView) findViewById(R.id.tx_times);
        tv_tx = (TextView) findViewById(R.id.tv_tx);
        sv_v = (ScrollView) findViewById(R.id.sv_v);
        sv_h = (HorizontalScrollView) findViewById(R.id.sv_h);
        dl = (com.mckuai.widget.ProgressButton) findViewById(R.id.dl);
        dl.setOnClickListener(this);
        client = MCkuai.getInstance().mClient;
        mLoader = ImageLoader.getInstance();
        sv_lh = (LinearLayout) findViewById(R.id.sv_lh);

    }


    private void showData() {
        if (null != map.getIcon() && 10 < map.getIcon().length()) {
            mLoader.displayImage(map.getIcon() + "", imag);
        }
        showPics();

        tv_name.setText(map.getViewName());
        String leixing = "类型:" + map.getResCategroyTwo().substring(map.getResCategroyTwo().indexOf("|") + 1, map.getResCategroyTwo().length());
        leixing = leixing.replace("|", " ");
        tv_category.setText(leixing);
        tx_times.setText(map.getInsertTime());
        tv_nm.setText("作者:" + map.getUploadMan());
        tv_tx.setText(Html.fromHtml(map.getDres() + ""));
        switch (downloadstate) {
            case 2:
                dl.setText("导入");
                break;
            case 1:
                handler.sendMessage(handler.obtainMessage(0));

                break;
            case 0:
                dl.setText("下载" + "   " + map.getResSize() + "KB");
                break;
            default:
                break;
        }
////     pictures 详细图片
//        for (int i = 0; i < 5; i++) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(424, 238);
//            ImageView imv = new ImageView(this);
//
//            params.setMargins(4, 0, 0, 0);
//            imv.setLayoutParams(params);
//            mLoader.displayImage("http://e.hiphotos.baidu.com/image/pic/item/a9d3fd1f4134970a1caaa23097cad1c8a6865dd7.jpg", imv);
////            String str = "";
////            String[] list;
////            list = str.split(",");   //json
////            if(list!=null&&list.length!=0){
////                for (int j = 0; j < list.length; j++) {
////                 String tmp=   list[j];
////                }
////            }
//            sv_lh.addView(imv);
//        }
    }

    private void showPics() {
        if (null != map.getPictures() && 1 < map.getPictures().length()) {
            String[] pic = map.getPictures().split(",");
            sv_lh.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);
//            for (int i = 0; i < 5; i++) {
            for (String curpic : pic) {
                ImageView imageView = (ImageView) inflater.inflate(R.layout.item_pic, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(213), dp2px(120));
                params.setMargins(dp2px(2), dp2px(10), dp2px(2), dp2px(10));

                imageView.setLayoutParams(params);
                mLoader.displayImage(curpic, imageView);
                mLoader.displayImage(curpic, imageView, options);
                imageView.setTag(curpic);
                sv_lh.addView(imageView);
            }
//            }
        }
    }

    private int dp2px(int dp) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected void loadData() {
        final RequestParams params = new RequestParams();
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_details);
        if (map == null) {
            map = new Map();
        }
        Log.e("url:", url + "&" + params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                //popupLoadingToast("download");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response != null && response.has("state")) {
                    try {
                        if (response.getString("state").equals("ok")) {
                            JSONObject object = response.getJSONObject("dataObject");
                            map = mGson.fromJson(object.toString(), Map.class);
                            if (null == map) {
                                map = new Map();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (map != null) {
                        showData();
                    } else {
                        showNotification(0, "详情加载出错", R.id.md_r1);
                    }
                } else {
                    showNotification(0, "加载数据错误", R.id.md_r1);
                }

            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //cancleLodingToast(false);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
//                 CustomShareBoard shareBoard = new CustomShareBoard();
//                 CustomShareBoard(this,mShareService,map);
//                 shareBoard.showAtLocation(this.getWindow().getDecorView(),
//                 Gravity.BOTTOM, 0, 0);
                shareMap();
                break;
            case R.id.dl:
                manager = DLManager.getInstance(mContext);
                taskListener = new MCDTListener();
                String downloadDir = MCkuai.getInstance().getMapDownloadDir();
                String filename = downloadDir + map.getFileName();
                switch (downloadstate) {
                    case 0:
                        MCkuai.getInstance().addDownloadTask(map.getResId(), taskListener);
                        String url = map.getSavePath();
                        manager.dlStart(url, downloadDir, taskListener);
                        showNotification(1, "下载成功", R.id.md_r1);
                        break;
                    case 2:
                        mapManager.importMap(filename);
                        showNotification(1, "导入成功", R.id.md_r1);
                        break;
                }
                break;
            default:
                break;
        }
    }

//    class taskListeners extends MCDTListener {
//        private com.mckuai.widget.ProgressButton btn;
//
//        public taskListeners(com.mckuai.widget.ProgressButton btn) {
//            this.btn = btn;
//        }
//
//        @Override
//        public void onProgress(int progress) {
//            super.onProgress(progress);
//            btn.updateProgress(progress);
//        }
//    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dl.updateProgress(taskListener.getProcess());
                    handler.sendMessageDelayed(handler.obtainMessage(0), 500);
                    break;
                case 1:
                    dl.setText("导入");
                    downloadstate = 2;
                    break;
                case 5:
                    configPlatforms();
                    break;
            }
        }
    };

    protected void shareMap() {
        if (null == map) {
            return;
        }
        mShareService.setShareContent(map.getViewName());
        if (null != map.getIcon() || 10 < map.getIcon().length()) {
            mShareService.setShareMedia(new UMImage(this, map.getIcon()));
        }
        mShareService.openShare(this, false);
    }

    private void configPlatforms() {
        String targetUrl = "http://www.mckuai.com/thread-" + map.getId() + ".html";
        String title = "麦块for我的世界盒子";
        String context = map.getViewName();
        UMImage image;
        if (null != map.getIcon() && 10 < map.getIcon().length()) {
            image = new UMImage(this, map.getIcon());
        } else {
            image = new UMImage(this, R.drawable.icon_share_default);
        }

        String appID_QQ = "101155101";
        String appAppKey_QQ = "78b7e42e255512d6492dfd135037c91c";
        // 添加qq
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appID_QQ, appAppKey_QQ);
        qqSsoHandler.setTargetUrl(targetUrl);
        qqSsoHandler.setTitle(title);
        qqSsoHandler.addToSocialSDK();
        // 添加QQ空间参数
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appID_QQ, appAppKey_QQ);
        qZoneSsoHandler.setTargetUrl(targetUrl);
        qZoneSsoHandler.addToSocialSDK();

        String appIDWX = "wx49ba2c7147d2368d";
        String appSecretWX = "85aa75ddb9b37d47698f24417a373134";
        // 添加微信
        UMWXHandler wxHandler = new UMWXHandler(this, appIDWX, appSecretWX);
        wxHandler.setTargetUrl(targetUrl);
        wxHandler.setTitle(title);
        wxHandler.showCompressToast(false);
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appIDWX, appSecretWX);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.setTargetUrl(targetUrl);
        wxHandler.showCompressToast(false);
        wxCircleHandler.setTitle(title);
        wxCircleHandler.addToSocialSDK();
        // 移除多余平台
        mShareService.getConfig().removePlatform(SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA);
        // 添加内容和图片
        mShareService.setShareContent(context);
        mShareService.setShareMedia(image);
    }
}