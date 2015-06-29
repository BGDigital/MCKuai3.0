package com.mckuai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mckuai.bean.Map;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/6/29.
 */
public class MapExportAdapter extends BaseAdapter {
    private Context mContext;
    private View view;
    private LayoutInflater mInflater;
    private ArrayList<Map> mMapBeans;
    private ImageLoader mLoader;

    public MapExportAdapter(Context context, ArrayList<Map> mapBeans) {
        mMapBeans = mapBeans;
        this.mContext = context;
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
        Map map = (Map) getItem(position);
        if (null == map) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_position, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(map.getViewName());
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView tv_name;
    }
}