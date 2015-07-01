package com.mckuai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.until.GameUntil;
import com.mckuai.until.MCMapManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/7/1.
 */
public class MymapAdapter extends BaseAdapter {
    private static Context mContext;
    private View view;
    private LayoutInflater mInflater;
    private ArrayList<Map> mMapBeans;
    private ImageLoader mLoader;
    private MCkuai app;
    private MCMapManager mcMapManager;

    public MymapAdapter(Context context, ArrayList<Map> mapBeans) {
        mMapBeans = mapBeans;
        this.mContext = context;
        app = MCkuai.getInstance();
        mcMapManager = new MCMapManager();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMapBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mMapBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Map map = (Map) getItem(position);
        if (null == map) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_export, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.rbtn_ok = (Button) convertView.findViewById(R.id.rbtn_ok);
            holder.rbtn_ok.setTag(map);
            holder.rbtn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dir = app.getMapDownloadDir();
                    Map map = (Map) v.getTag();
                    dir += map.getFileName();
                    mcMapManager.importMap(dir);
                    GameUntil.startGame(mContext);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_time.setText(map.getInsertTime());
        holder.tv_name.setText(map.getViewName());
        holder.tv_size.setText(map.getResSize());
        holder.tv_category.setText(map.getResCategroyTwo());
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView tv_name;
        public TextView tv_category;
        public TextView tv_time;
        public TextView tv_size;
        public Button rbtn_ok;
    }
}

