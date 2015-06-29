package com.mckuai.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.bean.GameServerInfo;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.ResponseParseResult;
import com.mckuai.bean.ServerBean;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.until.ParseResponse;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class ServerFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private UltimateRecyclerView serverListView;
    private UltimateRecyclerView serverTypeListView;
    private RelativeLayout rl_serverTypeLayout;


    private AsyncHttpClient client;
    private MCkuai application;
    private boolean isOrderByDownload = false;
    private String serverType;
    private static  final  String TAG = "ServerFragment";
    private ArrayList<GameServerInfo> serverInfos;
    private PageInfo page;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view){
            view = inflater.inflate(R.layout.fragment_server, container, false);
            initView();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == application){
            application = MCkuai.getInstance();
        }
        showData();
    }

    private void initView(){
        serverListView = (UltimateRecyclerView) view.findViewById(R.id.urv_serverList);
        view.findViewById(R.id.ll_serverRank).setOnClickListener(this);
        view.findViewById(R.id.ll_serverType).setOnClickListener(this);
    }

    private void showData(){

    }

    private void showServerType(){

    }

    private void hideServerType(){

    }

    private void LoadData(){
        if (isLoading){
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

        String url = getString(R.string.interface_domainName) + getString(R.string.interface_serverlist);
        Log.w(TAG, url + "&" + params.toString());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                popupLoadingToast("正在加载列表！");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                cancleLodingToast(false);
                isLoading = false;
                ParseResponse parse = new ParseResponse();
                ResponseParseResult result = parse.parse(response);
                if (result.isSuccess) {
                    cancleLodingToast(true);
                    parseData(result);
                    showData();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_serverType:
                if (isLoading)
                    showNotification(1,"正在获取数据，请稍候！",R.id.rl_serverList_Root);
                else {
                    if (null == serverType){
                        showServerType();
                    }
                    else {
                        serverType = null;
                    }
                }
                break;
            case R.id.ll_serverRank:
                isOrderByDownload = !isOrderByDownload;
                LoadData();
                break;
        }
    }
}
