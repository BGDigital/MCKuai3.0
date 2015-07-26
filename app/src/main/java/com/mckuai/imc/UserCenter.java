package com.mckuai.imc;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.adapter.MCDynamicAdapter;
import com.mckuai.adapter.MCMessageAdapter;
import com.mckuai.bean.MCDynamic;
import com.mckuai.bean.MCDynamicBean;
import com.mckuai.bean.MCMessage;
import com.mckuai.bean.MCMessageBean;
import com.mckuai.bean.MCUser;
import com.mckuai.bean.Post;
import com.mckuai.bean.PostBaen;
import com.mckuai.fragment.MCSildingMenu;
import com.mckuai.widget.FastBlur;
import com.mckuai.widget.MC_RadioButton;
import com.mckuai.widget.XListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import  com.mckuai.adapter.UserCentPostAdapter;

import java.util.Collection;

import slidingmenu.SlidingMenu;

public class UserCenter extends BaseActivity implements OnCheckedChangeListener, MC_RadioButton.OnCheckChangeListener,
		OnClickListener, OnItemClickListener, XListView.IXListViewListener
{
	private RadioGroup rg_messageType;
	private ImageButton btn_showSet;
	private MC_RadioButton rb_Message;
	private MC_RadioButton rb_Dynamic;
	private MC_RadioButton rb_Post;
	private RelativeLayout rl_top;
	private XListView list;
	private com.mckuai.widget.CircleImageView iv_User;
	private TextView tv_Name;
	private TextView tv_Location;
	private TextView tv_level;
	private TextView tv_friend_opt;
	private LinearLayout ll_operator;
	private LinearLayout ll_quickReply;
	private ImageView iv_background_layer;
	private EditText edt_quickreply;

	private MCUser user;
	private static boolean isFrend = false;// 是否是好友
	private static boolean isShowReply = false;
	private MCkuai application;
	private MCMessageAdapter mMessageAdapter;
	private MCDynamicAdapter mDynamicAdapter;
	private UserCentPostAdapter mPostAdapter;
	private MCMessageBean mMessageBean;// 当前的消息
	private MCMessageBean atMessageBean = new MCMessageBean();
	private MCMessageBean sysMessageBean = new MCMessageBean();
	private MCDynamicBean mDynamicBean = new MCDynamicBean();// 当前的动态
	private PostBaen mPostBaen = new PostBaen();// 当前的作品
	private AsyncHttpClient mClient;
	private Gson mGson;
	private ImageLoader mLoader;
	private DisplayImageOptions mCircle;
	private com.umeng.socialize.controller.UMSocialService mShareService;
	private static MCMessage msg;

	private SlidingMenu mySlidingMenu;
	private MCSildingMenu menu;

	private String[] mMessageType = { "all", "system" };
	private String[] mType = { "message", "dynamic", "work" };
	private boolean isOther = true;

	private static final int FOR_USERCENTER = 0;
	private static final int FOR_REPLY = 1;
	private static final int FOR_CHAT = 2;
	private static final int FOR_ADDFRIEND=3;
	/**
	 * 当前消息类型,回复或者系统消息
	 */
	private String mCurMessageType;
	/**
	 * 当前类别,消息,动态或帖子
	 */
	private String mCurType;
	private int showGroup;// 显示的类型,1:消息,2:动态,3:作品

	private static final int GROUP_MESSAGE = 1;
	private static final int GROUP_DYNAMIC = 2;
	private static final int GROUP_POST = 3;
	private static String url;


	private static final String TAG = "UserCenter";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		user = (MCUser) getIntent().getSerializableExtra(getString(R.string.user));
		application = MCkuai.getInstance();
		mClient = application.mClient;
		mLoader = ImageLoader.getInstance();
		mCircle = application.getCircleOption();
		url = getString(R.string.interface_domainName) + getString(R.string.interface_userCenter);
		mGson = new Gson();
		initShare();
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("个人中心");
		// setNotificationViewGroup(R.id.lv_list);
		if (null == list)
		{
			initView();
		}
		if (null == mySlidingMenu)
		{
			initSlidingMenu();
		}
		if (isShowReply)
		{
			return;// 这是回复消息时登录的回调
		}
		if (null != user || application.isLogin())
		{
			showData();
		} else
		{
			callLogin(FOR_USERCENTER);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.activity.BaseActivity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageStart("个人中心");
	}

	private void initView()
	{
		rg_messageType = (RadioGroup) findViewById(R.id.rg_Message_Type);
		btn_showSet = (ImageButton) findViewById(R.id.btn_ShowSetting);
		rb_Message = (MC_RadioButton) findViewById(R.id.rb_Message);
		rb_Dynamic = (MC_RadioButton) findViewById(R.id.rb_Dynamic);
		rb_Post = (MC_RadioButton) findViewById(R.id.rb_Post);
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		ll_operator = (LinearLayout) findViewById(R.id.ll_operator);
		ll_quickReply = (LinearLayout) findViewById(R.id.ll_quickreply);
		iv_background_layer = (ImageView) findViewById(R.id.iv_usercenter_above_userinfo);
		edt_quickreply = (EditText) findViewById(R.id.edt_quickreply);
		rb_Post.setText(getString(R.string.post));
		rb_Dynamic.setText(getString(R.string.dynamic));
		rb_Message.setText(getString(R.string.message));
		list = (XListView) findViewById(R.id.lv_list);
		list.setPullRefreshEnable(true);
		list.setPullLoadEnable(false);
		list.setXListViewListener(this);
		list.setEmptyView(findViewById(R.id.empty));
		iv_User = (com.mckuai.widget.CircleImageView) findViewById(R.id.prg_UserHead);
		tv_Name = (TextView) findViewById(R.id.tv_UserName);
		tv_level = (TextView) findViewById(R.id.tv_userLevel);
		tv_Location = (TextView) findViewById(R.id.tv_UserLocation);
		tv_friend_opt = (TextView) findViewById(R.id.tv_addTopackage);
		tv_Location.setOnClickListener(this);
		btn_showSet.setOnClickListener(this);
		tv_friend_opt.setOnClickListener(this);
		findViewById(R.id.btn_Return).setOnClickListener(this);
		findViewById(R.id.btn_Share).setOnClickListener(this);
		findViewById(R.id.tv_makechat).setOnClickListener(this);
		findViewById(R.id.btn_quickreply).setOnClickListener(this);
		findViewById(R.id.btn_showpost).setOnClickListener(this);
		findViewById(R.id.btn_closereply).setOnClickListener(this);

		if (isOthers())
		{
			btn_showSet.setVisibility(View.INVISIBLE);
			rb_Message.setVisibility(View.GONE);
			rg_messageType.setVisibility(View.GONE);
			rb_Dynamic.setChecked(true);
			mCurType = mType[1];
			findViewById(R.id.v_divi_usercenter).setVisibility(View.GONE);
			showGroup = GROUP_DYNAMIC;
		} else
		{
			btn_showSet.setVisibility(View.VISIBLE);
			rb_Message.setVisibility(View.VISIBLE);
			rg_messageType.setVisibility(View.VISIBLE);
			rb_Message.setChecked(true);
			showGroup = GROUP_MESSAGE;
			mCurType = mType[0];
			mMessageBean = atMessageBean;
			mCurMessageType = mMessageType[0];
			ll_operator.setVisibility(View.GONE);
		}
		// 选择不同类型的消息
		rg_messageType.setOnCheckedChangeListener(this);
		// 选择不同类弄的内容
		rb_Dynamic.setOnCheckChangeListener(this);
		rb_Message.setOnCheckChangeListener(this);
		rb_Post.setOnCheckChangeListener(this);
		list.setOnItemClickListener(this);
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
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menu).commit();
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
				showData();
				hideKeyboard(mySlidingMenu);
			}
		});
	}

	private void initShare()
	{
		mShareService = UMServiceFactory.getUMSocialService("com.umeng.share");
		String targetUrl = "http://www.mckuai.com/u/" + user.getId();
		String title = "麦块for我的世界盒子";
		String context;
		if (application.isLogin() && application.getUser().getId() == user.getId())
		{
			context = "我正在使用《麦块for我的世界盒子》，看看我厉害吧！";
		} else
		{
			context = "我发现了一个高手，大家速来围观";
		}

		String appID_QQ = "101155101";
		String appAppKey_QQ = "78b7e42e255512d6492dfd135037c91c";
		// 添加qq
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appID_QQ, appAppKey_QQ);
		qqSsoHandler.setTargetUrl(targetUrl);
		qqSsoHandler.setTitle(title);
		qqSsoHandler.addToSocialSDK();
		// 添加QQ空间参数
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appID_QQ, appAppKey_QQ);
		qZoneSsoHandler.setTargetUrl(targetUrl);
		qZoneSsoHandler.addToSocialSDK();

		String appIDWX = "wx49ba2c7147d2368d";
		String appSecretWX = "85aa75ddb9b37d47698f24417a373134";
		// 添加微信
		UMWXHandler wxHandler = new UMWXHandler(this, appIDWX, appSecretWX);
		wxHandler.setTargetUrl(targetUrl);
		wxHandler.setTitle(title);
		wxHandler.showCompressToast(false);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appIDWX, appSecretWX);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.setTargetUrl(targetUrl);
		wxHandler.showCompressToast(false);
		wxCircleHandler.setTitle(title);
		wxCircleHandler.addToSocialSDK();
		// 移除多余平台
		mShareService.getConfig().removePlatform(SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA);
		mShareService.setShareContent(context);
	}

	private void showData()
	{
		switch (showGroup)
		{
		case GROUP_MESSAGE:
			showMessage();
			break;
		case GROUP_DYNAMIC:
			showDynamic();
			break;
		case GROUP_POST:
			showPost();
			break;

		default:
			break;
		}
		showUser();
	}

	private void showMessage()
	{
		if (0 == mMessageBean.getPage())
		{
			loadMessage();
			return;
		}
		if (null == mMessageAdapter)
		{
			mMessageAdapter = new MCMessageAdapter(this, mMessageBean.getData());
			list.setAdapter(mMessageAdapter);
		} else
		{
			mMessageAdapter.setData(mMessageBean.getData());
		}
		if (mMessageBean.EOF())
		{
			list.setPullLoadEnable(false);
		} else
		{
			list.setPullLoadEnable(true);
		}
		if (null == mMessageBean.getData())
		{
			mMessageAdapter.notifyDataSetInvalidated();
		}
	}

	private void showDynamic()
	{
		// 第一次，需要获取数据
		if (0 == mDynamicBean.getPage())
		{
			loadDynamic();
			return;
		}
		if (null == mDynamicAdapter)
		{
			mDynamicAdapter = new MCDynamicAdapter(this, mDynamicBean.getData());
			list.setAdapter(mDynamicAdapter);
		} else
		{
			mDynamicAdapter.setData(mDynamicBean.getData());
		}
		if (mDynamicBean.EOF())
		{
			list.setPullLoadEnable(false);
		} else
		{
			list.setPullLoadEnable(true);
		}
		if (null == mDynamicBean.getData())
		{
			mDynamicAdapter.notifyDataSetInvalidated();
		}
	}

	private void showPost()
	{
		if (0 == mPostBaen.getPage())
		{
			loadPost();
			return;
		}
		if (null == mPostAdapter)
		{
			mPostAdapter = new UserCentPostAdapter(this, mPostBaen.getdata());
			list.setAdapter(mPostAdapter);
		} else
		{
			mPostAdapter.setData(mPostBaen.getdata());
		}
		if (mPostBaen.EOF())
		{
			list.setPullLoadEnable(false);
		} else
		{
			list.setPullLoadEnable(true);
		}
		if (null == mPostBaen.getdata())
		{
			mPostAdapter.notifyDataSetInvalidated();
		}
	}

	private void showUser()
	{
		Log.e(TAG, "显示用户信息");
		if (isOther)
		{
			getUserMark();
		}
		else {
			user = application.getUser();
		}
		if (null != user)
		{
			tv_Location.setText(user.getAddr() + "");
			tv_Location.setVisibility(View.VISIBLE);
			tv_Name.setText(user.getNike() + "");
			tv_level.setVisibility(View.VISIBLE);
			tv_level.setText("LV" + user.getLevel());
			iv_User.setProgress(user.getProcess());
			rb_Dynamic.setCount(user.getDynamicNum());
			rb_Message.setCount(user.getMessageNum());
			rb_Post.setCount(user.getWorkNum());

			String url = (String) iv_User.getTag() + "";
			if (null != user.getHeadImg() && 10 < user.getHeadImg().length()
					&& !url.equalsIgnoreCase(user.getHeadImg()))
			{
				UMImage image = null;
				if (null != user.getHeadImg() && 10 < user.getHeadImg().length())
				{
					image = new UMImage(this, user.getHeadImg());
				} else
				{
					image = new UMImage(this, R.drawable.icon_share_default);
				}
				mShareService.setShareMedia(image);
				mLoader.displayImage(user.getHeadImg(), iv_User, mCircle, new ImageLoadingListener()
				{

					@Override
					public void onLoadingStarted(String arg0, View arg1)
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
					{
						// TODO Auto-generated method stub

					}

					@SuppressLint("NewApi")
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2)
					{
						// TODO Auto-generated method stub
						if (null == arg2)
						{
							return;
						}
						Bitmap tag = FastBlur.fastblur(arg2, 4);
						BitmapDrawable drawable = new BitmapDrawable(tag);
						rl_top.setBackground(drawable);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(rl_top.getWidth(), rl_top
								.getHeight());
						iv_background_layer.setLayoutParams(params);
						iv_background_layer.getBackground().setAlpha(76);
						return;

					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1)
					{
						// TODO Auto-generated method stub

					}
				});
				iv_User.setTag(user.getHeadImg());
			}
		}
		else {
			if (mySlidingMenu.isShown())
			{
				mySlidingMenu.toggle();
			}
			this.finish();
		}
	}

	private void loadMessage()
	{
		final RequestParams params = new RequestParams();
		params.put("id", user.getId());
		params.put("page", mMessageBean.getNextPage());
		params.put("type", mCurType);
		params.put("messageType", mCurMessageType);
		mClient.post(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				String resultString = getData(url, params);
				if (null != resultString)
				{
					JSONObject object = null;
					try
					{
						object = new JSONObject(resultString);
					} catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						popupLoadingToast(getString(R.string.onloading_hint));
					}
					if (null != object)
					{
						parseMessage(null, object, false);
						showMessage();
					}
				} else
				{
					popupLoadingToast(getString(R.string.onloading_hint));
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				if (response.has("state"))
				{
					try
					{
						parseMessage(url + "&" + params.toString(), response.getJSONObject("dataObject"), true);
						//Log.e(TAG, url + "&" + params.toString());
						showMessage();
						cancleLodingToast(true);
						return;
					} catch (Exception e)
					{
						// TODO: handle exception
					}
				}
                cancleLodingToast(false);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
                cancleLodingToast(false);
			}
		});
	}

	private void loadDynamic()
	{
		final RequestParams params = new RequestParams();
		params.put("id", user.getId());
		params.put("page", mDynamicBean.getNextPage());
		params.put("type", mCurType);
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
				String result = getData(url, params);
				if (null != result && 10 < result.length())
				{
					JSONObject object = null;
					try
					{
						object = new JSONObject(result);
					} catch (Exception e)
					{
						// TODO: handle exception
						popupLoadingToast(getString(R.string.onloading_hint));
					}
					if (null != object)
					{
						parseDynamic(null, object, false);
					}
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
				super.onSuccess(statusCode, headers, response);
				if (response.has("state"))
				{
					try
					{
						parseDynamic(url + "&" + params.toString(), response.getJSONObject("dataObject"), true);
						//Log.e(TAG, url + "&" + params.toString());
						showDynamic();
                        cancleLodingToast(true);
						if (mDynamicBean.getData().isEmpty())
						{
							if (application.getUser().getId() == user.getId())
							{
                                showNotification(1,"你还没有任何动态，快去回复个帖子吧！",R.id.rl_top);
							} else
							{
                                showNotification(1,"该用户还没有动态！",R.id.rl_top);
							}
						}
						return;
					} catch (Exception e)
					{
						// TODO: handle exception
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
//				showAlertDialog(getString(R.string.faile), "用户ID:" + user.getId() + "\n"
//						+ getString(R.string.loadfaile_context) + throwable.getLocalizedMessage());
			}
		});

	}

	private void loadPost()
	{
		final RequestParams params = new RequestParams();
		params.put("id", user.getId());
		params.put("page", mPostBaen.getNextPage());
		params.put("type", mCurType);
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
				String result = getData(url, params);
				if (null != result && 10 < result.length())
				{
					JSONObject object = null;
					try
					{
						object = new JSONObject(result);
					} catch (Exception e)
					{
						// TODO: handle exception
						popupLoadingToast(getString(R.string.onloading_hint));
					}
					if (null != object)
					{
						parsePost(null, object, false);
						showPost();
					}
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
				super.onSuccess(statusCode, headers, response);
				if (response.has("state"))
				{
					try
					{
						parsePost(url + "&" + params.toString(), response.getJSONObject("dataObject"), true);
						//Log.e(TAG, url + "&" + params.toString());
						if (null != mPostBaen && null != mPostBaen.getdata() && !mPostBaen.getdata().isEmpty())
						{
							showPost();
							cancleLodingToast(true);
							return;
						}
					} catch (Exception e)
					{
						// TODO: handle exception
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
			}
		});
	}

	private void parseMessage(String url, JSONObject msg, boolean enableCache)
	{
		if (msg.has("user"))
		{
			try
			{
				parseUser(msg.getString("user"));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (msg.has("list"))
		{
			MCMessageBean bean = null;
			try
			{
				bean = mGson.fromJson(msg.getString("list"), MCMessageBean.class);
			} catch (JsonSyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null != bean && null != bean.getData())
			{
				// 如果是系统消息，需调整其类型
				if (mCurMessageType.equals(mMessageType[1]))
				{
					for (MCMessage mcMessage : bean.getData())
					{
						mcMessage.setSystemMessage(true);
					}
				}
				// 缓存从网络上取下来的第一页
				if (1 == bean.getPage())
				{
					mMessageBean = bean;
					if (enableCache)
					{
						cacheData(url, msg.toString());
					}
				}
				// 不是第一页
				else
				{
					mMessageBean.getData().addAll((Collection<? extends MCMessage>) bean.getData().clone());
					mMessageBean.setAllCount(bean.getAllCount());
					mMessageBean.setPage(bean.getPage());
					mMessageBean.setPageCount(bean.getPageCount());
					mMessageBean.setPageSize(bean.getPageSize());
				}

			}
		}
	}

	private void parseDynamic(String url, JSONObject dynamic, boolean enableCache)
	{
		if (dynamic.has("user"))
		{
			try
			{
				parseUser(dynamic.getString("user"));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (dynamic.has("list"))
		{
			MCDynamicBean bean = null;
			try
			{
				bean = mGson.fromJson(dynamic.getString("list"), MCDynamicBean.class);
			} catch (JsonSyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null != bean)
			{
				// 缓存从网络上取下来的第一页
				if (1 == bean.getPage())
				{
					mDynamicBean = bean;
					if (enableCache)
					{
						cacheData(url, dynamic.toString());
					}
				} else
				{
					mDynamicBean.getData().addAll((Collection<? extends MCDynamic>) bean.getData().clone());
					mDynamicBean.setAllCount(bean.getAllCount());
					mDynamicBean.setPage(bean.getPage());
					mDynamicBean.setPageCount(bean.getPageCount());
					mDynamicBean.setPageSize(bean.getPageSize());
				}
			}
		}
	}

	private void parsePost(String url, JSONObject post, boolean enableCache)
	{
		if (post.has("user"))
		{
			try
			{
				parseUser(post.getString("user"));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (post.has("list"))
		{
			PostBaen bean = null;
			try
			{
				bean = mGson.fromJson(post.getString("list"), PostBaen.class);
			} catch (JsonSyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null != bean)
			{
				// 缓存从网络上取下来的第一页
				if (1 == bean.getPage())
				{
					mPostBaen = bean;
					if (enableCache)
					{
						cacheData(url, post.toString());
					}
				} else
				{
					mPostBaen.getdata().addAll((Collection<? extends Post>) bean.getdata());
					mPostBaen.setAllCount(bean.getAllCount());
					mPostBaen.setPage(bean.getPage());
					mPostBaen.setPageCount(bean.getPageCount());
					mPostBaen.setPageSize(bean.getPageSize());
				}
			}
		}
	}

	private void parseUser(String userString)
	{
		user = mGson.fromJson(userString, MCUser.class);
		if (!isOther)
		{
			application.setUser(user);
		}
		showUser();
	}

	private boolean isOthers()
	{
		if (null == user)
		{
			isOther = false;
			return false;
		}
		if (!application.isLogin())
		{
			isOther = true;
		} else
		{
			isOther = application.getUser().getId() != user.getId();
		}
		return isOther;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		// TODO Auto-generated method stub
		switch (checkedId)
		{
		case R.id.rb_atMessage:
			mMessageBean = atMessageBean;
			mCurMessageType = mMessageType[0];
			showData();
			break;
		case R.id.rb_systemMessage:
			mMessageBean = sysMessageBean;
			mCurMessageType = mMessageType[1];
			showData();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChang(int id)
	{
		// TODO Auto-generated method stub
		switch (id)
		{
		case R.id.rb_Message:
			mCurType = mType[0];// 消息
			rb_Dynamic.setChecked(false);
			rb_Post.setChecked(false);
			rg_messageType.setVisibility(View.VISIBLE);
			showGroup = GROUP_MESSAGE;
			mMessageAdapter = null;
			showData();
			break;
		case R.id.rb_Dynamic:
			mCurType = mType[1];// 动态
			rb_Message.setChecked(false);
			rb_Post.setChecked(false);
			showGroup = GROUP_DYNAMIC;
			rg_messageType.setVisibility(View.GONE);
			mDynamicAdapter = null;
			showData();
			break;
		case R.id.rb_Post:
			mCurType = mType[2];// 作品
			rb_Message.setChecked(false);
			rb_Dynamic.setChecked(false);
			showGroup = GROUP_POST;
			rg_messageType.setVisibility(View.GONE);
			mPostAdapter = null;
			showData();
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId())
		{
		case R.id.btn_Return:
			this.finish();
			break;
		case R.id.btn_ShowSetting:
			intent = new Intent(UserCenter.this, ChangeInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_Share:
			MobclickAgent.onEvent(this, "shareUser_UserCenter");
			mShareService.openShare(this, false);
			break;
		case R.id.tv_addTopackage:
			if (isFrend)
			{
				MobclickAgent.onEvent(this, "cancleFollowSomebody");
			} else
			{
				MobclickAgent.onEvent(this, "followSomebody");
			}
			attentionUser();
			break;
		case R.id.tv_makechat:
			/*MobclickAgent.onEvent(this, "chat");
			if (application.isLogin() && application.isLoginRC())
			{
				RongIM.getInstance().startPrivateChat(UserCenter.this, user.getName(), user.getNike() + "");
			} else
			{
				Toast.makeText(this, "你未登录，请先登录！", Toast.LENGTH_SHORT).show();
				// showNotification("你未登录，请先登录！");
			}*/
			break;
		case R.id.tv_UserLocation:
			MobclickAgent.onEvent(this, "updateLocation_UserCenter");
			if (application.isLogin() && user.getId() == application.getUser().getId())
			{
				intent = new Intent(this, LocationActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.btn_quickreply:
			MobclickAgent.onEvent(this, "quickReply");
			quickReplyMessag();
			break;

		case R.id.btn_showpost:
			hideQuickReply();
			Post post = new Post();
			post.setId(Integer.parseInt(msg.getCont1()));
			intent = new Intent(this, PostActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(getString(R.string.tag_post), post);
			intent.putExtras(bundle);
			startActivity(intent);
			break;

		case R.id.btn_closereply:
			hideQuickReply();
			break;

		default:
			break;
		}
	}

	private void attentionUser()
	{
		if (application.isLogin())
		{
			String url;
			if (isFrend)
			{
				url = getString(R.string.interface_domainName) + getString(R.string.interface_cancle_fellowuser);
			} else
			{
				url = getString(R.string.interface_domainName) + getString(R.string.interface_fellowuser);
			}
			RequestParams params = new RequestParams();
			// 关注用户接口，参数：ownerId=当前用户id&otherId=被关注的用户id
			params.put("ownerId", application.getUser().getId());
			params.put("otherId", user.getId());
			//Log.e(TAG, url + "&" + params.toString());
			mClient.get(url, params, new JsonHttpResponseHandler()
			{
				@Override
				public void onStart()
				{
					// TODO Auto-generated method stub
					super.onStart();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response)
				{
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, headers, response);
					if (response.has("state"))
					{
						try
						{
							if (response.getString("state").equals("ok"))
							{
								isFrend = !isFrend;
								Toast.makeText(UserCenter.this, response.getString("msg") + "", Toast.LENGTH_SHORT)
										.show();
							} else
							{
								Toast.makeText(UserCenter.this, response.getString("msg") + "", Toast.LENGTH_SHORT);
							}
							setButtonFunction();
							return;
						} catch (Exception e)
						{
							// TODO: handle exception
							Log.e(TAG, "error:" + e.getLocalizedMessage() + "");
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
				{
					// TODO Auto-generated method stub
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
		else {
			callLogin(FOR_ADDFRIEND);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		Post post = new Post();
		Intent intent = new Intent(UserCenter.this, PostActivity.class);
		Bundle bundle = new Bundle();
//		int postid = 0;
		switch (showGroup)
		{
		case GROUP_MESSAGE:
			if (mCurMessageType == mMessageType[1])
			{
				return;
			}
			msg = (MCMessage) mMessageAdapter.getItem(position - 1);
			showQuickReply();
			return;
			// break;
		case GROUP_DYNAMIC:
			MCDynamic dynamic = (MCDynamic) mDynamicAdapter.getItem(position - 1);
			post.setId(dynamic.getId());
			break;
		case GROUP_POST:
			post = (Post) mPostAdapter.getItem(position - 1);
			break;
		default:
			break;
		}
		bundle.putSerializable(getString(R.string.tag_post), post);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void onLoad()
	{
		cancleLodingToast(false);
		list.stopLoadMore();
		list.stopRefresh();
	}

	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		handleLoadOnPull(true);
	}

	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		handleLoadOnPull(false);
	}

	public void handleLoadOnPull(boolean isRefresh)
	{
		list.stopRefresh();
		list.stopLoadMore();
		switch (showGroup)
		{
		case GROUP_DYNAMIC:
			if (isRefresh)
			{
				mDynamicBean.setPage(0);
			}
			loadDynamic();
			break;
		case GROUP_MESSAGE:
			if (isRefresh)
			{
				mMessageBean.setPage(0);
			}
			loadMessage();
			break;
		case GROUP_POST:
			if (isRefresh)
			{
				mPostBaen.setPage(0);
			}
			loadPost();
			break;

		default:
			break;
		}
	}

	private void getUserMark()
	{
		if (application.isLogin())
		{
			RequestParams params = new RequestParams();
			params.put("ownerId", application.getUser().getId());
			params.put("otherId", user.getId());
			String url = getString(R.string.interface_domainName) + getString(R.string.interface_getusermark);
			mClient.get(url, params, new JsonHttpResponseHandler()
			{
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response)
				{
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, headers, response);
					if (null != response && response.has("state"))
					{
						try
						{
							if (response.getString("state").equalsIgnoreCase("ok"))
							{
								isFrend = true;
							} else
							{
								isFrend = false;
							}
							setButtonFunction();
						} catch (Exception e)
						{
							// TODO: handle exception
						}
					}
				}
			});
		}
	}

	private void setButtonFunction()
	{
		if (isFrend)
		{
			tv_friend_opt.setText("取消关注");
		} else
		{
			tv_friend_opt.setText("加入背包");
		}
	}

	private void showQuickReply()
	{
		isShowReply = true;
		ll_quickReply.setVisibility(View.VISIBLE);
		showKeyboard(edt_quickreply);
		findViewById(R.id.edt_quickreply).setActivated(true);
	}

	private void hideQuickReply()
	{
		hideKeyboard(edt_quickreply);
		ll_quickReply.setVisibility(View.GONE);
		isShowReply = false;
	}

	private void quickReplyMessag()
	{
		String contextString = edt_quickreply.getText().toString().trim();
		if (application.isLogin())
		{
			if (0 < contextString.length())
			{
				if (151 > contextString.length())
				{
					sendReply(contextString);
				} else
				{
					Toast.makeText(this, "回复不能超过150个字!", Toast.LENGTH_SHORT).show();
				}
			} else
			{
				Toast.makeText(this, "不能回复空内容!", Toast.LENGTH_SHORT).show();
			}
		} else
		{
			callLogin(FOR_REPLY);
		}
	}

	private void sendReply(String context)
	{
		RequestParams params = new RequestParams();
		params.put("loginId", application.getUser().getId());
		params.put("replyContent", context);
		params.put("cont2", msg.getCont2());
		String url = getString(R.string.interface_domainName) + getString(R.string.interface_quickreply);
		mClient.post(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				popupLoadingToast("正在发布回复!");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				if (null != response && response.has("state"))
				{
					try
					{
						if (response.getString("state").equalsIgnoreCase("ok"))
						{
							cancleLodingToast(true);
							hideQuickReply();
							Toast.makeText(UserCenter.this, "回复 " + msg.getUserName() + " 成功", Toast.LENGTH_SHORT)
									.show();
							MobclickAgent.onEvent(UserCenter.this, "quickReply_Success");
							return;
						}
					} catch (Exception e)
					{
						// TODO: handle exception
					}
				}
				cancleLodingToast(false);
				Toast.makeText(UserCenter.this, "回复失败!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
				cancleLodingToast(false);
				Toast.makeText(UserCenter.this, "发布回复失败,原因:" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private void callLogin(int requestCode)
	{
		Intent intent = new Intent(UserCenter.this, LoginActivity.class);
		intent.putExtra(getString(R.string.needLoginResult), true);
		startActivityForResult(intent, requestCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.activity.BaseActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case FOR_USERCENTER:
			if (RESULT_OK == resultCode)
			{
				showData();
			} else
			{
				Toast.makeText(UserCenter.this, "登录后才能查看个人中心!", Toast.LENGTH_SHORT).show();
				this.finish();
			}
			break;

		case FOR_REPLY:
			if (RESULT_OK == resultCode)
			{
				quickReplyMessag();
			} else
			{
				Toast.makeText(UserCenter.this, "登录后才能回复消息!", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case FOR_ADDFRIEND:
			if (RESULT_OK == resultCode)
			{
				attentionUser();
			}
			else {
				Toast.makeText(UserCenter.this, "登录后才能添加到背包", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (isShowReply)
			{
				hideQuickReply();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	};

}
