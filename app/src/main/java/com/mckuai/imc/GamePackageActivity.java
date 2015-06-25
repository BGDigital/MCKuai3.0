package com.mckuai.imc;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.adapter.ArticAdapter;
import com.mckuai.bean.ArticItem;

import java.util.ArrayList;
import java.util.HashMap;


public class GamePackageActivity extends BaseActivity implements View.OnClickListener {

    private UltimateRecyclerView itemListView;
    private ArticAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_package);
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
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        itemListView.setLayoutManager(layoutManager);
        findViewById(R.id.btn_addItem).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addItem:
                HashMap<Integer,Integer> items = adapter.getSelectedItem();
                if (null != items){
                    showNotification(1,"当前共选了"+items.size()+"个物品",R.id.rl_search);
                }
                break;
        }
    }
}
