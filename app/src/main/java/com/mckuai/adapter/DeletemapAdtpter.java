package com.mckuai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mckuai.bean.Map;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/28.
 */
public class DeletemapAdtpter extends BaseAdapter {
    private Context mContext;
    private View view;
    private LayoutInflater mInflater;
    private ArrayList<Map> mMapBeans = new ArrayList<Map>();
    private ImageLoader mLoader;

    public DeletemapAdtpter(Context context, ArrayList<Map> mapBeans) {
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
            convertView = mInflater.inflate(R.layout.item_export, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.rbtn_ok = (RadioButton) convertView.findViewById(R.id.rbtn_ok);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_time.setText(map.getInsertTime());
        holder.tv_name.setText(map.getViewName());
        holder.tv_size.setText(map.getResSize());
        holder.tv_category.setText(map.getResCategroyTwo());
        holder.rbtn_ok.setTag(position);
        holder.rbtn_ok.setChecked(mMapBeans.get(position).getIsSelected());
        holder.rbtn_ok.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int index=(int) buttonView.getTag();
                 mMapBeans.get(index).setIsSelected(isChecked);
            }
        });
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView tv_name;
        public TextView tv_category;
        public TextView tv_time;
        public TextView tv_size;
        public RadioButton rbtn_ok;
    }
}
