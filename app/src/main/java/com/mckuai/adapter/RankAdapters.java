package com.mckuai.adapter;

import android.content.Context;
import android.media.session.MediaSession;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.until.MCMapManager;
import com.mckuai.widget.fabbutton.FabButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zzz on 2015/7/9.
 */
public class RankAdapters extends RecyclerView.Adapter<RankAdapters.ViewHolder>{
    private final String TAG = "mapadaptres";
    private ArrayList<Map> maps;
    private ImageLoader loader;
    private OnItemClickListener itemClickListener;
    private OnMapDownloadListener addListener;
    private ThinDownloadManager dlManager;
    private Context mContext;
    private boolean isPaihang = false;
    private HashMap<Integer, DownloadTask> downloadTask;   //下载任务，第一个参数是下载的token，第二个是包含了其按钮和地图的结构体
    private DownloadStatusListener statusListener;//下载进度监听


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

    public void setData(ArrayList<Map> maplist) {
        this.maps = maplist;

        notifyDataSetChanged();
    }

    public RankAdapters(Context context) {
        this.mContext = context;
        statusListener = new DownloadStatusListener() {
            @Override
            public void onDownloadComplete(int i) {

            }

            @Override
            public void onDownloadFailed(int i, int i1, String s) {

            }

            @Override
            public void onProgress(int downToken, long l, int progress) {
                DownloadTask task = downloadTask.get(downToken);
                task.map.setDownloadProgress(progress);
                task.button.setProgress(progress);
                Log.e("",""+progress);
            }
        } ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
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
                if (null == dlManager) {
                    dlManager = new ThinDownloadManager(3);
                }
                if (null == downloadTask){
                    downloadTask = new HashMap<>();
                }

                final FabButton button = (FabButton) v.getTag();
                final Map map = (Map) button.getTag();
                String url = map.getSavePath();
                try {
                    url = url.substring(0, url.lastIndexOf("/") + 1) + URLEncoder.encode(url.substring(url.lastIndexOf("/") + 1, url.length()), "UTF-8");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                url = url.replaceAll("\\+","%20");
                String downloadDir = MCkuai.getInstance().getMapDownloadDir()+url.substring(url.lastIndexOf("/")+1, url.length());
                button.resetIcon();
                DownloadRequest request = new DownloadRequest(Uri.parse(url)).setDestinationURI(Uri.parse(downloadDir));
                request.setDownloadListener(statusListener);
                map.setTasktoken(dlManager.add(request));
                DownloadTask task = new DownloadTask(button,map) ;
                downloadTask.put(map.getTasktoken(),task);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map map = maps.get(position);
        if (null != map) {
            holder.itemView.setTag(position);
            if (null == loader) {
                loader = ImageLoader.getInstance();
            }
            if (null != map.getIcon() && map.getIcon().length() > 10) {
                loader.displayImage(map.getIcon(), holder.image);
            }
            holder.tv_name.setText(map.getViewName());
            String leixing = map.getResCategroyTwo().substring(map.getResCategroyTwo().indexOf("|") + 1, map.getResCategroyTwo().length());
            leixing = leixing.replace("|", " ");
            holder.tv_category.setText(leixing);
            holder.tv_size.setText(map.getResSize() + "KB");
            holder.tv_time.setText(map.getInsertTime());
            if (isPaihang == true) {
                holder.rk_tv.setVisibility(View.VISIBLE);
                holder.rk_tv.setText((position + 1) + "");
                if (position == 0) {
                    holder.rk_tv.setBackgroundResource(R.drawable.map_one);
                } else {
                    holder.rk_tv.setBackgroundResource(R.drawable.map_tow);
                }
            } else {
                holder.rk_tv.setVisibility(View.GONE);
            }
            if (map.isDownload()){
                holder.btn_download.setProgress(100);
            }
            holder.btn_download.setTag(map);
            holder.itemView.setTag(position);
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
        public TextView rk_tv;
        public FabButton btn_download;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            rk_tv = (TextView) itemView.findViewById(R.id.rk_tv);
            btn_download = (FabButton) itemView.findViewById(R.id.download_map);
        }
    }

    class DownloadTask implements Serializable{
        public FabButton button;
        public Map map;

        public DownloadTask(FabButton btn,Map map){
            this.button = btn;
            this.map = map;
        }
    }

    public void setpaihang(boolean isChanged) {
        isPaihang = isChanged;
        notifyDataSetChanged();
    }

}
