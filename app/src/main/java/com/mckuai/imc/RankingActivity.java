package com.mckuai.imc;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.MapAdapter;
import com.mckuai.adapter.RankAdapters;
import com.mckuai.adapter.RankingAdapter;
import com.mckuai.adapter.mapadapters;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.bean.PageInfo;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RankingActivity extends BaseActivity implements View.OnClickListener, RankAdapters.OnItemClickListener, RankAdapters.OnMapDownloadListener {
    //    private ListView ranking_lv;
    private String searchContext;//输入内容
    private ImageView btn_left, btn_right;
    private TextView tv_title;
    private Button btn_showOwner;
    private Map map;
    private PageInfo page;
    private EditText map_ed;
    private RankingAdapter adapter;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private MCkuai application;
    private String mapType = null;
    private String orderFiled = null;
    private MapBean mapList;
    private LinearLayout r_l1;
    private UltimateRecyclerView urv_mapList;
    private RankAdapters adapters;
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
        if (null == urv_mapList) {
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
        adapters.setData(mapList.getData());
    }

    protected void initview() {
//        ranking_lv = (ListView) findViewById(R.id.ranking_lv);
//        ranking_lv.setOnItemClickListener(this);
        urv_mapList = (UltimateRecyclerView) findViewById(R.id.urv_mapList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        urv_mapList.setLayoutManager(manager);
        adapters = new RankAdapters();
        adapters.setOnItemClickListener(this);
        adapters.setOnMapDownloadListener(this);
        urv_mapList.setAdapter(adapters);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        urv_mapList.addItemDecoration(dividerItemDecoration);
        urv_mapList.enableLoadmore();
        urv_mapList.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int i, int i1) {
                loadData();
            }
        });

        urv_mapList.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != page) {
                    page.setPage(0);
                }
                loadData();
            }
        });
        map_ed = (EditText) findViewById(R.id.map_ed);
        map_ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sousuo();
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

    private String getUrl() {
        String url = getString(R.string.interface_domainName);
        if (null != searchContext) {
            //搜索
            url += getString(R.string.interface_map_search);
        } else {
            url += getString(R.string.interface_map);
        }
        return url;
    }

    private RequestParams getparams() {
        RequestParams params = new RequestParams();
        if (null != searchContext) {
            params.put("type", "map");
            params.put("key", searchContext);
        } else {
            params.put("kinds", mapType);
            params.put("orderFiled", "DownNum");
        }
        return params;
    }

    protected void loadData() {
        if (null != mapList && null != mapList.getPageBean() && mapList.getPageBean().EOF()) {
            return;
        }
        final RequestParams params = getparams();
        final String url = getUrl();
        if (mapList == null) {
            mapList = new MapBean();
        }
        params.put("page", mapList.getPageBean().getNextPage() + "");
//        if (null != mapType) {
//            params.put("kinds", mapType);
//        }
//        if (null != orderFiled) {
//            params.put("orderField", orderFiled);
//        }
//        params.put("orderFiled", "DownNum");
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
                        showNotification(0, "没有排行信息", R.id.r_l1);
                    }
                } else {
                    showNotification(0, "加载数据错误", R.id.r_l1);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                if (map_ed.getVisibility() == View.GONE) {
                    map_ed.setVisibility(View.VISIBLE);
                } else {
                    sousuo();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(Map mapinfo) {
        if (null != mapinfo) {
            Intent intent = new Intent(RankingActivity.this, Map_detailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.Details), mapinfo);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
        }
    }

    @Override
    public void afterMapDownload() {

    }

    public void sousuo() {
        if (null != map_ed.getText() && 0 < map_ed.getText().toString().trim().length()) {
            searchContext = map_ed.getText().toString();
            if (mapList != null && mapList.getPageBean() != null) {
                mapList.getPageBean().setPage(0);
            }
            loadData();
        } else {
            Toast.makeText(RankingActivity.this, "不能搜索空内容!", Toast.LENGTH_SHORT).show();
        }
    }
}

