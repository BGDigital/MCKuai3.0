package com.mckuai.imc;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mckuai.adapter.FragmentAdapter;
import com.mckuai.fragment.BaseFragment;
import com.mckuai.fragment.ForumFragment;
import com.mckuai.fragment.GameEditerFragment;
import com.mckuai.fragment.MapFragment;
import com.mckuai.fragment.ServerFragment;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener {
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
    private MCkuai application;



    private ArrayList<BaseFragment> mList;
    private boolean isFragmentChanged=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = MCkuai.getInstance();
        initView();
        initPage();
    }


    private void initView(){
        vp = (ViewPager) findViewById(R.id.pager);
        img1 = (TextView)findViewById(R.id.btn_1);
        img2 = (TextView)findViewById(R.id.btn_2);
        img3 = (TextView)findViewById(R.id.btn_3);
        img4 = (TextView)findViewById(R.id.btn_4);
        tv1 = (TextView)findViewById(R.id.tv_1);
        tv2 = (TextView)findViewById(R.id.tv_2);
        tv3 = (TextView)findViewById(R.id.tv_3);
        tv4 = (TextView)findViewById(R.id.tv_4);
        ll1 = (LinearLayout)findViewById(R.id.rb1);
        ll2 = (LinearLayout)findViewById(R.id.rb2);
        ll3 = (LinearLayout)findViewById(R.id.rb3);
        ll4 = (LinearLayout)findViewById(R.id.rb4);

        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll4.setOnClickListener(this);

        changeCheckedButton(0);
    }

    private void initPage(){
        mList = new ArrayList<>(4);
        mList.add(new GameEditerFragment());
        mList.add(new MapFragment());
        mList.add(new ServerFragment());
        mList.add(new ForumFragment());
        vp.setAdapter(new FragmentAdapter(getSupportFragmentManager(), mList));
        vp.setOnPageChangeListener(this);

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            //mySlidingMenu.toggle();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK)
        {
           /* if (isMenuShowing)
            {
                mySlidingMenu.toggle();
                return true;
            }*/
            showAlert("退出", "是否退出麦块？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        return super.onKeyDown(keyCode, event);
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

    private void changeCheckedButton(int position){
        setUnChecked();
        setChecked(position);
    }

    private void setUnChecked(){
        switch (lastPosition){
            case  1:
                tv2.setEnabled(true);
                img2.setEnabled(true);
                ll2.setEnabled(true);
                break;
            case 2:
                tv3.setEnabled(true);
                img3.setEnabled(true);
                ll3.setEnabled(true);
                break;
            case 3:
                tv4.setEnabled(true);
                img4.setEnabled(true);
                ll4.setEnabled(true);
                break;
            default:
                tv1.setEnabled(true);
                img1.setEnabled(true);
                ll1.setEnabled(true);
                break;
        }
    }
    private void setChecked(int position){
        lastPosition = position;
        switch (position){
            case 1:
                tv2.setEnabled(false);
                img2.setEnabled(false);
                ll2.setEnabled(false);
                break;
            case 2:
                tv3.setEnabled(false);
                img3.setEnabled(false);
                ll3.setEnabled(false);
                break;
            case 3:
                tv4.setEnabled(false);
                img4.setEnabled(false);
                ll4.setEnabled(false);
                break;
            default:
                tv1.setEnabled(false);
                img1.setEnabled(false);
                ll1.setEnabled(false);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb1:
                application.fragmentIndex = 0;
                vp.setCurrentItem(0,false);
                break;
            case R.id.rb2:
                application.fragmentIndex = 1;
                vp.setCurrentItem(1,false);
                break;
            case R.id.rb3:
                application.fragmentIndex = 2;
                vp.setCurrentItem(2,false);
                break;
            case R.id.rb4:
                application.fragmentIndex = 3;
                vp.setCurrentItem(3,false);
                break;

        }
    }
}
