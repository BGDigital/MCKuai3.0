package com.mckuai.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.RankAdapters;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.bean.PageInfo;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.MainActivity;
import com.mckuai.imc.Map_detailsActivity;
import com.mckuai.imc.MymapActivity;
import com.mckuai.imc.R;
import com.mckuai.service_and_recevier.DownloadProgressRecevier;
import com.mckuai.mctools.WorldUtil.MCMapManager;
import com.mckuai.widget.fabbutton.FabButton;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapFragment extends BaseFragment implements View.OnClickListener, RankAdapters.OnItemClickListener, RankAdapters.OnMapDownloadListener {
    private View view;
    private Button rb_map, rb_classification, rb_mymap;
    private UltimateRecyclerView urv_mapList;
    private RelativeLayout mp_r1;
    private MapBean mapList;
    private PageInfo page;
    private LinearLayout cf_l1, cf_l2, cf_l3, cf_l4, cf_l5, cf_l6;
    private RankAdapters mapadapters;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private String[] mOrderFields = {"DownNum", "InsertTime"};
    private int orderFieldIndex = 1;
    private String[] mMapType = {"生存", "解密", "跑酷", "建筑", "pvp竞技"};
    private int typeFieldIndex = mMapType.length;
    private TextView tit;
    private static final String TAG = "MapFragment";
    private MCkuai application = MCkuai.getInstance();
    private MCMapManager mapManager;
    private DownloadProgressRecevier recevier;
    private long lastUpdateTime;
    private String maptype;
    private String searchContext;
    private RecyclerView.LayoutManager manager;
    private boolean isSearch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        if (mapManager == null) {
            mapManager = MCkuai.getInstance().getMapManager();
        }
        tit = MainActivity.gettitle();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()){
            showData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && null != view) {
            showData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancleLodingToast(false);
        if (null != mp_r1) {
            mp_r1.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        if (null != recevier) {
            try {
                getActivity().unregisterReceiver(recevier);
                recevier = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void showData() {
        if (null == urv_mapList) {
            initView();
        }
        MainActivity.setRightButtonView(0,R.drawable.btn_search_selector);
        setFilterUI();
        initReciver();

        if (null == mapList || null == mapList.getData() || null == page || 0 == page.getPage()) {
            loadData();
            return;
        }
        panduanxiazai(mapList.getData(), mapManager.getDownloadMaps());

        if (null == mapadapters) {
            mapadapters = new RankAdapters(getActivity());
            mapadapters.setOnItemClickListener(this);
            mapadapters.setData(mapList.getData());
            mapadapters.setOnMapDownloadListener(this);
            urv_mapList.setAdapter(mapadapters);
        } else {
            mapadapters.setData(mapList.getData());
        }
    }

    protected void initView() {
        rb_map = (Button) view.findViewById(R.id.rb_map);
        rb_classification = (Button) view.findViewById(R.id.rb_classification);
        rb_mymap = (Button) view.findViewById(R.id.rb_mymap);
        urv_mapList = (UltimateRecyclerView) view.findViewById(R.id.urv_mapList);
        manager = new LinearLayoutManager(getActivity());
        urv_mapList.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        urv_mapList.addItemDecoration(dividerItemDecoration);
        urv_mapList.setHasFixedSize(true);
        urv_mapList.enableLoadmore();
        urv_mapList.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int i, int i1) {
                if (page == null || !page.EOF()) {
                    loadData();
                }
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
        client = application.mClient;
        mp_r1 = (RelativeLayout) view.findViewById(R.id.mp_r1);
        cf_l1 = (LinearLayout) view.findViewById(R.id.cf_l1);
        cf_l2 = (LinearLayout) view.findViewById(R.id.cf_l2);
        cf_l3 = (LinearLayout) view.findViewById(R.id.cf_l3);
        cf_l4 = (LinearLayout) view.findViewById(R.id.cf_l4);
        cf_l5 = (LinearLayout) view.findViewById(R.id.cf_l5);
        cf_l6 = (LinearLayout) view.findViewById(R.id.cf_l6);
        rb_map.setOnClickListener(this);
        rb_classification.setOnClickListener(this);
        rb_mymap.setOnClickListener(this);
        cf_l1.setOnClickListener(this);
        cf_l2.setOnClickListener(this);
        cf_l3.setOnClickListener(this);
        cf_l4.setOnClickListener(this);
        cf_l5.setOnClickListener(this);
        cf_l6.setOnClickListener(this);
    }

    private void initReciver() {
        if (null == recevier) {
            recevier = new DownloadProgressRecevier() {
                @Override
                public void onProgress(int resType, String resId, int progress) {
                    if (resType != 1) {
                        return;
                    }
                    if (null != mapList && null != mapList.getData() && !mapList.getData().isEmpty()) {
                        int i = 0;
                        for (Map map : mapList.getData()) {
                            if (map.getResId().equals(resId)) {
                                map.setDownloadProgress(progress);
                                long time = System.currentTimeMillis();
                                if (time - lastUpdateTime > 500 || progress == 100 || progress == 1) {
                                    ViewGroup itemView = (ViewGroup) manager.findViewByPosition(i);
                                    if (null != itemView) {
                                        FabButton progressBtn = (FabButton) ((ViewGroup) itemView.getChildAt(0)).getChildAt(1);
                                        ImageButton downloadedBtn = (ImageButton) ((ViewGroup) itemView.getChildAt(0)).getChildAt(2);
                                        if (100 == progress) {
                                            progressBtn.setVisibility(View.INVISIBLE);
                                            downloadedBtn.setVisibility(View.VISIBLE);
                                            updatadownnum(map.getId());
                                            String filename = MCkuai.getInstance().getMapDownloadDir() + map.getFileName();
                                            if (!mapManager.importMap(filename)) {
                                                showNotification(0, "地图导入失败", R.id.urv_mapList);
                                            }
                                        } else {
                                            progressBtn.setProgress(progress);
                                        }
                                    }
                                    lastUpdateTime = time;
                                }
                            }
                            i++;
                        }
                    }
                }

            };
            IntentFilter filter = new IntentFilter("com.mckuai.imc.downloadprogress");
            try {
                getActivity().registerReceiver(recevier, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void updatadownnum(int id) {
        String url = getString(R.string.interface_domainName) + (R.string.interface_map_downnum) + "&id=" + id;
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("MapFragment", "" + throwable.getLocalizedMessage());
            }
        });
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rb_map:
                MobclickAgent.onEvent(getActivity(), "mapRank");
                orderFieldIndex = Math.abs(orderFieldIndex - 1);
                setFilterUI();
                typeFieldIndex = mMapType.length;
                reLoadData();
                break;
            case R.id.rb_classification:
                MobclickAgent.onEvent(getActivity(), "mapType");
                if (null == maptype) {
                    if (mp_r1.getVisibility() == View.GONE) {
                        tit.setText("地图分类");
                        mp_r1.setVisibility(View.VISIBLE);
                    } else {
                        mp_r1.setVisibility(View.GONE);
                        tit.setText("资源");
                    }
                } else {
                    maptype = null;
                }
                break;
            case R.id.rb_mymap:
                MobclickAgent.onEvent(getActivity(), "myMap");
                intent = new Intent(getActivity(), MymapActivity.class);
                getActivity().startActivityForResult(intent, 1);
                break;
            //生存
            case R.id.cf_l1:
                typeFieldIndex = 0;
                orderFieldIndex = 0;
                setFilterUI();
                reLoadData();
                break;
            //解密
            case R.id.cf_l2:
                typeFieldIndex = 1;
                orderFieldIndex = 0;
                reLoadData();
                break;
            //酷跑
            case R.id.cf_l3:
                typeFieldIndex = 2;
                orderFieldIndex = 0;
                setFilterUI();
                reLoadData();
                break;
            //建筑
            case R.id.cf_l4:
                typeFieldIndex = 3;
                orderFieldIndex = 0;
                setFilterUI();
                reLoadData();
                break;
            //pvp
            case R.id.cf_l5:
                typeFieldIndex = 4;
                orderFieldIndex = 0;
                setFilterUI();
                reLoadData();
                break;
            case R.id.cf_l6:
                typeFieldIndex = mMapType.length;
                orderFieldIndex = 0;
                setFilterUI();
                reLoadData();
                break;
            default:
                break;
        }
    }

    private void setFilterUI(){
        if (0 == orderFieldIndex){
            tit.setText("地图排行");
        }else {
            mp_r1.setVisibility(View.GONE);
            switch (typeFieldIndex){
                case 0:
                    tit.setText("地图分类-生存");
                    break;
                case 1:
                    tit.setText("地图分类-解密");
                    break;
                case 2:
                    tit.setText("地图分类-酷跑");
                    break;
                case 3:
                    tit.setText("地图分类-建筑");
                    break;
                case 4:
                    tit.setText("地图分类-PVP");
                    break;
                default:
                    if (null != searchContext){
                            tit.setText("搜索地图");
                    }else {
                        tit.setText("资源");
                    }
                    break;
            }
        }
    }

    private void reLoadData() {
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        page = null;
        searchContext = null;
        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && mapList != null) {
            if (mapManager == null) {
                mapManager = MCkuai.getInstance().getMapManager();
            }
            panduanxiazai(mapList.getData(), mapManager.getDownloadMaps());
            mapadapters.setData(mapList.getData());
        }
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
            //搜索
            params.put("type", "map");
            params.put("key", searchContext);
        } else {
            //列表
            if (typeFieldIndex != mMapType.length){
                //类型
                params.put("kinds",mMapType[typeFieldIndex]);
            }
            params.put("orderField", mOrderFields[orderFieldIndex]);
            params.put("orderType", 1 + "");
        }
        if (null != page) {
            params.put("page", page.getNextPage());
        }
        return params;
    }

    protected void loadData() {
        if (isLoading || (page != null && page.EOF())) {
            return;
        } else {
            isLoading = true;
        }
        final RequestParams params = getparams();
        final String url = getUrl();
        if (mapList == null) {
            mapList = new MapBean();
        }

        //Log.e("url:", url + "&" + params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                if (isCacheEnabled) {
                    String data = getData(url, params);
                    if (null != data && 10 < data.length()) {
                        parseData(null, null, data);
                        isCacheEnabled = false;
                        showData();
                        return;
                    }
                }
                popupLoadingToast("正在加载，请稍后");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                isLoading = false;
                if (response != null && response.has("state")) {
                    try {
                        if (response.getString("state").equals("ok")) {
                            JSONObject object = response.getJSONObject("dataObject");
                            parseData(url, params, object.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!mapList.getData().isEmpty()) {
                        cancleLodingToast(true);
                        panduanxiazai(mapList.getData(), mapManager.getDownloadMaps());
                        showData();
                        return;
                    } else {
                        if (typeFieldIndex != mOrderFields.length || null != searchContext) {
                            showNotification(0, null == searchContext ? "此类型下暂无地图，显示所有地图！" : "没找到满足条件的地图！", R.id.urv_mapList);
                            searchContext = null;
                            mapadapters.setData(null);
                        }
                    }
                } else {
                    showNotification(0, "加载数据错误！", R.id.urv_mapList);
                }
                cancleLodingToast(false);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                cancleLodingToast(false);
                isLoading = false;
            }

            @Override
            public void onCancel() {
                super.onCancel();
                isLoading = false;
            }
        });
    }

    private void parseData(String url, RequestParams params, String data) {
        MapBean bean = mGson.fromJson(data, MapBean.class);
        if (null == mapList) {
            mapList = new MapBean();
        }
        if (bean.getPageBean().getPage() == 1) {
            if (null != mapList.getData()) {
                mapList.getData().clear();
            }
            if (null != url && null != params) {
                cacheData(url, params, data);
            }
        }
        mapList.getData().addAll(bean.getData());
        page = bean.getPageBean();
    }

    @Override
    public void onRightButtonClicked(String searchContent) {
        this.searchContext = searchContent;
        doSearch();
    }

    protected void panduanxiazai(ArrayList<Map> liebiao, ArrayList<Map> yixiazai) {
        if (liebiao == null || yixiazai == null) {
            return;
        }
        for (Map curMap : liebiao) {
            Boolean isDownload = false;
            for (Map xiazaiMap : yixiazai) {
                if (curMap.getResId().equals(xiazaiMap.getResId())) {
                    curMap.setDownloadProgress(100);
                    isDownload = true;
                    break;
                }
            }
            if (!isDownload && curMap.getDownloadProgress() == 100) {
                curMap.setDownloadProgress(0);
            }
        }
    }


    @Override
    public void onItemClick(Map mapinfo) {
        if (null != mapinfo) {
            MCkuai.getInstance().setMap(mapinfo);
            Intent intent = new Intent(getActivity(), Map_detailsActivity.class);
            getActivity().startActivity(intent);
        }
    }


    @Override
    public void afterMapDownload() {

    }

    public void doSearch() {
        if (null != searchContext && !searchContext.isEmpty()) {
            typeFieldIndex = mMapType.length;
            isSearch = true;
            page = null;
            setFilterUI();
            loadData();
        } else {
            Toast.makeText(getActivity(), "不能搜索空内容!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onBackKeyPressed() {
        if (isSearch){
            isSearch = false;
            searchContext = null;
            if (null != mapList && null != mapList.getData()){
                mapList.getData().clear();
                page.setPage(0);
            }
            showData();
            return true;
        }
        else return super.onBackKeyPressed();
    }
}
