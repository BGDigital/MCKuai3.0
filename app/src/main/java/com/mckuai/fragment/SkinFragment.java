package com.mckuai.fragment;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.mckuai.imc.MainActivity;
import com.mckuai.imc.R;
import com.mckuai.imc.ServerDetailsActivity;
import com.mckuai.imc.SkinDetailedActivity;
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.MCSkinManager;
import com.mckuai.mctools.WorldUtil.OptionUntil;
import com.mckuai.service_and_recevier.DownloadProgressRecevier;
import com.mckuai.utils.ParseResponse;
import com.mckuai.widget.fabbutton.FabButton;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.Collection;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SkinFragment extends BaseFragment implements SkinAdapter.OnItemClickListener {
    private View view;
    private ArrayList<SkinItem> skins;
    private PageInfo mPage;
    private String[] fileds = {"InsertTime", "DownNum"};
    private String mOrderFiled = fileds[0];
    private int mOrderType = 1;
    private long lastUpdateTime = 0;

    private UltimateRecyclerView urv_list;
    private SkinAdapter mAdapter;
    private RecyclerView.LayoutManager manager;

    private MCSkinManager mSkinManager;

    private MCkuai app = MCkuai.getInstance();
    private AsyncHttpClient mClient;
    private Gson mGson;

    private DownloadProgressRecevier recevier;

    public SkinFragment() {
        setmTitle("皮肤");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_skin, container, false);
        }
        if (null == mSkinManager) {
            mSkinManager = MCkuai.getInstance().getSkinManager();
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
        if (isVisibleToUser) {
            if (null != view) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView() {
        if (null == urv_list) {
            urv_list = (UltimateRecyclerView) view.findViewById(R.id.urv_skinList);
            manager = new LinearLayoutManager(getActivity());
            urv_list.setLayoutManager(manager);
            mAdapter = new SkinAdapter(getActivity());
            mAdapter.setOnItemClickListener(this);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
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
                public void onProgress(int resType, String resId, int progress) {
                    if (resType != 2) {
                        return;
                    }
                    if (null != skins && !skins.isEmpty()) {
                        int i = 0;
                        for (SkinItem item : skins) {
                            if (item.getId() == Integer.parseInt(resId)) {
                                item.setProgress(progress);
                                ViewGroup itemView = (ViewGroup) manager.findViewByPosition(i);
                                if (null != itemView) {
                                    FabButton progressBtn = (FabButton) (itemView.getChildAt(1));
                                    progressBtn.setProgress(progress);
                                    if (100 == progress) {
                                        updateDownloadCount(resId);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showData() {

        if (null == urv_list) {
            initView();
        }
        MainActivity.setRightButtonView(false);
        if (null == skins || null == mPage) {
            loadData();
            return;
        }
        initReciver();
        mergeData();
        mAdapter.setData(skins);
    }

    private void mergeData() {
        ArrayList<SkinItem> downloadSkins = mSkinManager.getDownloadSkins();
        if (null != downloadSkins && !downloadSkins.isEmpty() && null != skins && !skins.isEmpty()) {
            for (SkinItem item : downloadSkins) {
                for (SkinItem skin : skins) {
                    if (skin.getId() == item.getId()) {
                        skin.setProgress(100);
                        break;
                    }
                }
            }
        }
    }

    private void loadData() {
        if (isLoading) {
            return;
        }

        if (null == mClient) {
            mClient = app.mClient;
        }

        final RequestParams params = new RequestParams();
        params.add("orderField", mOrderFiled);
        params.add("orderType", mOrderType + "");

        if (null != mPage) {
            params.add("page", mPage.getNextPage() + "");
        }

        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_skinlist);

        mClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isCacheEnabled && null != mPage && 1 == mPage.getNextPage()) {
                    String data = getData(url, params);
                    if (null != data && 10 < data.length()) {
                        parseData(data);
                        showData();
                    }
                } else {
                    isLoading = true;
                    popupLoadingToast("正在加载列表！");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ParseResponse parse = new ParseResponse();
                ResponseParseResult result = parse.parse(response);
                if (result.isSuccess) {
                    cancleLodingToast(true);
                    parseData(result.data);
                    showData();
                    if (null != mPage && 1 == mPage.getPage()) {
                        cacheData(url, params, result.data);
                        isCacheEnabled = false;
                    }
                } else {
                    cancleLodingToast(false);
                    showNotification(3, result.msg, R.id.rl_skinRoot);
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

    private void parseData(String data) {
        if (null != data && 10 < data.length()) {
            if (null == mGson) {
                mGson = new Gson();
            }

            SkinBean bean = mGson.fromJson(data, SkinBean.class);
            if (null != bean && null != bean.getData() && null != bean.getPageBean()) {
                if (1 == bean.getPageBean().getPage()) {
                    if (null == skins) {
                        skins = new ArrayList<>(bean.getData().size() + 1);
                    } else {
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
        if (null != item){
            switch (item.getProgress()){
                case -1:
                    MobclickAgent.onEvent(getActivity(), "downloadSkin");
                    Intent intent = new Intent();
                    intent.setAction("com.mckuai.downloadservice");
                    intent.setPackage(getActivity().getPackageName());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SKIN", item);
                    intent.putExtras(bundle);
                    getActivity().startService(intent);
                    break;
                case 100:
                    OptionUntil.setSkin(2);//配置成自定义皮肤
                    if (null == mSkinManager){
                        mSkinManager = MCkuai.getInstance().getSkinManager();
                    }
                    mSkinManager.moveToGame(item);
                    MobclickAgent.onEvent(getActivity(), "startGame_skin");
                    if(!GameUntil.startGame(getActivity(),11)){
                        downloadGame(11);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void updateDownloadCount(String id) {
        String url = getString(R.string.interface_domainName) + getString(R.string.interface_skinupdatecount) + "&id=" + id;
        AsyncHttpClient client = MCkuai.getInstance().mClient;
        client.post(url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("","更新成功！");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("", "更新下载计数失败，原因：" + throwable.getLocalizedMessage());
            }
        });

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
}
