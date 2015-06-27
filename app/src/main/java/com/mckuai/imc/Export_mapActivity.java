package com.mckuai.imc;

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


public class Export_mapActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String searchContext;//输入内容
    private ImageView btn_left, pt_im;
    private ImageButton btn_right;
    private TextView tv_title;
    private EditText map_ed;
    private ListView mpt_ls;
    private Button bt_go;
    private AsyncHttpClient client;
    private Gson mGson = new Gson();
    private MCkuai application;
    private String mapType = null;
    private String orderFiled = null;
    private MapBean mapList;
    private ExportAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymap_export);
        initview();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mpt_ls == null) {
            initview();
        }
        showData();
    }

    private void showData() {

    }

    protected void initview() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_go = (Button) findViewById(R.id.bt_go);
        tv_title.setText("我的地图");
        map_ed = (EditText) findViewById(R.id.map_ed);
        mpt_ls = (ListView) findViewById(R.id.mpt_ls);
        mpt_ls.setOnItemClickListener(this);
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
            case R.id.btn_right:
                map_ed.setVisibility(View.VISIBLE);
                if (0 < map_ed.getText().length()) {
                    searchContext = map_ed.getText().toString().trim();//trim() 表示空格
                    search();
                } else {
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_go:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Export_mapActivity.this, Map_detailsActivity.class);
        Map mapList = (Map) adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.Details), mapList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void search() {

    }
}
