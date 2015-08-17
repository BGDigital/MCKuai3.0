package com.mckuai.imc;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mckuai.adapter.FragmentAdapter;
import com.mckuai.fragment.BaseFragment;
import com.mckuai.fragment.ForumFragment;
import com.mckuai.fragment.GameEditerFragment;
import com.mckuai.fragment.MCSildingMenu;
import com.mckuai.fragment.ResourceFragment;
import com.mckuai.fragment.ServerFragment;
import com.mckuai.utils.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.ArrayList;

import slidingmenu.SlidingMenu;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private static MainActivity instance;

    private ViewPager vp;
    private TextView img1;
    private TextView img2;
    private TextView img3;
    private TextView img4;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll3;
    private LinearLayout ll4;

    private int lastPosition = 0;
    private static MCkuai application;

    private static TextView tv_titlebar_title;
    private static ImageView btn_titlebar_left;
    private static ImageView btn_titlebar_right;
    private static Spinner sp_titlebar_spinner;
    private static FrameLayout fl_leftBtn_background;

    private SlidingMenu mySlidingMenu;
    private MCSildingMenu menu;
    private static Drawable drawable_right_button;
    private ArrayList<BaseFragment> mList;
    private boolean isFragmentChanged = false;
    private static boolean isShowreturn = false;
    private static boolean isShowSearch = false;

    private static View.OnClickListener leftButtonListener_myMaps;
    private static View.OnClickListener rightButtonListener_myMaps;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = MCkuai.getInstance();
        initView();
        initPage();
        initSlidingMenu();
        mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 1500);
        MobclickAgent.updateOnlineConfig(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        showUser();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public static void setOnclickListener(View.OnClickListener leftButtonListener, View.OnClickListener rightButtonListener) {
        if (null != leftButtonListener) {
            leftButtonListener_myMaps = leftButtonListener;
        }
        if (null != rightButtonListener) {
            rightButtonListener_myMaps = rightButtonListener;
        }
    }

    private void initView() {
        vp = (ViewPager) findViewById(R.id.pager);
        img1 = (TextView) findViewById(R.id.btn_1);
        img2 = (TextView) findViewById(R.id.btn_2);
        img3 = (TextView) findViewById(R.id.btn_3);
        img4 = (TextView) findViewById(R.id.btn_4);
        tv1 = (TextView) findViewById(R.id.tv_1);
        tv2 = (TextView) findViewById(R.id.tv_2);
        tv3 = (TextView) findViewById(R.id.tv_3);
        tv4 = (TextView) findViewById(R.id.tv_4);
        ll1 = (LinearLayout) findViewById(R.id.rb1);
        ll2 = (LinearLayout) findViewById(R.id.rb2);
        ll3 = (LinearLayout) findViewById(R.id.rb3);
        ll4 = (LinearLayout) findViewById(R.id.rb4);
        fl_leftBtn_background = (FrameLayout)findViewById(R.id.fl_background_left);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        btn_titlebar_left = (ImageView) findViewById(R.id.btn_titlebar_left);
        btn_titlebar_right = (ImageView) findViewById(R.id.btn_titlebar_right);
        sp_titlebar_spinner = (Spinner) findViewById(R.id.sp_titlebar_type);
        btn_titlebar_right.setImageResource(R.drawable.btn_post_publish);
        application.setSpinner(sp_titlebar_spinner);

        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll4.setOnClickListener(this);
        // btn_titlebar_right.setOnClickListener(this);
        btn_titlebar_left.setOnClickListener(this);
        application.setBtn_publish(btn_titlebar_right);
        btn_titlebar_left.setOnClickListener(this);
        btn_titlebar_right.setOnClickListener(this);
        changeCheckedButton(0);
    }

    private static void showUser() {
        if (application.isLogin()) {
            ImageLoader loader = ImageLoader.getInstance();
            String userCover = application.mUser.getHeadImg();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).displayer(new CircleBitmapDisplayer()).build();
            if (null != userCover && 10 < userCover.length()) {
                loader.displayImage(application.mUser.getHeadImg(), btn_titlebar_left, options);
            }
        } else {
            btn_titlebar_left.setBackgroundResource(R.drawable.background_user_cover_default);
        }
    }

    private void initPage() {
        mList = new ArrayList<>(4);
        mList.add(new GameEditerFragment());
        mList.add(new ResourceFragment());
        mList.add(new ServerFragment());
        mList.add(new ForumFragment());
//        mList.add(new SkinFragment());
        vp.setAdapter(new FragmentAdapter(getSupportFragmentManager(), mList));
        vp.setOnPageChangeListener(this);
    }

    private void initSlidingMenu() {
        menu = new MCSildingMenu();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        width = (int) (width / 3.5);
        mySlidingMenu = new SlidingMenu(this);
        mySlidingMenu.setMode(SlidingMenu.LEFT);
        mySlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mySlidingMenu.setBehindOffsetRes(R.dimen.com_margin);
        mySlidingMenu.setFadeDegree(0.42f);
        mySlidingMenu.setMenu(R.layout.frame_menu);
        mySlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mySlidingMenu.setBackgroundResource(R.drawable.background_slidingmenu);
        mySlidingMenu.setBehindOffset(width);
        mySlidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                // TODO Auto-generated method stub
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
        mySlidingMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {

            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                // TODO Auto-generated method stub
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menu).commit();
        mySlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {

            @Override
            public void onOpened() {
                // TODO Auto-generated method stub
                menu.callOnResumeForUpdate();
                menu.showData();
                isShowingMenu = true;
            }
        });
        mySlidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {

            @Override
            public void onClose() {
                // TODO Auto-generated method stub
                menu.callOnPauseForUpdate();
                hideKeyboard(mySlidingMenu);
                isShowingMenu = false;
                showUser();
            }
        });
    }


    @Override
    protected boolean onMenuKeyPressed() {
        mySlidingMenu.toggle();
        return true;
    }


    @Override
    protected boolean onBackKeyPressed() {
        if (isShowingMenu) {
            mySlidingMenu.toggle();
            return true;
        }
        if (!mList.get(vp.getCurrentItem()).onBackKeyPressed()) {
            showAlert("退出", "是否退出麦块？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    application.mCache.saveCacheFile();
                    Intent intent = new Intent();
                    intent.setAction("com.mckuai.downloadservice");
                    intent.setPackage(MainActivity.this.getPackageName());
                    stopService(intent);
                    MobclickAgent.onKillProcess(MainActivity.this);
                    System.exit(0);
                }
            });
            return true;
        }
        else {
            return true;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        isFragmentChanged = true;
        application.fragmentIndex = position;
        changeCheckedButton(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void changeCheckedButton(int position) {
        setUnChecked();
        setChecked(position);
        application.fragmentIndex = position;
    }

    private void setUnChecked() {
        switch (lastPosition) {
            case 1:
                tv2.setEnabled(true);
                img2.setEnabled(true);
                ll2.setEnabled(true);
                setRightButtonView(false);
                break;
            case 2:
                tv3.setEnabled(true);
                img3.setEnabled(true);
                ll3.setEnabled(true);
                sp_titlebar_spinner.setVisibility(View.GONE);
                break;
            case 3:
                tv4.setEnabled(true);
                img4.setEnabled(true);
                ll4.setEnabled(true);
                btn_titlebar_right.setVisibility(View.INVISIBLE);
                break;
            default:
                tv1.setEnabled(true);
                img1.setEnabled(true);
                ll1.setEnabled(true);
                break;
        }
    }

    private void setChecked(int position) {
        lastPosition = position;
        switch (position) {
            case 1:
                tv_titlebar_title.setText("地图");
                tv2.setEnabled(false);
                img2.setEnabled(false);
                ll2.setEnabled(false);
                setRightButtonView(true);
                break;
            case 2:
                tv_titlebar_title.setText("联机");
                //sp_titlebar_spinner.setVisibility(View.VISIBLE);
                tv3.setEnabled(false);
                img3.setEnabled(false);
                ll3.setEnabled(false);
                break;
            case 3:
                tv_titlebar_title.setText("社区");
                tv4.setEnabled(false);
                img4.setEnabled(false);
                ll4.setEnabled(false);
                btn_titlebar_right.setVisibility(View.VISIBLE);
                btn_titlebar_right.setImageResource(R.drawable.btn_post_publish);
                break;
            default:
                tv_titlebar_title.setText("工具");
                tv1.setEnabled(false);
                img1.setEnabled(false);
                ll1.setEnabled(false);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb1:
                application.fragmentIndex = 0;
                vp.setCurrentItem(0, false);
                break;
            case R.id.rb2:
                application.fragmentIndex = 1;
                vp.setCurrentItem(1, false);
                break;
            case R.id.rb3:
                application.fragmentIndex = 2;
                vp.setCurrentItem(2, false);
                break;
            case R.id.rb4:
                application.fragmentIndex = 3;
                vp.setCurrentItem(3, false);
                break;
            case R.id.btn_titlebar_left:
                if (isShowreturn && leftButtonListener_myMaps != null) {
                    leftButtonListener_myMaps.onClick(v);
                } else {
                    if (!application.isLogin()) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(this,UserCenter.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.user),application.getUser());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.btn_titlebar_right:
                if (isShowSearch && rightButtonListener_myMaps != null) {
                    rightButtonListener_myMaps.onClick(v);
                } else {
                    Intent intent = new Intent(this, PublishPostActivity.class);
                    startActivity(intent);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == requestCode && resultCode == RESULT_OK) {
            showUser();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    checkUpdate();
                    break;
            }
        }
    };

    private void checkUpdate() {
        menu.callOnResumeForUpdate();
        menu.checkUpdate(true);
    }

    public static TextView gettitle() {
        return tv_titlebar_title;
    }

    public static void setLeftButtonView(boolean isShowreturns) {
        isShowreturn = isShowreturns;
        if (isShowreturn == true) {
            btn_titlebar_left.setBackgroundColor(0x00000000);
            fl_leftBtn_background.setBackgroundColor(0x00000000);
            btn_titlebar_left.setImageResource(R.drawable.btn_back);
        } else {
            btn_titlebar_left.setImageResource(0x00000000);
            fl_leftBtn_background.setBackgroundResource(R.drawable.background_circle_usericon);
            showUser();
        }
    }

    public static void setRightButtonView(boolean isShowSearchs) {
        isShowSearch = isShowSearchs;
        if (isShowSearch == true) {
            if (drawable_right_button == null) {
                drawable_right_button = btn_titlebar_right.getBackground();
            }
            btn_titlebar_right.setImageResource(R.drawable.btn_search_selector);
            btn_titlebar_right.setVisibility(View.VISIBLE);
        } else {
            btn_titlebar_right.setVisibility(View.GONE);
        }
    }

}
