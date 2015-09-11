package com.mckuai.imc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mckuai.adapter.FragmentAdapter;
import com.mckuai.fragment.MCSildingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import com.mckuai.widget.slidingmenu.SlidingMenu;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ViewPager vp;
    private RadioButton rb_navigation_tool;
    private RadioButton rb_navigation_resource;
    private RadioButton rb_navigation_server;
    private RadioButton rb_navigation_forum;
    private static TextView tv_titlebar_title;
    private static ImageView btn_titlebar_left;
    private static ImageView btn_titlebar_right;
    private EditText edt_titlebar_search;
    //    private static View v_circle;
    private SlidingMenu mySlidingMenu;
    private MCSildingMenu menu;

    private static MCkuai application;
    private FragmentAdapter adapter;
    private boolean isFragmentChanged = false;

    private static int btn_right_action_type = 0;

//    private static View.OnClickListener listener_titlebar_rightbtn;

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

    private void initView() {
        vp = (ViewPager) findViewById(R.id.pager);
        rb_navigation_tool = (RadioButton) findViewById(R.id.rb_navigation_gameedit);
        rb_navigation_resource = (RadioButton) findViewById(R.id.rb_navigation_resource);
        rb_navigation_server = (RadioButton) findViewById(R.id.rb_navigation_server);
        rb_navigation_forum = (RadioButton) findViewById(R.id.rb_navigation_forum);
        edt_titlebar_search = (EditText) findViewById(R.id.edt_titlebar_search);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        btn_titlebar_left = (ImageView) findViewById(R.id.btn_titlebar_left);
        btn_titlebar_right = (ImageView) findViewById(R.id.btn_titlebar_right);
        btn_titlebar_right.setVisibility(View.GONE);

        ((RadioGroup) findViewById(R.id.rg_navigation)).setOnCheckedChangeListener(this);
        btn_titlebar_right.setOnClickListener(this);
        btn_titlebar_left.setOnClickListener(this);
        edt_titlebar_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (null != edt_titlebar_search.getText() && 0 < edt_titlebar_search.getText().length()) {
                        /*adapter.currentFragment.onRightButtonClicked(edt_titlebar_search.getText().toString());
                        edt_titlebar_search.setText("");
                        edt_titlebar_search.setVisibility(View.GONE);*/
                        callSearch();
                    }
                    return true;
                }
                return false;
            }
        });
        changeCheckedButton(0);
    }

    private static void showUser() {
        if (application.isLogin()) {
            ImageLoader loader = ImageLoader.getInstance();
            String userCover = application.mUser.getHeadImg();
            if (null != userCover && 10 < userCover.length()) {
                loader.displayImage(application.mUser.getHeadImg(), btn_titlebar_left, application.getCircleOption());
            }
        } else {
            btn_titlebar_left.setImageResource(R.drawable.background_user_cover_default);
        }
    }

    private void initPage() {
        if (null == adapter) {
            adapter = new FragmentAdapter(getSupportFragmentManager());
        }
        vp.setAdapter(adapter);
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
        if (!adapter.currentFragment.onBackKeyPressed()) {
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
        } else {
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
        isFragmentChanged = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (2 == state) {
            btn_titlebar_right.setVisibility(View.GONE);
        }
    }

    private void changeCheckedButton(int position) {
        application.fragmentIndex = position;
        switch (position) {
            case 1:
                tv_titlebar_title.setText("资源");
                rb_navigation_resource.setChecked(true);
                break;
            case 2:
                tv_titlebar_title.setText("联机");
                rb_navigation_server.setChecked(true);
                break;
            case 3:
                tv_titlebar_title.setText("社区");
                rb_navigation_forum.setChecked(true);
                break;
            default:
                tv_titlebar_title.setText("工具");
                rb_navigation_tool.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_titlebar_left:
                if (!application.isLogin()) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(this, UserCenter.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.user), application.getUser());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.btn_titlebar_right:
                switch (btn_right_action_type) {
                    case 1:
                        adapter.currentFragment.onRightButtonClicked(null);
                        break;
                    default:
                        callSearch();
                        break;
                }
                break;
        }
    }

    private void callSearch() {
        if (edt_titlebar_search.getVisibility() == View.GONE) {
            edt_titlebar_search.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) edt_titlebar_search.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.showSoftInputFromInputMethod(edt_titlebar_search.getApplicationWindowToken(), 0);
            }
        } else {
            if (0 < edt_titlebar_search.getText().length()) {
                edt_titlebar_search.setVisibility(View.GONE);
                adapter.currentFragment.onRightButtonClicked(edt_titlebar_search.getText().toString());
                edt_titlebar_search.setText("");
                InputMethodManager imm = (InputMethodManager) edt_titlebar_search.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(edt_titlebar_search.getApplicationWindowToken(), 0);
                }
            } else {
                edt_titlebar_search.setVisibility(View.GONE);
                Log.e("Main", "搜索内容为空");
            }
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

    /**
     * 设置标题栏右侧按钮的图片和行为
     *
     * @param action_type 行为类型，0为搜索，其它为点击
     * @param resId       按钮显示的图片的资源id
     */
    public static void setRightButtonView(int action_type, int resId) {
        if (0 < resId) {
            btn_right_action_type = action_type;
            btn_titlebar_right.setImageResource(resId);
            btn_titlebar_right.setVisibility(View.VISIBLE);
        } else {
            btn_titlebar_right.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (!isFragmentChanged)
            switch (checkedId) {
                case R.id.rb_navigation_resource:
                    vp.setCurrentItem(1);
                    break;
                case R.id.rb_navigation_server:
                    vp.setCurrentItem(2);
                    break;
                case R.id.rb_navigation_forum:
                    vp.setCurrentItem(3);
                    break;
                default:
                    vp.setCurrentItem(0);
                    break;
            }
    }
}
