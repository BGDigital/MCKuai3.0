package com.mckuai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.MapBean;
import com.mckuai.widget.MasterLayout;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/6/24.
 */
public class MapAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<MapBean> mMapBeans = new ArrayList<MapBean>();
    public MapAdapter(Context context, ArrayList<MapBean> mapBeans){
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

        return null;
    }
    class ViewHolder
    {
        public ImageView image;
        public TextView tv_name;
        public TextView tv_category;
        public TextView tv_tim;
        public TextView tv_size;
        public MasterLayout MasterLayout01;


    }
}
