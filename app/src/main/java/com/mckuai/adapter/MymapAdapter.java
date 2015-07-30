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
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.MCMapManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/7/1.
 */
public class MymapAdapter extends BaseAdapter {
    private static Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Map> mMapBeans;
    private ImageLoader loader;
    private MCkuai app;
    private MCMapManager mcMapManager;
    private DisplayImageOptions options = MCkuai.getInstance().getNormalOption();

    public MymapAdapter(Context context, ArrayList<Map> mapBeans) {
        mMapBeans = mapBeans;
        this.mContext = context;
        app = MCkuai.getInstance();
        mcMapManager = MCkuai.getInstance().getMapManager();
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
            convertView = mInflater.inflate(R.layout.item_mymap, null);
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
        String time = map.getInsertTime().substring(map.getInsertTime().indexOf("-") + 1, map.getInsertTime().indexOf(" "));
        time = time.replace("-", "月");
        holder.tv_time.setText("更新时间：" + time + "日");
        holder.tv_name.setText(map.getViewName());
        holder.tv_size.setText(map.getResSize()+"MB");
        String leixing = map.getResCategroyTwo().substring(map.getResCategroyTwo().indexOf("|") + 1, map.getResCategroyTwo().length());
        leixing = leixing.replace("|", " ");
        if (null == loader) {
            loader = ImageLoader.getInstance();
        }
        if (null != map.getIcon() && map.getIcon().length() > 10) {
            loader.displayImage(map.getIcon(), holder.image, options);
            holder.image.setTag(map.getIcon());
        }
        holder.tv_category.setText(leixing);
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

