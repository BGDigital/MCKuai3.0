package com.mckuai.fragment;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.SkinAdapter;
import com.mckuai.bean.Map;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.ResponseParseResult;
import com.mckuai.bean.SkinBean;
import com.mckuai.bean.SkinItem;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.imc.ServerDetailsActivity;
import com.mckuai.imc.SkinDetailedActivity;
import com.mckuai.service_and_recevier.DownloadProgressRecevier;
import com.mckuai.utils.ParseResponse;
import com.mckuai.widget.fabbutton.FabButton;
import com.thin.downloadmanager.ThinDownloadManager;

import org.apache.http.Header;
import org.json.JSONObject;

import java.lang.Integer;
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
    private long lastUpdateTime = 0;

    private UltimateRecyclerView urv_list;
    private SkinAdapter mAdapter;
    private RecyclerView.LayoutManager manager;

    private MCkuai app = MCkuai.getInstance();
    private AsyncHttpClient mClient;
    private Gson mGson;

    private DownloadProgressRecevier recevier;

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
        if (getUserVisibleHint()) {
            showData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (null != view){
                showData();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (null != recevier) {
            try {
                getActivity().unregisterReceiver(recevier);
                recevier = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView(){
        if (null == urv_list){
            urv_list = (UltimateRecyclerView) view.findViewById(R.id.urv_skinList);
            manager = new LinearLayoutManager(getActivity());
            urv_list.setLayoutManager(manager);
            mAdapter = new SkinAdapter(getActivity());
            mAdapter.setOnItemClickListener(this);DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
            urv_list.addItemDecoration(dividerItemDecoration);
            urv_list.enableLoadmore();
            urv_list.setAdapter(mAdapter);
            urv_list.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
                @Override
                public void loadMore(int i, int i1) {
                    if (null != mPage && !mPage.EOF()) {
                        loadData();
                        showData();
                    }
                }
            });
        }

        urv_list.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mPage) {
                    mPage.setPage(0);
                    skins.clear();
                    loadData();
                    showData();
                }
            }
        });
    }

    private void initReciver() {
        if (null == recevier) {
            recevier = new DownloadProgressRecevier() {
                @Override
                public void onProgress(int resType,String resId, int progress) {
                    if (resType != 2){
                        return;
                    }
                    if (null != skins  && !skins.isEmpty()) {
                        int i = 0;
                        for (SkinItem item:skins){
                            if (item.getId() == Integer.parseInt(resId)){
                                item.setProgress(progress);
                                long time = System.currentTimeMillis();
                                if (time - lastUpdateTime > 500 || progress == 100){
                                    int count = urv_list.getChildCount();
                                    ViewGroup itemView = (ViewGroup)manager.findViewByPosition(i);
                                    if (null != itemView){
                                        FabButton progressBtn =(FabButton)((ViewGroup) itemView.getChildAt(0)).getChildAt(1);
                                        ImageButton downloadedBtn = (ImageButton)((ViewGroup)itemView.getChildAt(0)).getChildAt(2);
                                        progressBtn.setProgress(progress);
                                        if (100 == progress){
                                            //
                                        }
                                    }
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
        if (null != item) {
            Intent intent = new Intent(getActivity(), SkinDetailedActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("SKIN_ITEM", item);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onAddButtonClicked(SkinItem item) {

    }
}
