package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.GameServerInfo;
import com.mckuai.imc.R;
import com.mckuai.until.ServerEditer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/6/25.
 */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> {

    private final String TAG = "ServerAdapter";

    private ArrayList<GameServerInfo> serverInfos;
    private ImageLoader loader;
    private OnItemClickListener itemClickListener;
    private OnServerAddListener addListener;

    public interface OnItemClickListener{
        public void onItemClick(GameServerInfo gameServerInfo);
    }

    public interface OnServerAddListener{
        public void afterServerInfoAdded();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    public void SetOnServerAddListener(OnServerAddListener listener){
        this.addListener = listener;
    }


    public void setData(ArrayList<GameServerInfo> serverlist) {
        this.serverInfos = serverlist;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameServerInfo serverInfo = (GameServerInfo) v.getTag();
                if (null != itemClickListener){
                    itemClickListener.onItemClick(serverInfo);
                }
                else {
                    Log.e(TAG,"OnItemClickListener not set!");
                }
            }
        });
        holder.addToGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameServerInfo infos = (GameServerInfo) v.getTag();
                if (null != infos) {
                    ServerEditer editer = new ServerEditer();
                    editer.addServer(infos);
                    editer.save();
                    if (null != addListener){
                        addListener.afterServerInfoAdded();
                    }
                }
                else {
                    Log.w(TAG,"OnServerAddListener:no data get!");
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GameServerInfo serverInfo = serverInfos.get(position);
        if (null !=serverInfo){
            holder.itemView.setTag(serverInfo);
            if (null == loader){
                loader = ImageLoader.getInstance();
            }
            if (null != serverInfo.getIcon() && serverInfo.getIcon().length() > 10){
                loader.displayImage(serverInfo.getIcon(),holder.icon);
            }

            holder.name.setText(serverInfo.getViewName());
            holder.version.setText(serverInfo.getResVersion()+"");
            holder.type.setText(serverInfo.getServerTag());
            holder.userCount.setText("人数："+serverInfo.getOnlineNum() +"/"+serverInfo.getPeopleNum());
            holder.owner.setText("服主：" + serverInfo.getUserName());
            if (serverInfo.getIsOnline()){
                holder.state.setText("在线");
                holder.state.setBackgroundResource(R.color.background_green);
            }
            else {
                holder.state.setText("离线");
                holder.state.setBackgroundResource(R.color.background_litegray);
            }

            holder.addToGame.setTag(serverInfo);

        }
    }

    @Override
    public int getItemCount() {
        return null == serverInfos ? 0:serverInfos.size();
    }

    public static  class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView icon;
        TextView name;
        TextView version;
        TextView type;
        TextView owner;
        TextView state;
        TextView userCount;
        Button addToGame;

        public ViewHolder(View itemView){
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.iv_serverCover);
            name = (TextView)itemView.findViewById(R.id.tv_serverName);
            version = (TextView)itemView.findViewById(R.id.tv_serverVersion);
            type = (TextView)itemView.findViewById(R.id.tv_serverType);
            owner = (TextView)itemView.findViewById(R.id.tv_serverOwner);
            state = (TextView)itemView.findViewById(R.id.tv_serverState);
            userCount = (TextView)itemView.findViewById(R.id.tv_serverPlayerCount);
            addToGame = (Button)itemView.findViewById(R.id.btn_addServer) ;
        }
    }

}
