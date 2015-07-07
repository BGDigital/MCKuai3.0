package com.mckuai.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.adapter.MapAdapter;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.Map_detailsActivity;
import com.mckuai.imc.MymapActivity;
import com.mckuai.imc.R;
import com.mckuai.imc.RankingActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private View view;
    private Context mContent;
    private Button rb_map, rb_classification, rb_mymap;
    private EditText map_ed;
    private TextView tv_title;
    private ListView map_ls;
    private RelativeLayout mp_r1;
    private MapBean mapList;
    private LinearLayout l1, cf_l1, cf_l2, cf_l3, cf_l4, cf_l5, cf_l6;
    private MapAdapter adapter;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private MCkuai application;
    private String mapType = null;
    private String orderFiled = null;

    private static final String TAG = "MapFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        application = MCkuai.getInstance();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume");
        if (null == map_ls) {
            initView();
        }
        showData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            showData();
        }
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
            adapter = new MapAdapter(getActivity(), mapList.getData());
            map_ls.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    protected void initView() {
        map_ed = (EditText) view.findViewById(R.id.map_ed);
        rb_map = (Button) view.findViewById(R.id.rb_map);
        rb_classification = (Button) view.findViewById(R.id.rb_classification);
        rb_mymap = (Button) view.findViewById(R.id.rb_mymap);
        map_ls = (ListView) view.findViewById(R.id.map_ls);
        map_ls.setOnItemClickListener(this);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        client = application.mClient;
        l1 = (LinearLayout) view.findViewById(R.id.l1);
        mp_r1 = (RelativeLayout) view.findViewById(R.id.mp_r1);
        cf_l1 = (LinearLayout) view.findViewById(R.id.cf_l1);
        cf_l2 = (LinearLayout) view.findViewById(R.id.cf_l2);
        cf_l3 = (LinearLayout) view.findViewById(R.id.cf_l3);
        cf_l4 = (LinearLayout) view.findViewById(R.id.cf_l4);
        cf_l5 = (LinearLayout) view.findViewById(R.id.cf_l5);
        cf_l6 = (LinearLayout) view.findViewById(R.id.cf_l6);
//        btn_left = (ImageView) view.findViewById(R.id.btn_left);
//        btn_right = (ImageButton) view.findViewById(R.id.btn_right);
//        btn_left.setOnClickListener(this);
//        btn_right.setOnClickListener(this);
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

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rb_map:
                intent = new Intent(getActivity(), RankingActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.rb_classification:
                showTypeLayout();
//                tv_title.setText("��ͼ����");
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
                break;
            case R.id.btn_left:

                break;
            case R.id.btn_right:

                break;
            default:
                break;
        }
    }

    private void showTypeLayout() {
        map_ls.setVisibility(View.GONE);
        mp_r1.setVisibility(View.VISIBLE);
    }

    private void hideTypeLayout() {
        map_ls.setVisibility(View.VISIBLE);
        l1.setVisibility(View.VISIBLE);
        mp_r1.setVisibility(View.GONE);
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

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                cancleLodingToast(false);
                isLoading = false;
            }
        });
    }

    protected void survival() {
        hideTypeLayout();
        mapType = "生存";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void decipt() {
        hideTypeLayout();
        mapType = "解密";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void parkour() {
        hideTypeLayout();
        mapType = "跑酷";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void architecture() {
        hideTypeLayout();
        mapType = "建筑";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void pvp() {
        hideTypeLayout();
        mapType = "pvp竞技";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void totle() {
        hideTypeLayout();
        mapType = null;
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), Map_detailsActivity.class);
        Map mapList = (Map) adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.Details), mapList);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
}
