package com.mckuai.imc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.until.MCMapManager;

import java.util.ArrayList;


public class MapleaveActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btn_left, pt_im;
    private ImageButton btn_right;
    private TextView tv_title;
    private Button bt_go;

    private MCMapManager mapManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_leave);
        initview();
        mapManager = new MCMapManager();
    }

    protected void initview() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        pt_im = (ImageView) findViewById(R.id.pt_im);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的地图");
        bt_go = (Button) findViewById(R.id.bt_go);
        btn_left.setOnClickListener(this);
        pt_im.setOnClickListener(this);
        bt_go.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> curMaps = mapManager.getCurrentMapDirList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.pt_im:

                break;
            case R.id.bt_go:

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mapManager.closeDB();
        super.onDestroy();
    }
}
