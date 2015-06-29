package com.mckuai.imc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mckuai.adapter.ExportAdapter;
import com.mckuai.adapter.MapExportAdapter;
import com.mckuai.bean.Map;
import com.mckuai.until.MCMapManager;

import java.io.File;
import java.util.ArrayList;


public class MapexportActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private String searchContext;//输入内容
    private ImageView btn_left, pt_im;
    private ImageButton btn_right;
    private TextView tv_title;
    private EditText map_ed;
    private ListView map_lv_leave;
    private Button bt_go;
    private MapExportAdapter adapter;
    private MCMapManager mapManager;;
    private ArrayList<String> curMaps;
    private Context mContent;
    private ArrayList<Map> mMapBeans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_leave);
        initview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mapManager) {
            mapManager = new MCMapManager();
            initview();
        }
        ArrayList<String> curmap = mapManager.getCurrentMaps();
        showData();
    }
    @Override
    protected void onDestroy() {

        if(null!=mapManager){
            mapManager.closeDB();
        }
        super.onDestroy();
    }
    protected void showData() {
        adapter = new MapExportAdapter(mContent, mMapBeans);
        map_lv_leave.setAdapter(adapter);
    }

    protected ArrayList<String> getData(String dar) {
        File[] files = new File(dar).listFiles();
        if (null == files || 0 == files.length) {
            return null;
        }

        curMaps = new ArrayList<String>();
        for (File file : files) {
            if (file.isDirectory()) {
                curMaps.add(file.getPath() + file.getName());
            }
        }
        return curMaps;
    }

    protected void initview() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_go = (Button) findViewById(R.id.bt_go);
        tv_title.setText("我的地图");
        map_lv_leave = (ListView) findViewById(R.id.map_lv_leave);
        map_lv_leave.setOnItemSelectedListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        bt_go.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_go:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void importMap(String mapFile){

    }
}
