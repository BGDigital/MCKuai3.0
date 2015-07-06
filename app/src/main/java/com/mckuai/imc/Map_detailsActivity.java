package com.mckuai.imc;

import android.content.Context;
import android.os.Bundle;
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

import com.google.common.base.Utf8;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.until.MCDTListener;
import com.mckuai.until.MCMapManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private MCDTListener McDLTaskListene;
    private Map map;
    private LinearLayout sv_lh;
    private DLManager manager;
    private MCMapManager mapManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_details);
        map = (Map) getIntent().getSerializableExtra(getString(R.string.Details));
        mLoader = ImageLoader.getInstance();
        mapManager = new MCMapManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void initview() {
//        iv_cover = (ImageView) findViewById(R.id.iv_serverCover);
        imag = (ImageView) findViewById(R.id.image);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        btn_right = (ImageView) findViewById(R.id.btn_right);
//        btn_right.setOnClickListener(this);
        btn_right.setVisibility(View.GONE);
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

//        sv_h.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//               sv_h.requestDisallowInterceptTouchEvent(true);
//
//                return false;
//            }
//        });

    }


    private void showData() {
        if (null != map.getIcon() && 10 < map.getIcon().length()) {
            mLoader.displayImage(map.getIcon() + "", imag);
        }
        showPics();
//        mLoader.displayImage(map.getIcon(), image);
        tv_name.setText(map.getViewName());
//        tv_category.setText(map.getResCategroyTwo());
        if (null != map.getResCategroyTwo() && 1 < map.getResCategroyTwo().length()) {
            String tag[] = map.getResCategroyTwo().split("|");
            tv_category.setText("类型：" + tag[0]);
        }
        tx_times.setText(map.getInsertTime());
        tv_nm.setText(map.getUploadMan());
        tv_tx.setText(Html.fromHtml(map.getDres() + ""));
////      pictures 详细图片
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
            for (int i = 0; i < 5; i++) {
                for (String curpic : pic) {
                    ImageView imageView = (ImageView) inflater.inflate(R.layout.item_pic, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(213), dp2px(120));
                    params.setMargins(dp2px(2), dp2px(10), dp2px(2), dp2px(10));

                    imageView.setLayoutParams(params);
                    mLoader.displayImage(curpic, imageView);
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
                        showNotification(0, "no data!!", R.id.md_r1);
                    }
                } else {
                    showNotification(0, "load data error!!", R.id.md_r1);
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
            case R.id.dl:
                manager = DLManager.getInstance(mContext);
                McDLTaskListene = new MCDTListener();
//                if (map.getViewName() != mapManager.addDownloadMap(map.getViewName()) {
                MCkuai.getInstance().addDownloadTask(map.getResId(), McDLTaskListene);
                String url = "http://" + map.getSavePath();
                String downloadDir = MCkuai.getInstance().getMapDownloadDir();
                manager.dlStart(url, downloadDir, McDLTaskListene);
//            }


            break;
            default:
                break;
        }
    }
}
