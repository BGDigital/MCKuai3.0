package com.mckuai.imc;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.bean.MCUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

public class ModifyNameActivity extends BaseActivity implements OnClickListener
{
private TextView title;
private ImageView btn_return;
private Button btn_showOwner;
private EditText et_mn;
private String searchContext;//输入新昵称
private MCkuai application;
private MCUser user;
private DisplayImageOptions options;
private AsyncHttpClient client;
private LocationClient locationClient;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_name);
		application = MCkuai.getInstance();
		user = application.getUser();
		client = application.mClient;
		initview();
	}
	
	@Override
		protected void onResume()
		{
			// TODO Auto-generated method stub
			super.onResume();
			MobclickAgent.onPageStart("修改昵称");
		}
	
	@Override
		protected void onPause()
		{
			// TODO Auto-generated method stub
			super.onPause();
			MobclickAgent.onPageEnd("修改昵称");
		}
	public void initview(){
		title =(TextView)findViewById(R.id.tv_title);
		btn_return = (ImageView)findViewById(R.id.btn_left);
		btn_showOwner = (Button)findViewById(R.id.btn_showOwner);
		et_mn = (EditText)findViewById(R.id.et_mn);
		et_mn.setText(application.getUser().getNike());
		title.setText("修改昵称");
		btn_showOwner.setText("保存");
		btn_return.setOnClickListener(this);
		btn_showOwner.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_showOwner:
//			Intent intent =new Intent(this,ChangeInfoActivity.class);
			if (0 < et_mn.getText().length())
			{
				searchContext = et_mn.getText().toString().trim();
				if(!searchContext.equals(application.getUser().getNike())){
					//
					updateUserNick();
				}else {
					Toast.makeText(this, "昵称没有变化", Toast.LENGTH_SHORT).show();}
			}
			if(0 == et_mn.getText().length()) {
				Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	private void updateUserNick()
	{
		RequestParams params = new RequestParams();
		params.put("userId", user.getId());
		params.put("flag", "name");
		params.put("nickName", et_mn.getText().toString());
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
						user.setNike(et_mn.getText().toString());
						et_mn.setTextColor(getResources().getColor(R.color.font_secondary));
						application.setUser(user);
						cancleLodingToast(true);
						finish();
						return;
					}
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				showNotification("更新昵称失败!");
				Toast.makeText(ModifyNameActivity.this, "更新昵称失败!", Toast.LENGTH_SHORT).show();
				cancleLodingToast(false);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				showNotification(3,"更新昵称失败!原因:" + throwable.getLocalizedMessage(),R.id.rl_userNick);
				cancleLodingToast(false);
			}

		});
	}
}
