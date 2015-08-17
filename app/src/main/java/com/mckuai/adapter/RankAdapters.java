package com.mckuai.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.MCMapManager;
import com.mckuai.widget.fabbutton.FabButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zzz on 2015/7/9.
 */
public class RankAdapters extends RecyclerView.Adapter<RankAdapters.ViewHolder> {
    private final String TAG = "mapadaptres";
    private ArrayList<Map> maps;
    private ImageLoader loader;
    private OnItemClickListener itemClickListener;
    private OnMapDownloadListener addListener;
    private Context mContext;
    private boolean isPaihang = false;
    private DisplayImageOptions options = MCkuai.getInstance().getNormalOption();


    public interface OnItemClickListener {
        public void onItemClick(Map mapinfo);
    }

    public interface OnMapDownloadListener {
        public void afterMapDownload();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnMapDownloadListener(OnMapDownloadListener listener) {
        this.addListener = listener;
    }

    public ImageLoader getLoader(){
        if (null == loader){
            loader = ImageLoader.getInstance();
        }
        return loader;
    }


    public void setData(final ArrayList<Map> maplist) {
        this.maps = maplist;
        notifyDataSetChanged();
    }

    public RankAdapters(Context context) {
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positions = (int) v.getTag();
                if (null != itemClickListener) {
                    itemClickListener.onItemClick(maps.get(positions));
                } else {
                    Log.e(TAG, "OnItemClickListener not set!");
                }
            }
        });
        holder.btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final FabButton button = (FabButton) v.getTag();
                int position = (int) button.getTag();
                final Map map = maps.get(position);
                switch (map.getDownloadProgress()) {
                    case 0:
                        MobclickAgent.onEvent(mContext, "downloadMap");
                        Intent intent = new Intent();
                        intent.setAction("com.mckuai.downloadservice");
                        intent.setPackage(mContext.getPackageName());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("MAP", map);
                        intent.putExtras(bundle);
                        mContext.startService(intent);
                        break;
                    case 100:
                        MobclickAgent.onEvent(mContext, "startGame_map");
                        String filename = MCkuai.getInstance().getMapDownloadDir() + map.getFileName();
                        MCMapManager mapManager = MCkuai.getInstance().getMapManager();
                        GameUntil.startGame(mContext);
                        break;
                    default:
                        break;
                }
            }
        });
        holder.btn_download_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "startGame_map");
                int position = (int) v.getTag();
                final Map map = maps.get(position);
                String filename = MCkuai.getInstance().getMapDownloadDir() + map.getFileName();
                MCMapManager mapManager = MCkuai.getInstance().getMapManager();
                if (!mapManager.importMap(filename)) {
                    Toast.makeText(mContext, "导入失败", Toast.LENGTH_SHORT).show();
                } else {
                    GameUntil.startGame(mContext);
                }
            }
        });
//        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPaihang) {
            switch (maps.get(position).getDownloadProgress()) {
                case 0:
                    return 0;
                case 100:
                    return 2;
                default:
                    return 1;
            }
        } else {
            switch (maps.get(position).getDownloadProgress()) {
                case 0:
                    return 3;
                case 100:
                    return 5;
                default:
                    return 4;
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map map = maps.get(position);
        if (null != map) {
            String oldIcon = (String) holder.image.getTag();
            if (null != oldIcon && oldIcon.equals(map.getIcon())) {
                //只是刷新进度
                holder.btn_download.setProgress(map.getDownloadProgress());
                return;
            }
         /*   if (2 == getItemViewType(position)){
                holder.setIsRecyclable(false);
            }*/
            if (null == loader) {
                loader = ImageLoader.getInstance();
            }
            if (null != map.getIcon() && map.getIcon().length() > 10) {
                loader.displayImage(map.getIcon(), holder.image, options);
                holder.image.setTag(map.getIcon());
            }
            holder.tv_name.setText(map.getViewName());
            String leixing = map.getResCategroyTwo().substring(map.getResCategroyTwo().indexOf("|") + 1, map.getResCategroyTwo().length());
            leixing = leixing.replace("|", " ");
            holder.tv_category.setText(leixing);
            holder.tv_size.setText(map.getResSize() + "MB");
            String time = map.getInsertTime().substring(map.getInsertTime().indexOf("-") + 1, map.getInsertTime().indexOf(" "));
            time = time.replace("-", "月");
            holder.tv_time.setText("更新时间：" + time + "日");

            if (map.isDownload()) {
                holder.btn_download.setProgress(100);
                holder.btn_download_image.setVisibility(View.VISIBLE);
                holder.btn_download.setVisibility(View.INVISIBLE);
            } else {
                holder.btn_download_image.setVisibility(View.GONE);
                holder.btn_download.setVisibility(View.VISIBLE);
                holder.btn_download.setProgress(map.getDownloadProgress());
            }
            holder.btn_download.setTag(position);
            holder.itemView.setTag(position);
            holder.btn_download_image.setTag(position);
        }

    }

    @Override
    public int getItemCount() {
        return null == maps ? 0 : maps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView tv_name;
        public TextView tv_category;
        public TextView tv_time;
        public TextView tv_size;
//        public TextView rk_tv;
        public FabButton btn_download;
        public ImageButton btn_download_image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
//            rk_tv = (TextView) itemView.findViewById(R.id.rk_tv);
            btn_download = (FabButton) itemView.findViewById(R.id.download_map);
            btn_download_image = (ImageButton) itemView.findViewById(R.id.map_download_image);
        }
    }

    class DownloadTask implements Serializable {
        public FabButton button;
        public Map map;

        public DownloadTask(FabButton btn, Map map) {
            this.button = btn;
            this.map = map;
        }
    }

    public void setpaihang(boolean isChanged) {
        isPaihang = isChanged;
        notifyDataSetEmpty();
    }

    public void notifyDataSetEmpty(){
        if (null != maps && !maps.isEmpty()){
            notifyItemRangeRemoved(0, maps.size());
        }
    }

    public boolean getIsPaihang() {
        return isPaihang;
    }

}
