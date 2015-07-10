package com.mckuai.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
import com.mckuai.until.MCDTListener;
import com.mckuai.until.MCMapManager;
import com.mckuai.widget.MasterLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.aigestudio.downloader.bizs.DLManager;

/**
 * Created by Administrator on 2015/7/8.
 */
public class mapadapters extends RecyclerView.Adapter<mapadapters.ViewHolder> {

    private final String TAG = "mapadaptres";
    private ArrayList<Map> maps;
    private ImageLoader loader;
    private OnItemClickListener itemClickListener;
    private OnMapDownloadListener addListener;
    private MCMapManager mapManager;
    private DLManager manager;
    private Context mContext;


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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        final MasterLayout btn = holder.MasterLayout01;
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
        Map map = (Map) view.getTag();
        btn.setOnClickListener(new ClickLinstener(map, btn));
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
            holder.MasterLayout01.setTag(map);
        }
    }

    @Override
    public int getItemCount() {
        return null == maps ? 0 : maps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tv_name;
        TextView tv_category;
        TextView tv_time;
        TextView tv_size;
        MasterLayout MasterLayout01;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            MasterLayout01 = (MasterLayout) itemView.findViewById(R.id.MasterLayout01);
        }
    }

    class ClickLinstener implements View.OnClickListener {
        private Map map;
        private MasterLayout MasterLayout01;

        public ClickLinstener(Map map, MasterLayout MasterLayout01) {
            this.map = map;
            this.MasterLayout01 = MasterLayout01;
        }

        @Override
        public void onClick(View v) {
            final MasterLayout btn = MasterLayout01;
            btn.animation();
            Map clickedMap = (Map) v.getTag();
            if (mapManager == null) {
                mapManager = MCkuai.getInstance().getMapManager();
            }

            if (null == clickedMap) {
//                Toast.makeText(mContext, "点击出错", LENGTH_SHORT).show();
//                return;
            }
            switch (btn.getFlg_frmwrk_mode()) {
                case 1:
                    if (null == manager) {
                        manager = DLManager.getInstance(mContext);
                    }
                    String downloadDir = MCkuai.getInstance().getMapDownloadDir();
                    String url = clickedMap.getSavePath();
//                    url=URLEncoder.encode(url);
                    Log.e(TAG, "downloaddir:" + downloadDir);
                    Log.e(TAG, "url:" + url);

                    manager.dlStart(url, downloadDir, new McDLTaskListener(clickedMap, btn) {


                    });
                    break;
                //Download stopped
                case 2:

                    break;
                //Download complete
                case 3:

                    break;
            }
        }
    }

    class McDLTaskListener extends MCDTListener {
        private Map clickedMap;
        private MasterLayout MasterLayout01;

        public McDLTaskListener(Map clickedMap, MasterLayout MasterLayout01) {
            super();
            this.clickedMap = clickedMap;
            this.MasterLayout01 = MasterLayout01;

        }

//        final MasterLayout btn = MasterLayout01;

        @Override
        public void onStart(String fileName, String url) {
            super.onStart(fileName, url);
            MCkuai.getInstance().addDownloadTask(clickedMap.getResId(), this);
            Log.e("111111", "onStart");
            //Toast.makeText(mContext, "Start", LENGTH_SHORT).show();
        }

        @Override
        public boolean onConnect(int type, String msg) {

            //Toast.makeText(mContext, "Connect", LENGTH_SHORT).show();
            Log.e("111111", "onConnect");
            return super.onConnect(type, msg);
        }

        @Override
        public void onProgress(int progress) {
            super.onProgress(progress);
            MasterLayout01.cusview.setupprogress(progress);
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
}
