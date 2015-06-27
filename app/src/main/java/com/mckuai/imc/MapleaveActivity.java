package com.mckuai.imc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class MapleaveActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btn_left, pt_im;
    private ImageButton btn_right;
    private TextView tv_title;
    private Button bt_go;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_leave);
        initview();
    }

    protected void initview() {
        btn_left = (ImageView) findViewById(R.id.btn_left);
        pt_im = (ImageView) findViewById(R.id.pt_im);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("ÎÒµÄµØÍ¼");
        bt_go = (Button) findViewById(R.id.bt_go);
        btn_left.setOnClickListener(this);
        pt_im.setOnClickListener(this);
        bt_go.setOnClickListener(this);
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
}
