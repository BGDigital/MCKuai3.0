package com.mckuai.imc;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Zzz on 2015/6/24.
 */
public class ClassificationActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_title;
    private String searchContext;//输入内容
    private EditText map_ed;
    private ImageView btn_left;
    private ImageButton btn_right;
    private LinearLayout cf_l1,cf_l2,cf_l3,cf_l4,cf_l5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_classification);
        initView();
    }

    private void initView() {
        map_ed = (EditText)findViewById(R.id.map_ed);
        btn_left = (ImageView)findViewById(R.id.btn_left);
        btn_right = (ImageButton)findViewById(R.id.btn_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("地图分类");
        cf_l1 = (LinearLayout)findViewById(R.id.cf_l1);
        cf_l2 = (LinearLayout)findViewById(R.id.cf_l2);
        cf_l3 = (LinearLayout)findViewById(R.id.cf_l3);
        cf_l4 = (LinearLayout)findViewById(R.id.cf_l4);
        cf_l5 = (LinearLayout)findViewById(R.id.cf_l5);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        cf_l1.setOnClickListener(this);
        cf_l2.setOnClickListener(this);
        cf_l3.setOnClickListener(this);
        cf_l4.setOnClickListener(this);
        cf_l5.setOnClickListener(this);
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
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();}
                break;
            //生存
            case R.id.cf_l1:
            break;
            //解密
            case R.id.cf_l2:
            break;
            //跑酷
            case R.id.cf_l3:
            break;
            //建筑
            case R.id.cf_l4:
            break;
            //pvp竞技
            case R.id.cf_l5:
            break;
            default:
                break;
        }
    }
    private void search(){

    }
}
