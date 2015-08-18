package com.mckuai.imc;

import slidingmenu.SlidingMenu;
import org.apache.http.Header;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.adapter.PostAdapter;
import com.mckuai.adapter.UserAdapter;
import com.mckuai.bean.MCUser;
import com.mckuai.bean.Post;
import com.mckuai.bean.PostBaen;
import com.mckuai.bean.UserBaen;
import com.mckuai.fragment.MCSildingMenu;
import com.mckuai.widget.CircleImageView;
import com.mckuai.widget.XListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener,
		OnItemClickListener
{
	private XListView postList;
	private GridView userList;
	private Button btn_operate_user;
	private LinearLayout userLayout;
	// private Button btn_operate_post;
	private Button btn_chat;
	private EditText edt_search;
	private ImageButton btn_search;
	private TextView tv_title;
	private ImageView btn_return;

	private String searchContext;// 搜索的内容，如果为空则为背包
	private AsyncHttpClient mClient;
	private ImageLoader mLoader;
	private DisplayImageOptions mOptions;
	private Gson gson;
	private PostBaen postData;
	private UserBaen userData;
	private ArrayList<Post> posts;
	private ArrayList<MCUser> users;
	private UserAdapter userAdapter;
	private PostAdapter postAdapter;
	private boolean isHandlePost = true;// true:处理的对象是帖子;false:处理的对象是用户
	private MCkuai application;
	private boolean isLoading = false;
	private View selectView;
	private MCUser mUser;
	private SlidingMenu mySlidingMenu;
	private MCSildingMenu menu;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
//		setNotificationViewGroup(R.id.root);
		searchContext = getIntent().getStringExtra(getString(R.string.search_contxt));
		mLoader = ImageLoader.getInstance();
		gson = new Gson();
		application = MCkuai.getInstance();
		mClient = application.mClient;
		mOptions = application.getCircleOption();
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("背包与搜索");
		initView();
		if (null == mySlidingMenu)
		{
			initSlidingMenu();
		}
		if (null != searchContext)
		{
			btn_search.setVisibility(View.VISIBLE);
			edt_search.setVisibility(View.VISIBLE);
			tv_title.setText("搜索");
			edt_search.setText(searchContext);
			// loadData();// 这是搜索
			if (isHandlePost)
			{
				showPostList();
			} else
			{
				showUserList();
			}
		} else
		{
			tv_title.setText("背包");
			btn_search.setVisibility(View.GONE);
			edt_search.setVisibility(View.GONE);
			if (null != application.getUser() && 0 != application.getUser().getId())
			{
				if (isHandlePost)
				{
					showPostList();
				} else
				{
					showUserList();
				}
			} else
			{
				// showNotification("此功能需要登录，既将为你跳转到登录！");
				Toast.makeText(this, "此功能需要登录，既将为你跳转到登录！", Toast.LENGTH_SHORT).show();
				mHandler.sendMessageDelayed(mHandler.obtainMessage(2), 1000);
			}
		}
	}

	private void initView()
	{
		if (null == postList)
		{
			userList = (GridView) findViewById(R.id.gv_userList);
			btn_chat = (Button) findViewById(R.id.btn_chat);
			btn_operate_user = (Button) findViewById(R.id.btn_collectUser);
			postList = (XListView) findViewById(R.id.lv_postList);
			btn_search = (ImageButton) findViewById(R.id.btn_search);
			btn_return = (ImageView) findViewById(R.id.btn_left);
			tv_title = (TextView) findViewById(R.id.tv_title);
			edt_search = (EditText) findViewById(R.id.edt_search);
			userLayout = (LinearLayout) findViewById(R.id.ll_bottom_user);
//			postList.setXListViewListener(this);
			postList.setPullLoadEnable(false);
			postList.setPullRefreshEnable(true);
			postList.setEmptyView(findViewById(R.id.empty));
			userList.setEmptyView(findViewById(R.id.empty));

			findViewById(R.id.btn_showOwner).setVisibility(View.INVISIBLE);
			userList.setOnItemClickListener(this);
			postList.setOnItemClickListener(this);

			btn_chat.setOnClickListener(this);
			btn_operate_user.setOnClickListener(this);
			btn_return.setOnClickListener(this);
			btn_search.setOnClickListener(this);
			((RadioGroup) findViewById(R.id.rg_switch)).setOnCheckedChangeListener(this);
			edt_search.setOnEditorActionListener(new OnEditorActionListener()
			{

				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
					// TODO Auto-generated method stub
					if (actionId == EditorInfo.IME_ACTION_SEARCH
							|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
					{
						if (null != edt_search.getText() && 0 < edt_search.getText().toString().trim().length())
						{
							searchContext = edt_search.getText().toString();
							loadData();
						} else
						{
							Toast.makeText(SearchResultActivity.this, "不能搜索空内容!", Toast.LENGTH_SHORT).show();
						}
						return true;
					}
					return false;
				}
			});
		}
	}
	
	private void initSlidingMenu()
	{
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
		mySlidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer()
		{
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen)
			{
				// TODO Auto-generated method stub
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		mySlidingMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer()
		{

			@Override
			public void transformCanvas(Canvas canvas, float percentOpen)
			{
				// TODO Auto-generated method stub
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menu).commitAllowingStateLoss();
		mySlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener()
		{

			@Override
			public void onOpened()
			{
				// TODO Auto-generated method stub
				menu.callOnResumeForUpdate();
				menu.showData();
			}
		});
		mySlidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener()
		{

			@Override
			public void onClose()
			{
				// TODO Auto-generated method stub
				menu.callOnPauseForUpdate();
				hideKeyboard(mySlidingMenu);
			}
		});
	}

	private void showPostList()
	{
		userList.setVisibility(View.GONE);
		userLayout.setVisibility(View.GONE);
		postList.setVisibility(View.VISIBLE);
		if (null == posts)
		{
			loadData();
			return;
		} else
		{
			if (0 == posts.size())
			{
				if (null == searchContext)
				{
					// showNotification("你还没有收藏有帖子！");
					Toast.makeText(this, "你还没有收藏有帖子！", Toast.LENGTH_SHORT).show();
				} else
				{
					// showNotification("没有找到帖子！");
					Toast.makeText(this, "没有搜索到帖子！", Toast.LENGTH_SHORT).show();
				}
				return;
			}
		}
		if (null != posts && posts.size() != 0)
		{
			if (null == postAdapter)
			{
				postAdapter = new PostAdapter(SearchResultActivity.this);
//				postList.setAdapter(postAdapter);
			} else
			{
				postAdapter.setData(posts);
			}
		}
	}

	private void showUserList()
	{
		userList.setVisibility(View.VISIBLE);
		// userLayout.setVisibility(View.VISIBLE);
		postList.setVisibility(View.GONE);
		if (null == users)
		{
			loadData();
			return;
		} else if (0 == users.size())
		{
			if (null != searchContext)
			{
//				showNotification("你还没有添加有好友！\n点其头像可以进入个人中心然后再添加好友。");
				Toast.makeText(this, "你还没有添加有好友！\n点其头像可以进入个人中心然后再添加好友。", Toast.LENGTH_SHORT).show();
			} else
			{
				// showNotification("没有你要找的用户！");
				Toast.makeText(this, "没有你要搜索的用户！", Toast.LENGTH_SHORT).show();
			}
			return;
		}
		if (null != users && 0 != users.size())
		{
			if (null == userAdapter)
			{
				userAdapter = new UserAdapter(this, users);
				userList.setAdapter(userAdapter);
			} else
			{
				userAdapter.setData(users);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.BaseActivity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("背包与搜索");
	}

	private void loadData()
	{
		postList.stopRefresh();
		if (isLoading)
		{
			return;
		}
		isLoading = true;
		final String url = getUrl();
		final RequestParams params = getParams();
		//Log.e(TAG, url + "&" + params.toString());
		mClient.post(url, params, new JsonHttpResponseHandler()
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
				if (null == searchContext && !isHandlePost && null == userData)
				{
					if (null == userData)
					{
//						String result = MCkuai.getInstance().getFriends();
						String result=null;
						if (null != result)
						{
							parseResult(result);
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
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				isLoading = false;
				if (response.has("state") && response.has("dataObject"))
				{
					String result = null;
					try
					{
						result = response.getString("dataObject");
					} catch (Exception e)
					{
						// TODO: handle exception
						cancleLodingToast(false);
						// showNotification("数据错误！");
						Toast.makeText(SearchResultActivity.this, "数据错误！", Toast.LENGTH_SHORT).show();
						return;
					}
					if (null != result)
					{
						cancleLodingToast(true);
						parseResult(result);
						return;
					}
				}
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
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				cancleLodingToast(false);
				isLoading = false;
			}
		});
	}

	private void parseResult(String result)
	{
		if (isHandlePost)
		{
			postData = gson.fromJson(result, PostBaen.class);
			posts = postData.getdata();
			showPostList();
		} else
		{
			userData = gson.fromJson(result, UserBaen.class);
			users = userData.getdata();
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
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		// TODO Auto-generated method stub
		switch (checkedId)
		{
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
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btn_chat:
			if (application.isLogin())
			{
				if (null != mUser)
				{
//					RongIM.getInstance().startPrivateChat(SearchResultActivity.this, mUser.getName(), mUser.getNike());
				} else
				{
					// showNotification("选中用户后才能与TA聊天");
					Toast.makeText(this, "选中用户后才能与TA聊天!", Toast.LENGTH_SHORT).show();
				}

			} else
			{
				// showNotification("你当前未登录，登录后才可与用户聊天!");
				Toast.makeText(this, "你当前未登录，登录后才可与用户聊天!", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.btn_collectPost:
			break;

		case R.id.btn_collectUser:
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
//					showNotification("选中用户后才能进行此操作!");
					Toast.makeText(this, "选中用户后才能进行此操作!", Toast.LENGTH_SHORT).show();
				}
			}
			break;

		case R.id.btn_left:
			this.finish();
			break;

		case R.id.btn_search:
			if (0 < edt_search.getText().length())
			{
				searchContext = edt_search.getText().toString();
				loadData();
			}
			break;

		default:
			break;
		}
	}

	private String getUrl()
	{
		String url = getString(R.string.interface_domainName);
		if (null != searchContext)
		{
			url += getString(R.string.interface_search);
		} else
		{
			url += isHandlePost ? getString(R.string.interface_collectpost) : getString(R.string.interface_fellowuser);
		}
		return url;
	}

	private RequestParams getParams()
	{
		RequestParams params = new RequestParams();
		if (null != searchContext)
		{
			// 这是搜索
			params.put("key", searchContext);
			params.put("type", isHandlePost ? "talk" : "people");
		} else
		{
			// 这是背包
			params.put("id", application.getUser().getId());
			params.put("page", "1");
		}
		return params;
	}

	private void callLogin()
	{
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
	protected void onActivityResult(int arg0, int arg1, Intent arg2)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (Activity.RESULT_OK == arg1)
		{
			loadData();
		} else
		{
//			showNotification("未登录，既将退出！");
			mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 1000);
		}
	}

	Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case 2:
				callLogin();

				break;
			case 1:
				finish();
				break;

			default:
				break;
			}
		};
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
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

	}


}
