package com.mckuai.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.SkinItem;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.widget.fabbutton.FabButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/8/14.
 */
public class SkinAdapter extends RecyclerView.Adapter<SkinAdapter.ViewHolder> {
    private ArrayList<SkinItem> mSkins;
    private ImageLoader mLoader;
    private OnItemClickListener l;
    private DisplayImageOptions options;

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
            if (null != item.getIcon() && 10 < item.getIcon().length()) {
                if (null == options) {
                    options = MCkuai.getInstance().getNormalOption();
                }
                mLoader.displayImage(item.getIcon(), holder.iv_skinCover, options);
            }
            holder.tv_skinName.setText(item.getViewName() + "");
            holder.tv_skinType.setText(item.getVersion() + "");
            holder.tv_skinOwner.setText(item.getUploadMan() + "");
            holder.btn_operation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != l) {
                        l.onAddButtonClicked(item);
                        holder.btn_operation.resetIcon();
                        holder.btn_operation.showProgress(true);
                        setProgress(holder.btn_operation, 0);
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
    }

    @Override
    public int getItemCount() {
        return null == mSkins ? 0 : mSkins.size();
    }


    private void setProgress(FabButton button, int progress) {
          genProgress(button,progress);
    }


    private Runnable genProgress(final FabButton button,final int progress) {
        Log.e("000000","progress="+progress);
        return new Runnable() {
            @Override
            public void run() {
                Log.e("11111","progress="+progress);
                final int p = progress+1;
                if (100 >= p){
                    Activity activity = (Activity)button.getContext();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setProgress(p);
                            Log.e("222222","progress="+p);
                        }
                    });
                    Handler handler = new Handler();
                    handler.postDelayed(genProgress(button,p),50);
                }

            }
        };
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
