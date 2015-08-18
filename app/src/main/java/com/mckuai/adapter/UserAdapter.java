package com.mckuai.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mckuai.bean.MCUser;
import com.mckuai.imc.MCkuai;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.mckuai.imc.R;

public class UserAdapter extends BaseAdapter implements View.OnClickListener
{

	private LayoutInflater inflater;
	private ArrayList<MCUser> musers = new ArrayList<MCUser>();
	private Context mContext;
	private ImageLoader mLoader;
	private DisplayImageOptions mOptions;

	public UserAdapter(Context context, ArrayList<MCUser> users)
	{
		musers = users;
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		mLoader = ImageLoader.getInstance();
		mOptions = MCkuai.getInstance().getCircleOption();
	}

	public void setData(ArrayList<MCUser> user)
	{
		if (null != user && 0 != user.size())
		{
			this.musers = user;
			notifyDataSetChanged();
		} else
		{
			notifyDataSetInvalidated();
		}
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (null != musers)
		{
			return musers.size();
		} else
		{
			return 0;
		}

	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return musers.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		MCUser user = (MCUser) getItem(position);
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.item_user, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.civ_name);
			viewHolder.addr = (TextView) convertView.findViewById(R.id.civ_city);
			viewHolder.level = (TextView) convertView.findViewById(R.id.tv_userLevel);
			viewHolder.headImg = (com.mckuai.widget.CircleImageView) convertView.findViewById(R.id.civ_user);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(user.getNike() + "");
		viewHolder.addr.setText(user.getAddr() + "");
		viewHolder.level.setText("lv"+user.getLevel());
		if (null != user.getHeadImg() && 10 < user.getHeadImg().length())
		{
			mLoader.displayImage(user.getHeadImg(), viewHolder.headImg, mOptions);
			viewHolder.headImg.setProgress(user.getProcess());
		}
		return convertView;
	}

	class ViewHolder
	{
		public TextView name;
		public TextView addr;
		public TextView level;
		public com.mckuai.widget.CircleImageView headImg;

	}

	@Override
	public void onClick(View v)
	{

		// TODO Auto-generated method stub
		Toast.makeText(mContext, "通往聊天", Toast.LENGTH_SHORT).show();
	}

}
