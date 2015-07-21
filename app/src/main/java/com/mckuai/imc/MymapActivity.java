package com.mckuai.imc;

import android.content.Context;
import android.content.Intent;
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

import com.mckuai.adapter.ExportAdapter;
import com.mckuai.adapter.MymapAdapter;
import com.mckuai.bean.Map;
import com.mckuai.fragment.MapFragment;
import com.mckuai.until.MCMapManager;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/6/24.
 */
public class MymapActivity extends BaseActivity implements OnClickListener {
    private String searchContext;//输入内容
    private Context mContent;
    private ListView map_mymap_lv;
    private ImageView btn_left, btn_right;
    private TextView tv_title;
    private EditText map_ed;
    private Button go_map, leave_map, delete_map, btn_showOwner;
    private MCMapManager mapManager;
    private MymapAdapter adapter;
    ArrayList<Map> downloadMap;
    private Map map;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_mymap);
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
        }
//        else {
//            for (int i = 0; i < downloadMap.size(); i++) {
//                if (downloadMap.get(i).getIsSelected()) {
//                    String zipname = downloadMap.get(i).getSavePath();
//                    zipname = zipname.substring(map.getSavePath().lastIndexOf(".") + 1, map.getSavePath().length());
//                    if (!zipname.equalsIgnoreCase("zip")) {
//                        showNotification(1, "请下载地图", R.id.mymaprot);
//                    }
//                }
        else {
            adapter = new MymapAdapter(this, downloadMap);
            map_mymap_lv.setAdapter(adapter);
        }
//            }
//        }
    }

    private void initView() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageView) findViewById(R.id.btn_right);
        map_ed = (EditText) findViewById(R.id.map_ed);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的地图");
        map_mymap_lv = (ListView) findViewById(R.id.map_mymap_lv);
        go_map = (Button) findViewById(R.id.go_map);
        leave_map = (Button) findViewById(R.id.leave_map);
        btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
        btn_showOwner.setVisibility(View.GONE);
        delete_map = (Button) findViewById(R.id.delete_map);
        btn_left.setOnClickListener(this);
        go_map.setOnClickListener(this);
        leave_map.setOnClickListener(this);
        delete_map.setOnClickListener(this);
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
                intent = new Intent(this, MapimportActivity.class);
                startActivity(intent);
                break;
            case R.id.leave_map:
                intent = new Intent(this, Export_mapActivity.class);
                startActivity(intent);
                break;
            case R.id.delete_map:
                intent = new Intent(this, MapdeleteActivity.class);
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
}
