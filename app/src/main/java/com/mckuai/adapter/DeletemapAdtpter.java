package com.mckuai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.utils.MCMapManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/28.
 */
public class DeletemapAdtpter extends BaseAdapter {
    private Context mContext;
    private MCMapManager mapManager;
    private View view;
    private LayoutInflater mInflater;
    private ImageLoader loader;
    private ArrayList<Map> mMapBeans = new ArrayList<Map>();
    private Map map;
    private ArrayList<Integer> selectedList;
    private DisplayImageOptions options = MCkuai.getInstance().getNormalOption();


    public DeletemapAdtpter(Context context, ArrayList<Map> mapBeans) {
        mapManager = MCkuai.getInstance().getMapManager();
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
            convertView = mInflater.inflate(R.layout.item_delete, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.rbtn_delete = (ImageView) convertView.findViewById(R.id.rbtn_delete);

//            holder.rbtn_delete.setBackgroundResource(R.drawable.btn_cooper_normal);
//            holder.rbtn_delete.setTag(position);
//            holder.rbtn_ok.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setBackgroundResource(R.drawable.btn_cooper_checked);
//                    int index = (int) v.getTag();
//                    mMapBeans.get(index).setIsSelected(true);
//                }
//            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_time.setText(map.getInsertTime());
        holder.tv_name.setText(map.getViewName());
        Long size = Long.parseLong(map.getResSize()) / 1024;
        if (size < 1024) {
            holder.tv_size.setText(size + "KB");
        } else {
            holder.tv_size.setText((size / 1024) + "MB");
        }
        String leixing = map.getResCategroyTwo().substring(map.getResCategroyTwo().indexOf("|") + 1, map.getResCategroyTwo().length());
        leixing = leixing.replace("|", " ");
        holder.tv_category.setText(leixing);
        if (null == loader) {
            loader = ImageLoader.getInstance();
        }
        if (null != map.getIcon() && map.getIcon().length() > 10) {
            loader.displayImage(map.getIcon(), holder.image, options);
            holder.image.setTag(map.getIcon());
        }
        if (getItemViewType(position) == 1) {
            holder.rbtn_delete.setImageResource(R.drawable.btn_map_detele_checked);
        } else {
            holder.rbtn_delete.setImageResource(R.drawable.btn_map_detele_normal);
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView tv_name;
        public TextView tv_category;
        public TextView tv_time;
        public TextView tv_size;
        public ImageView rbtn_delete;
    }

    public void setchuancan(ArrayList<Map> maps) {
        mMapBeans = maps;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (selectedList == null || selectedList.isEmpty()) {
            return 0;
        }
        for (Integer curposition : selectedList) {
            if (curposition == position) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void setSelectedList(ArrayList<Integer> selectedList) {
        this.selectedList = selectedList;
        notifyDataSetChanged();
    }
}
