package com.mckuai.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.ArticItem;
import com.mckuai.bean.GameServerInfo;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kyly on 2015/6/25.
 */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> {

    private ArrayList<GameServerInfo> serverInfos;
    private ImageLoader loader;


    public void setData(ArrayList<GameServerInfo> serverlist) {
        this.serverInfos = serverlist;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server,parent,false);

        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameServerInfo serverInfo = (GameServerInfo) v.getTag();
                //
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
            }
            else {
                holder.state.setText("离线");
            }

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

        public ViewHolder(View itemView){
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.iv_serverCover);
            name = (TextView)itemView.findViewById(R.id.tv_serverName);
            version = (TextView)itemView.findViewById(R.id.tv_serverVersion);
            type = (TextView)itemView.findViewById(R.id.tv_serverType);
            owner = (TextView)itemView.findViewById(R.id.tv_serverOwner);
            state = (TextView)itemView.findViewById(R.id.tv_serverState);
            userCount = (TextView)itemView.findViewById(R.id.tv_serverPlayerCount);
        }
    }

}
