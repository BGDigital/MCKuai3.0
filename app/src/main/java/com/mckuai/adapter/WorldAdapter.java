package com.mckuai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mckuai.mctools.item.WorldItem;
import com.mckuai.imc.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kyly on 2015/7/15.
 */
public class WorldAdapter  extends BaseAdapter{
    private List<WorldItem> worldItems;
    private Context mContext;

    public WorldAdapter(Context context){
         this.mContext = context;
    }

    public void setData(List<WorldItem> list){
        this.worldItems = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == worldItems ? 0: worldItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null == worldItems ? null: worldItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolde vh;
        WorldItem world = (WorldItem)getItem(position);
        if (null == convertView){
            vh  = new ViewHolde();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_world,parent,false);
            vh.name = (TextView)convertView.findViewById(R.id.tv_name);
            vh.worldSize = (TextView)convertView.findViewById(R.id.tv_worldSize);
            vh.lastTime = (TextView)convertView.findViewById(R.id.tv_lastplayed);
            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolde)convertView.getTag();
        }

        if (null != world && null != world.getLevel()) {
            vh.name.setText(world.getLevel().getLevelName() + "");
            vh.worldSize.setText(world.getSizeEx()+"");
            java.util.Date date = new Date(world.getLevel().getLastPlayed() *1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            vh.lastTime.setText("时间:"+sdf.format(date));
        }



        return convertView;
    }

    class ViewHolde{
        TextView name;
        TextView worldSize;
        TextView lastTime;
    }
}
