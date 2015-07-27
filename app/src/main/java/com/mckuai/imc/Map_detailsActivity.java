package com.mckuai.imc;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
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
import com.mckuai.service_and_recevier.DownloadProgressRecevier;
import com.mckuai.utils.MCMapManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zzz on 2015/6/25.
 */
public class Map_detailsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title, tv_name, tv_nm, tv_category, tx_times, tv_tx;
    private ImageView btn_left, imag, btn_right;
    private com.mckuai.widget.ProgressButton dl;
    private ScrollView sv_v;
    private HorizontalScrollView sv_h;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private ImageLoader mLoader;
    private Map map;
    private LinearLayout sv_lh;
    private int downloadstate;//0未下载，1正在下载，2已下载
    private DisplayImageOptions options;
    private com.umeng.socialize.controller.UMSocialService mShareService;
    private ImageView iv_serverPic;//只有一张图时显示
    private MCMapManager mapManager;
    private DownloadProgressRecevier recevier;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_details);
        map = (Map) getIntent().getSerializableExtra("details");
        if (null == map) {
            map = MCkuai.getInstance().getMap();
        }

        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.background_user_cover_default)
                .showImageForEmptyUri(R.drawable.background_user_cover_default)
                .showImageOnFail(R.drawable.background_user_cover_default)
                .build();
        handler.sendMessageDelayed(handler.obtainMessage(5), 1000);
        mShareService = UMServiceFactory.getUMSocialService("com.umeng.share");
        if (mapManager == null) {
            mapManager = MCkuai.getInstance().getMapManager();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkState();
        mLoader = ImageLoader.getInstance();

        dl = (com.mckuai.widget.ProgressButton) findViewById(R.id.dl);

        if (null == imag) {
            initview();
            initReciver();
        }
        if (null != map) {
            showData();
            loadData();
        } else {
            showNotification(3, "未获取到地图信息,请返回!", R.id.details);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        if (null != recevier) {
            unregisterReceiver(recevier);
            recevier = null;
        }
    }

    public void checkState() {
        if (map.isDownload()) {
            downloadstate = 2;
        } else {
            if (map.getDownloadProgress() == 0) {
                downloadstate = 0;
            } else {
                downloadstate = 1;
            }
        }
    }

    public void initview() {
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
        iv_serverPic = (ImageView) findViewById(R.id.iv_pic);
    }

    private void initReciver() {
        if (null == recevier) {
            recevier = new DownloadProgressRecevier() {
                @Override
                public void onProgress(String resId, int progress) {
                    if (resId.equals(map.getResId())) {
                        dl.updateProgress(progress);
                        if (100 == progress) {
                            downloadstate = 2;
                            dl.setText("导入");
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

    private void showData() {
        if (null != map.getIcon() && 10 < map.getIcon().length()) {
            mLoader.displayImage(map.getIcon() + "", imag);
        }
        showPics();

        tv_name.setText(map.getViewName());
        String leixing = "类型:" + map.getResCategroyTwo().substring(map.getResCategroyTwo().indexOf("|") + 1, map.getResCategroyTwo().length());
        leixing = leixing.replace("|", " ");
        tv_category.setText(leixing);
        String time = map.getInsertTime().substring(map.getInsertTime().indexOf("-") + 1, map.getInsertTime().indexOf(" "));
        time = time.replace("-", "月");
        tx_times.setText("时间：" + time + "日");
        tv_nm.setText("作者:" + map.getUploadMan());
        tv_tx.setText(Html.fromHtml(map.getDres() + ""));
        String sizes;
        Long size = Long.parseLong(map.getResSize()) / 1024;
        if (size < 1024) {
            sizes = size + "KB";
        } else {
            sizes = (size / 1024) + "MB";
        }
        switch (downloadstate) {
            case 2:
                dl.setText("导入");
                break;
            case 1:
                dl.setText("正在下载");
                handler.sendMessage(handler.obtainMessage(0));
                break;
            case 0:
                dl.setText("下载" + "   " + sizes);
                break;
            default:
                break;
        }

    }

    private void showPics() {
        if (null != map.getPictures() && 1 < map.getPictures().length()) {
            String[] pic = map.getPictures().split(",");
            if (pic.length == 1) {
                mLoader.displayImage(pic[0], iv_serverPic, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                        float scale = loadedImage.getWidth() * 1.0f / screenWidth;
                        int height = (int) (loadedImage.getHeight() / scale);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, height);
                        params.setMargins(0, 20, 0, 20);
                        iv_serverPic.setLayoutParams(params);
                        iv_serverPic.setScaleType(ImageView.ScaleType.FIT_XY);
                        iv_serverPic.setImageBitmap(loadedImage);

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            } else {
                sv_lh.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(this);
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
            }
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
                shareMap();
                break;
            case R.id.dl:
                String downloadDir = MCkuai.getInstance().getMapDownloadDir();
                String filename = downloadDir + map.getFileName();
                switch (downloadstate) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setAction("com.mckuai.downloadservice");
                        intent.setPackage(this.getPackageName());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("MAP", map);
                        intent.putExtras(bundle);
                        startService(intent);
                        dl.setText("正在下载");
                        break;
                    case 2:
                        MCMapManager mapManager = MCkuai.getInstance().getMapManager();
                        if (!mapManager.importMap(filename)) {
                            Toast.makeText(this, "游戏导入失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "游戏导入成功", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
            default:
                break;
        }
    }


    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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