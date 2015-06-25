package com.mckuai.imc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;


public class RankingActivity extends BaseActivity {
    private ListView ranking_lv;
    private ImageView btn_left;
    private ImageButton btn_right;
    private EditText map_ed;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_map);
        initview();
    }

    protected void initview() {
        ranking_lv = (ListView) findViewById(R.id.ranking_lv);
        map_ed = (EditText) findViewById(R.id.map_ed);
        btn_left = (ImageView) findViewById(R.id.btn_left);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
    }
}
