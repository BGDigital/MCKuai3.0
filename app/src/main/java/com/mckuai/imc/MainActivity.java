package com.mckuai.imc;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.mckuai.adapter.FragmentAdapter;
import com.mckuai.fragment.BaseFragment;
import com.mckuai.fragment.ForumFragment;
import com.mckuai.fragment.GameEditerFragment;
import com.mckuai.fragment.MapFragment;
import com.mckuai.fragment.ServerFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    private static MainActivity instance;

    private ViewPager vp;
    private FragmentTabHost mTabHost;

    private LayoutInflater mInflater;
    private String mFragmentTitle[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentTitle = getResources().getStringArray(R.array.main_fragment);
        mInflater = LayoutInflater.from(this);
        initView();
        initPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(){
        vp = (ViewPager) findViewById(R.id.pager);
        //vp.setOnPageChangeListener(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        mTabHost.getTabWidget().setDividerDrawable(null);
        //mTabHost.setOnTabChangedListener(this);
        Class fragmentArray[] = {android.support.v4.app.Fragment.class,android.support.v4.app.Fragment.class,android.support.v4.app.Fragment.class,android.support.v4.app.Fragment.class};
        for (int i = 0;i < 4 ;i++){
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mFragmentTitle[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec,fragmentArray[i],null);
        }
    }

    private void initPage(){
        List<BaseFragment> list = new ArrayList<>(4);
        list.add(new GameEditerFragment());
        list.add(new MapFragment());
        list.add(new ServerFragment());
        list.add(new ForumFragment());
        vp.setAdapter(new FragmentAdapter(getSupportFragmentManager(),list));
    }

    private View getTabItemView(int i)
    {
        View view = mInflater.inflate(R.layout.item_table, null);
        ImageView mImageView = (ImageView) view.findViewById(R.id.tab_imageview);
        TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
        //mImageView.setBackgroundResource(mFragmentTitle[i]);
        mTextView.setText(mFragmentTitle[i]);
        // if (2 == i)
        // {
        // newMessageView = (View) view.findViewById(R.id.newmessage);
        // }
        return view;
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
}
