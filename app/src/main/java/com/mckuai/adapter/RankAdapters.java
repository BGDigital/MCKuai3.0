package com.mckuai.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.until.MCDTListener;
import com.mckuai.until.MCMapManager;
import com.mckuai.widget.fabbutton.FabButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

/**
 * Created by Zzz on 2015/7/9.
 */
public class RankAdapters extends RecyclerView.Adapter<RankAdapters.ViewHolder> {
    private final String TAG = "mapadaptres";
    private ArrayList<Map> maps;
    private ImageLoader loader;
    private OnItemClickListener itemClickListener;
    private OnMapDownloadListener addListener;
    private MCMapManager mapManager;
    private DLManager manager;
    private Context mContext;
    private boolean isPaihang = false;
    private int currentProgress = 0;

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

    public RankAdapters(Context context){
        this.mContext = context;
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
                if (null == manager) {
                    manager = DLManager.getInstance(mContext);
                }
                final FabButton button = (FabButton) v.getTag();
                Map map = (Map) button.getTag();
                String downloadDir = MCkuai.getInstance().getMapDownloadDir();
                String url = map.getSavePath();
//                    url = URLEncoder.encode(url);
                Log.e(TAG, "downloaddir:" + downloadDir);
                Log.e(TAG, "url:" + url);
                button.resetIcon();
                manager.dlStart(url,downloadDir,new DLTaskListener(){
                    @Override
                    public boolean onConnect(int type, String msg) {
                        Log.e("","已连接");
                        return super.onConnect(type, msg);
                    }

                    @Override
                    public void onStart(String fileName, String url) {
                        Log.e("","开始下载");
                        super.onStart(fileName, url);
                    }

                    @Override
                    public void onProgress(int progress) {
                        button.setProgress(progress);
                        Log.e("", "当前进度：" + progress);
                    }

                    @Override
                    public void onFinish(File file) {
                        Log.e("","开始完成");
                        super.onFinish(file);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("","出错了");
                        super.onError(error);
                    }
                });
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
            holder.tv_category.setText(leixing);
            holder.tv_size.setText(map.getResSize());
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
            /*btn_progress = (DonutProgress) itemView.findViewById(R.id.download_map);
            btn_operator = (ImageButton) itemView.findViewById(R.id.btn_operat);
            btn_operator.setTag(btn_progress);*/
        }
    }

    class ClickLinstener implements View.OnClickListener {
        private Map map;
        private FabButton btn_download_map;

        public ClickLinstener(Map map, FabButton btn_download_map) {
            this.map = map;
            this.btn_download_map = btn_download_map;
        }

        @Override
        public void onClick(View v) {
            final FabButton btn = btn_download_map;
//            btn.animation();
            //Map clickedMap = (Map) v.getTag();
            if (mapManager == null) {
                mapManager = MCkuai.getInstance().getMapManager();
            }

            if (null == map) {
//                Toast.makeText(mContext, "点击出错", LENGTH_SHORT).show();
//                return;
            }

            if (null == manager) {
                manager = DLManager.getInstance(mContext);
            }
            String downloadDir = MCkuai.getInstance().getMapDownloadDir();
            String url = map.getSavePath();
//                    url = URLEncoder.encode(url);
            Log.e(TAG, "downloaddir:" + downloadDir);
            Log.e(TAG, "url:" + url);

            manager.dlStart(url, downloadDir, new McDLTaskListener(map, btn) {

            });

        }
    }

    class McDLTaskListener extends MCDTListener {
        private Map clickedMap;
        private FabButton btn_download_map;

        public McDLTaskListener(Map clickedMap, FabButton btn_download_map) {
            //super();
            this.clickedMap = clickedMap;
            this.btn_download_map = btn_download_map;

        }

//        final MasterLayout btn = MasterLayout01;

        @Override
        public void onStart(String fileName, String url) {
            //super.onStart(fileName, url);
            MCkuai.getInstance().addDownloadTask(clickedMap.getResId(), this);
            Log.e("111111", "onStart");
            //Toast.makeText(mContext, "Start", LENGTH_SHORT).show();
        }

        @Override
        public boolean onConnect(int type, String msg) {

            //Toast.makeText(mContext, "Connect", LENGTH_SHORT).show();
            Log.e("111111", "onConnect");
            return true;
        }

        @Override
        public void onProgress(int progress) {
            //super.onProgress(progress);
            btn_download_map.setProgress(progress);
            btn_download_map.resetIcon();
            currentProgress = 0;
            btn_download_map.showProgress(true);
            btn_download_map.setProgress(currentProgress);
            Log.e("10101", "progress");
        }

        @Override
        public void onFinish(File file) {
            Log.e("111", "" + file.getPath() + file.getName());
            super.onFinish(file);
            mapManager.addDownloadMap(clickedMap);
            mapManager.closeDB();
            MCkuai.getInstance().deleteDownloadTask(clickedMap.getResId());
        }

        @Override
        public void onError(String error) {
            Log.e("22222222222222", "onError");
            super.onError(error);
            //Toast.makeText(mContext, "Error", LENGTH_SHORT).show();

        }
    }

    public void setpaihang(boolean isChanged) {
        isPaihang = isChanged;
        notifyDataSetChanged();
    }
}
