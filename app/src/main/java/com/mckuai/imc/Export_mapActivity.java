package com.mckuai.imc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.mckuai.bean.WorldInfo;
import com.mckuai.until.MCGameEditer;
import com.mckuai.until.MCMapManager;

import java.util.ArrayList;


public class Export_mapActivity extends BaseActivity implements View.OnClickListener ,AdapterView.OnItemClickListener{
    private ImageView btn_left, btn_right;
    private TextView tv_title;
    private ListView mpt_ls;
    private Button bt_go, btn_showOwner;
    private ExportAdapter adapter;
    private MCMapManager mapManager;
    ArrayList<String> downloadMap;


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
        downloadMap = mapManager.getCurrentMapDirList();
        ArrayList<WorldInfo> worlds = MCGameEditer.getAllWorldLite();
        if (downloadMap == null) {
            showNotification(1, "请下载地图", R.id.maproot);
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("DELETE", adapter.chuancan());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
