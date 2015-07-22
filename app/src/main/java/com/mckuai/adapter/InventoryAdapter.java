package com.mckuai.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.InventorySlot;
import com.mckuai.ItemStack;
import com.mckuai.imc.R;
import com.mckuai.material.Material;
import com.mckuai.material.MaterialKey;
import com.mckuai.material.icon.MaterialIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyly on 2015/6/25.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder>  implements Filterable{

    private List<InventorySlot> inventorySlotArrayList;//这个是背包里的东西，最大数量为40
    private List<Material> itemList;//这个是所有的物品的列表,显示的就是它
    private OnItemClickedListener mListener;
    private int mSlotPosition;//这个是所选的物品的插槽id。-1：新添加物品且插槽已满，需要自己创建。其它情况为对应的插槽位置

    private Filter mFilter;

    public interface  OnItemClickedListener{
        public void OnItemClicked(ItemStack item,Material material,Drawable icon);
    }

    public InventoryAdapter(){
        super();
        itemList = Material.materials;
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
     * @param item 被修改过的物品
     */
    public void updateInventory(ItemStack item){
        if (-1 == mSlotPosition) {
        //需要创建新的插槽
            InventorySlot slot = createInventorySlot(item);
            if (null != slot) {
                inventorySlotArrayList.add(slot);
            }
        }
        else {
            //已有现成的
            InventorySlot slot = inventorySlotArrayList.get(mSlotPosition);
            slot.setContents(item);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取修改后的背包
     * @return
     */
    public List<InventorySlot> getInventorySlots(){
        return  inventorySlotArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_article, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Material material= (Material) holder.itemView.getTag();
                mSlotPosition = getInventoryIndex(material);
                if (null != mListener) {
                    if (-1 == mSlotPosition) {
                        mListener.OnItemClicked(new ItemStack((short)material.getId(), (short) 255, 0),material,holder.iv_icon.getDrawable());
                    } else {
                        mListener.OnItemClicked(inventorySlotArrayList.get(mSlotPosition).getContents(),material,holder.iv_icon.getDrawable());
                    }
                }
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Material item = itemList.get(position);
        if (null != item){
            MaterialIcon icon = MaterialIcon.icons.get(new MaterialKey((short)item.getId(),item.getDamage()));
            if (null == icon){
                icon = MaterialIcon.icons.get(new MaterialKey((short)item.getId(),(short)0));
            }
            if (null != icon){
                BitmapDrawable drawable = new BitmapDrawable(icon.bitmap);
                drawable.setDither(false);
                drawable.setAntiAlias(true);
                drawable.setFilterBitmap(false);
                holder.iv_icon.setImageDrawable(drawable);
                holder.iv_icon.setVisibility(View.VISIBLE);
                holder.iv_icon.setTag(drawable);
            }
            holder.itemView.setTag(item);
            holder.tv_name.setText(item.getName());
            int index = getInventoryIndex(item);
            if (0 > index){
                holder.iv_selected.setVisibility(View.INVISIBLE);
                holder.tv_count.setVisibility(View.INVISIBLE);
            }
            else {
                holder.iv_selected.setVisibility(View.VISIBLE);
                holder.tv_count.setText("数量："+inventorySlotArrayList.get(index).getContents().getAmount());
                holder.tv_count.setVisibility(View.VISIBLE);
            }
            holder.itemView.setTag(item);
        }
    }

    /**
     * 获取物品在于背包中的位置，如果没在背包中则返回-1
     * @param item
     * @return
     */
    private int getInventoryIndex(Material item){
        if (item.getId() == 255){
            return -1;
        }
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
    private InventorySlot createInventorySlot(ItemStack item){
        if (null == inventorySlotArrayList){
            inventorySlotArrayList = new ArrayList<>(10);
        }

        byte slotId = -1;
        boolean result = false;

        do {
            slotId ++;
            for (InventorySlot inventorySlot:inventorySlotArrayList){
                if (inventorySlot.getSlot() == slotId){
                    break;
                }
            }
            result = true;
        }while (!result)  ;

        InventorySlot inventorySlot = new InventorySlot(slotId,item);
        return  inventorySlot;
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
        TextView tv_count;

        public ViewHolder(View itemView){
            super(itemView);
            iv_icon = (ImageView)itemView.findViewById(R.id.iv_articleItem);
            iv_selected = (ImageView)itemView.findViewById(R.id.iv_selected);
            tv_name = (TextView)itemView.findViewById(R.id.tv_articleName);
            tv_count = (TextView)itemView.findViewById(R.id.tv_count);
        }
    }


    /**
     * 设置item点击事件监听器
     * @param listener item点击监听器
     */
    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.mListener = listener;
    }

    private int getItemCount(int id){
        if (null != inventorySlotArrayList && !inventorySlotArrayList.isEmpty()){
            for (InventorySlot slot :inventorySlotArrayList){
                if (slot.getContents().getTypeId() == id){
                    return  slot.getContents().getAmount();
                }
            }
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (null == mFilter){
            mFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                }
            } ;
        }
        return mFilter;
    }
}
