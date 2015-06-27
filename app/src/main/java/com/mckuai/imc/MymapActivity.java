package com.mckuai.imc;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mckuai.bean.Map;
import com.mckuai.until.MCMapManager;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/6/24.
 */
public class MymapActivity extends BaseActivity implements OnClickListener {
    private String searchContext;//输入内容
    private ListView map_mymap_lv;
    private ImageView btn_left;
    private ImageButton btn_right;
    private TextView tv_title;
    private EditText map_ed;
    private Button go_map, leave_map, delete_map;
    private MCMapManager mapManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_mymap);
        initView();
    }

    @Override
    protected void onResume() {
        if (null == mapManager){
            mapManager = new MCMapManager();
        }
        ArrayList<String> curmap = mapManager.getCurrentMaps();
        ArrayList<Map> downloadMap = mapManager.getDownloadMaps();
        super.onResume();
    }

    private void initView() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        map_ed = (EditText) findViewById(R.id.map_ed);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的地图");
        map_mymap_lv = (ListView) findViewById(R.id.map_mymap_lv);
        go_map = (Button) findViewById(R.id.go_map);
        leave_map = (Button) findViewById(R.id.leave_map);
        delete_map = (Button) findViewById(R.id.delete_map);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        go_map.setOnClickListener(this);
        leave_map.setOnClickListener(this);
        delete_map.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            //根据输入名搜索地图
            case R.id.btn_right:
                map_ed.setVisibility(View.VISIBLE);
                if (0 < map_ed.getText().length()) {
                    searchContext = map_ed.getText().toString().trim();//trim() 表示空格
                    search();
                } else {
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.go_map:

            break;
            case R.id.leave_map:

                break;
            case R.id.delete_map:

                break;
            default:
                break;
        }
    }
    private void search() {

    }
}
