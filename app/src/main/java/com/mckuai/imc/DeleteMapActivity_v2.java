package com.mckuai.imc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mckuai.adapter.DeletemapAdtpter;
import com.mckuai.bean.Map;
import com.mckuai.mctools.WorldUtil.MCMapManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;


public class DeleteMapActivity_v2 extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView btn_left, btn_right;
    private TextView tv_title;
    private ListView map_mymap_lv;
    private Button map_imp, go_map, leave_map, btn_showOwner;
    private ArrayList<Map> list;
    private MCMapManager mapManager;
    ArrayList<Map> downloadMap;
    private DeletemapAdtpter adapter;
    private String downloadDir;
    private ArrayList<String> selectedList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapdelete);
        list = new ArrayList<Map>();
        downloadDir = MCkuai.getInstance().getMapDownloadDir();
        initview();
    }

    protected void onResume() {
        MobclickAgent.onPageStart("删除地图");
        super.onResume();
        if (null == mapManager) {
            mapManager = MCkuai.getInstance().getMapManager();
            initview();
        }
        showData();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd("删除地图");
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (null != mapManager) {
            mapManager.closeDB();
        }
        super.onDestroy();
    }

    protected void showData() {
        downloadMap = mapManager.getDownloadMaps();
        if (downloadMap == null) {
            showNotification(1, "没有地图,请下载地图", R.id.detdleroot);
        } else {
            adapter = new DeletemapAdtpter(this, downloadMap);
            map_mymap_lv.setAdapter(adapter);
        }
    }

    protected void initview() {
        map_imp = (Button) findViewById(R.id.bt_deteled);
        map_imp.setOnClickListener(this);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的地图");
        go_map = (Button) findViewById(R.id.go_map);
        btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
        btn_showOwner.setVisibility(View.GONE);
        go_map.setOnClickListener(this);
        leave_map = (Button) findViewById(R.id.leave_map);
        leave_map.setOnClickListener(this);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.GONE);
        map_mymap_lv = (ListView) findViewById(R.id.map_mymap_lv);
        map_mymap_lv.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_deteled:
                MobclickAgent.onEvent(this,"deleteMaps",null == selectedList ? 0:selectedList.size());
                deleteMap();
                break;
            case R.id.go_map:
                intent = new Intent(DeleteMapActivity_v2.this, ImportMapActivity_v2.class);
                startActivity(intent);
                break;
            case R.id.leave_map:
                intent = new Intent(DeleteMapActivity_v2.this, ExportMapActivity_v2.class);
                startActivity(intent);
            default:
                break;
        }
    }

    private void deleteMap() {
        if (null != selectedList && !selectedList.isEmpty() && null != downloadMap && !downloadMap.isEmpty()) {
            Iterator iterator = selectedList.iterator();
            String resid ;
            while (iterator.hasNext()) {
                resid = (String) iterator.next();
                if (null != resid) {
                    Map map = getMap(resid);
                    if (null != map) {
                        if (mapManager.delDownloadMap(map.getResId())) {
                            iterator.remove();
                            downloadMap.remove(map);
                        }
                    }
                }
            }
            adapter.setchuancan(downloadMap);
            if (!selectedList.isEmpty()) {
                showNotification(1, "部分地图删除失败", R.id.detdleroot);
                selectedList.clear();
            }
        } else {
            showNotification(1, "请先选择地图再删除", R.id.detdleroot);
        }
    }

    private Map getMap(String resid){
        if (null != resid){
            for (Map map :downloadMap){
                if (map.getResId().equals(resid)){
                    return map;
                }
            }
        }
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.getItemViewType(position) == 1) {
            for (String resid : selectedList) {
                if (resid.equals(((Map)adapter.getItem(position)).getResId())) {
                    selectedList.remove(resid);
                    break;
                }
            }
        } else {
            if (selectedList == null) {
                selectedList = new ArrayList<>();
            }
            selectedList.add(((Map)adapter.getItem(position)).getResId());
        }
        adapter.setSelectedList(selectedList);
    }
}