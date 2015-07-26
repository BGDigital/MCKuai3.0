package com.mckuai.imc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.Header;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.mckuai.bean.MCUser;
import com.mckuai.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ChangeInfoActivity extends BaseActivity implements OnClickListener, OnEditorActionListener
{
	private TextView tv_title;
	private TextView tv_nick;
	private TextView tv_location;
	private EditText edt_nick;
	private CircleImageView civ_cover;

	private MCUser user;
	private Bitmap bmp_Cover;
	private String coverUrl;
	private boolean isUploading = false;

	private MCkuai application;
	private ImageLoader loader;
	private DisplayImageOptions options;
	private AsyncHttpClient client;
	private LocationClient locationClient;
	private MyLocationListener listener;

	private static final int GETPIC = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_info);
		application = MCkuai.getInstance();
		loader = ImageLoader.getInstance();
		options = application.getCircleOption();
		client = application.mClient;
		initView();
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("修改资料");
		if (application.isLogin())
		{
			user = application.getUser();
		} else
		{
			user = null;
		}
		showData();
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("修改资料");
	}

	private void initView()
	{
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_nick = (TextView) findViewById(R.id.tv_nick_input);
		tv_location = (TextView) findViewById(R.id.tv_location_input);
		edt_nick = (EditText) findViewById(R.id.edt_nick);
		civ_cover = (CircleImageView) findViewById(R.id.civ_UserHead);
		findViewById(R.id.btn_showOwner).setVisibility(View.GONE);

		tv_title.setText("用户信息");

		findViewById(R.id.btn_left).setOnClickListener(this);
		tv_location.setOnClickListener(this);
		tv_nick.setOnClickListener(this);
		civ_cover.setOnClickListener(this);
		edt_nick.setOnEditorActionListener(this);
		edt_nick.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (!hasFocus)
				{
					edt_nick.setVisibility(View.GONE);
					tv_nick.setVisibility(View.VISIBLE);
				} else
				{
					edt_nick.setVisibility(View.VISIBLE);
					tv_nick.setVisibility(View.GONE);
				}
			}
		});
	}

	private void showData()
	{
		if (null != user)
		{
			tv_nick.setText(user.getNike() + "");
			tv_location.setText(user.getAddr() + "");
			if (null != user.getHeadImg() && 10 < user.getHeadImg().length())
			{
				loader.displayImage(user.getHeadImg(), civ_cover, options);
			} else
			{
				civ_cover.setImageResource(R.drawable.background_user_cover_default);
			}
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
		case R.id.civ_UserHead:
			changeCover();
			MobclickAgent.onEvent(this, "changeCover");
			break;
		case R.id.btn_left:
			finishActivity();
			break;
		case R.id.tv_location_input:
			MobclickAgent.onEvent(this, "updateLocation_UserInfoSetting");
			Intent intent = new Intent(this, LocationActivity.class);
			startActivity(intent);
			// getCurrentLocation();
			break;

		case R.id.tv_nick_input:
			MobclickAgent.onEvent(this, "changeNick");
			Intent intent2 = new Intent(this, ModifyNameActivity.class);
			startActivity(intent2);
			// edt_nick.setText(user.getNike());
			// edt_nick.setVisibility(View.VISIBLE);
			// tv_nick.setVisibility(View.GONE);
			// edt_nick.setActivated(true);
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != locationClient)
		{
			if (locationClient.isStarted())
			{
				locationClient.stop();
			}
			locationClient.unRegisterLocationListener(listener);
			listener = null;
			locationClient = null;
		}
		application.setUser(user);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			if (edt_nick.getVisibility() == View.VISIBLE)
			{
				edt_nick.setText(tv_nick.getText());
				edt_nick.setVisibility(View.GONE);
				tv_nick.setVisibility(View.VISIBLE);
				return true;
			}
			finishActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode)
		{
			switch (requestCode)
			{
			case GETPIC:
				getNewCover(data);
				break;

			default:
				break;
			}
		}
	}

	private void finishActivity()
	{
		if (isUploading)
		{
            showNotification(1,"正在更新用户信息,请稍候!",R.id.root);
		} else
		{
			this.finish();
		}
	}

	private void changeCover()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, GETPIC);
	}

	private void getCurrentLocation()
	{
		if (null == locationClient)
		{
			listener = new MyLocationListener();
			locationClient = new LocationClient(getApplicationContext());
			locationClient.setLocOption(getOption());
			locationClient.registerLocationListener(listener);
			locationClient.start();
			locationClient.requestLocation();
		}
	}

	// 更新文字信息,如昵称
	private void updateUserNick()
	{
		RequestParams params = new RequestParams();
		params.put("userId", user.getId());
		params.put("flag", "name");
		params.put("nickName", edt_nick.getText().toString());
		String url = getString(R.string.interface_domainName) + getString(R.string.interface_update_userinfo);
		client.post(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				popupLoadingToast("正在应用昵称");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try
				{
					if (response.has("state") && response.getString("state").equalsIgnoreCase("ok"))
					{
						user.setNike(tv_nick.getText().toString());
						tv_nick.setTextColor(getResources().getColor(R.color.font_secondary));
                        showNotification(1,"昵称更新成功！",R.id.root);
						cancleLodingToast(true);
						return;
					}
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                showNotification(1,"更新昵称失败！",R.id.root);
				cancleLodingToast(false);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				showNotification(1,"更新昵称失败!原因:" + throwable.getLocalizedMessage(),R.id.root);
				cancleLodingToast(false);
			}

		});
	}

	// 上传头像
	private void uploadUserCover()
	{
		String url = "http://www.mckuai.com/" + getString(R.string.interface_uploadpic);
		RequestParams params = new RequestParams();
		params.put("fileHeadImg", Bitmap2IS(bmp_Cover), "cover.jpg", "image/jpeg");
		client.post(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				isUploading = true;
				super.onStart();
				popupLoadingToast("正在上传头像");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				isUploading = false;
				super.onSuccess(statusCode, headers, response);
				if (response.has("state"))
				{
					try
					{
						if (response.getString("state").equals("ok"))
						{
							coverUrl = response.getString("msg");
							if (null != coverUrl)
							{
                                showNotification(1, "头像上传完成！", R.id.root);
								cancleLodingToast(true);
								updateCoverUrl();
								return;
							}
						}
					} catch (Exception e)
					{
						// TODO: handle exception
                        showNotification(1, "上传图片返回内容不正确！", R.id.root);
						cancleLodingToast(false);
						return;
					}
				}
                showNotification(1, "上传图片失败！", R.id.root);
				cancleLodingToast(false);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				isUploading = false;
				super.onFailure(statusCode, headers, responseString, throwable);
                showNotification(1,"上传图片失败！原因：" + throwable.getLocalizedMessage(), R.id.root);
				cancleLodingToast(false);
			}
		});
	}

	private void updateCoverUrl()
	{
		String url = getString(R.string.interface_domainName) + getString(R.string.interface_update_userinfo);
		RequestParams params = new RequestParams();
		params.put("userId", user.getId());
		params.put("flag", "headImg");
		params.put("headImg", coverUrl);
		client.get(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				popupLoadingToast("正在设置头像");
				isUploading = true;
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				isUploading = false;
				super.onSuccess(statusCode, headers, response);
				try
				{
					if (response.has("state") && response.getString("state").equalsIgnoreCase("ok"))
					{
						user.setHeadImg(coverUrl);
						loader.displayImage(coverUrl, civ_cover, options);
                        showNotification(1, "头像更新成功！", R.id.root);
						cancleLodingToast(true);
						return;
					}
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cancleLodingToast(false);
                showNotification(1, "头像更新失败！", R.id.root);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				isUploading = false;
				super.onFailure(statusCode, headers, responseString, throwable);
                showNotification(1, "头像更新失败！原因：" + throwable.getLocalizedMessage(), R.id.root);
				cancleLodingToast(false);
			}
		});
	}

	// 更新当前位置
	private void updateLocation()
	{
		RequestParams params = new RequestParams();
		params.put("id", user.getId());
		params.put("addr", tv_location.getText().toString());
		String url = getString(R.string.interface_domainName) + getString(R.string.interface_updateLocation);
		client.post(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				isUploading = true;
                showNotification(1, "正在更新位置...", R.id.root);
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				isUploading = false;
				super.onSuccess(statusCode, headers, response);
				try
				{
					if (response.has("state") && response.getString("state").equalsIgnoreCase("ok"))
					{
						tv_location.setTextColor(getResources().getColor(R.color.font_secondary));
						user.setAddr(tv_location.getText().toString());
                        showNotification(1, "更新位置成功！", R.id.root);
						cancleLodingToast(true);
						return;
					}
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
                    showNotification(1, "更新位置失败！", R.id.root);
					cancleLodingToast(false);
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				isUploading = false;
				super.onFailure(statusCode, headers, responseString, throwable);
                showNotification(1,"更新位置失败!原因:" + throwable.getLocalizedMessage(), R.id.root);
				cancleLodingToast(false);
			}
		});
	}

	private void getNewCover(Intent data)
	{
		if (null != data)
		{
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			// 获取图片
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picturePath, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, 1080 * 700);
			opts.inJustDecodeBounds = false;
			Bitmap bmp = null;
			try
			{
				bmp = BitmapFactory.decodeFile(picturePath, opts);
			} catch (OutOfMemoryError err)
			{
                showNotification(1, "图片过大！", R.id.root);
				return;
			}
			civ_cover.setImageBitmap(bmp);
			// isCoverChanged = true;
			bmp_Cover = bmp;
			uploadUserCover();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (actionId == EditorInfo.IME_ACTION_DONE)
		{
			if (0 < edt_nick.getText().length())
			{
				if (!user.getNike().equalsIgnoreCase(edt_nick.getText().toString()))
				{
					// isNickChanged = true;
					tv_nick.setText(edt_nick.getText().toString());
					tv_nick.setTextColor(getResources().getColor(R.color.font_green));
					updateUserNick();
				}
				tv_nick.setVisibility(View.VISIBLE);
				edt_nick.setVisibility(View.GONE);
			} else
			{
                showNotification(1, "请输入用户名！", R.id.root);
			}
		}
		return false;
	}

	// 加载大图时,计算缩放比例,以免出现OOM
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
	{
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8)
		{
			roundedSize = 1;
			while (roundedSize < initialSize)
			{
				roundedSize <<= 1;
			}
		} else
		{
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
	{
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));

		if (upperBound < lowerBound)
		{
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1))
		{
			return 1;
		} else if (minSideLength == -1)
		{
			return lowerBound;
		} else
		{
			return upperBound;
		}
	}

	private LocationClientOption getOption()
	{
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);// 返回的定位结果包含手机机头的方向
		return option;
	}

	public class MyLocationListener implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			// TODO Auto-generated method stub

			if (null == location || null == location.getCity() || 0 == location.getCity().length())
			{
				return;
			}
			if (!tv_location.getText().equals(location.getCity()))
			{
				tv_location.setText(location.getCity());
				tv_location.setTextColor(getResources().getColor(R.color.font_green));
				updateLocation();
			}
			locationClient.stop();
		}
	}

	// 将bitmap转成流，以便于上传
	private InputStream Bitmap2IS(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

}
