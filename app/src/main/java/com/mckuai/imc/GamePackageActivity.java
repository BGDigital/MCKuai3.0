package com.mckuai.imc;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.mckuai.adapter.InventoryAdapter;
import com.mckuai.bean.WorldInfo;

import java.util.HashMap;


public class GamePackageActivity extends BaseActivity implements View.OnClickListener,OnLongClickListener,InventoryAdapter.OnItemLongClickListener {

    private UltimateRecyclerView itemListView;
    private SeekBar sb_itemCountPeeker;
    private InventoryAdapter adapter;
    private WorldInfo world;
    private InventorySlot curInventory;

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
            adapter.setOnItemLongClickListener(this);
            itemListView.setAdapter(adapter);
            adapter.setInventorySlot(world.getRealInventory(world.getInventory()));
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
        findViewById(R.id.btn_right).setOnLongClickListener(this);
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
        //sb_itemCountPeeker.setProgress(7);
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
                HashMap<Integer,Integer> items = adapter.getSelectedItem();
                if (null != items){
                    //showNotification(1,"当前共选了"+items.size()+"个物品",R.id.rl_search);
                        world.setInventory(adapter.getInventorySlots());
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
                curInventory.getContents().setAmount(sb_itemCountPeeker.getProgress());
                adapter.updateInventory(curInventory);
                break;
        }

    }



    @Override
    public boolean onLongClick(View v) {
        changeItemCountView.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onLongClick(InventorySlot inventorySlot) {
        this.curInventory = inventorySlot;
        changeItemCountView.setVisibility(View.VISIBLE);
        tv_itemName.setText("ID:" + inventorySlot.getContents().getId() + "");
        sb_itemCountPeeker.setProgress(inventorySlot.getContents().getAmount());
        tv_itemCount.setText(inventorySlot.getContents().getAmount() + "");
        tv_itemType.setText("耐久:"+inventorySlot.getContents().getDurability());
    }
}
