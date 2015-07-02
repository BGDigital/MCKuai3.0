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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mckuai.adapter.ExportAdapter;
import com.mckuai.adapter.MapExportAdapter;
import com.mckuai.adapter.MapImportAdapter;
import com.mckuai.bean.Map;
import com.mckuai.until.MCMapManager;

import java.io.File;
import java.util.ArrayList;


public class MapexportActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView btn_left, pt_im;
    private ImageButton btn_right;
    private TextView tv_title;
    private ListView map_lv_leave;
    private Button bt_go;
    private MapExportAdapter adapter;
    private MCMapManager mapManager;
    private Map map;
    private ArrayList<String> curMaps;
    private Context mContent;
    ArrayList<Map> downloadMap;
    private ArrayList<String> dirList;
    private ArrayList<String> fileList;
    private String currentDir;
    private String filename;
    private LinearLayout pt_ly;

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
        showData();
    }

    @Override
    protected void onDestroy() {

        if (null != mapManager) {
            mapManager.closeDB();
        }
        super.onDestroy();
    }

    protected void showData() {
        if (null == dirList && null == fileList) {
            currentDir = MCkuai.getInstance().getSDPath();
            if (!getFileList(currentDir)) {
                //no data
                showNotification(1, "请确认SD卡是否存在", R.id.import_tit);
            } else {
                adapter = new MapExportAdapter(this, fileList, dirList);
                map_lv_leave.setAdapter(adapter);
                return;
            }
        } else {
            adapter.notifyDataSetChanged();
        }
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
        pt_ly = (LinearLayout) findViewById(R.id.pt_ly);
        pt_ly.setOnClickListener(this);
//        map_lv_leave.setOnItemSelectedListener(this);
        map_lv_leave.setOnItemClickListener(this);
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
//                mapManager.exportMap();
                break;
            case R.id.pt_ly:
                String zimulu = showParentDir();

                if (zimulu == null) {
                    showNotification(1, "没有SD卡", R.id.import_tit);
                } else {
                    getFileList(zimulu);
                    adapter.setdate(fileList, dirList);
                }
                break;
            default:
                break;
        }
    }


    private String showParentDir() {
        if (currentDir != null && !currentDir.equalsIgnoreCase(MCkuai.getInstance().getSDPath())) {
            int index = currentDir.lastIndexOf("/");
            if (index >= 0) {
                String temname = currentDir.substring(0, index + 1);
//                currentDir = temname;
                return temname;
            }

        } else {
            //
            showNotification(1, "已经在最上层", R.id.import_tit);
        }
        return null;
    }

    private boolean getFileList(String path) {
        if (null == path || path.isEmpty()) {
            return false;
        }

        File file[] = new File(path).listFiles();
        if (null != file && 0 < file.length) {
            if (null == dirList) {
                dirList = new ArrayList<>();
                fileList = new ArrayList<>();
            } else {
                dirList.clear();
                fileList.clear();
            }
            for (File curFile : file) {
                if (curFile.isDirectory()) {
                    dirList.add(curFile.getPath());
                }
                if (curFile.isFile()) {
                    if (isZipFile(curFile.getName())) {
                        fileList.add(curFile.getPath());
                    }
                }
            }
            return true;
        }
        return false;
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
        if (position >= fileList.size()) {
            currentDir = (String) adapter.getItem(position);
            if (getFileList(currentDir)) {
                adapter.notifyDataSetChanged();
            }
        } else {
            filename = (String) adapter.getItem(position);
//            mapManager.importMap(filename);
//            GameUntil.startGame(this);
        }
    }
}
