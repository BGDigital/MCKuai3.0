package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.Map;
import com.mckuai.imc.R;
import com.mckuai.widget.MasterLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/8.
 */
public class mapadapters extends RecyclerView.Adapter<mapadapters.ViewHolder> {

    private final String TAG = "mapadaptres";
    private ArrayList<Map> maps;
    private ImageLoader loader;
    private OnItemClickListener itemClickListener;
    private OnMapAddListener addListener;

    public interface OnItemClickListener {
        public void onItemClick(Map mapinfo);
    }

    public interface OnMapAddListener {
        public void afterMapAdded();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void SetOnMapAddListener(OnMapAddListener listener) {
        this.addListener = listener;
    }

    public void setData(ArrayList<Map> maplist) {
        this.maps = maplist;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map mapinfo = (Map) v.getTag();
                if (null == itemClickListener) {
                    itemClickListener.onItemClick(mapinfo);
                } else {
                    Log.e(TAG, "OnItemClickListener not set!");
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map map = maps.get(position);
        if (null != map) {
            holder.itemView.getTag(position);
            if (null == loader) {
                loader = ImageLoader.getInstance();
            }
            if (null != map.getIcon() && map.getIcon().length() > 10) {
                loader.displayImage(map.getIcon(), holder.image);
            }
            holder.tv_name.setText(map.getViewName());
            String leixing = map.getResCategroyTwo().substring(map.getResCategroyTwo().indexOf("|") + 1, map.getResCategroyTwo().length());
            holder.tv_category.setText(leixing);
            holder.tv_size.setText(map.getResSize());
            holder.tv_time.setText(map.getInsertTime());
            holder.MasterLayout01.getTag();
        }
    }

    @Override
    public int getItemCount() {
        return null == maps ? 0 : maps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tv_name;
        TextView tv_category;
        TextView tv_time;
        TextView tv_size;
        MasterLayout MasterLayout01;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            MasterLayout01 = (MasterLayout) itemView.findViewById(R.id.MasterLayout01);
        }
    }
}
