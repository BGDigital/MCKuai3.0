package com.mckuai.imc;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.adapter.ArticAdapter;
import com.mckuai.bean.ArticItem;
import com.mckuai.until.GameEditer;

import java.util.ArrayList;
import java.util.HashMap;


public class GamePackageActivity extends BaseActivity implements View.OnClickListener,OnLongClickListener {

    private UltimateRecyclerView itemListView;
    private SeekBar sb_itemCountPeeker;
    private ArticAdapter adapter;
    private GameEditer editer;

    private EditText edt_search;
    private TextView tv_itemName;
    private TextView tv_itemType;
    private TextView tv_itemCount;
    private ImageView iv_itemIcon;
//    private ImageButton btn_search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_package);
        editer = new GameEditer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == itemListView){
            initView();
        }
        showData();
    }

    private void showData(){
        ArrayList<ArticItem> articItems = new ArrayList<>(10);
        for (int i = 0; i < 10;i++){
            ArticItem item = new ArticItem();
            item.setName("火把"+i);
            item.setId(i);
            articItems.add(item);
        }

        if (null == adapter)
        adapter = new ArticAdapter();
        itemListView.setAdapter(adapter);
        adapter.setArtics(articItems);
    }

    private void initView(){
        itemListView = (UltimateRecyclerView) findViewById(R.id.recy_itemList);
        edt_search = (EditText)findViewById(R.id.edt_search);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        itemListView.setLayoutManager(layoutManager);
        findViewById(R.id.btn_addItem).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnLongClickListener(this);
        findViewById(R.id.btn_left).setOnClickListener(this);
        edt_search.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_title)).setText("背包物品");

        tv_itemName = (TextView) findViewById(R.id.tv_itemName);
        tv_itemType = (TextView) findViewById(R.id.tv_itemType);
        tv_itemCount = (TextView) findViewById(R.id.tv_currentCount);
        iv_itemIcon = (ImageView) findViewById(R.id.iv_itemIcon);

        sb_itemCountPeeker = (SeekBar)findViewById(R.id.sb_countPeeker);
        sb_itemCountPeeker.setProgress(7);
        sb_itemCountPeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float count = progress * 0.64f;
                tv_itemCount.setText((int)count+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addItem:
                HashMap<Integer,Integer> items = adapter.getSelectedItem();
                if (null != items){
                    //showNotification(1,"当前共选了"+items.size()+"个物品",R.id.rl_search);
                    }
                break;
            case R.id.btn_right:
                    if (edt_search.getVisibility() == View.GONE){
                        edt_search.setVisibility(View.VISIBLE);
                    }
                    else {
                        String value = editer.getString(edt_search.getText().toString());
                        if (!edt_search.getText().toString().isEmpty()){
                            edt_search.setVisibility(View.GONE);
                        }
                    }
                break;
            case R.id.btn_left:
                finish();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int value = editer.getInt(edt_search.getText().toString());
        edt_search.setText(value+"");
        editer.closeDB();
        return false;
    }
}
