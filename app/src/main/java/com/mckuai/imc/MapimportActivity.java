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
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.mckuai.adapter.ExportAdapter;
import com.mckuai.adapter.MapImportAdapter;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.until.MCMapManager;

import java.io.File;
import java.util.ArrayList;


public class MapimportActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ImageView btn_left, pt_im;
    private ImageButton btn_right;
    private TextView tv_title;
    private ListView mpt_ls;
    private Button bt_go;
    private MapImportAdapter adapter;
    private MCMapManager mapManager;
    private ArrayList<String> curMaps;
    private Context mContent;
    private ArrayList<Map> mMapBeans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapimport);
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

        if (null != mapManager) {
            mapManager.closeDB();
        }
        super.onDestroy();
    }

    private void showData() {

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
        mpt_ls = (ListView) findViewById(R.id.mpt_ls);
        mpt_ls.setOnItemSelectedListener(this);
        btn_left.setOnClickListener(this);
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
    public void exportMap(String mapName,String dstFileDir){

    }
}
