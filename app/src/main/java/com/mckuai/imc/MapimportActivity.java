package com.mckuai.imc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.mckuai.adapter.ExportAdapter;
import com.mckuai.adapter.MapImportAdapter;
import com.mckuai.bean.Map;
import com.mckuai.bean.MapBean;
import com.mckuai.until.GameUntil;
import com.mckuai.until.MCMapManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MapimportActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView btn_left, btn_right;
    private TextView tv_title;
    private ListView mpt_ls;
    private Button bt_go, btn_showOwner;
    private MapImportAdapter adapter;
    private MCMapManager mapManager;
    private ArrayList<String> curMaps;
    private Context mContent;
    private Map map;
    ArrayList<String> curmap;
    ArrayList<Map> downloadMap;
    private LinearLayout pt_ly;
    private String currentDir;
    private String filename;
    private String namefile;

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
            mapManager = MCkuai.getInstance().getMapManager();

            initview();
        }

        showData();
        curmap = mapManager.getCurrentMapDirList();
    }

    @Override
    protected void onDestroy() {

        if (null != mapManager) {
            mapManager.closeDB();
        }
        super.onDestroy();
    }

    private void showData() {
//        downloadMap = mapManager.getDownloadMaps();
        if (null == dirList && null == fileList) {
            currentDir = MCkuai.getInstance().getSDPath();
            if (!getFileList(currentDir)) {
                //no data
                showNotification(1, "请确认SD卡是否存在", R.id.import_tit);
            } else {
                adapter = new MapImportAdapter(this, fileList, dirList);
                mpt_ls.setAdapter(adapter);
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
        btn_right = (ImageView) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_go = (Button) findViewById(R.id.bt_go);
        btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
        btn_showOwner.setVisibility(View.GONE);
        tv_title.setText("我的地图");
        mpt_ls = (ListView) findViewById(R.id.mpt_ls);
        pt_ly = (LinearLayout) findViewById(R.id.pt_ly);
        pt_ly.setOnClickListener(this);
        mpt_ls.setOnItemClickListener(this);
        btn_left.setOnClickListener(this);
        bt_go.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
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
            case R.id.bt_go:
                if (namefile == null) {
                    showNotification(1, "请选择文件", R.id.import_tit);
                } else {
                    if (!mapManager.importMap(namefile)) {
                        showNotification(1, "游戏导入失败", R.id.import_tit);
                    } else {
                        Toast.makeText(this, "游戏导入成功", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            default:
                break;
        }
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
            namefile = filename;
//            mapManager.importMap(filename);
//            GameUntil.startGame(this);
        }

    }

    private String showParentDir() {
        if (currentDir != null && !currentDir.equalsIgnoreCase(MCkuai.getInstance().getSDPath())) {
            int index = currentDir.lastIndexOf("/");
            if (index >= 0) {
                String temname = currentDir.substring(0, index);
                currentDir = temname;
                return temname;
            }

        } else {
            showNotification(1, "已经在最上层", R.id.import_tit);
        }
        return null;
    }


    private ArrayList<String> dirList;
    private ArrayList<String> fileList;

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
            Comparator<String> comparator = new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareToIgnoreCase(rhs);
                }
            };
            Collections.sort(dirList);
            Collections.sort(fileList);
            Collections.sort(dirList, comparator);
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
}
