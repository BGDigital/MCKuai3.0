package com.mckuai.imc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.bean.MCUser;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends  BaseActivity implements View.OnClickListener{


    private final  String TAG = "LoginActivity";

    private TextView tv_title;

    private static Tencent mTencent;
    private UserInfo mInfo;// QQ返回的用户信息
    private static boolean mAutoLogin = false;
    private static MCUser mUser_MC;// 平台中的用户信息
    private static long mQQToken_Birthday;// 产生日期
    private static long mQQToken_Expires;// 有效期
    private static String mQQToken;// QQ登录后所获取到的token
    private static MCkuai application;
    private AsyncHttpClient mClint;
    private static boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("登录");
        setContentView(R.layout.activity_login);
        application = MCkuai.getInstance();
        mTencent = application.tencent;
        mClint = application.mClient;
        getProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getmTitle());
        initView();
    }

    private void initView(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getmTitle());
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_anonymous).setOnClickListener(this);
        findViewById(R.id.btn_left).setOnClickListener(this);
    }

    private void getProfile()
    {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), 0);
        mUser_MC = new MCUser();
        mAutoLogin = preferences.getBoolean(getString(R.string.enable_autologin), false);
        mUser_MC.setLevel(preferences.getInt("MC_LEVEL", 0));
        mUser_MC.setProcess(preferences.getFloat("MC_PROCESS", 0f));
        mUser_MC.setAddr(preferences.getString("MC_ADDR", null));
        mUser_MC.setId(preferences.getInt(getString(R.string.mc_id), 0));
        mUser_MC.setName(preferences.getString(getString(R.string.qq_OpenId), null));// qq_OpenId用于融云的用户名
        mUser_MC.setHeadImg(preferences.getString(getString(R.string.mc_userFace), null));// 用户头像
        mUser_MC.setNike(preferences.getString(getString(R.string.mc_nick), null));// 显示名
        mUser_MC.setGender(preferences.getString(getString(R.string.mc_gender), null));// 性别
        mUser_MC.setToken(preferences.getString(getString(R.string.rcToken), null));// 融云令牌
        mQQToken_Birthday = preferences.getLong(getString(R.string.tokenTime), 0);
        mQQToken_Expires = preferences.getLong(getString(R.string.tokenExpires), 0);
        mUser_MC.setProcess(preferences.getFloat("USER_PROCESS",0f));
        // 检查qq的token是否有效如果在有效期内则获取qqtoken
        if (verificationTokenLife(mQQToken_Birthday, mQQToken_Expires))
        {
            mQQToken = preferences.getString(getString(R.string.qqToken), null);// qqtoken，登录MC服务器用
        }
    }

    private void saveProfile()
    {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("MC_LEVEL", mUser_MC.getLevel());
        editor.putFloat("MC_PROCESS", mUser_MC.getProcess());
        editor.putString("MC_ADDR", mUser_MC.getAddr());
        editor.putBoolean(getString(R.string.enable_autologin), true);
        editor.putString(getString(R.string.qq_OpenId), mUser_MC.getName() + "");
        editor.putString(getString(R.string.mc_userFace), mUser_MC.getHeadImg() + "");
        editor.putString(getString(R.string.mc_nick), mUser_MC.getNike() + "");
        editor.putString(getString(R.string.mc_gender), mUser_MC.getGender() + "");
        editor.putString(getString(R.string.rcToken), mUser_MC.getToken() + "");
        editor.putString(getString(R.string.qqToken), mQQToken + "");
        editor.putLong(getString(R.string.tokenTime), mQQToken_Birthday);
        editor.putLong(getString(R.string.tokenExpires), mQQToken_Expires);
        editor.putInt(getString(R.string.mc_id), mUser_MC.getId());
        editor.putFloat("USER_PROCESS",mUser_MC.getProcess());

        editor.commit();
    }

    private boolean verificationTokenLife(Long birthday, long expires)
    {
        return (System.currentTimeMillis() - birthday) < expires * 1000;
    }

    private void logoutQQ()
    {
        if (null != mTencent)
        {
            mAutoLogin = false;
            isLogin = false;
            mTencent.logout(LoginActivity.this);
        }
    }


    private void loginToQQ()
    {
        if (mTencent != null)
        {
            mTencent.login(this, "all", new BaseUiListener() {
                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * com.youxigt.imc.ui.LoginActivity.BaseUiListener#doComplete
                 * (org.json.JSONObject)
                 */
                @Override
                protected void doComplete(JSONObject values) {
                    // TODO Auto-generated method stub
                    initOpenidAndToken(values);
                }
            });
        }
    }

    private void loginToMC()
    {
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_qqlogin);
        final RequestParams params = new RequestParams();
        // 添加参数
        params.put("accessToken", mQQToken);
        params.put("openId", mUser_MC.getName());
        params.put("nickName", mUser_MC.getNike());
        params.put("gender", mUser_MC.getGender());
        params.put("headImg", mUser_MC.getHeadImg());
        String requestUrl = url + "&" + params.toString();
        mClint.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                popupLoadingToast("正在登录到麦块!");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, response);
                isLogin = false;
                Gson gson = new Gson();
                MCUser tempUser = null;
                if (response.has("state")) {
                    try {
                        if (response.getString("state").equalsIgnoreCase("ok")) {
                            // 开始解析
                            tempUser = gson.fromJson(response.getString("dataObject"), MCUser.class);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        cancleLodingToast(false);
                        showNotification(3, "登录到麦块时失败！" + e.getLocalizedMessage(), R.id.rl_login_root);
                        return;
                    }
                    if (null != tempUser) {
                        tempUser.setGender(mUser_MC.getGender());
                        application.setUser(tempUser);
                        application.mUser = tempUser;
                        mUser_MC = application.getUser();
                    }
                    mAutoLogin = true;
                    saveProfile();
                    cancleLodingToast(true);
                    handleResult(true);
                    return;
                }
                cancleLodingToast(false);
                showNotification(1, "登录到麦块时失败！", R.id.rl_login_root);
                logoutQQ();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, headers, responseString, throwable);
                cancleLodingToast(false);
				showNotification(1, "登录失败，原因：" + throwable.getLocalizedMessage(), R.id.rl_login_root);
                logoutQQ();
            }
        });
    }

    private void handleResult(Boolean result){
        setResult(true == result ? RESULT_OK:RESULT_CANCELED);
        this.finish();
    }





    private void updateUserInfo()
    {
        if (mTencent != null && mTencent.isSessionValid())
        {
            IUiListener listener = new IUiListener()
            {

                @Override
                public void onError(UiError e)
                {
                    logoutQQ();
                    showMessage(getString(R.string.loginFail), getString(R.string.loginFail_QQ));
                }

                @Override
                public void onComplete(final Object response)
                {
                    JSONObject json = (JSONObject) response;
                    if (json.has("nickname"))
                    {
                        try
                        {
                            Log.e("updateUserInfo", "");
                            mUser_MC.setNike(json.getString("nickname"));
                            mUser_MC.setHeadImg(json.getString("figureurl_2"));// 取空间头像做为头像
                            mUser_MC.setGender(json.getString("gender"));
                            mUser_MC.setAddr(json.getString("city"));
                            application.mUser = mUser_MC;
                        } catch (Exception e)
                        {
                            // TODO: handle exception
                            showMessage(getString(R.string.loginFail),
                                    getString(R.string.loginFail_type) + json.toString());
                            mTencent.logout(LoginActivity.this);
                            return;
                        }
                        loginToMC();
                    }
                }

                @Override
                public void onCancel()
                {
                    logoutQQ();
                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else
        {
            showMessage(getString(R.string.loginFail), getString(R.string.loginFail_nouser));
            logoutQQ();
        }
    }

    public static boolean initOpenidAndToken(JSONObject jsonObject)
    {
        try
        {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            mQQToken = token;
            mUser_MC.setName(openId);
            mQQToken_Birthday = System.currentTimeMillis() - 600000;
            mQQToken_Expires = jsonObject.getLong(Constants.PARAM_EXPIRES_IN);
//			Log.e(TAG, "QQToken_Expires=" + mQQToken_Expires);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId))
            {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e)
        {
            // mAutoLogin = false;
            isLogin = false;
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (null != mQQToken){
                    loginToMC();
                }
                else {
                    loginToQQ();
                }
                break;
            default:
                handleResult(false);
                break;
        }
    }


private class BaseUiListener implements IUiListener
    {

        /*
         * (non-Javadoc)
         *
         * @see com.tencent.tauth.IUiListener#onCancel()
         */
        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            logoutQQ();
        }

        /*
         * (non-Javadoc)
         *
         * @see com.tencent.tauth.IUiListener#onComplete(java.lang.Object)
         */
        @Override
        public void onComplete(Object response)
        {
            // TODO Auto-generated method stub
            if (null == response)
            {
                showMessage(getString(R.string.loginFail), getString(R.string.loginFailContext_QQ));
                logoutQQ();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0)
            {
                showMessage(getString(R.string.loginFail), getString(R.string.loginFailContext_QQ));
                logoutQQ();
                return;
            }
            doComplete(jsonResponse);
            updateUserInfo();
        }

        /*
         * (non-Javadoc)
         *
         * @see com.tencent.tauth.IUiListener#onError(com.tencent.tauth.UiError)
         */
        @Override
        public void onError(UiError arg0)
        {
            // TODO Auto-generated method stub
            mTencent.logout(LoginActivity.this);
            showMessage(getString(R.string.loginFail), arg0.errorMessage + "," + getString(R.string.loginFailCode)
                    + arg0.errorCode + "\t" + arg0.errorDetail);
        }

        protected void doComplete(JSONObject values)
        {

        }
    }




}

