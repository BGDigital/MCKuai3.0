package com.mckuai.imc;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Zzz on 2015/6/24.
 */
public class MapActivity extends BaseActivity implements View.OnClickListener {
    private View view;
    private String searchContext;//输入内容
    private ListView ranking_lv;
    private ImageView btn_left;
    private ImageButton btn_right;
    private TextView tv_title;
    private EditText map_ed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_map);
        initView();
    }

    private void initView() {
        ranking_lv = (ListView) findViewById(R.id.ranking_lv);
        btn_left = (ImageView)findViewById(R.id.btn_left);
        btn_right = (ImageButton)findViewById(R.id.btn_right);
        tv_title = (TextView)findViewById(R.id.tv_title);
        map_ed = (EditText)findViewById(R.id.map_ed);
        tv_title.setText("地图排行");
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_left:
                finish();
            break;
            //根据输入名搜索地图
            case R.id.btn_right:
                map_ed.setVisibility(View.VISIBLE);
                 if(0 <map_ed.getText().length()){
                     searchContext = map_ed.getText().toString().trim();//trim() 表示空格
                     search();
                 }else {
                     Toast.makeText(this, "", Toast.LENGTH_SHORT).show();}
                break;
            default:
                break;
        }
    }
    private void search(){

    }
}
