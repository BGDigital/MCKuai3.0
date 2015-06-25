package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.ArticItem;
import com.mckuai.imc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kyly on 2015/6/25.
 */
public class ArticAdapter extends RecyclerView.Adapter<ArticAdapter.ViewHolder> {

    private ArrayList<ArticItem> artics;
    private HashMap<Integer,Integer> selecteds = new HashMap<>(10);


    public void setArtics(ArrayList<ArticItem> artics) {
        this.artics = artics;
        notifyDataSetChanged();
    }

    public HashMap<Integer,Integer> getSelectedItem(){
        return  selecteds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_article,parent,false);

        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticItem item = (ArticItem) holder.itemView.getTag();
                if (null != item)
                {
                    if (holder.iv_selected.getVisibility() == View.VISIBLE){
                        holder.iv_selected.setVisibility(View.INVISIBLE);
                        selecteds.remove(item.getId());
                    }
                    else {
                        holder.iv_selected.setVisibility(View.VISIBLE);
                        selecteds.put(item.getId(),item.getId());
                    }
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(artics.get(position).getName());
        holder.itemView.setTag(artics.get(position));
    }

    @Override
    public int getItemCount() {
        return null == artics ? 0:artics.size();
    }

    public static  class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView iv_icon;
        ImageView iv_selected;
        TextView tv_name;

        public ViewHolder(View itemView){
            super(itemView);
            iv_icon = (ImageView)itemView.findViewById(R.id.iv_articleItem);
            iv_selected = (ImageView)itemView.findViewById(R.id.iv_selected);
            tv_name = (TextView)itemView.findViewById(R.id.tv_articleName);
        }
    }

}
