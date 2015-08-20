package com.mckuai.imc;

import com.mckuai.widget.slidingmenu.SlidingMenu;

import org.apache.http.Header;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;
import com.mckuai.adapter.PostAdapter;
import com.mckuai.adapter.UserAdapter;
import com.mckuai.bean.MCUser;
import com.mckuai.bean.PageInfo;
import com.mckuai.bean.Post;
import com.mckuai.bean.PostBaen;
import com.mckuai.bean.UserBaen;
import com.mckuai.fragment.MCSildingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnEditorActionListener, UltimateRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private UltimateRecyclerView urv_postlistView;
    private UltimateRecyclerView urv_userlistView;
//    private LinearLayout userLayout;
    private EditText edt_search;
    private ImageButton btn_search;
    private TextView tv_title;
    private ImageView btn_return;
    private View selectView;
    private SlidingMenu mySlidingMenu;

    private AsyncHttpClient mClient;
    private ImageLoader mLoader;
    private DisplayImageOptions mOptions;
    private Gson gson;
    private MCSildingMenu menu;
    private UserAdapter userAdapter;
    private PostAdapter postAdapter;
//    private StaggeredGridLayoutManager layoutManager;

    private boolean isLoading = false;
    private boolean isHandlePost = true;// true:处理的对象是帖子;false:处理的对象是用户
    private String searchContext;// 搜索的内容，如果为空则为背包
    private PageInfo mUserPage = new PageInfo();
    private PageInfo mPostPage = new PageInfo();
    private MCUser mUser;
    private MCkuai application;
    private ArrayList<Post> posts;
    private ArrayList<MCUser> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchContext = getIntent().getStringExtra(getString(R.string.search_contxt));
        mLoader = ImageLoader.getInstance();
        gson = new Gson();
        application = MCkuai.getInstance();
        mClient = application.mClient;
        mOptions = application.getCircleOption();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart("背包与搜索");
        initView();
        if (null == mySlidingMenu) {
            initSlidingMenu();
        }
        if (null != searchContext) {
            btn_search.setVisibility(View.VISIBLE);
            edt_search.setVisibility(View.VISIBLE);
            tv_title.setText("搜索");
            edt_search.setText(searchContext);
            // loadData();// 这是搜索
            if (isHandlePost) {
                showPostList();
            } else {
                showUserList();
            }
        } else {
            tv_title.setText("背包");
            btn_search.setVisibility(View.GONE);
            edt_search.setVisibility(View.GONE);
            if (null != application.getUser() && 0 != application.getUser().getId()) {
                if (isHandlePost) {
                    showPostList();
                } else {
                    showUserList();
                }
            } else {
                showNotification(1, "此功能需要登录，既将为你跳转到登录！", R.id.rl_searchroot);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(2), 1000);
            }
        }
    }

    private void initView() {
        if (null == urv_postlistView) {
            urv_postlistView = (UltimateRecyclerView) findViewById(R.id.url_postlist);
            urv_userlistView = (UltimateRecyclerView) findViewById(R.id.url_userlist);
            btn_search = (ImageButton) findViewById(R.id.btn_search);
            btn_return = (ImageView) findViewById(R.id.btn_left);
            tv_title = (TextView) findViewById(R.id.tv_title);
            edt_search = (EditText) findViewById(R.id.edt_search);
//            userLayout = (LinearLayout) findViewById(R.id.ll_bottom_user);

            RecyclerView.LayoutManager manager1 = new LinearLayoutManager(this);
            urv_postlistView.setLayoutManager(manager1);
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            urv_userlistView.setLayoutManager(manager);

            urv_postlistView.setEmptyView(getResources().getIdentifier("view_empty", "layout", getPackageName()));
            urv_userlistView.setEmptyView(getResources().getIdentifier("view_empty", "layout", getPackageName()));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
            urv_postlistView.addItemDecoration(dividerItemDecoration);
            urv_postlistView.enableLoadmore();
            urv_postlistView.setOnLoadMoreListener(this);
            urv_postlistView.setDefaultOnRefreshListener(this);
            urv_userlistView.enableLoadmore();
            urv_userlistView.setOnLoadMoreListener(this);
            urv_userlistView.setDefaultOnRefreshListener(this);

//            userLayout.setOnClickListener(this);
            btn_return.setOnClickListener(this);
            btn_search.setOnClickListener(this);
            ((RadioGroup) findViewById(R.id.rg_switch)).setOnCheckedChangeListener(this);
            edt_search.setOnEditorActionListener(this);
        }
    }

    private void initSlidingMenu() {
        menu = new MCSildingMenu();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        width = (int) (width / 3.5);
        mySlidingMenu = new SlidingMenu(this, null);
        mySlidingMenu.setMode(SlidingMenu.LEFT);
        mySlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mySlidingMenu.setBehindOffsetRes(R.dimen.com_margin);
        mySlidingMenu.setFadeDegree(0.42f);
        mySlidingMenu.setMenu(R.layout.frame_menu);
        mySlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mySlidingMenu.setBackgroundResource(R.drawable.background_slidingmenu);
        mySlidingMenu.setBehindOffset(width);
        mySlidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                // TODO Auto-generated method stub
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
        mySlidingMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {

            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                // TODO Auto-generated method stub
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menu).commitAllowingStateLoss();
        mySlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {

            @Override
            public void onOpened() {
                // TODO Auto-generated method stub
                menu.callOnResumeForUpdate();
                menu.showData();
            }
        });
        mySlidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {

            @Override
            public void onClose() {
                // TODO Auto-generated method stub
                menu.callOnPauseForUpdate();
                hideKeyboard(mySlidingMenu);
            }
        });
    }

    private void showPostList() {
//        userLayout.setVisibility(View.GONE);
        urv_userlistView.setVisibility(View.GONE);
        urv_postlistView.setVisibility(View.VISIBLE);
        if (null == posts) {
            loadData();
            return;
        } else {
            if (0 == posts.size()) {
                if (null == searchContext) {
                    // showNotification("你还没有收藏有帖子！");
                    Toast.makeText(this, "你还没有收藏有帖子！", Toast.LENGTH_SHORT).show();
                } else {
                    // showNotification("没有找到帖子！");
                    Toast.makeText(this, "没有搜索到帖子！", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != posts && !posts.isEmpty()) {
                    StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                    urv_postlistView.setLayoutManager(manager);
                    if (null == postAdapter) {
                        postAdapter = new PostAdapter(SearchResultActivity.this);
                        urv_postlistView.setAdapter(postAdapter);
                    }
                }
                postAdapter.setData(posts);
            }
        });
    }

    private void showUserList() {
//        userLayout.setVisibility(View.GONE);
        urv_postlistView.setVisibility(View.GONE);
        urv_userlistView.setVisibility(View.VISIBLE);
        if (null == users) {
            loadData();
            return;
        } else if (0 == users.size()) {
            if (null == searchContext) {
//				showNotification("你还没有添加有好友！\n点其头像可以进入个人中心然后再添加好友。");
                Toast.makeText(this, "你还没有添加有好友！\n点其头像可以进入个人中心然后再添加好友。", Toast.LENGTH_SHORT).show();
            } else {
                // showNotification("没有你要找的用户！");
                Toast.makeText(this, "没有你要搜索的用户！", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (null != users && 0 != users.size()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null == userAdapter) {
                        userAdapter = new UserAdapter(SearchResultActivity.this);
                        urv_userlistView.setAdapter(userAdapter);
                    }
                    userAdapter.setData(users);
                }
            });
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see com.mckuai.imc.BaseActivity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd("背包与搜索");
    }

    private void loadData() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        final String url = getUrl();
        final RequestParams params = getParams();
        //Log.e(TAG, url + "&" + params.toString());
        mClient.post(url, params, new JsonHttpResponseHandler() {
            /*
             * (non-Javadoc)
             *
             * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
             */
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                if (null == searchContext) //读取背包中的缓存信息
                {
                    if ((0 == mUserPage.getPage()) || (0 == mPostPage.getPage())) {
                        String result = getData(url, params);
                        if (null != result) {
                            parseResult(null, null, result);
                            return;
                        }
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
                super.onSuccess(statusCode, headers, response);
                isLoading = false;
                if (response.has("state") && response.has("dataObject")) {
                    String result = null;
                    try {
                        if (response.getString("state").equals("ok")) {
                            result = response.getString("dataObject");
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        cancleLodingToast(false);
                        showNotification(3, "返回数据错误！", R.id.rl_searchroot);
                        return;
                    }
                    if (null != result) {
                        cancleLodingToast(true);
                        parseResult(url, params, result);
                        return;
                    }
                }
                showNotification(3, "返回数据错误！", R.id.rl_searchroot);
                cancleLodingToast(false);
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
                super.onFailure(statusCode, headers, responseString, throwable);
                cancleLodingToast(false);
                showNotification(3, "获取数据失败，原因：" + throwable.getLocalizedMessage(), R.id.rl_searchroot);
                isLoading = false;
            }
        });
    }

    private void parseResult(String url, RequestParams params, String result) {
        if (isHandlePost) {
            PostBaen postData = gson.fromJson(result, PostBaen.class);
            mPostPage.setAllCount(postData.getAllCount());
            mPostPage.setPage(postData.getPage());
            mPostPage.setPageSize(postData.getPageSize());
            if (1 == mPostPage.getPage()){
                posts = postData.getdata();
            }
            else {
                posts.addAll(postData.getdata());
            }
            if (null != url && null != params && 1 == mPostPage.getPage()) {
                cacheData(url, params, result);
            }
            showPostList();
        } else {
            UserBaen userData = gson.fromJson(result, UserBaen.class);
//            users = userData.getdata();
            mUserPage.setAllCount(userData.getallCount());
            mUserPage.setPage(userData.getpage());
            mUserPage.setPageSize(userData.getpageSize());
            if (1 == mUserPage.getPage()){
                users = userData.getdata();
            }
            else {
                users.addAll(userData.getdata());
            }
            if (null != url && null != params && 1 == mUserPage.getPage()) {
                cacheData(url, params, result);
            }
            showUserList();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android
     * .widget.RadioGroup, int)
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        switch (checkedId) {
            case R.id.rb_searchPost:
                isHandlePost = true;
                showPostList();
                break;
            case R.id.rb_searchUser:
                isHandlePost = false;
                showUserList();
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        /*case R.id.btn_collectUser:
            if (application.isLogin())
			{
				if (null != mUser)
				{
					Intent intent = new Intent(SearchResultActivity.this, UserCenter.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(getString(R.string.user), mUser);
					intent.putExtras(bundle);
					startActivity(intent);
				} else
				{
					Toast.makeText(this, "选中用户后才能进行此操作!", Toast.LENGTH_SHORT).show();
				}
			}
			break;*/

            case R.id.btn_left:
                this.finish();
                break;

            case R.id.btn_search:
                if (0 < edt_search.getText().length()) {
                    searchContext = edt_search.getText().toString();
                    loadData();
                }
                break;

            default:
                break;
        }
    }

    private String getUrl() {
        String url = getString(R.string.interface_domainName);
        if (null != searchContext) {
            url += getString(R.string.interface_search);
        } else {
            url += isHandlePost ? getString(R.string.interface_collectpostlist) : getString(R.string.interface_fellowuserlist);
        }
        return url;
    }

    private RequestParams getParams() {
        RequestParams params = new RequestParams();
        if (null != searchContext) {
            // 这是搜索
            params.put("key", searchContext);
            params.put("type", isHandlePost ? "talk" : "people");
        } else {
            // 这是背包
            params.put("id", application.getUser().getId());
            if (isHandlePost) {
                params.put("page", mPostPage.getNextPage());
            } else {
                params.put("page", mUserPage.getNextPage());
            }
        }
        return params;
    }

    private void callLogin() {
        Intent intent = new Intent(SearchResultActivity.this, LoginActivity.class);
        intent.putExtra(getString(R.string.needLoginResult), true);
        startActivityForResult(intent, 1);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (Activity.RESULT_OK == arg1) {
            loadData();
        } else {
//			showNotification("未登录，既将退出！");
            mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 1000);
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    callLogin();

                    break;
                case 1:
                    finish();
                    break;

                default:
                    break;
            }
        }

        ;
    };

	/*
     * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
    /*@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		Intent intent;
		switch (parent.getId())
		{
		case R.id.gv_userList:
			if (null != users.get(position))
			{
				CircleImageView image;
				if (null != selectView)
				{
					image = (CircleImageView) selectView.findViewById(R.id.civ_user);
					image.setProgress(0);
				}
				image = (CircleImageView) view.findViewById(R.id.civ_user);
				image.setProgress(1);
				this.selectView = view;
				mUser = users.get(position);
				btn_chat.setVisibility(View.VISIBLE);
				btn_operate_user.setVisibility(View.VISIBLE);
				intent = new Intent(SearchResultActivity.this, UserCenter.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(getString(R.string.user), mUser);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		case R.id.lv_postList:
			Post post = posts.get(position - 1);
			if (null != post)
			{
				intent = new Intent(SearchResultActivity.this, PostActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(getString(R.string.tag_post), post);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		default:
			break;
		}

	}*/

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (null != edt_search.getText() && 0 < edt_search.getText().toString().trim().length()) {
                searchContext = edt_search.getText().toString();
                loadData();
            } else {
                Toast.makeText(SearchResultActivity.this, "不能搜索空内容!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    public void loadMore(int i, int i1) {
        if (isHandlePost) {
            if (!mPostPage.EOF()) {
                loadData();
            }
        } else {
            if (!mUserPage.EOF()) {
                loadData();
            }
        }
    }

    @Override
    public void onRefresh() {
        if (isHandlePost) {
            if (null != mPostPage) {
                mPostPage.setPage(0);
                posts.clear();
            }
        } else {
            if (null != mUserPage) {
                mUserPage.setPage(0);
                users.clear();
            }
        }
        loadData();
    }

}
