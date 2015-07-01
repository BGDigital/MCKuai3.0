package com.mckuai.imc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.mckuai.adapter.DeletemapAdtpter;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;

import java.util.ArrayList;


public class MapdeleteActivity extends BaseActivity implements View.OnClickListener {
    private String searchContext;//输入内容
    private ImageView btn_left;
    private ImageButton btn_right;
    private TextView tv_title;
    private EditText map_ed;
    private ListView map_mymap_lv;
    private DeletemapAdtpter adtpter;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private MCkuai application;
    private String mapType = null;
    private String orderFiled = null;
    private MapBean mapList;
    private Button map_imp;
    private ArrayList<Map> list = new ArrayList<Map>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapdelete);
        initview();
    }

    protected void initview() {
        map_ed = (EditText) findViewById(R.id.map_ed);
        map_imp = (Button) findViewById(R.id.bt_go);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的地图");
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.GONE);
        map_mymap_lv = (ListView) findViewById(R.id.map_mymap_lv);
        client = application.mClient;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.bt_go:
                for (int i = list.size(); i >= 0; i--) {
                    if (list.get(i).getIsSelected()) {
                        list.remove(i);
                    }
                }

                    adtpter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}