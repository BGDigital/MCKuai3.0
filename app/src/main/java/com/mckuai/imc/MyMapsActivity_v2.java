package com.mckuai.imc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mckuai.adapter.MymapAdapter;
import com.mckuai.bean.Map;
import com.mckuai.mctools.WorldUtil.MCMapManager;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/6/24.
 */
public class MyMapsActivity_v2 extends BaseActivity implements OnClickListener, AdapterView.OnItemClickListener {
    private ListView map_mymap_lv;
    private ImageView btn_left;
    private TextView tv_title;
    private Button go_map, leave_map, delete_map, btn_showOwner;
    private MCMapManager mapManager;
    private MymapAdapter adapter;
    ArrayList<Map> downloadMap;
    private Map map;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymap);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mapManager) {
            mapManager = MCkuai.getInstance().getMapManager();
            initView();
        }
        showData();
    }

    protected void showData() {
        downloadMap = mapManager.getDownloadMaps();
        if (downloadMap == null) {
            showNotification(1, "请下载地图", R.id.mymaprot);
        } else {
            adapter = new MymapAdapter(this, downloadMap);
            map_mymap_lv.setAdapter(adapter);
        }
    }

    private void initView() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的地图");
        map_mymap_lv = (ListView) findViewById(R.id.map_mymap_list);
        go_map = (Button) findViewById(R.id.go_map);
        leave_map = (Button) findViewById(R.id.leave_map);
        btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
        btn_showOwner.setVisibility(View.GONE);
        delete_map = (Button) findViewById(R.id.delete_map);
        btn_left.setOnClickListener(this);
        go_map.setOnClickListener(this);
        leave_map.setOnClickListener(this);
        delete_map.setOnClickListener(this);
        map_mymap_lv.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_left:
                setResult(RESULT_OK);
                this.finish();
                break;
            case R.id.go_map:
                intent = new Intent(this, ImportMapActivity_v2.class);
                startActivity(intent);
                break;
            case R.id.leave_map:
                intent = new Intent(this, ExportMapActivity_v2.class);
                startActivity(intent);
                break;
            case R.id.delete_map:
                intent = new Intent(this, DeleteMapActivity_v2.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private boolean isZipFile(String fileName) {
        if (null != fileName) {
            int index = fileName.lastIndexOf(".");
            if (0 < index) {
                return fileName.substring(index + 1).equalsIgnoreCase("zip");
            }
        }
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, Map_detailsActivity.class);
        map = (Map) adapter.getItem(position);
        map.setDownloadProgress(100);
        Bundle bundle = new Bundle();
        bundle.putSerializable("details", map);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
