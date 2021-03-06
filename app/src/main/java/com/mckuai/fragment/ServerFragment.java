package com.mckuai.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.utils.ParseResponse;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

public class ServerFragment extends BaseFragment implements View.OnClickListener,ServerAdapter.OnItemClickListener,ServerAdapter.OnServerAddListener {

    private View view;
    private UltimateRecyclerView serverListView;
    private UltimateRecyclerView serverTypeListView;
    private TextView tv_serverRank;
    private TextView tv_serverType;

    private AsyncHttpClient client;
    private MCkuai application;
    private String[] mOrderFileds = {"UpdateTime","onlineNum"};
    private int mOrderFiledIndex = 0;
    private String[] type;
    private String serverType;
    private static  final  String TAG = "ServerFragment";
    private ArrayList<GameServerInfo> serverInfos;
    private PageInfo page;
    private Gson gson;
    private ServerAdapter adapter;
    private ServerTypeAdapter typeAdapter;
    private boolean isSearch = false;


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
        if (isVisibleToUser) {
            if (null == serverListView && null != view) {
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
        if (null != view) {
            serverListView = (UltimateRecyclerView) view.findViewById(R.id.urv_serverList);
            serverTypeListView = (UltimateRecyclerView) view.findViewById(R.id.urv_serverTypeList);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
            tv_serverRank = (TextView) view.findViewById(R.id.tv_server_rank);
            tv_serverType = (TextView) view.findViewById(R.id.tv_server_type);

            int width = application.sp2Px(getActivity(), 20);
            Drawable drawable = getResources().getDrawable(R.drawable.map_ranking);

            drawable.setBounds(0, 0, width, width);
            SpannableString spannableString = new SpannableString("icon 服务器排行");
            spannableString.setSpan(new ImageSpan(drawable), 0, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_serverRank.setText(spannableString);

            drawable = getResources().getDrawable(R.drawable.map_classification);
            drawable.setBounds(0, 0, width, width);
            spannableString = new SpannableString("icon 服务器类型");
            spannableString.setSpan(new ImageSpan(drawable), 0, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_serverType.setText(spannableString);

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

            StaggeredGridLayoutManager typeLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            serverTypeListView.setLayoutManager(typeLayoutManager);
            typeAdapter = new ServerTypeAdapter();
            typeAdapter.setOnItemClickListener(new ServerTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String type) {
                    serverTypeListView.setVisibility(View.GONE);
                    mOrderFiledIndex = 0;
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
            view.findViewById(R.id.tv_server_rank).setOnClickListener(this);
            view.findViewById(R.id.tv_server_type).setOnClickListener(this);
        }
    }

    private void showData(){
        if (!isShowCatche &&isLoading){
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
        if (null != serverTypeListView) {
            serverTypeListView.setVisibility(View.VISIBLE);
//            serverListView.setVisibility(View.GONE);
        }
    }

    private void hideServerType(){
        if (null != serverTypeListView) {
            serverTypeListView.setVisibility(View.GONE);
//            serverListView.setVisibility(View.VISIBLE);
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
        params.put("orderField",mOrderFileds[mOrderFiledIndex]);
        //类型
        if (null != serverType){
            params.put("kinds",serverType);
        }
        //页数
        if (null != page){
            params.put("page",page.getPage()+1);
        }
        params.put("orderType","1");

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
                    //showNotification(3, result.msg,R.id.rl_serverList_Root);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //showNotification(3, "获取数据失败，原因：" + throwable.getLocalizedMessage(), R.id.rl_serverList_Root);
                cancleLodingToast(false);
                isLoading = false;
            }
        });
    }

    @Override
    public void onRightButtonClicked(String searchContent) {
        if (null != searchContent && !searchContent.isEmpty()){
            isSearch = true;
            String url = getString(R.string.interface_domainName) + getString(R.string.interface_map_search);
            final RequestParams params = new RequestParams();
            params.put("type", "server");
            params.put("key", searchContent);
            client.cancelRequests(getActivity(), true);
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //super.onSuccess(statusCode, headers, response);
                    ParseResponse parse = new ParseResponse();
                    ResponseParseResult result = parse.parse(response);
                    isLoading = false;
                    if (result.isSuccess) {
                        cancleLodingToast(true);
                        parseData(result.data);
                        showData();
                    } else {
                        cancleLodingToast(false);
                        showNotification(3, result.msg, R.id.rl_skinRoot);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    isLoading = false;
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                    isLoading = false;
                }
            });
        }
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
            //showNotification(1, "正在获取数据，请稍候！", R.id.rl_serverList_Root);
            return;
        }
        switch (v.getId()){
            case R.id.tv_server_type:
                MobclickAgent.onEvent(getActivity(),"serverType");
                mOrderFiledIndex = 0;
                if (serverTypeListView.getVisibility() == View.GONE){
                    showServerType();
                }
                else {
                    hideServerType();
                }
                break;
            case R.id.tv_server_rank:
                MobclickAgent.onEvent(getActivity(),"serverRank");
                if (0 == mOrderFiledIndex){
                    mOrderFiledIndex = 1;
                }
                else {
                    mOrderFiledIndex = 0;
                }
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
    }

    @Override
    public void afterServerInfoAdded(int version) {
        MobclickAgent.onEvent(getActivity(), "startGame_server");
        if (0 < version){
            if(!GameUntil.startGame(getActivity(),version)){
                downloadGame(version);
            }
        }
        else {
            GameUntil.startGame(getActivity());
        }

    }

    private void downloadGame(final int version){
        String url = "";
        String msgText = null;
        switch (version){
            case 10:
                url = "http://softdown.mckuai.com:8081/mcpe0.10.5.apk";
                msgText = "此服务器需要安装0.10版我的世界。\n是否下载安装？";
                break;
            case 11:
                url = "http://softdown.mckuai.com:8081/mcpe0.11.1.apk";
                msgText = "此服务器需要安装0.11版我的世界。\n是否下载安装？";
                break;
        }
        final String downloadUrl = url;
        showAlert("提示", msgText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DLManager.getInstance(getActivity()).dlStart(downloadUrl, MCkuai.getInstance().getGameDownloadDir(), new DLTaskListener() {
                    @Override
                    public void onStart(String fileName, String url) {
                        super.onStart(fileName, url);
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        installGame(file);
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);

                        showError(version, error);
                    }
                });
            }
        });
    }

    private void showError(final int version,String msg){
        showAlert("下载失败", "下载游戏失败，原因：" + msg + "\n是否重新下载？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadGame(version);
            }
        });
    }

    private void installGame(final File file){
        showAlert("安装游戏", "游戏下载完成，是否立即安装？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameUntil.installGame(getActivity(), file.getPath());
            }
        });

    }

    @Override
    public boolean onBackKeyPressed() {
        if (isSearch){
            isLoading = false;
            if (null != serverInfos) {
                page.setPage(0);
                serverInfos.clear();
                serverInfos = null;
            }
            showData();
        }
        return super.onBackKeyPressed();
    }

    @Override
    public void onPageShow() {
        MainActivity.setRightButtonView(0, R.drawable.btn_search_selector);
    }
}
