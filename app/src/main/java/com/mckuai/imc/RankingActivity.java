package com.mckuai.imc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.adapter.MapAdapter;
import com.mckuai.adapter.RankingAdapter;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class RankingActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView ranking_lv;
    private ImageView btn_left;
    private ImageButton btn_right;
    private TextView tv_title;
    private EditText map_ed;
    private RankingAdapter adapter;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private MCkuai application;
    private String mapType = null;
    private String orderFiled = null;
    private MapBean mapList;
    private LinearLayout r_l1;
    private static final String TAG = "Ranking";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_map);
        application = MCkuai.getInstance();
        initview();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume");
        if (null == ranking_lv) {
            initview();
        }
        showData();
    }

    private void showData() {
        if (isLoading || application.fragmentIndex != 1) {
            Log.w(TAG, "当前页面不是可显示页面,返回");
            return;
        }
        if (null == mapList || null == mapList.getData() || 0 == mapList.getPageBean().getPage()) {
            isLoading = true;
            loadData();
            return;
        }
        if (null == adapter) {
            adapter = new RankingAdapter(this, mapList.getData());
            ranking_lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    protected void initview() {
        ranking_lv = (ListView) findViewById(R.id.ranking_lv);
        map_ed = (EditText) findViewById(R.id.map_ed);
        r_l1 = (LinearLayout) findViewById(R.id.r_l1);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("地图排行");
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        client = application.mClient;

    }

    protected void loadData() {
        final RequestParams params = new RequestParams();
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_map);
        if (mapList == null) {
            mapList = new MapBean();
        }
        params.put("page", mapList.getPageBean().getPage() + 1 + "");
        if (null != mapType) {
            params.put("kinds", mapType);
        }
        if (null != orderFiled) {
            params.put("orderField", orderFiled);
        }
        params.put("orderFiled", "DownNum");
        Log.e("url:", url + "&" + params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
//                popupLoadingToast("download");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                isLoading = false;
                if (response != null && response.has("state")) {
                    try {
                        if (response.getString("state").equals("ok")) {
                            JSONObject object = response.getJSONObject("dataObject");
                            MapBean bean = mGson.fromJson(object.toString(), MapBean.class);
                            if (null == mapList) {
                                mapList = new MapBean();
                            }
                            if (bean.getPageBean().getPage() == 1) {
                                mapList.getData().clear();
                            }
                            mapList.getData().addAll(bean.getData());
                            mapList.setPageBean(bean.getPageBean());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!mapList.getData().isEmpty()) {
//                        cancleLodingToast(true);
                        showData();
                        return;
                    } else {
                        showNotification(0, "no data!!", R.id.ranking_lv);
                    }
                } else {
                    showNotification(0, "load data error!!", R.id.ranking_lv);
                }
//                cancleLodingToast(false);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                cancleLodingToast(false);
                isLoading = false;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, Map_detailsActivity.class);
        Map mapList = (Map) adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.Details), mapList);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
