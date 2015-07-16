package com.mckuai.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.MapAdapter;
import com.mckuai.adapter.RankAdapters;
import com.mckuai.adapter.mapadapters;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.bean.PageInfo;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.MainActivity;
import com.mckuai.imc.Map_detailsActivity;
import com.mckuai.imc.MymapActivity;
import com.mckuai.imc.R;
import com.mckuai.imc.RankingActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapFragment extends BaseFragment implements View.OnClickListener, RankAdapters.OnItemClickListener, RankAdapters.OnMapDownloadListener {
    private View view;
    private String searchContext;//输入内容
    private Context mContent;
    //    private ImageView btn_left, btn_right;
    private Button rb_map, rb_classification, rb_mymap;
    private EditText map_ed;
    private TextView tv_titles;
    //    private ListView map_ls;
    private UltimateRecyclerView urv_mapList;
    private RelativeLayout mp_r1;
    private MapBean mapList;
    private PageInfo page;
    private LinearLayout l1, cf_l1, cf_l2, cf_l3, cf_l4, cf_l5, cf_l6;
    //    private MapAdapter adapter;
    private RankAdapters mapadapters;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private String mapType = null;
    private String orderFiled = null;
    private ArrayList<Map> map;
    private TextView tit;
    private RelativeLayout hidetitle;
    private boolean isChanged = false;
    private static final String TAG = "MapFragment";
    private View.OnClickListener leftButtonListener_myMaps;
    private View.OnClickListener rightButtonListener_myMaps;
    private MCkuai application = MCkuai.getInstance();
    private ImageView btn_right_view = application.getBtn_publish();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        tit = MainActivity.gettitle();
//        application = MCkuai.getInstance();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume");
        if (null == urv_mapList) {
            initView();
        }
        showData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            showData();
        } else {
            cancleLodingToast(false);
            if ((mp_r1 != null && mp_r1.getVisibility() == view.VISIBLE) || (urv_mapList != null && l1.getVisibility() == View.GONE)) {
                hideTypeLayout();
                map_ed.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancleLodingToast(false);

    }

    private void showData() {
        if (isLoading || application.fragmentIndex != 1) {
            Log.w(TAG, "当前页面不是可显示页面,返回");
            return;
        }
        btn_right_view.setOnClickListener(this);
        if (null == mapList || null == mapList.getData() || 0 == mapList.getPageBean().getPage()) {
            loadData();
            return;
        }
        mapadapters.setData(mapList.getData());
    }

    protected void initView() {
        map_ed = (EditText) view.findViewById(R.id.map_ed);
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
        rb_map = (Button) view.findViewById(R.id.rb_map);
        rb_classification = (Button) view.findViewById(R.id.rb_classification);
        rb_mymap = (Button) view.findViewById(R.id.rb_mymap);
//        map_ls = (ListView) view.findViewById(R.id.map_ls);
//        map_ls.setOnItemClickListener(this);
        urv_mapList = (UltimateRecyclerView) view.findViewById(R.id.urv_mapList);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        urv_mapList.setLayoutManager(manager);
        mapadapters = new RankAdapters(getActivity());
        mapadapters.setOnItemClickListener(this);
        mapadapters.setOnMapDownloadListener(this);
        urv_mapList.setAdapter(mapadapters);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
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
        client = application.mClient;
        l1 = (LinearLayout) view.findViewById(R.id.l1);
        mp_r1 = (RelativeLayout) view.findViewById(R.id.mp_r1);
        cf_l1 = (LinearLayout) view.findViewById(R.id.cf_l1);
        cf_l2 = (LinearLayout) view.findViewById(R.id.cf_l2);
        cf_l3 = (LinearLayout) view.findViewById(R.id.cf_l3);
        cf_l4 = (LinearLayout) view.findViewById(R.id.cf_l4);
        cf_l5 = (LinearLayout) view.findViewById(R.id.cf_l5);
        cf_l6 = (LinearLayout) view.findViewById(R.id.cf_l6);
//        btn_right = (ImageView) view.findViewById(R.id.btn_right);
//        btn_right.setVisibility(View.VISIBLE);
//        btn_left = (ImageView) view.findViewById(R.id.btn_left);
//        btn_right.setOnClickListener(this);
//        btn_left.setOnClickListener(this);
        rb_map.setOnClickListener(this);
        rb_classification.setOnClickListener(this);
        rb_mymap.setOnClickListener(this);
        cf_l1.setOnClickListener(this);
        cf_l2.setOnClickListener(this);
        cf_l3.setOnClickListener(this);
        cf_l4.setOnClickListener(this);
        cf_l5.setOnClickListener(this);
        cf_l6.setOnClickListener(this);
        leftButtonListener_myMaps = new onTitleButtonClickListener();
        rightButtonListener_myMaps = new onTitleButtonClickListener();
        MainActivity.setOnclickListener(leftButtonListener_myMaps, rightButtonListener_myMaps);

    }

    class onTitleButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_titlebar_left:
                    hideTypeLayout();
                    MainActivity.setLeftButtonView(false);
                    map_ed.setVisibility(View.GONE);
                    mapadapters.setpaihang(false);
                    tit.setText("地图");
                    break;
//                case R.id.btn_titlebar_right:
//                    if (map_ed.getVisibility() == View.GONE) {
//                        map_ed.setVisibility(View.VISIBLE);
//                    } else {
//                        sousuo();
//                    }
//                    break;
            }
        }
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rb_map:
                searchContext = null;
                l1.setVisibility(View.GONE);
                tit.setText("地图排行");
                MainActivity.setLeftButtonView(true);
                mapadapters.setpaihang(true);
                mapType = null;
                mapList.getPageBean().setPage(0);
                loadData();
                break;
            case R.id.rb_classification:
                tit.setText("地图分类");
                showTypeLayout();
                break;
            case R.id.rb_mymap:
                intent = new Intent(getActivity(), MymapActivity.class);
                getActivity().startActivity(intent);
                break;
            //����
            case R.id.cf_l1:
                survival();
                break;
            //����
            case R.id.cf_l2:
                decipt();
                break;
            //�ܿ�
            case R.id.cf_l3:
                parkour();
                break;
            //����
            case R.id.cf_l4:
                architecture();
                break;
            //pvp����
            case R.id.cf_l5:
                pvp();
                break;
            case R.id.cf_l6:
                totle();
                tit.setText("地图");
                break;
            case R.id.btn_titlebar_right:
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

    private void showTypeLayout() {
        urv_mapList.setVisibility(View.GONE);
        mp_r1.setVisibility(View.VISIBLE);
    }

    private void hideTypeLayout() {
        urv_mapList.setVisibility(View.VISIBLE);
        l1.setVisibility(View.VISIBLE);
        mp_r1.setVisibility(View.GONE);
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
        params.put("page", mapList.getPageBean().getPage() + 1 + "");
//        if (null != mapType) {
//            params.put("kinds", mapType);
//        }
//        if (null != orderFiled) {
//            params.put("orderField", orderFiled);
//        }
        Log.e("url:", url + "&" + params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                popupLoadingToast("正在加载，请稍后");
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
                        cancleLodingToast(true);
                        map_ed.setVisibility(View.GONE);
                        showData();
                        return;
                    } else {
                        showNotification(0, "没找到所选地图", R.id.urv_mapList);
                        if (mapType != null && mapList.getData().size() == 0) {
                            totle();
                        }
                    }
                } else {
                    showNotification(0, "加载数据错误", R.id.urv_mapList);
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

    protected void survival() {
        hideTypeLayout();
        mapType = "生存";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        mapadapters.setpaihang(false);
        loadData();
    }

    protected void decipt() {
        hideTypeLayout();
        mapType = "解密";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        mapadapters.setpaihang(false);
        loadData();
    }

    protected void parkour() {
        hideTypeLayout();
        mapType = "跑酷";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        mapadapters.setpaihang(false);
        loadData();
    }

    protected void architecture() {
        hideTypeLayout();
        mapType = "建筑";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        mapadapters.setpaihang(false);
        loadData();
    }

    protected void pvp() {
        hideTypeLayout();
        mapType = "pvp竞技";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        mapadapters.setpaihang(false);
        loadData();
    }

    protected void totle() {
        hideTypeLayout();
        mapType = null;
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        searchContext = null;
        loadData();
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(getActivity(), Map_detailsActivity.class);
//        Map mapList = (Map) mapadapters.getItem(position);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(getString(R.string.Details), mapList);
//        intent.putExtras(bundle);
//        getActivity().startActivity(intent);
//    }

    @Override
    public void onItemClick(Map mapinfo) {
        if (null != mapinfo) {
            Intent intent = new Intent(getActivity(), Map_detailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.Details), mapinfo);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
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
            searchContext = null;
            map_ed.setVisibility(View.GONE);
        } else {
            Toast.makeText(getActivity(), "不能搜索空内容!", Toast.LENGTH_SHORT).show();
        }
    }
}
