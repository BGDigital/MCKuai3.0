package com.mckuai.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.adapter.ForumAdapter;
import com.mckuai.adapter.PostAdapter;
import com.mckuai.bean.ForumBean;
import com.mckuai.bean.ForumInfo;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.Post;
import com.mckuai.bean.PostBaen;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForumFragment extends BaseFragment {

    private View view;
    private PageInfo page;

    private ArrayList<ForumInfo> mForums;
    private PageInfo mCurPageInfo = new PageInfo();
    private ArrayList<Post> mPosts;
    private String[] listGroupType = { "lastChangeTime", "isJing", "isDing" };
    private String curGroupType = listGroupType[0];
    private com.marshalchen.ultimaterecyclerview.UltimateRecyclerView mPostListView;
    private com.marshalchen.ultimaterecyclerview.UltimateRecyclerView mForumsListView;
    private PostAdapter mPostAdapter;
    private ForumAdapter mForumAdapter;
    private LinearLayoutManager mLayoutManager;
    private MCkuai application = MCkuai.getInstance().getInstance();
    private AsyncHttpClient mClient = application.getHttpClient();
    private String TAG = "Forums";
    private Gson mGson = new Gson();
    private boolean isLoading = false;
    private boolean isReadyToShow = false;
    private ForumInfo curForum;

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

    private void showForums()
    {
        if (null != mForums && 0 < mForums.size())
        {
            if (null == mForumAdapter)
            {
                mForumAdapter = new ForumAdapter(mForums);
                mForumsListView.setAdapter(mForumAdapter);
            } else
            {
                mForumAdapter.setData(mForums);
            }

            if (curForum == null)
            {
                curForum = mForums.get(0);
                loadPostListDelay();
//				publishPost.setVisibility(View.GONE);
                showForums();
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
                //mPostListView.setAdapter(mPostAdapter);
            }
            mPostAdapter.setData(mPosts);
            if (mCurPageInfo.getPage() < mCurPageInfo.getPageCount())
            {
                mPostListView.enableLoadmore();
            } else
            {
                mPostListView.disableLoadmore();
            }
//			publishPost.setVisibility(View.VISIBLE);
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
                String result = getData(url);
                if (null != result)
                {
                    parseForumList(null, result);
                    showForums();
                }
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
                            mCurPageInfo = new PageInfo();
                            showForums();
                            if (null == mPosts)
                            {
                                curForum = mForums.get(0);
                                loadPostList(curForum);
                            }
                            cacheData(url, response.toString());
                        }
                    } catch (Exception e)
                    {
                        // TODO: handle exception
//						showNotification("获取板块信息失败,请重试!");
                        Toast.makeText(getActivity(), "获取板块信息失败,请重试!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
            {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, headers, responseString, throwable);
                isLoading = false;
                Toast.makeText(getActivity(), "获取板块信息失败,原因："+throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
        mCurPageInfo.setAllCount(bean.getAllCount());
        mCurPageInfo.setPage(bean.getPage());
        if (1 == mCurPageInfo.getPage())
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
        mClient.get(url, params, new JsonHttpResponseHandler()
        {
            /*
             * (non-Javadoc)
             *
             * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
             */
            @Override
            public void onStart()
            {
                // TODO Auto-generated method stub
                super.onStart();
                String result = getData(url, params);
                if (null != result && 10 < result.length())
                {
                    parsePostList(null, null, result);
                    showPosts();
                } else
                {
                    popupLoadingToast(getString(R.string.onloading_hint));
                }
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
                isLoading = false;
                cancleLodingToast(true);
                super.onSuccess(statusCode, headers, response);
                if (null != response && response.has("state"))
                {
                    try
                    {
                        // parsePostList(url,response.getString("dataObject"));
                        if (response.getString("state").equalsIgnoreCase("ok"))
                        {
                            parsePostList(url, params, response.getString("dataObject"));
                            showPosts();
                            return;
                        }
                    } catch (Exception e)
                    {
                        // TODO: handle exception
                    }

                }
                Toast.makeText(getActivity(), "获取帖子列表失败，请重试！", Toast.LENGTH_SHORT).show();
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * com.loopj.android.http.JsonHttpResponseHandler#onFailure(int,
             * org.apache.http.Header[], java.lang.String, java.lang.Throwable)
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
            {
                // TODO Auto-generated method stub
                isLoading = false;
                cancleLodingToast(false);
                super.onFailure(statusCode, headers, responseString, throwable);
//				showNotification("出错啦，原因："+ throwable.getLocalizedMessage());
                Toast.makeText(getActivity(), "出错啦，原因："+ throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostListDelay()
    {
        mHandler.sendMessage(mHandler.obtainMessage(9945));
    }

    Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 9945:
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(5543), 200);
                    break;
                case 5543:
                    loadPostList(curForum);
                    break;
                default:
                    break;
            }
        };
    };

}
