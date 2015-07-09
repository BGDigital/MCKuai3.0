package com.mckuai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.ServerAdapter;
import com.mckuai.bean.GameServerInfo;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.ResponseParseResult;
import com.mckuai.bean.ServerBean;
import com.mckuai.imc.Export_mapActivity;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.imc.ServerDetailsActivity;
import com.mckuai.until.GameUntil;
import com.mckuai.until.ParseResponse;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class ServerFragment extends BaseFragment implements View.OnClickListener,ServerAdapter.OnItemClickListener,ServerAdapter.OnServerAddListener {

    private View view;
    private UltimateRecyclerView serverListView;
    private UltimateRecyclerView serverTypeListView;
    private RelativeLayout rl_serverTypeLayout;
    private Spinner spinner;
    private AsyncHttpClient client;
    private MCkuai application;
    private boolean isOrderByDownload = false;
    private String serverType;
    private static  final  String TAG = "ServerFragment";
    private ArrayList<GameServerInfo> serverInfos;
    private PageInfo page;
    private Gson gson;
    private ServerAdapter adapter;
    private boolean isLoadmoreAlowed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view){
            view = inflater.inflate(R.layout.fragment_server, container, false);
            //initView();
        }
        application = MCkuai.getInstance();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == serverListView){
            initView();
        }
        showData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && null != view){
            if (null == serverListView){
                initView();
            }
            showData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancleLodingToast(false);
    }

    private void initView(){
        serverListView = (UltimateRecyclerView) view.findViewById(R.id.urv_serverList);
        rl_serverTypeLayout = (RelativeLayout) view.findViewById(R.id.rl_serverType);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        serverListView.setLayoutManager(manager);

        adapter = new ServerAdapter();
        adapter.setOnItemClickListener(this);
        adapter.SetOnServerAddListener(this);
        serverListView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
        serverListView.addItemDecoration(dividerItemDecoration);serverListView.enableLoadmore();
        serverListView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int i, int i1) {
                loadData();
            }
        });

        serverListView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != page) {
                    page.setPage(0);
                }
                loadData();
            }
        });

        spinner = application.getSpinner();
        String[] items = getResources().getStringArray(R.array.server_Type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, items);
        spinner.setAdapter(adapter);



        view.findViewById(R.id.ll_serverRank).setOnClickListener(this);
        view.findViewById(R.id.ll_serverType).setOnClickListener(this);
    }

    private void showData(){
        if (!isShowCatche &&(isLoading || 2 != application.fragmentIndex)){
            return;
        }

        if (null == serverInfos){
            loadData();
            return;
        }

        if (0 == adapter.getItemCount()){
            adapter.setData(serverInfos);
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

    private void showServerType(){
        rl_serverTypeLayout.setVisibility(View.VISIBLE);
    }

    private void hideServerType(){
        rl_serverTypeLayout.setVisibility(View.GONE);
    }

    private void loadData(){
        if (isLoading || (null != page && page.EOF())){
            return;
        }
        else {
            isLoading = true;
        }
        if (null == client){
            client = application.getHttpClient();
        }


        final RequestParams params = new RequestParams();
        //按在线人数排序
        if (isOrderByDownload){
            params.put("orderField","onlineNum");
        }
        //类型
        if (null != serverType){
            params.put("kinds",serverType);
        }
        //页数
        if (null != page){
            params.put("page",page.getPage()+1);
        }

        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_serverlist);
        Log.w(TAG, url + "&" + params.toString());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isCacheEnabled){
                    String result = getData(url,params);
                    if (null != result){
                        parseData(result);
                        showData();
                        isShowCatche = false;
                        return;
                    }
                }
                popupLoadingToast("正在加载列表！");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                isLoading = false;
                ParseResponse parse = new ParseResponse();
                ResponseParseResult result = parse.parse(response);
                if (result.isSuccess) {
                    cancleLodingToast(true);
                    parseData(result);
                    showData();
                    if (page.getPage() == 1){
                        cacheData(url,params,result.data);
                        isCacheEnabled = false;
                    }
                } else {
                    showNotification(3, result.msg,R.id.rl_serverList_Root);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                showNotification(3, "获取数据失败，原因：" + throwable.getLocalizedMessage(), R.id.rl_serverList_Root);
                cancleLodingToast(false);
                isLoading = false;
            }
        });
    }

    private void parseData(ResponseParseResult result){
        if (null == gson){
            gson =new Gson();
        }

        ServerBean bean = gson.fromJson(result.data,ServerBean.class);
        if (null != bean){

            if (bean.getPageBean().getPage() == 1){
                if (null == serverInfos){
                    serverInfos = new ArrayList<>(20);
                }
                else {
                    serverInfos.clear();
                }
            }

            page =  bean.getPageBean();
            serverInfos.addAll((Collection<? extends GameServerInfo>) bean.getData().clone());
        }

    }

    private void parseData(String data){
        if (null == gson){
            gson = new Gson();
        }

        ServerBean bean = gson.fromJson(data,ServerBean.class);
        if (null != bean){

            if (bean.getPageBean().getPage() == 1){
                if (null == serverInfos){
                    serverInfos = new ArrayList<>(20);
                }
                else {
                    serverInfos.clear();
                }
            }

            page =  bean.getPageBean();
            serverInfos.addAll((Collection<? extends GameServerInfo>) bean.getData().clone());
        }
    }

    @Override
    public void onClick(View v) {
        if (isLoading){
            showNotification(1,"正在获取数据，请稍候！",R.id.rl_serverList_Root);
            return;
        }
        switch (v.getId()){
            case R.id.ll_serverType:
                    if (null == serverType){
                        showServerType();
                    }
                    else {
                        serverType = null;
                    }
                break;
            case R.id.ll_serverRank:
                isOrderByDownload = !isOrderByDownload;
                if (null != page){
                page.setPage(0);
                 }
                loadData();
                break;
        }
    }

    @Override
    public void onItemClick(GameServerInfo gameServerInfo) {
        if (null != gameServerInfo){
            Intent intent = new Intent(getActivity(), ServerDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("SERVER_INFO",gameServerInfo);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
        else
        {

        }
    }

    @Override
    public void afterServerInfoAdded() {
        GameUntil.startGame(getActivity());
    }
}
