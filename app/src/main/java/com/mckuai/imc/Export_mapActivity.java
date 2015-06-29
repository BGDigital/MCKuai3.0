package com.mckuai.imc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.mckuai.adapter.ExportAdapter;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.until.MCMapManager;

import java.util.ArrayList;


public class Export_mapActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Context mContent;
    private ImageView btn_left, pt_im;
    private ImageButton btn_right;
    private TextView tv_title;
    private ListView mpt_ls;
    private Button bt_go;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private MCkuai application;
    private String mapType = null;
    private String orderFiled = null;
    private MapBean mapList;
    private ExportAdapter adapter;
    private MCMapManager mapManager;
    private ArrayList<Map> mMapBeans;
    private String data;

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
            mapManager = new MCMapManager();
            initview();
        }
        ArrayList<String> curmap = mapManager.getCurrentMaps();
        ArrayList<Map> downloadMap = mapManager.getDownloadMaps();
        showData();
    }

    @Override
    protected void onDestroy() {

        if(null!=mapManager){
           mapManager.closeDB();
        }
        super.onDestroy();
    }

    private void showData() {
        if (mapManager.getDownloadMaps() == null) {
            showNotification(1, "请下载地图", R.id.maproot);
        } else {
            adapter = new ExportAdapter(mContent, mMapBeans);
            mpt_ls.setAdapter(adapter);
        }
    }

    protected void initview() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_go = (Button) findViewById(R.id.bt_go);
        tv_title.setText("我的地图");
        mpt_ls = (ListView) findViewById(R.id.mpt_ls);
        mpt_ls.setOnItemSelectedListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setVisibility(View.GONE);
        bt_go.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_go:
                intent = new Intent(this, MapexportActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
     Map map = (Map)adapter.getItem(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
