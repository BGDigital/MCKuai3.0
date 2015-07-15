package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.InventorySlot;
import com.mckuai.ItemStack;
import com.mckuai.entity.EntityItem;
import com.mckuai.imc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyly on 2015/6/25.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventorySlot> inventorySlotArrayList;//这个是背包里的东西，最大数量为36
    private ArrayList<EntityItem> itemList;//这个是所有的物品的列表,显示的就是它
    private OnItemClickedListener mListener;

    public interface  OnItemClickedListener{
        public void OnItemClicked(InventorySlot item);
    }

    /**
     * 设置item点击事件监听器
     * @param listener item点击监听器
     */
    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.mListener = listener;
    }


    /**
     * 设置背包，背包中的物品将呈现出被选中的状态
     * @param inventorySlotArrayList
     */
    public void setInventorySlot(List<InventorySlot>inventorySlotArrayList){
        this.inventorySlotArrayList = inventorySlotArrayList;
        notifyDataSetChanged();
    }

    /**
     * 更新被修改过数量的插槽
     * @param inventorySlot 被修改过的插槽
     */
    public void updateInventory(InventorySlot inventorySlot){
        if (null != inventorySlot && null != inventorySlotArrayList && !inventorySlotArrayList.isEmpty()){
            for (InventorySlot item:inventorySlotArrayList){
                if (item.getSlot() == inventorySlot.getSlot() && item.getContents().getTypeId() == item.getContents().getTypeId()){
                    item = inventorySlot;
                    break;
                }
            }
            if (inventorySlot.getContents().getAmount() == 0){
                inventorySlotArrayList.remove(inventorySlot);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 获取修改后的背包
     * @return
     */
    public List<InventorySlot> getInventorySlots(){
        return  inventorySlotArrayList;
    }




    public InventoryAdapter(){
        super();
        itemList = EntityItem.getAllItem();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_article, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntityItem item = (EntityItem) holder.itemView.getTag();
                //如果是已有的背包，直接取，否则够造一个
                int index = getInventoryIndex(item);
                InventorySlot inventorySlot;
                if (0 > index){
                   inventorySlot = createInventorySlot(item);
                }
                else {
                    inventorySlot = inventorySlotArrayList.get(index);
                }
                if (null != mListener){
                    mListener.OnItemClicked(inventorySlot);
                }
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EntityItem item = itemList.get(position);
        if (null != item){
            holder.itemView.setTag(item);
            holder.tv_name.setText(item.getName());
            int index = getInventoryIndex(item);
            if (0 > index){
                holder.iv_selected.setVisibility(View.INVISIBLE);
            }
            else {
                holder.iv_selected.setVisibility(View.VISIBLE);
            }
            holder.itemView.setTag(item);
        }
    }

    /**
     * 检查所给的物品是否已存在于背包中
     * @param item
     * @return
     */
    private int getInventoryIndex(EntityItem item){
        if (null != inventorySlotArrayList && !inventorySlotArrayList.isEmpty()){
            for (int i = 0;i < inventorySlotArrayList.size();i++){
                InventorySlot itemSlot = inventorySlotArrayList.get(i);
                if (itemSlot.getContents().getId() == item.getId()){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 生成一个新的InventorySlot
     * @param item
     * @return
     */
    private InventorySlot createInventorySlot(EntityItem item){
        if (null == inventorySlotArrayList){
            inventorySlotArrayList = new ArrayList<>(10);
        }
        if (inventorySlotArrayList.size()  < 40){
            ItemStack itemStack = new ItemStack(item.getId(),(short)255,0);
            short index = 0;
            for (short i = 9;i < 51;i++){
                boolean result = false;
                for (InventorySlot inventorySlot:inventorySlotArrayList){

                    if (byte2Short(inventorySlot.getSlot())  == i){
                        result = true;
                        break;
                    }
                }
                if (!result){
                    index = i;
                    break;
                }
            }
            InventorySlot inventorySlot = new InventorySlot(short2Byte(index),itemStack);
            inventorySlotArrayList.add(inventorySlot);
           return inventorySlot;
        }
        return null;
    }

    private short byte2Short(byte b){
        return (short)(b&0xFF);
    }

    private byte short2Byte(short s) {
        return (byte)(s & 0xff);
    }

    @Override
    public int getItemCount() {
        return null ==itemList ? 0:itemList.size();
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
