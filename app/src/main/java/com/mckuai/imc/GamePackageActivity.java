package com.mckuai.imc;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.InventorySlot;
import com.mckuai.ItemStack;
import com.mckuai.adapter.InventoryAdapter;
import com.mckuai.bean.WorldInfo;
import com.mckuai.entity.EntityItem;
import com.mckuai.io.xml.MaterialIconLoader;
import com.mckuai.io.xml.MaterialLoader;
import com.mckuai.material.Material;
import com.mckuai.material.icon.MaterialIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GamePackageActivity extends BaseActivity implements View.OnClickListener,InventoryAdapter.OnItemClickedListener,TextWatcher {

    private UltimateRecyclerView itemListView;
    private SeekBar sb_itemCountPeeker;
    private InventoryAdapter adapter;
    private WorldInfo world;
    private ItemStack itemStack;

    private EditText edt_search;
    private TextView tv_itemName;
    private TextView tv_itemType;
    private TextView tv_itemCount;
    private ImageView iv_itemIcon;
    private RelativeLayout changeItemCountView;
    private final String TAG = "GamePackageActivity";
//    private ImageButton btn_search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_package);
        world = MCkuai.getInstance().world;
        setTitle("游戏背包管理");
        new MaterialLoader(getResources().getXml(R.xml.item_data)).run();
        new MaterialIconLoader(this).run();
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
        if (null == adapter) {
            adapter = new InventoryAdapter();
            itemListView.setAdapter(adapter);
            adapter.setOnItemClickedListener(this);
            adapter.setInventorySlot(world.getInventory());
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initView(){
        itemListView = (UltimateRecyclerView) findViewById(R.id.recy_itemList);
        itemListView.setHasFixedSize(false);
        edt_search = (EditText)findViewById(R.id.edt_search);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        itemListView.setLayoutManager(layoutManager);
        findViewById(R.id.btn_addItem).setOnClickListener(this);
        //findViewById(R.id.btn_right).setOnLongClickListener(this);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_submitItem).setOnClickListener(this);
        edt_search.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_title)).setText("背包物品");
        changeItemCountView = (RelativeLayout)findViewById(R.id.rl_changeItemCount);

        tv_itemName = (TextView) findViewById(R.id.tv_itemName);
        tv_itemType = (TextView) findViewById(R.id.tv_itemType);
        tv_itemCount = (TextView) findViewById(R.id.tv_currentCount);
        iv_itemIcon = (ImageView) findViewById(R.id.iv_itemIcon);

        sb_itemCountPeeker = (SeekBar)findViewById(R.id.sb_countPeeker);
        sb_itemCountPeeker.setMax(255);
        sb_itemCountPeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e(TAG,"progress:"+progress);
                tv_itemCount.setText((int)progress+"");
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
                //一键新增
                List<InventorySlot> items = adapter.getInventorySlots();
                if (null != items){
                        if(world.setInventory(adapter.getInventorySlots())){
                            Log.e(TAG,"保存成功！");
                        }
                        MCkuai.getInstance().world = world;
                        setResult(RESULT_OK);
                        this.finish();
                    }
                break;
            case R.id.btn_right:
                    if (edt_search.getVisibility() == View.GONE){
                        edt_search.setVisibility(View.VISIBLE);
                    }
                    else {
//                        String value = editer.getString(edt_search.getText().toString());
                        if (!edt_search.getText().toString().isEmpty()){
                            edt_search.setVisibility(View.GONE);
                        }
                    }
                break;
            case R.id.btn_left:
                finish();
                break;

            case R.id.btn_submitItem:
                changeItemCountView.setVisibility(View.GONE);
                itemStack.setAmount(sb_itemCountPeeker.getProgress());
                adapter.updateInventory(itemStack);
                break;
        }

    }

    @Override
    protected boolean onBackKeyPressed() {
        if (changeItemCountView.getVisibility() == View.VISIBLE){
            changeItemCountView.setVisibility(View.GONE);
            return  true;
        }
        return super.onBackKeyPressed();
    }

    @Override
    public void OnItemClicked(ItemStack item) {
        if (null != item){
            sb_itemCountPeeker.setProgress(item.getAmount());
            tv_itemName.setText(EntityItem.getNameById(item.getId()));
            changeItemCountView.setVisibility(View.VISIBLE);
            itemStack = item;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private List<Material> getMaterials(){
        return Material.materials;
    }
}
