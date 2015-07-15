package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.InventorySlot;
import com.mckuai.ItemStack;
import com.mckuai.bean.ArticItem;
import com.mckuai.imc.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kyly on 2015/6/25.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventorySlot> inventorySlotArrayList;
    private HashMap<Integer,Integer> selecteds = new HashMap<>(10);
    private OnItemLongClickListener mListener;

    public interface OnItemLongClickListener{
        public void onLongClick(InventorySlot inventorySlot);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l){
        this.mListener = l;
    }

    public void setInventorySlot(List<InventorySlot>inventorySlotArrayList){
        this.inventorySlotArrayList = inventorySlotArrayList;
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
                InventorySlot item = (InventorySlot) holder.itemView.getTag();
                if (null != item)
                {
                    if (holder.iv_selected.getVisibility() == View.VISIBLE){
                        holder.iv_selected.setVisibility(View.INVISIBLE);
                        selecteds.remove(item.getContents().getTypeId());
                    }
                    else {
                        holder.iv_selected.setVisibility(View.VISIBLE);
                        selecteds.put((int)item.getContents().getTypeId(),item.getContents().getAmount());
                    }
                }

            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                InventorySlot item = (InventorySlot) holder.itemView.getTag();
                if (null != item && null != mListener){
                    mListener.onLongClick(item);
                    return  true;
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemStack itemStack =  inventorySlotArrayList.get(position).getContents();
        holder.tv_name.setText("ID:" + itemStack.getTypeId() + ",数量:" +itemStack.getAmount());
        holder.itemView.setTag(inventorySlotArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return null == inventorySlotArrayList ? 0:inventorySlotArrayList.size();
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
