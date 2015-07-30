package com.mckuai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateDifferentViewTypeAdapter;
import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/7/28.
 */
public class testAdapter extends UltimateDifferentViewTypeAdapter{
    private ArrayList<Map> maps;
    private ImageLoader loader;
    private OnItemClickListener itemClickListener;
    private OnMapDownloadListener addListener;
    private Context mContext;
    private boolean isPaihang = false;
    private DisplayImageOptions options;

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


    public void setData(final ArrayList<Map> maplist) {
        this.maps = maplist;
        notifyDataSetChanged();
    }

    public testAdapter(Context context){
        this.mContext = context;
        loader = ImageLoader.getInstance();
        options = MCkuai.getInstance().getNormalOption();
    }

    @Override
    public Enum getEnumFromPosition(int i) {
        return null;
    }

    @Override
    public Enum getEnumFromOrdinal(int i) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int i) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }
}
