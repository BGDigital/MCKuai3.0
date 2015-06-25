package com.mckuai.imc;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Zzz on 2015/6/25.
 */
public class map_details extends BaseActivity {
    private TextView tv_title,tv_name,tv_nm,tv_category,tx_times,tv_tx;
    private ImageView btn_left,image;
    private ImageButton btn_right;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_details);
        initview();
    }
    public void initview(){

    }
}
