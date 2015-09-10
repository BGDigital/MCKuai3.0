package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.imc.R;

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
      /*  view.setOnClickListener(new View.OnClickListener() {
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
        });*/
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String type = typeList[position];
        if (null !=type){
//            holder.icon.setImageResource();
            holder.name.setText(type);
            holder.itemView.setTag(type);
            switch (type){
                case "全部":
                    holder.icon.setImageResource(R.drawable.icon_type_all);
                break;
                case "生存":
                    holder.icon.setImageResource(R.drawable.icon_type_survival);
                    break;
                case "冒险":
                    holder.icon.setImageResource(R.drawable.icon_type_decrypt);
                    break;
                case "创造":
                    holder.icon.setImageResource(R.drawable.icon_type_create);
                    break;
                case "混合":
                    holder.icon.setImageResource(R.drawable.icon_type_mix);
                    break;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemClickListener){
                        itemClickListener.onItemClick(type);
                    }
                }
            });
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
