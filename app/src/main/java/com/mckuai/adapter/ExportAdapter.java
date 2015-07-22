package com.mckuai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.Map;
import com.mckuai.bean.WorldInfo;
import com.mckuai.imc.R;
import com.mckuai.until.MCGameEditer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2015/6/27.
 */
public class ExportAdapter extends BaseAdapter {
    private Context mContext;
    private View view;
    private LayoutInflater mInflater;
    private ArrayList<Map> mMapBeans;
    private ImageLoader mLoader;
    private ArrayList<WorldInfo> worlds;
    private ArrayList<Integer> selectedList;

    public ExportAdapter(Context context, ArrayList<WorldInfo> mapBeans) {
        worlds = MCGameEditer.getAllWorldLite();
        worlds = mapBeans;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return worlds.size();
    }

    @Override
    public Object getItem(int position) {
        return worlds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
//        Map map = (Map) getItem(position);
        WorldInfo world = (WorldInfo) getItem(position);
        if (null == world) {
            return null;
        }
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_export, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.rbtn_ok = (ImageView) convertView.findViewById(R.id.rbtn_ok);
            holder.rbtn_ok.setBackgroundResource(R.drawable.btn_map_export_normal);
            holder.rbtn_ok.setTag(position);
            holder.rbtn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundResource(R.drawable.btn_map_export_checked);
                    int index = (int) v.getTag();
                    worlds.get(index).setIsSelected(true);
                }
            });
//            if (getItemViewType(position) == 1) {
//                holder.rbtn_ok.setImageResource(R.drawable.btn_map_export_checked);
//            } else {
//                holder.rbtn_ok.setImageResource(R.drawable.btn_map_export_normal);
//            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String dateString = "未知";
        String mapName = "未知地图";
        if (null != world.getLevel()) {
            Date currentTime = new Date(world.getLevel().getLastPlayed() * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");//你需要的时间格式
            dateString = formatter.format(currentTime);//得到字符串类型的时间
            mapName = world.getLevel().getLevelName();
        }
        holder.tv_time.setText("更新时间：" + dateString);
        holder.tv_name.setText(mapName);
        Long size = world.getSize()/1024;
        if (size<1024){
            holder.tv_size.setText(size + "KB");
        }else {
            holder.tv_size.setText((size/1024)+"MB");
        }
//        holder.tv_category.setText(map.getResCategroyTwo());
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView tv_name;
        //        public TextView tv_category;
        public TextView tv_time;
        public TextView tv_size;
        public ImageView rbtn_ok;
    }

    public ArrayList<WorldInfo> chuancan() {
        return worlds;
    }
}
