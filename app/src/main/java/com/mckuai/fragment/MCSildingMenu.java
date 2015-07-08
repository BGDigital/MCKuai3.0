package com.mckuai.fragment;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.lurencun.service.autoupdate.AppUpdate;
import com.lurencun.service.autoupdate.AppUpdateService;
import com.lurencun.service.autoupdate.ResponseParser;
import com.lurencun.service.autoupdate.Version;
import com.lurencun.service.autoupdate.internal.SimpleJSONParser;
import com.mckuai.bean.MCUser;
import com.mckuai.imc.MCkuai;
import com.mckuai.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.mckuai.imc.LoginActivity;
import com.mckuai.imc.MainActivity;
import com.mckuai.imc.R;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MCSildingMenu extends BaseFragment implements OnClickListener,
		DialogInterface.OnClickListener
{
	private static MCSildingMenu instence;
	private CircleImageView user_cover;
	private TextView user_name;
	private TextView user_level;
//	private EditText edt_Search;
//	private Button btn_Package;
	private Button btn_Share;
//	private Button btn_Setting;
	private Button btn_Praise;
	private Button btn_Logout;
	private Button btn_CheckUpgread;
	private View view;
	private String TAG = "MCSildingMenu";
	private ImageLoader mLoader;
	private AppUpdate appUpdate;
	private com.umeng.socialize.controller.UMSocialService mShareService;
	private static Tencent mTencent;
	private static boolean isMainActivity;
	private AlertDialog mAlertDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mLoader = ImageLoader.getInstance();
		mTencent = Tencent.createInstance("101155101", getActivity().getApplicationContext());
		Log.e(TAG, "onCreate");
		isMainActivity = getActivity().getClass().getName().equalsIgnoreCase("com.mckuai.imc.MainActivity");
		initShare();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.sidemenu, container, false);
		return view;
	}

	private void initShare()
	{
		mShareService = UMServiceFactory.getUMSocialService("com.umeng.share");
		String targetUrl = "http://www.mckuai.com/down.html";
		String title = "麦块for我的世界盒子";

		String appID_QQ = "101155101";
		String appAppKey_QQ = "78b7e42e255512d6492dfd135037c91c";
		// 添加qq
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), appID_QQ, appAppKey_QQ);
		qqSsoHandler.setTargetUrl(targetUrl);
		qqSsoHandler.setTitle(title);
		qqSsoHandler.addToSocialSDK();
		// 添加QQ空间参数
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), appID_QQ, appAppKey_QQ);
		qZoneSsoHandler.setTargetUrl(targetUrl);
		qZoneSsoHandler.addToSocialSDK();

		String appIDWX = "wx49ba2c7147d2368d";
		String appSecretWX = "85aa75ddb9b37d47698f24417a373134";
		// 添加微信
		UMWXHandler wxHandler = new UMWXHandler(getActivity(), appIDWX, appSecretWX);
		wxHandler.setTargetUrl(targetUrl);
		wxHandler.setTitle(title);
		wxHandler.showCompressToast(false);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), appIDWX, appSecretWX);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.setTargetUrl(targetUrl);
		wxHandler.showCompressToast(false);
		wxCircleHandler.setTitle(title);
		wxCircleHandler.addToSocialSDK();
		// 移除多余平台
		mShareService.getConfig().removePlatform(SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA);
		mShareService.setShareContent("我正在使用《麦块for我的世界盒子》，太好玩了，你也来吧！");
		UMImage image = new UMImage(getActivity(), R.drawable.icon_share_default);
		mShareService.setShareMedia(image);
	}

	public static MCSildingMenu getInstence()
	{
		if (null == instence)
		{
			instence = new MCSildingMenu();
		}
		return instence;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(TAG, "onResume");
		if (null != view && null == btn_CheckUpgread)
		{
			initView();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.fragment.BaseFragment#onPause()
	 */
	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		callOnPauseForUpdate();
	}

	public void callOnResumeForUpdate()
	{
		if (null == appUpdate)
		{
			appUpdate = AppUpdateService.getAppUpdate(getActivity());
			Log.e(TAG, "init appupdate," + getActivity().getClass().getName());
		}
		appUpdate.callOnResume();
	}

	public void callOnPauseForUpdate()
	{
		if (null != appUpdate)
		{
			appUpdate.callOnPause();
		}
	}

	private void initView()
	{
//		edt_Search = (EditText) view.findViewById(R.id.edt_search);
//		btn_Package = (Button) view.findViewById(R.id.btn_myPackage);
		btn_Share = (Button) view.findViewById(R.id.btn_shareMe);
//		btn_Setting = (Button) view.findViewById(R.id.btn_softSetting);
		btn_Praise = (Button) view.findViewById(R.id.btn_tappraise);
		btn_Logout = (Button) view.findViewById(R.id.btn_logout);
		btn_CheckUpgread = (Button) view.findViewById(R.id.tv_checkUpgread);
		user_name = (TextView) view.findViewById(R.id.tv_userName);
		user_level = (TextView) view.findViewById(R.id.tv_userLevel);
		user_cover = (CircleImageView) view.findViewById(R.id.user_Progress);

		user_cover.setOnClickListener(this);
		btn_CheckUpgread.setOnClickListener(this);
//		btn_Package.setOnClickListener(this);
		btn_Share.setOnClickListener(this);
//		btn_Setting.setOnClickListener(this);
		btn_Praise.setOnClickListener(this);
		btn_Logout.setOnClickListener(this);
		btn_CheckUpgread.setOnClickListener(this);
		user_cover.setOnClickListener(this);

		if (!isMainActivity)
		{
			btn_CheckUpgread.setVisibility(View.GONE);
			view.findViewById(R.id.v_checkupgread).setVisibility(View.GONE);
		}

		/*edt_Search.setOnEditorActionListener(new OnEditorActionListener()
		{

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
				{
					if (null != edt_Search.getText() && !edt_Search.getText().toString().isEmpty()
							&& 0 < edt_Search.getText().toString().trim().length())
					{
						Intent intent = new Intent(getActivity(), SearchResultActivity.class);
						intent.putExtra(getString(R.string.search_contxt), edt_Search.getText().toString());
						startActivity(intent);
					} else
					{
						Toast.makeText(getActivity(), "不能搜索空内容!", Toast.LENGTH_SHORT).show();
					}
					return true;
				}
				return false;
			}
		});*/
	}

	public void showData()
	{
		MCUser user = MCkuai.getInstance().mUser;
		if (null != user && 0 != user.getId())
		{
			user_name.setText(user.getNike() + "");
			user_level.setText("Lv." + user.getLevel() + "");
			user_level.setVisibility(View.VISIBLE);
			mLoader.displayImage(user.getHeadImg() + "", user_cover);
			btn_Logout.setVisibility(View.VISIBLE);
		} else
		{
			user_cover.setImageResource(R.drawable.background_user_cover_default);
			user_name.setText("未登录");
			user_level.setVisibility(View.GONE);
			btn_Logout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId())
		{
		/*case R.id.btn_myPackage:
			MobclickAgent.onEvent(getActivity(), "openBackPackage_Menu");
			intent = new Intent(getActivity(), SearchResultActivity.class);
			getActivity().startActivity(intent);
			break;*/

		case R.id.btn_logout:
			MobclickAgent.onEvent(getActivity(), "logout");

			if (MCkuai.getInstance().isLogin())
			{
				//showLogoutAlertDialog();
				showAlert("是否退出", "退出后将不能,发布帖子等操作.\n" +
						"是否退出?", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						MCkuai.getInstance().LogOut();
						showData();
					}
				});
			}
			break;
		case R.id.btn_tappraise:
			MobclickAgent.onEvent(getActivity(), "evealuation");

			Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
			intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		case R.id.btn_shareMe:
			MobclickAgent.onEvent(getActivity(), "shareAPP");
			mShareService.openShare(getActivity(), false);
			break;

		case R.id.user_Progress:
			if (!MCkuai.getInstance().isLogin())
			{
				callLogin();
			}
			break;

		case R.id.tv_checkUpgread:
			MobclickAgent.onEvent(getActivity(), "checkUpdate");
			checkUpdate(false);
			break;
		default:
			break;
		}
	}

	public void checkUpdate(boolean isQuiet)
	{
		if (null == appUpdate)
		{
			appUpdate = AppUpdateService.getAppUpdate(getActivity());
		}
		String url = getString(R.string.interface_domainName) + getString(R.string.interface_checkupgread);
		url = url + "&pushMan=" + URLEncoder.encode(getString(R.string.channel_Owner));
		if (isQuiet)
		{
			appUpdate.checkLatestVersionQuiet(url, new MyJsonParser());
		}
		else {
			appUpdate.checkLatestVersion(url, new MyJsonParser());
		}
	}

	class MyJsonParser extends SimpleJSONParser implements ResponseParser
	{
		@Override
		public Version parser(String response)
		{
			try
			{
				JSONTokener jsonParser = new JSONTokener(response);
				JSONObject json = (JSONObject) jsonParser.nextValue();
				Version version = null;
				if (json.has("state") && json.has("dataObject"))
				{
					JSONObject dataField = json.getJSONObject("dataObject");
					int code = dataField.getInt("code");
					String name = dataField.getString("name");
					String feature = dataField.getString("feature");
					String targetUrl = dataField.getString("targetUrl");
					version = new Version(code, name, feature, targetUrl);
				}
				return version;
			} catch (JSONException exp)
			{
				exp.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public int getVersionCode()
	{
		try
		{
			PackageManager manager = getActivity().getPackageManager();
			PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	private void callLogin()
	{
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.putExtra(getString(R.string.needLoginResult), true);
		startActivityForResult(intent, 521);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		if (requestCode == 521)
		{
			if (Activity.RESULT_OK == resultCode)
			{
				showData();
			}
		} else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	protected AlertDialog showLogoutAlertDialog()
	{
		mAlertDialog = new AlertDialog.Builder(getActivity()).setTitle("是否退出").setMessage("退出后将不能参与聊天,发布帖子等操作.\n是否退出?")
				.setPositiveButton(android.R.string.ok, this).setNegativeButton(android.R.string.cancel, this).show();
		return mAlertDialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		// TODO Auto-generated method stub

		switch (which)
		{
		case Dialog.BUTTON_POSITIVE:
			if (MCkuai.getInstance().LogOut())
			{
				Toast.makeText(getActivity(), "已退出！", Toast.LENGTH_SHORT).show();
				showData();
			}
			break;

		case Dialog.BUTTON_NEGATIVE:
			break;

		default:
			break;
		}
//		MainActivity.getInstance().toggleMenu();
	}

}
