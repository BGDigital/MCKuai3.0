package com.mckuai.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.SkinItem;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.MCSkinManager;
import com.mckuai.mctools.WorldUtil.OptionUntil;
import com.mckuai.widget.fabbutton.FabButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/8/14.
 */
public class SkinAdapter extends RecyclerView.Adapter<SkinAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<SkinItem> mSkins;
    private ImageLoader mLoader;
    private OnItemClickListener l;
    private DisplayImageOptions options;
    private MCSkinManager manager;

    public SkinAdapter(Context context){
        this.mContext = context;
        if (null == manager){
            manager = MCkuai.getInstance().getSkinManager();
        }
    }

    public interface OnItemClickListener {
        public void onItemClicked(SkinItem item);

        public void onAddButtonClicked(SkinItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        l = listener;
    }

    public void setData(ArrayList<SkinItem> skinItems) {
        if (null != skinItems) {
            mSkins = skinItems;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skin, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SkinItem item = mSkins.get(position);
        if (null != item) {
            if (null == mLoader) {
                mLoader = ImageLoader.getInstance();
            }

            if (null != item.getIcon() && 10 < item.getIcon().length() && (null == holder.iv_skinCover.getTag() ||!((String)holder.iv_skinCover.getTag()).equals(item.getIcon()))) {
                if (null == options) {
                    options = MCkuai.getInstance().getNormalOption();
                }
                mLoader.displayImage(item.getIcon(), holder.iv_skinCover, options);
            }
            holder.tv_skinName.setText(item.getViewName() + "");
            holder.tv_skinType.setText(item.getVersion() + "");
            holder.tv_skinOwner.setText(item.getUploadMan() + "");
            if (0 < item.getProgress()){
                holder.btn_operation.setProgress(item.getProgress());
            }
            else {
                holder.btn_operation.resetIcon();
            }
            holder.btn_operation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (item.getProgress()){
                        case -1:
                            MobclickAgent.onEvent(mContext, "downloadSkin");
                            Intent intent = new Intent();
                            intent.setAction("com.mckuai.downloadservice");
                            intent.setPackage(mContext.getPackageName());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("SKIN", item);
                            intent.putExtras(bundle);
                            mContext.startService(intent);
                            break;
                        case 100:
                            OptionUntil.setSkin(2);//配置成自定义皮肤
                            manager.moveToGame(item);
                            MobclickAgent.onEvent(mContext, "startGame_skin");
                            GameUntil.startGame(mContext,11);
                            break;
                        default:
                            break;
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != l) {
                        l.onItemClicked(item);
                    }
                }
            });
        }
        holder.iv_skinCover.setTag(item.getIcon());
    }

    @Override
    public int getItemCount() {
        return null == mSkins ? 0 : mSkins.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_skinCover;
        private TextView tv_skinName;
        private TextView tv_skinType;
        private TextView tv_skinOwner;
        private FabButton btn_operation;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_skinCover = (ImageView) itemView.findViewById(R.id.iv_skinCover);
            tv_skinName = (TextView) itemView.findViewById(R.id.tv_skinName);
            tv_skinType = (TextView) itemView.findViewById(R.id.tv_skinType);
            tv_skinOwner = (TextView) itemView.findViewById(R.id.tv_skinOwner);
            btn_operation = (FabButton) itemView.findViewById(R.id.btn_addSkin);
        }
    }
}
