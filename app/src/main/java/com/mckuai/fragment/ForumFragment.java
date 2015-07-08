package com.mckuai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.adapter.ForumAdapter;
import com.mckuai.adapter.PostAdapter;
import com.mckuai.bean.ForumBean;
import com.mckuai.bean.ForumInfo;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.Post;
import com.mckuai.bean.PostBaen;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.PublishPostActivity;
import com.mckuai.imc.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForumFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener,ForumAdapter.OnItemClickListener,View.OnClickListener {

    private View view;
    private PageInfo page;

    private ArrayList<ForumInfo> mForums;
    //private PageInfo mCurPageInfo = new PageInfo();
    private ArrayList<Post> mPosts;
    private String[] listGroupType = { "lastChangeTime", "isJing", "isDing" };
    private String curGroupType = listGroupType[0];

    private com.marshalchen.ultimaterecyclerview.UltimateRecyclerView mPostListView;
    private com.marshalchen.ultimaterecyclerview.UltimateRecyclerView mForumsListView;
    private PostAdapter mPostAdapter;
    private ForumAdapter mForumAdapter;

//    private LinearLayoutManager mLayoutManager;
    private MCkuai application = MCkuai.getInstance().getInstance();
    private AsyncHttpClient mClient = application.getHttpClient();
    private String TAG = "Forums";
    private Gson mGson = new Gson();
    private boolean isLoading = false;
    private boolean isReadyToShow = false;
    private ForumInfo curForum;
    private boolean isAllowedLoadMore = false;
    private ImageView btn_publish = application.getBtn_publish();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if( null == view){
            view = inflater.inflate(R.layout.fragment_forum, container, false);
            page = new PageInfo();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == mPostListView) {
            initView();
        }
        showForums();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            showForums();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancleLodingToast(false);
    }

    private void initView(){
        if (null == view || null != mPostListView)
        {
            return;
        }
        mForumsListView = (UltimateRecyclerView) view.findViewById(R.id.rv_forums);
        mPostListView = (UltimateRecyclerView) view.findViewById(R.id.rv_postList);
        LinearLayoutManager forumLayoutManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager postLayoutManager = new LinearLayoutManager(getActivity());
        forumLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        postLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mForumsListView.setLayoutManager(forumLayoutManager);
        mPostListView.setLayoutManager(postLayoutManager);
        mPostListView.setHasFixedSize(true);
        mPostListView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int i, int i1) {
                if (!page.EOF()) {
                    loadPostList(curForum);
                } else {
                    showNotification(1, "没有更多内容了！", R.id.rl_post_root);
                }
            }
        });
        mPostListView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page.setPage(0);
                mPosts.clear();
                loadPostList(curForum);
            }
        });
        mPostListView.enableLoadmore();

        mForumAdapter = new ForumAdapter();
        mPostAdapter = new PostAdapter(getActivity());
        mForumsListView.setAdapter(mForumAdapter);
        mPostListView.setAdapter(mPostAdapter);
        mForumAdapter.setOnItemClickListener(this);

        ((RadioGroup) view.findViewById(R.id.rg_indicator)).setOnCheckedChangeListener(this);
        btn_publish.setOnClickListener(this);
    }

    private void showForums()
    {
        if (application.fragmentIndex != 3){
            return;
        }
        if (null != mForums &&  !mForums.isEmpty())
        {
            if (null == mForumAdapter)
            {
                mForumAdapter = new ForumAdapter();
                mForumsListView.setAdapter(mForumAdapter);
            } else
            {
                mForumAdapter.setData(mForums);
            }
            //加载帖子列表
            if (curForum == null)
            {
                curForum = mForums.get(0);
                loadPostList(curForum);
//				publishPost.setVisibility(View.GONE);
//                showForums();
            }
        } else
        {
            loadForumList();
        }
    }

    private void showPosts()
    {
        if (null != mPosts && !mPosts.isEmpty())
        {
            if (null == mPostAdapter)
            {
                mPostAdapter = new PostAdapter(getActivity());
            }
            mPostAdapter.setData(mPosts);
        }
    }


    private void loadForumList()
    {
        if (isLoading)
        {
            return;
        }
        isLoading = true;
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_forumList);
        mClient.get(url, new JsonHttpResponseHandler()
        {
            @Override
            public void onStart()
            {
                // TODO Auto-generated method stub
                super.onStart();
                if (isCacheEnabled) {
                    String result = getData(url);
                    if (null != result) {
                        parseForumList(null, result);
                        showForums();
                        isCacheEnabled = false;
                        return;
                    }
                }
                popupLoadingToast(getString(R.string.onloading_hint));
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int,
             * org.apache.http.Header[], org.json.JSONObject)
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, response);
                isLoading = false;
                if (null != response && response.has("state"))
                {
                    try
                    {
                        if (response.getString("state").equalsIgnoreCase("ok"))
                        {
                            parseForumList(url, response.toString());
                            cacheData(url, response.toString());
                        }
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                        showNotification(2,"获取板块信息失败,请重试!！",R.id.rl_post_root);
                    }
                    page = new PageInfo();
                    showForums();
                    if (null == mPosts)
                    {
                        curForum = mForums.get(0);
                        loadPostList(curForum);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                isLoading = false;
                showNotification(2, "获取板块信息失败,原因：" + throwable.getLocalizedMessage(), R.id.rl_post_root);
            }
        });
    }

    private void parseForumList(String url, String result)
    {
        try
        {
            ForumBean bean = mGson.fromJson(result, ForumBean.class);
            this.mForums = bean.getDataObject();
        } catch (Exception e)
        {
            // TODO: handle exception
        }

        if (null != url)
        {
            cacheData(url, result);
        }
    }

    private void parsePostList(String url, RequestParams params, String result)
    {
        PostBaen bean = mGson.fromJson(result, PostBaen.class);
        page.setAllCount(bean.getAllCount());
        page.setPage(bean.getPage());
        page.setPageSize(bean.getPageSize());
        if (1 == page.getPage())
        {
            if (null != url && null != params)
            {
                cacheData(url, params, result);
            }
            if (null != mPosts)
            {
                mPosts.clear();
            } else
            {
                mPosts = new ArrayList<Post>(20);
            }
        }
        mPosts.addAll(bean.getdata());
    }

    private void loadPostList(ForumInfo forumInfo)
    {
        if (isLoading)
        {
            return;
        }
        isLoading = true;
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_postList);
        final RequestParams params = new RequestParams();
        params.put("forumId", forumInfo.getId());
        params.put("page", page.getNextPage());
        params.put("type", curGroupType);
        Log.e(TAG, url + "&" + params.toString());
        mClient.get(url, params, new JsonHttpResponseHandler() {
            /*
             * (non-Javadoc)
             *
             * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
             */
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                if (isSecondCacheEnable) {
                    String result = getData(url, params);
                    if (null != result && 10 < result.length()) {
                        parsePostList(null, null, result);
                        showPosts();
                        isSecondCacheEnable = false;
                        return;
                    }
                }
                popupLoadingToast(getString(R.string.onloading_hint));
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int,
             * org.apache.http.Header[], org.json.JSONObject)
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // TODO Auto-generated method stub
                isLoading = false;
                cancleLodingToast(true);
                super.onSuccess(statusCode, headers, response);
                if (null != response && response.has("state")) {
                    try {
                        if (response.getString("state").equalsIgnoreCase("ok")) {
                            parsePostList(url, params, response.getString("dataObject"));
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    showPosts();
                    return;
                }
                showNotification(2, "获取帖子列表失败，请重试！", R.id.rl_post_root);
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * com.loopj.android.http.JsonHttpResponseHandler#onFailure(int,
             * org.apache.http.Header[], java.lang.String, java.lang.Throwable)
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO Auto-generated method stub
                isLoading = false;
                cancleLodingToast(false);
                super.onFailure(statusCode, headers, responseString, throwable);
                showNotification(2, "出错啦，原因：" + throwable.getLocalizedMessage(), R.id.rl_post_root);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_lastPost:
                curGroupType = listGroupType[0];
                showPosts();
                break;
            case R.id.rb_essencePost:
                curGroupType = listGroupType[1];

                break;
            case R.id.rb_topPost:
                curGroupType = listGroupType[2];
                break;
        }
        page.setPage(0);
        loadPostList(curForum);
    }

    @Override
    public void onItemClick(ForumInfo forumInfo) {
        page.setPage(0);
        mForumAdapter.notifyDataSetChanged();
        loadPostList(forumInfo);
        this.curForum = forumInfo;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_titlebar_right:
                if (null != mForums && !mForums.isEmpty()) {
                    Intent intent = new Intent(getActivity(), PublishPostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("FORUM_LIST",mForums);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {

                }
                break;
        }
    }
}
