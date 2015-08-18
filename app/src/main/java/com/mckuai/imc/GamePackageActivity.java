package com.mckuai.imc;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.mckuai.io.EntityDataConverter;
import com.mckuai.io.db.LevelDBConverter;
import com.mckuai.mctools.InventorySlot;
import com.mckuai.mctools.ItemStack;
import com.mckuai.adapter.InventoryAdapter;
import com.mckuai.mctools.item.WorldItem;
import com.mckuai.io.xml.MaterialIconLoader;
import com.mckuai.io.xml.MaterialLoader;
import com.mckuai.mctools.material.Material;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;


public class GamePackageActivity extends BaseActivity implements View.OnClickListener,InventoryAdapter.OnItemClickedListener,TextWatcher {

    private UltimateRecyclerView itemListView;
    private SeekBar sb_itemCountPeeker;
    private InventoryAdapter adapter;
    private WorldItem world;
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
       /* File dbFile = new File(world.worldRoot+world.getDir(),"db");
        EntityDataConverter.EntityData data;
        if (null != dbFile && dbFile.exists() && dbFile.isDirectory()) {
            data = LevelDBConverter.readAllEntities(dbFile);
        }*/
        setTitle("游戏背包管理");
        new MaterialLoader(getResources().getXml(R.xml.item_data)).run();
        new MaterialIconLoader(this).run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("游戏背包管理");
        if (null == itemListView){
            initView();
        }
        showData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("游戏背包管理");
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
                MobclickAgent.onEvent(this,"changeItem");
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
    public void OnItemClicked(ItemStack item,Material material,Drawable icon) {
        if (null != item){
            sb_itemCountPeeker.setProgress(item.getAmount());
            tv_itemName.setText(material.getName() + "");
            if (null != icon){
                iv_itemIcon.setImageDrawable(icon);
            }
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
