package com.mckuai.imc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

//import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.adapter.ForumAdapter_Publish;
import com.mckuai.adapter.PostTypeAdapter_Publish;
import com.mckuai.bean.ForumInfo;
import com.mckuai.bean.Post;
import com.mckuai.bean.PostType;
import com.mckuai.fragment.MCSildingMenu;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix.ScaleToFit;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import slidingmenu.SlidingMenu;

public class PublishPostActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener,
		OnFocusChangeListener
{

	private GridView mFroums;
	private GridView mPostType;
	private EditText mTitle;
	private EditText mContent;
	private TextView tv_Title;
	private TextView tv_selectedForum;
	private TextView tv_selectedType;
	private Button btn_publish;
	private ImageButton btn_pic;
	private RadioButton mSelectFroum;
	private RadioButton mSelectType;
	private LinearLayout mTypeLayout;
	private LinearLayout mTypeLayout_Checked;
	private LinearLayout mpics;

	private ForumAdapter_Publish mFroumAdapter;
	private PostTypeAdapter_Publish mTypeAdapter;

	private ArrayList<Bitmap> picsList;// 存储图片之用
	private String picUrl;// 图片上传后的路径
	private int forumId;
	private int typeId;
	private String forumName;
	private String typeName;
	private String postTitle;
	private String postContent;
	private static boolean isUploading = false;
	private static final int LOGIN = 0;
	private static final int GETPIC = 1;

	private SlidingMenu mySlidingMenu;
	private MCSildingMenu menu;
	private MCkuai application;
	private AsyncHttpClient mClient;

	private ArrayList<ForumInfo> mForums;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_post);
		initSlidingMenu();
		application = MCkuai.getInstance();
		mClient = application.mClient;
		Intent intent = getIntent();
		mForums = (ArrayList<ForumInfo>) intent.getSerializableExtra("FORUM_LIST");
		setTitle("发帖");
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("发帖");
		if (null == mFroums)
		{
			initView();
		}
		showData();
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
		MobclickAgent.onPageEnd("发帖");
	}

	@Override
	protected boolean onMenuKeyPressed() {
		mySlidingMenu.toggle();
		return true;
	}

	@Override
	protected boolean onBackKeyPressed() {
		if (isShowingMenu){
			mySlidingMenu.toggle();
			return  true;
		}
		return super.onBackKeyPressed();
	}

	private void initView(){
		mFroums = (GridView) findViewById(R.id.gv_forums);
		mPostType = (GridView) findViewById(R.id.gv_type);
		mTitle = (EditText) findViewById(R.id.edt_title);
		mContent = (EditText) findViewById(R.id.edt_content);
		tv_Title = (TextView) findViewById(R.id.tv_title);
		tv_selectedForum = (TextView) findViewById(R.id.tv_forum_Checked);
		tv_selectedType = (TextView) findViewById(R.id.tv_type_Checked);
		btn_publish = (Button) findViewById(R.id.btn_showOwner);
		btn_pic = (ImageButton) findViewById(R.id.imgbtn_pic);
		mTypeLayout = (LinearLayout) findViewById(R.id.ll_type);
		mTypeLayout_Checked = (LinearLayout) findViewById(R.id.ll_checkedType);
		mpics = (LinearLayout) findViewById(R.id.ll_pics);

		findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		mTypeLayout_Checked.setOnClickListener(this);
		mTitle.setOnFocusChangeListener(this);
		mContent.setOnFocusChangeListener(this);

		// btn_publish.setVisibility(View.VISIBLE);
		btn_publish.setText("发布");
		btn_publish.setOnClickListener(this);
		btn_publish.setVisibility(View.VISIBLE);
		btn_pic.setOnClickListener(this);
		tv_Title.setText(getString(R.string.publishpost));
		initSlidingMenu();
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
				menu.showData();
				menu.callOnResumeForUpdate();
				isShowingMenu = true;
			}
		});
		mySlidingMenu.setOnClosedListener(new SlidingMenu.OnClosedListener()
		{

			@Override
			public void onClosed()
			{
				// TODO Auto-generated method stub
				menu.callOnPauseForUpdate();
				isShowingMenu = false;
				hideKeyboard(mySlidingMenu);
			}
		});
	}

	private void showData()
	{
		if (null != mForums && !mForums.isEmpty())
		{
			if (null == mFroumAdapter)
			{
				mFroumAdapter = new ForumAdapter_Publish(this);
				mFroumAdapter.setOnCheckedChangeListener(this);
				mFroums.setAdapter(mFroumAdapter);
				mFroumAdapter.setData(mForums);

				/*
				 * mFroums.setSelection(0); RadioButton radioButton
				 * =(RadioButton) mFroums.getSelectedView(); if (null !=
				 * radioButton) { radioButton.setChecked(true); }
				 */

				mTypeAdapter = new PostTypeAdapter_Publish(this);
				mTypeAdapter.setOnCheckedChangeListiner(this);
				mPostType.setAdapter(mTypeAdapter);
				mTypeAdapter.show(mFroumAdapter.getItem(0).getIncludeType());
				Message msg = mHandler.obtainMessage();
				mHandler.sendMessageDelayed(msg, 100);
				mTypeLayout.setVisibility(View.VISIBLE);
				mTypeLayout_Checked.setVisibility(View.GONE);
				forumName = mFroumAdapter.getItem(0).getName();
				typeName = mTypeAdapter.getItem(0).getSmallName();
			}
		} else
		{
			btn_publish.setVisibility(View.VISIBLE);
		}

	}

	private void publishPost()
	{
		if (checkPublishInfo())
		{
			forumId = ((ForumInfo) mSelectFroum.getTag()).getId();
			// forumName = ((ForumInfo)mSelectFroum.getTag()).getName();
			forumName = mSelectFroum.getText().toString();
			typeId = ((PostType) mSelectType.getTag()).getSmallId();
			typeName = mSelectType.getText().toString();
			postTitle = mTitle.getText().toString();
			postContent = mContent.getText().toString();

			if (null != picsList && !picsList.isEmpty())
			{
				MobclickAgent.onEvent(this, "picCount_Publish", picsList.size());
				uploadPic();
			} else
			{
				uploadText();
			}
		}
	}

	private void uploadPic()
	{
		String url = "http://www.mckuai.com/" + getString(R.string.interface_uploadpic);
		RequestParams params = new RequestParams();
		params.put("upload", Bitmap2IS(picsList.get(0)), "01.jpg", "image/jpeg");
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
				isUploading = true;
				super.onStart();
				// showNotification("正在上传图片...");
				Toast.makeText(PublishPostActivity.this, "正在上传图片...", Toast.LENGTH_LONG).show();
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
				isUploading = false;
				super.onSuccess(statusCode, headers, response);
				if (response.has("state"))
				{
					try
					{
						if (response.getString("state").equals("ok"))
						{
							picUrl = response.getString("msg");
							if (null != picUrl)
							{
								// showNotification("图片上传完成");
								Toast.makeText(PublishPostActivity.this, "图片上传完成!", Toast.LENGTH_LONG).show();
								isUploading = false;
								uploadText();
								return;
							}
						}
					} catch (Exception e)
					{
						// TODO: handle exception
						// showNotification("上传图片返回内容不正确！");
						Toast.makeText(PublishPostActivity.this, "上传图片返回内容不正确！", Toast.LENGTH_LONG).show();
						return;
					}
				}
				// showNotification("上传图片失败！");
				Toast.makeText(PublishPostActivity.this, "上传图片失败！", Toast.LENGTH_LONG).show();
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
				isUploading = false;
				super.onFailure(statusCode, headers, responseString, throwable);
				showNotification(3,"上传图片失败！原因：" + throwable.getLocalizedMessage(),R.id.ll_top);
			}
		});
	}

	private InputStream Bitmap2IS(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	private void uploadText()
	{
		String url = getString(R.string.interface_domainName) + getString(R.string.interface_sendpost);
		RequestParams params = new RequestParams();
		params.put("userId", application.getUser().getId());
		params.put("forumId", forumId + "");
		params.put("forumName", forumName + "");
		params.put("talkTypeid", typeId + "");
		params.put("talkTypeName", typeName + "");
		params.put("talkTitle", postTitle + "");
		if (null != picUrl && 0 < picUrl.length())
		{
			params.put("content", postContent + picUrl);
		} else
		{
			params.put("content", postContent);
		}
		params.put("device", "android");
		mClient.post(url, params, new JsonHttpResponseHandler()
		{
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				popupLoadingToast("正在发布...");
				isUploading = true;
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				isUploading = false;
				int id = 0;
				if (response.has("state"))
				{
					try
					{
						if (response.getString("state").equalsIgnoreCase("ok"))
						{
							id = Integer.parseInt(response.getString("msg"));
						}
					} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(PublishPostActivity.this, "发帖失败!", Toast.LENGTH_SHORT).show();
						return;
					}
					if (0 != id)
					{
						MobclickAgent.onEvent(PublishPostActivity.this, "publishPost_Success");
						Post post = new Post();
						post.setId(id);
						Intent intent = new Intent(PublishPostActivity.this, PostActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(getString(R.string.tag_post), post);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
						return;
					}
				}
				Toast.makeText(PublishPostActivity.this, "发帖失败!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				isUploading = false;
				// Toast.makeText(PublishPostActivity.this, "发帖失败了,原因:" +
				// throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show();
				showNotification(2,"发帖失败,原因" + throwable.getLocalizedMessage(),R.id.ll_top);
			}

			@Override
			public void onCancel()
			{
				// TODO Auto-generated method stub
				super.onCancel();
				isUploading = false;
			}
		});
	}

	private boolean checkPublishInfo()
	{
		if (null == application.getUser() || 0 == application.getUser().getId())
		{
			callLogin();
			return false;
		}
		if (null == mSelectFroum)
		{
			Toast.makeText(this, "请选择要发帖的板块!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (null == mSelectType)
		{
			Toast.makeText(this, "请选择帖子类型!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (5 > mTitle.getText().toString().length() || 21 < mTitle.getText().length())
		{
			Toast.makeText(this, "标题长度为5-20个字!", Toast.LENGTH_SHORT).show();
			mTitle.setSelected(true);
			return false;
		} else if (mContent.getText().length() < 15 || mContent.getText().length() > 3000)
		{
			Toast.makeText(this, "内容长度为15-3000字!", Toast.LENGTH_SHORT).show();
			mContent.setSelected(true);
			return false;
		} else
		{
			return true;
		}
	}

	private void callLogin()
	{
		Intent intent = new Intent(PublishPostActivity.this, LoginActivity.class);
		intent.putExtra(getString(R.string.needLoginResult), true);
		startActivityForResult(intent, LOGIN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		if (Activity.RESULT_OK == resultCode)
		{
			switch (requestCode)
			{
			case LOGIN:
				publishPost();
				break;
			case GETPIC:
				addPic(data);
				//Log.e(TAG, data.getDataString());
				break;

			default:

				break;
			}
		} else
		{
			Toast.makeText(this, "未登录,不能发帖!", Toast.LENGTH_SHORT).show();
			// showNotification("未登录,不能发帖!");
		}
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if (isUploading)
		{
			if (v.getId() != R.id.btn_left)
			{
				Toast.makeText(this, "正在上传,请稍候再操作!", Toast.LENGTH_SHORT).show();
				// showNotification("正在上传,请稍候再操作!");
				return;
			} else
			{
				mClient.cancelRequests(this, true);
			}
		}
		switch (v.getId())
		{
		case R.id.btn_showOwner:
			MobclickAgent.onEvent(this, "publishPost");
			publishPost();
			break;

		case R.id.imgbtn_pic:
			MobclickAgent.onEvent(this, "addPic_Publish");
			Intent intent = new Intent(Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, GETPIC);
			break;

		case R.id.btn_left:
			if (isShowingMenu)
			{
				mySlidingMenu.toggle();
			} else
			{
				this.finish();
			}
			break;

		case R.id.ll_checkedType:
			mTypeLayout_Checked.setVisibility(View.GONE);
			mTypeLayout.setVisibility(View.VISIBLE);
            mTypeLayout_Checked.setFocusable(true);
            mTypeLayout_Checked.setFocusableInTouchMode(true);
			break;

		default:
			break;
		}
	}

	private void addPic(Intent data)
	{
		if (null != data)
		{
			// 取出所选图片的路径
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
			opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
			opts.inJustDecodeBounds = false;
			final Bitmap bmp;
			try
			{
				bmp = BitmapFactory.decodeFile(picturePath, opts);
			} catch (OutOfMemoryError err)
			{
				// showNotification("图片过大!");
				Toast.makeText(this, "图片过大!", Toast.LENGTH_SHORT).show();
				return;
			}
			if (null == picsList)
			{
				picsList = new ArrayList<Bitmap>(4);
			}
			picsList.add(bmp);

			// 将图片贴到imageview
			final ImageView image = new ImageView(PublishPostActivity.this);
			LayoutParams params = (LayoutParams) btn_pic.getLayoutParams();
			params.width = btn_pic.getWidth();
			params.height = btn_pic.getHeight();
			image.setScaleType(ScaleType.CENTER_CROP);
			image.setLayoutParams(params);
			image.setImageBitmap(bmp);
			image.setClickable(true);
			image.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (null != mpics && mpics.getChildCount() > 0) {
						mpics.removeView(image);
						picsList.remove(bmp);
						mpics.postInvalidate();
						if (mpics.getChildCount() < 5) {
							btn_pic.setVisibility(View.VISIBLE);
						}
						return true;
					}
					return false;
				}
			});
			if (4 == picsList.size())
			{
				btn_pic.setVisibility(View.GONE);
			}
			mpics.addView(image, picsList.size() - 1);
			mpics.postInvalidate();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		// TODO Auto-generated method stub
		if (isChecked)
		{
			ViewGroup parent = (ViewGroup) buttonView.getParent();
			switch (parent.getId())
			{
			case R.id.gv_forums:
				if (null != mSelectFroum)
				{
					mSelectFroum.setChecked(false);
				}
				mSelectFroum = (RadioButton) buttonView;
				mSelectFroum.setChecked(true);
                ForumInfo forumInfo = (ForumInfo) mSelectFroum.getTag();
                forumName = forumInfo.getName();
				mTypeAdapter.show(forumInfo.getIncludeType());
				((RadioButton) mPostType.getChildAt(0)).setChecked(true);
                typeName = ((RadioButton) mPostType.getChildAt(0)).getText().toString();
                tv_selectedForum.setText(forumName);
                tv_selectedType.setText(typeName);
				break;

			case R.id.gv_type:
				if (null != mSelectType)
				{
					mSelectType.setChecked(false);
				}
				mSelectType = (RadioButton) buttonView;
				mSelectType.setChecked(true);
				typeName = buttonView.getText().toString();
                mTypeLayout.setVisibility(View.GONE);
                mTypeLayout_Checked.setVisibility(View.VISIBLE);
                tv_selectedType.setText(typeName);
				break;

			default:
				break;
			}
		}
	}

	Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			RadioButton radioButton = (RadioButton) mFroums.getChildAt(0);
			if (null != radioButton)
			{
				radioButton.setChecked(true);
			} else
			{
				sendMessageDelayed(mHandler.obtainMessage(0), 100);
			}
		};
	};

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		// TODO Auto-generated method stub
		tv_selectedForum.setText(mSelectFroum.getText().toString());
		tv_selectedType.setText(mSelectType.getText().toString());
		mTypeLayout.setVisibility(View.GONE);
		mTypeLayout_Checked.setVisibility(View.VISIBLE);
	}
/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU || (keyCode == KeyEvent.KEYCODE_BACK && isShowingMenu))
		{
//			mySlidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

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
}
