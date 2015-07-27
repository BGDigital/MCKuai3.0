package com.mckuai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.ServerAdapter;
import com.mckuai.adapter.ServerTypeAdapter;
import com.mckuai.bean.GameServerInfo;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.ResponseParseResult;
import com.mckuai.bean.ServerBean;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.MainActivity;
import com.mckuai.imc.R;
import com.mckuai.imc.ServerDetailsActivity;
import com.mckuai.utils.GameUntil;
import com.mckuai.utils.ParseResponse;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class ServerFragment extends BaseFragment implements View.OnClickListener,ServerAdapter.OnItemClickListener,ServerAdapter.OnServerAddListener {

    private View view;
    private UltimateRecyclerView serverListView;
    private UltimateRecyclerView serverTypeListView;
    private RelativeLayout rl_serverTypeLayout;
    private LinearLayout ll_filter;
    private Spinner spinner;
    private AsyncHttpClient client;
    private MCkuai application;
    private boolean isOrderByDownload = false;
    private String[] type;
    private String serverType;
    private static  final  String TAG = "ServerFragment";
    private ArrayList<GameServerInfo> serverInfos;
    private PageInfo page;
    private Gson gson;
    private ServerAdapter adapter;
    private ServerTypeAdapter typeAdapter;
    private boolean isLoadmoreAlowed = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view){
            view = inflater.inflate(R.layout.fragment_server, container, false);
            //initView();
        }
        setmTitle("联机");
        application = MCkuai.getInstance();
        type = getResources().getStringArray(R.array.server_Type);
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
        else {
            isOrderByDownload=false;
            if (null != ll_filter) {
                setFiterLayoutView(true);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancleLodingToast(false);
    }

    private void initView(){
        serverListView = (UltimateRecyclerView) view.findViewById(R.id.urv_serverList);
        serverTypeListView = (UltimateRecyclerView) view.findViewById(R.id.urv_serverTypeList);
        rl_serverTypeLayout = (RelativeLayout) view.findViewById(R.id.rl_serverType);
        ll_filter = (LinearLayout)view.findViewById(R.id.ll_filter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        serverListView.setLayoutManager(manager);

        adapter = new ServerAdapter();
        adapter.setOnItemClickListener(this);
        adapter.SetOnServerAddListener(this);
        serverListView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        serverListView.addItemDecoration(dividerItemDecoration);
        serverListView.enableLoadmore();
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

        StaggeredGridLayoutManager typeLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        serverTypeListView.setLayoutManager(typeLayoutManager);
        typeAdapter = new ServerTypeAdapter();
        typeAdapter.setOnItemClickListener(new ServerTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String type) {
                rl_serverTypeLayout.setVisibility(View.GONE);
                if (type.trim().equals("全部")) {
                    serverType = null;
                } else {
                    serverType = type.trim();
                }
                if (null != serverInfos) {
                    serverInfos.clear();
                }
                page = null;
                loadData();
            }
        });
        typeAdapter.setData(getResources().getStringArray(R.array.server_Type));
        serverTypeListView.setAdapter(typeAdapter);

        spinner = application.getSpinner();
        String[] items = getResources().getStringArray(R.array.server_Type);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, items);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.server_Type, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position) {
                    serverType = null;
                } else {
                    serverType = type[position].trim();
                }
                if (null != serverInfos) {
                    serverInfos.clear();
                }
                page = null;
                loadData();
                rl_serverTypeLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.findViewById(R.id.ll_serverRank).setOnClickListener(this);
        view.findViewById(R.id.ll_serverType).setOnClickListener(this);

    }

    private void setLeftButtonListener(){
        MainActivity.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOrderByDownload = false;
                setFiterLayoutView(true);
            }
        },null);
    }

    private void  setFiterLayoutView(boolean isVisible){
        MainActivity.setLeftButtonView(isOrderByDownload);
        if (isVisible){
            ll_filter.setVisibility(View.VISIBLE);
        }
        else {
            ll_filter.setVisibility(View.GONE);
        }
    }

    private void showData(){
        if (!isShowCatche &&(isLoading || 2 != application.fragmentIndex)){
            return;
        }

        setLeftButtonListener();

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
        if (null != rl_serverTypeLayout) {
            rl_serverTypeLayout.setVisibility(View.GONE);
        }
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
                    if (null != page && page.getPage() == 1){
                        cacheData(url,params,result.data);
                        isCacheEnabled = false;
                    }
                } else {
                    cancleLodingToast(false);
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
        if (null != bean && null != bean.getPageBean() && null != bean.getData()){

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
        if (null != bean && null != bean.getPageBean() && null != bean.getData()){

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
            showData();
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
                MobclickAgent.onEvent(getActivity(),"serverType");
                isOrderByDownload = false;
                if (rl_serverTypeLayout.getVisibility() == View.GONE){
                    showServerType();
                }
                else {
                    hideServerType();
                }
                break;
            case R.id.ll_serverRank:
                MobclickAgent.onEvent(getActivity(),"serverRank");
                isOrderByDownload = !isOrderByDownload;
                setFiterLayoutView(!isOrderByDownload);
                if (null != page){
                page.setPage(0);
                 }
                serverType = null;
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
        MobclickAgent.onEvent(getActivity(),"startGame_server");
        GameUntil.startGame(getActivity());
    }
}
