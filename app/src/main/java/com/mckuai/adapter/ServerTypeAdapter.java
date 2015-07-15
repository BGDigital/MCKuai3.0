package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.GameServerInfo;
import com.mckuai.imc.R;
import com.mckuai.until.ServerEditer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/6/25.
 */
public class ServerTypeAdapter extends RecyclerView.Adapter<ServerTypeAdapter.ViewHolder> {

    private final String TAG = "ServerAdapter";

    private String[] typeList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener{
        public void onItemClick(String type);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    public void setData(String[] typeList) {
        this.typeList = typeList;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servertype, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = (String) v.getTag();
                if (null != itemClickListener){
                    itemClickListener.onItemClick(type);
                }
                else {
                    Log.e(TAG, "OnItemClickListener not set!");
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String type = typeList[position];
        if (null !=type){
//            holder.icon.setImageResource();
            holder.name.setText(type);
            holder.itemView.setTag(type);
        }
    }

    @Override
    public int getItemCount() {
        return null == typeList ? 0:typeList.length;
    }

    public static  class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.iv_typeIcon);
            name = (TextView) itemView.findViewById(R.id.tv_typeNmae);
        }
    }

}
