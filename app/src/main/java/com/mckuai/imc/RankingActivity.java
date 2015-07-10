package com.mckuai.imc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.adapter.MapAdapter;
import com.mckuai.adapter.RankingAdapter;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RankingActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView ranking_lv;
    private String searchContext;//输入内容
    private ImageView btn_left, btn_right;
    private TextView tv_title;
    private Button btn_showOwner;
    private Map map;
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
        ranking_lv.setOnItemClickListener(this);
        map_ed = (EditText) findViewById(R.id.map_ed);
        map_ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (null != map_ed.getText() && 0 < map_ed.getText().toString().trim().length()) {
                        searchContext = map_ed.getText().toString();
                        search();
                    } else {
                        Toast.makeText(RankingActivity.this, "不能搜索空内容!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
        r_l1 = (LinearLayout) findViewById(R.id.r_l1);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
        btn_showOwner.setVisibility(View.GONE);
        tv_title.setText("地图排行");
        btn_right = (ImageView) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setOnClickListener(this);
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
                        showNotification(0, "没有排行信息", R.id.ranking_lv);
                    }
                } else {
                    showNotification(0, "加载数据错误", R.id.ranking_lv);
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
        Intent intent = new Intent(RankingActivity.this, Map_detailsActivity.class);
        Map mapList = (Map) adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.Details), mapList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                map_ed.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    private void search() {
        final RequestParams params = new RequestParams();
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_map_search);
        if (mapList == null) {
            mapList = new MapBean();
        }
        params.put("page", mapList.getPageBean().getPage() + 1 + "");
        params.put("type", map);
        params.put("key", searchContext);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
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
                        cancleLodingToast(true);
                        showData();
                        return;
                    } else {
                        showNotification(0, "没有当前选项", R.id.l1);
                    }
                } else {
                    showNotification(0, "加载数据错误", R.id.l1);
                }
                cancleLodingToast(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}

