/**
 * 
 */

package com.mckuai.widget;

import javax.security.auth.PrivateCredentialPermission;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.mckuai.bean.Post;
import com.mckuai.imc.R;
import com.tencent.connect.share.QzoneShare;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener
{

	private UMSocialService mController;
	private Activity mActivity;
	private Post mPost;

	public CustomShareBoard(Activity activity,UMSocialService service,Post post)
	{
		super(activity);
		this.mActivity = activity;
		this.mPost = post;
		this.mController = service;
//		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
//		mController = UMServiceFactory.getUMSocialService("www.mckuai.com");
		initView(activity);
		configPlatforms();
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context)
	{
		View rootView = LayoutInflater.from(context).inflate(R.layout.custom_board, null);
		rootView.findViewById(R.id.wechat).setOnClickListener(this);
		rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
		rootView.findViewById(R.id.qq).setOnClickListener(this);
		rootView.findViewById(R.id.qzone).setOnClickListener(this);
		setContentView(rootView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}
	
	private void configPlatforms(){
		String appID_QQ = "101155101";
		String appAppKey_QQ = "78b7e42e255512d6492dfd135037c91c";
		//添加qq
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity, appID_QQ, appAppKey_QQ);
		qqSsoHandler.addToSocialSDK();
		// 添加QQ空间参数
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mActivity, appID_QQ, appAppKey_QQ);
		qZoneSsoHandler.addToSocialSDK();
		
		String appID_WX = "wx49ba2c7147d2368d";
		String appSecret_WX = "85aa75ddb9b37d47698f24417a373134";
		//添加微信
		UMWXHandler wxHandler = new UMWXHandler(mActivity, appID_WX, appSecret_WX);
		wxHandler.setRefreshTokenAvailable(true);
		wxHandler.addToSocialSDK();
		//添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, appID_WX, appSecret_WX);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.setRefreshTokenAvailable(true);
		wxCircleHandler.addToSocialSDK();
		//移除多余平台
		mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT,SHARE_MEDIA.SINA);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.wechat:
			performShare(SHARE_MEDIA.WEIXIN);
			break;
		case R.id.wechat_circle:
			performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			break;
		case R.id.qq:
			performShare(SHARE_MEDIA.QQ);
			break;
		case R.id.qzone:
			performShare(SHARE_MEDIA.QZONE);
			break;
		default:
			break;
		}
	}

	private void performShare(SHARE_MEDIA platform)
	{
		String title ="麦块for我的世界盒子";
		String targetUrl = "http://www.mckuai.com/thread-"+mPost.getId() + ".html";
		String content= mPost.getTalkTitle();
		UMImage image =null;
		if (null != mPost.getMobilePic() && 10 < mPost.getMobilePic().length())
		{
			image = new UMImage(mActivity, mPost.getMobilePic());
		}
		
		switch (platform)
		{
		case WEIXIN:
			WeiXinShareContent wxContent = new WeiXinShareContent();
			wxContent.setTargetUrl(targetUrl);
			wxContent.setTitle(title);
			wxContent.setShareContent(content);
			if (null != image)
			{
				wxContent.setShareImage(image);
			}
			mController.setShareMedia(wxContent);
			break;
		case WEIXIN_CIRCLE:
			CircleShareContent circleContent = new CircleShareContent();
			circleContent.setTargetUrl(targetUrl);
			circleContent.setTitle(title);
			circleContent.setShareContent(content);
			if (null != image)
			{
				circleContent.setShareImage(image);
			}
			mController.setShareMedia(circleContent);
			break;
		case QQ:
			QQShareContent qqContent = new QQShareContent();
			qqContent.setTargetUrl(targetUrl);
			qqContent.setTitle(title);
			qqContent.setShareContent(content);
			if (null != image )
			{
				qqContent.setShareImage(image);
			}
			mController.setShareMedia(qqContent);
			break;
		case QZONE:
			QZoneShareContent qzoneContent = new QZoneShareContent();
			qzoneContent.setTargetUrl(targetUrl);
			qzoneContent.setTitle(title);
			qzoneContent.setShareContent(content);
			if (null != image )
			{
				qzoneContent.setShareImage(image);
			}
			mController.setShareMedia(qzoneContent);
			break;

		default:
			break;
		}
		mController.postShare(mActivity, platform, new SnsPostListener()
		{

			@Override
			public void onStart()
			{
				
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity)
			{
				if (eCode == StatusCode.ST_CODE_SUCCESSED)
				{
					Toast.makeText(mActivity, "分享成功！", Toast.LENGTH_SHORT).show();
				} else if(-101 == eCode)
				{
					Toast.makeText(mActivity, "分享失败！没有授权！", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(mActivity, "分享失败！", Toast.LENGTH_SHORT).show();
				}
				
				dismiss();
			}
		});
	}

}
