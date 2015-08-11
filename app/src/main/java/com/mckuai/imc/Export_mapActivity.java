package com.mckuai.imc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mckuai.adapter.ExportAdapter;
import com.mckuai.mctools.item.WorldItem;
import com.mckuai.mctools.WorldUtil.MCWorldUtil;
import com.mckuai.mctools.WorldUtil.MCMapManager;

import java.util.ArrayList;


public class Export_mapActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView btn_left, btn_right;
    private TextView tv_title;
    private ListView mpt_ls;
    private Button bt_go, btn_showOwner;
    private ExportAdapter adapter;
    private MCMapManager mapManager;
    //    ArrayList<String> downloadMap;
    private ArrayList<Integer> selectedList;
    private ArrayList<WorldItem> worlds;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymap_export);
        initview();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == mapManager) {
            mapManager = MCkuai.getInstance().getMapManager();
            initview();
        }

        showData();
    }

    @Override
    protected void onDestroy() {

        if (null != mapManager) {
            mapManager.closeDB();
        }
        super.onDestroy();
    }

    private void showData() {
        worlds = MCWorldUtil.getAllWorldLite();
        if (worlds == null) {
            showNotification(1, "当前没有地图", R.id.maproot);
        } else {
            adapter = new ExportAdapter(this, worlds);
            mpt_ls.setAdapter(adapter);
        }
    }

    protected void initview() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_go = (Button) findViewById(R.id.bt_go);
        btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
        btn_showOwner.setVisibility(View.GONE);
        tv_title.setText("我的地图");
        mpt_ls = (ListView) findViewById(R.id.mpt_ls);
//        mpt_ls.setOnItemClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setVisibility(View.GONE);
        bt_go.setOnClickListener(this);
        mpt_ls.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_go:
                ArrayList<String> worldroot = new ArrayList<>();
                if (selectedList != null && !selectedList.isEmpty() && worlds != null && !worlds.isEmpty()) {
                    for (Integer xuanzong : selectedList) {
                        worldroot.add(worlds.get(xuanzong).getDir());
                    }
                    intent = new Intent(this, MapexportActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DELETE", worldroot);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(this, "请选择需要导出的地图", Toast.LENGTH_LONG).show();
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<WorldItem> deletedmap = new ArrayList<WorldItem>();

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                for (int i = 0; i < selectedList.size(); i++) {
//                    int location = selectedList.get(i);
//                    deletedmap.add(worlds.get(location));
//                }
//                for (WorldItem world : deletedmap) {
//                    worlds.remove(world);
//                }
//                adapter.notifyDataSetChanged();
//            } else {
//                ArrayList<String> detede = data.getStringArrayListExtra("mapzimulu");
//                for (String zimu : detede) {
//                    for (WorldItem zongliebiao : worlds) {
//                        if (zimu.equals(zongliebiao.getDir())) {
//                            worlds.remove(zongliebiao);
//                            break;
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//            selectedList.clear();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.getItemViewType(position) == 1) {
            for (Integer xuanzhong : selectedList) {
                if (xuanzhong == position) {
                    selectedList.remove(xuanzhong);
                    break;
                }
            }
        } else {
            if (selectedList == null) {
                selectedList = new ArrayList<>();
            }
            selectedList.add(position);
        }
        adapter.setSelectedList(selectedList);
    }
}
