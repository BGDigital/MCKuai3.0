package com.mckuai.imc;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.AndroidCharacter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.loopj.android.http.RequestParams;
import com.mckuai.utils.JsonCache;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import net.steamcrafted.loadtoast.LoadToast;


public class BaseActivity extends AppCompatActivity {

    protected boolean isLoading = false;    //正在加载数据
    protected boolean isCacheEnabled = true;         //启用缓存
    protected boolean isSecondCacheEnable = true;//第二个项目的缓存
    protected boolean isShowingMenu = false;
    protected String mTitle;
    private com.gitonway.lee.niftynotification.lib.Configuration msgCfg;
    private com.gitonway.lee.niftynotification.lib.Configuration warningCfg;
    private com.gitonway.lee.niftynotification.lib.Configuration errorCfg;
    private LoadToast mToast;
    private Point mPoint;
    private JsonCache mCache = MCkuai.getInstance().mCache;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        PushAgent.getInstance(this).onAppStart();
        Debug.startMethodTracing("mckial");
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Debug.stopMethodTracing();
    }

    protected void setTitle(String title) {
        if (null != title) {
            mTitle = title;
        }
    }

    public String getmTitle() {
        return mTitle;
    }

    /**
     * 显示通知
     * @param level 通知级别， 2为错误，1为警告，其它为普通
     * @param msg 通知内容
     * @param rootId 用于显示通知的viewgroup
     */
    protected void showNotification(final int level,final String msg,final int rootId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NiftyNotificationView.build(BaseActivity.this, msg, Effects.flip, rootId, getNotificationConfiguration(level)).show();
            }
        });
    }

    private com.gitonway.lee.niftynotification.lib.Configuration getNotificationConfiguration(int level) {
        switch (level) {
            case 2:
                if (null == errorCfg) {
                    errorCfg = new com.gitonway.lee.niftynotification.lib.Configuration.Builder()
                            .setAnimDuration(700)
                            .setDispalyDuration(1500)
                            .setBackgroundColor(getColorString(R.color.notification_background))
                            .setTextColor(getColorString(R.color.notification_textColor))
                                    // .setIconBackgroundColor("#FFFFFFFF")
                            .setTextPadding(5)                      //单位dp
                            .setViewHeight(48)                      //单位dp
                            .setTextLines(2)                        //最好同时指定行数和高度
                            .setTextGravity(Gravity.CENTER)         //仅只有文字内容时，使用 Gravity.CENTER,如果有图标，则需使用Gravity.CENTER_VERTICAL
                            .build();
                }
                return errorCfg;

            case 1:
                if (null == warningCfg) {
                    warningCfg = new com.gitonway.lee.niftynotification.lib.Configuration.Builder()
                            .setAnimDuration(700)
                            .setDispalyDuration(1500)
                            .setBackgroundColor(getColorString(R.color.notification_background))
                            .setTextColor(getColorString(R.color.notification_textColor))
                                    // .setIconBackgroundColor("#FFFFFFFF")
                            .setTextPadding(5)                      //单位dp
                            .setViewHeight(48)                      //单位dp
                            .setTextLines(2)                        //最好同时指定行数和高度
                            .setTextGravity(Gravity.CENTER)         //仅只有文字内容时，使用 Gravity.CENTER,如果有图标，则需使用Gravity.CENTER_VERTICAL
                            .build();
                }
                return warningCfg;
            default:
                if (null == msgCfg) {
                    msgCfg = new com.gitonway.lee.niftynotification.lib.Configuration.Builder()
                            .setAnimDuration(700)
                            .setDispalyDuration(1500)
                            .setBackgroundColor(getColorString(R.color.notification_background))
                            .setTextColor(getColorString(R.color.notification_textColor))
                                    // .setIconBackgroundColor("#FFFFFFFF")
                            .setTextPadding(5)                      //单位dp
                            .setViewHeight(48)                      //单位dp
                            .setTextLines(2)                        //最好同时指定行数和高度
                            .setTextGravity(Gravity.CENTER)         //仅只有文字内容时，使用 Gravity.CENTER,如果有图标，则需使用Gravity.CENTER_VERTICAL
                            .build();
                }
                return msgCfg;
        }
    }

    private String getColorString(int colorResId) {
        int color = getResources().getColor(colorResId);
        String c = "#" + Integer.toHexString(color);
        return c.toUpperCase();
    }

    protected void showMessage(String title, String msg) {
        NiftyDialogBuilder mMessageDialogBuilder = NiftyDialogBuilder.getInstance(this);
        mMessageDialogBuilder.withTitle(title + "")
                .withMessage(msg + "")
                .show();
    }


    protected void showAlert(final String title,final  String msg, final View.OnClickListener onCancle, final View.OnClickListener onOk) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final NiftyDialogBuilder mAlertDialogBuilder = NiftyDialogBuilder.getInstance(BaseActivity.this);
                mAlertDialogBuilder.withTitle(title + "")
                        .withMessage(msg + "")
                        .withEffect(Effectstype.Newspager);
                if (null != onCancle) {
                    mAlertDialogBuilder.withButton2Text("取消")
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onCancle.onClick(v);
                                    mAlertDialogBuilder.dismiss();
                                }
                            });
                }
                if (null != onOk) {
                    mAlertDialogBuilder.withButton1Text("确定")
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onOk.onClick(v);
                                    mAlertDialogBuilder.dismiss();
                                }
                            });
                }
                mAlertDialogBuilder.show();
            }
        });

    }

    /**
     * 重写此方法时请勿调用super方法
     *
     * @return
     */
    protected boolean onMenuKeyPressed() {
        return false;
    }

    /**
     * 重写此方法时请勿调用super方法
     *
     * @return
     */
    protected boolean onBackKeyPressed() {
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (onMenuKeyPressed()) {
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onBackKeyPressed()){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void popupLoadingToast(String msg) {
        if (null == mToast) {
            if (null == mPoint) {
                mPoint = new Point();
                getWindowManager().getDefaultDisplay().getSize(mPoint);
            }
            mToast = new LoadToast(this);
            mToast.setTranslationY((int) (mPoint.y * 0.4));
            mToast.setTextColor(getResources().getColor(R.color.font_white)).setBackgroundColor(
                    getResources().getColor(R.color.background_green));
        }
        mToast.setText(msg);
        mToast.show();
        _mHander.sendEmptyMessageDelayed(1, 15000);
    }

    /**
     * 将数据缓存到缓存中
     *
     * @param url  数据所归属的url
     * @param data 要缓存的数据
     */
    public void cacheData(String url, String data) {
        mCache.put(url, data);
    }

    /**
     * 将数据缓存到缓存中
     *
     * @param url    数据所归属的url
     * @param params 对应的请求参数
     * @param data   要缓存的数据
     */
    public void cacheData(String url, RequestParams params, String data) {
        if (null == params) {
            mCache.put(url, data);
        } else {
            mCache.put((url + "&" + params.toString()), data);
        }
    }

    /**
     * 从缓存中获取对应url缓存下来的数据
     *
     * @param url 要获取的数据对应的url
     * @return 如果对应的url有缓存数据, 则返回缓存数据, 否则返回空
     */
    public String getData(String url) {
        return mCache.get(url);
    }

    /**
     * 从缓存中获取对应url缓存下来的数据
     *
     * @param url    要获取的数据对应的url
     * @param params 对应url的参数
     * @return 如果对应的url有缓存数据, 则返回缓存数据, 否则返回空
     */
    public String getData(String url, RequestParams params) {
        if (null == params) {
            return mCache.get(url);
        } else {
            return mCache.get((url + "&" + params.toString()));
        }
    }

    protected void cancleLodingToast(boolean isSuccess) {
        if (null != mToast) {
            if (isSuccess) {
                mToast.success();
            } else mToast.error();
            mToast = null;
        }
    }

    Handler _mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != mToast) {
                cancleLodingToast(false);
            }
        }
    };

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
