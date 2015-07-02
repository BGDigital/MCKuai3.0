package com.mckuai.wxapi;
import android.os.Bundle;
import android.util.Log;

import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity
{
	
	/* (non-Javadoc)
	 * @see com.umeng.socialize.weixin.view.WXCallbackActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("WXEntryActivity", "onCreate");
	}
}
