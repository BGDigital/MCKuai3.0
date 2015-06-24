package com.mckuai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mckuai.imc.ClassificationActivity;
import com.mckuai.imc.MapActivity;
import com.mckuai.imc.R;

public class MapFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private Button rb_map, rb_classification, rb_mymap;
    private EditText map_ed;
    private ListView map_ls;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    protected void initView() {
        map_ed = (EditText) view.findViewById(R.id.map_ed);
        rb_map = (Button) view.findViewById(R.id.rb_map);
        rb_classification = (Button) view.findViewById(R.id.rb_classification);
        rb_mymap = (Button) view.findViewById(R.id.rb_mymap);
        map_ls = (ListView) view.findViewById(R.id.map_ls);
        rb_map.setOnClickListener(this);
        rb_classification.setOnClickListener(this);
        rb_mymap.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rb_map:
                intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                break;
            case R.id.rb_classification:
                intent = new Intent(getActivity(), ClassificationActivity.class);
                startActivity(intent);
                break;
            case R.id.rb_mymap:
                intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
