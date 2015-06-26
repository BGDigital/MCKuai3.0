package com.mckuai.imc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zzz on 2015/6/25.
 */
public class Map_detailsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title, tv_name, tv_nm, tv_category, tx_times, tv_tx;
    private ImageView btn_left, image;
    private ImageButton btn_right;
    private Button dl;
    private ScrollView sv_v;
    private HorizontalScrollView sv_h;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private ImageLoader mLoader;

    private Map map;
    private LinearLayout sv_lh;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_details);
        initview();
        map= (Map)getIntent().getSerializableExtra(getString(R.string.Details));
        loadData();
    }

    public void initview() {
        image = (ImageView) findViewById(R.id.image);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
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
        dl = (Button) findViewById(R.id.dl);
        dl.setOnClickListener(this);
        client = MCkuai.getInstance().mClient;
        mLoader = ImageLoader.getInstance();
        sv_lh = (LinearLayout)findViewById(R.id.sv_lh);

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

        mLoader.displayImage(map.getIcon(), image);
        tv_name.setText(map.getViewName());
        tv_category.setText(map.getResCategroyTwo());
        tx_times.setText(map.getInsertTime());
        tv_nm.setText(map.getUploadMan());
        tv_tx.setText(map.getDres() +"\n"+ map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres()+"\n"+map.getDres());
//      pictures 详细图片
for (int i = 0;i< 5;i++) {
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300,LinearLayout.LayoutParams.WRAP_CONTENT);
    ImageView imv = new ImageView(this);
    /*imv.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    });*/
    params.setMargins(8, 0, 0, 0);
    imv.setLayoutParams(params);
    mLoader.displayImage("http://e.hiphotos.baidu.com/image/pic/item/a9d3fd1f4134970a1caaa23097cad1c8a6865dd7.jpg", imv);
    sv_lh.addView(imv);
}
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
                    if (map!=null) {
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
            case R.id.btn_right:

                break;
            case R.id.dl:

                break;
            default:
                break;
        }
    }
}
