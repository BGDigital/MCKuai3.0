package com.mckuai.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.mckuai.imc.MapActivity;
import com.mckuai.imc.MymapActivity;
import com.mckuai.imc.R;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private Context mContent;
    private Button rb_map, rb_classification, rb_mymap;
    private EditText map_ed;
    private TextView tv_title;
//    private ImageView btn_left;
//    private ImageButton btn_right;
    private ListView map_ls;
    private RelativeLayout mp_r1;
    // private ArrayList<Map> mapList;
    private MapBean mapList;
    private LinearLayout l1, cf_l1, cf_l2, cf_l3, cf_l4, cf_l5, cf_l6;
    private MapAdapter adapter;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private String mapType = null;
    private String orderFiled = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (null == mapList) {
//            mapList = new MapBean();
//            Map map = new Map();
//            mapList.getData().add(map);
//        }

        initView();
        loadData();
    }

    private void showData() {
        adapter = new MapAdapter(getActivity(), mapList.getData());
        map_ls.setAdapter(adapter);
    }

    protected void initView() {
        map_ed = (EditText) view.findViewById(R.id.map_ed);
        rb_map = (Button) view.findViewById(R.id.rb_map);
        rb_classification = (Button) view.findViewById(R.id.rb_classification);
        rb_mymap = (Button) view.findViewById(R.id.rb_mymap);
        map_ls = (ListView) view.findViewById(R.id.map_ls);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        client = MCkuai.getInstance().mClient;
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
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rb_map:
                intent = new Intent(getActivity(), MapActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.rb_classification:
                map_ls.setVisibility(View.GONE);
                l1.setVisibility(View.GONE);
                mp_r1.setVisibility(View.VISIBLE);
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
        Log.e("url:",url+"&"+params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                popupLoadingToast("download");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
                        showData();
                    } else {
                        showNotification(0, "no data!!", R.id.l1);
                    }
                } else {
                    showNotification(0, "load data error!!", R.id.l1);
                }

            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                cancleLodingToast(false);
            }
        });
    }

    protected void survival() {
        mapType = "生存";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void decipt() {
        mapType = "解密";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void parkour() {
        mapType = "跑酷";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void architecture() {
        mapType = "建筑";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void pvp() {
        mapType = "pvp竞技";
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }

    protected void totle() {
        mapType = null;
        mapList.getData().clear();
        mapList.getPageBean().setPage(0);
        loadData();
    }
}
