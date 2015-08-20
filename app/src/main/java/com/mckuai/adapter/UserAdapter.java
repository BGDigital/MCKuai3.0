package com.mckuai.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mckuai.bean.MCDynamic;
import com.mckuai.bean.MCUser;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.imc.UserCenter;
import com.mckuai.widget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.net.utils.BaseNCodec;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/8/19.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ArrayList<MCUser> users;
    private Context mContext;
    private ImageLoader mLoader;
    private DisplayImageOptions mOptions;

    public UserAdapter(Context context){
        this.mContext = context;
        init();
    }

    public void setData(ArrayList<MCUser> userlist){
        users = userlist;
        if (null != userlist){
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MCUser user = users.get(position);
        String head = (String) holder.headImg.getTag();
        if (null == head || !head.equals(user.getHeadImg()) ) {
            holder.name.setText(null == user.getNike() || user.getNike().isEmpty() ? user.getName() + "" : user.getNike());
            holder.level.setText("lv"+user.getLevel());
            holder.addr.setText(user.getAddr());
            mLoader.displayImage(user.getHeadImg(), holder.headImg, mOptions);
            holder.headImg.setProgress(user.getProcess());
            holder.headImg.setTag(user.getHeadImg());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserCenter.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(mContext.getString(R.string.user),user);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == users ? 0:users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView addr;
        private TextView level;
        private com.mckuai.widget.CircleImageView headImg;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_username);
            addr = (TextView) itemView.findViewById(R.id.tv_usercity);
            level = (TextView) itemView.findViewById(R.id.tv_userLevel);
            headImg = (CircleImageView) itemView.findViewById(R.id.civ_usercover);
        }
    }

    private void init(){
        if (null == mLoader){
            mLoader = ImageLoader.getInstance();
            mOptions = MCkuai.getInstance().getCircleOption();
        }
    }
}
