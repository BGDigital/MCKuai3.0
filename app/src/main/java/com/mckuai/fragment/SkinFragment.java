package com.mckuai.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.SkinAdapter;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.ResponseParseResult;
import com.mckuai.bean.SkinBean;
import com.mckuai.bean.SkinItem;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.utils.ParseResponse;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class SkinFragment extends BaseFragment implements SkinAdapter.OnItemClickListener {
    private View view;
    private ArrayList<SkinItem> skins;
    private PageInfo mPage;
    private String[] fileds = {"InsertTime","DownNum"};
    private String mOrderFiled = fileds[0];
    private int mOrderType = 1;

    private UltimateRecyclerView urv_list;
    private SkinAdapter mAdapter;

    private MCkuai app = MCkuai.getInstance();
    private AsyncHttpClient mClient;
    private Gson mGson;

    public SkinFragment(){
        setmTitle("皮肤");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view){
            view =inflater.inflate(R.layout.fragment_skin, container, false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView(){
        if (null == urv_list){
            urv_list = (UltimateRecyclerView) view.findViewById(R.id.urv_skinList);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
            urv_list.setLayoutManager(manager);
            mAdapter = new SkinAdapter();
            mAdapter.setOnItemClickListener(this);DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
            urv_list.addItemDecoration(dividerItemDecoration);
            urv_list.enableLoadmore();
            urv_list.setAdapter(mAdapter);
            urv_list.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
                @Override
                public void loadMore(int i, int i1) {
                    if (null != mPage && !mPage.EOF()){
                        loadData();
                        showData();
                    }
                }
            });
        }

        urv_list.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mPage){
                    mPage.setPage(0);
                    skins.clear();
                    loadData();
                    showData();
                }
            }
        });
    }

    private void showData(){
        if (null == urv_list){
            initView();
        }
        if (null == skins || null == mPage){
            loadData();
            return;
        }
        mAdapter.setData(skins);
    }

    private void loadData(){
        if (isLoading){
            return;
        }

        if (null == mClient){
            mClient = app.mClient;
        }

        final RequestParams params = new RequestParams();
        params.add("orderField",mOrderFiled);
        params.add("orderType",mOrderType+"");

        if (null != mPage){
            params.add("page",mPage.getNextPage()+"");
        }

        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_skinlist);

        mClient.post(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                if (isCacheEnabled && null!= mPage && 1 == mPage.getNextPage()){
                    String data = getData(url,params);
                    if (null != data && 10 < data.length()){
                        parseData(data);
                        showData();
                    }
                }
                else {
                    isLoading = true;
                    popupLoadingToast("正在加载列表！");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ParseResponse parse = new ParseResponse();
                ResponseParseResult result = parse.parse(response);
                if (result.isSuccess){
                    cancleLodingToast(true);
                    parseData(result.data);
                    showData();
                    if (null != mPage && 1 == mPage.getPage()){
                        cacheData(url, params, result.data);
                        isCacheEnabled = false;
                    }
                }
                else {
                    cancleLodingToast(false);
                    showNotification(3,result.msg,R.id.rl_skinRoot);
                }
                isLoading = false;

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                showNotification(3, "获取数据失败，原因：" + throwable.getLocalizedMessage(), R.id.rl_skinRoot);
                cancleLodingToast(false);
                isLoading = false;
            }
        });
    }

    private void parseData(String data){
        if (null != data && 10 < data.length()){
            if (null == mGson){
                mGson = new Gson();
            }

            SkinBean bean = mGson.fromJson(data,SkinBean.class);
            if (null != bean &&null != bean.getData() && null != bean.getPageBean()){
                if (1==bean.getPageBean().getPage()){
                    if (null == skins){
                        skins = new ArrayList<>(bean.getData().size()+1);
                    }
                    else {
                        skins.clear();
                    }
                }
                mPage = bean.getPageBean();
                skins.addAll((Collection<? extends SkinItem>) bean.getData().clone());
            }
        }
    }

    @Override
    public void onItemClicked(SkinItem item) {

    }

    @Override
    public void onAddButtonClicked(SkinItem item) {

    }
}
