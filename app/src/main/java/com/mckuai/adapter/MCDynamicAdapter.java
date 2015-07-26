package com.mckuai.adapter;

import java.util.ArrayList;

import com.mckuai.bean.MCDynamic;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MCDynamicAdapter extends BaseAdapter
{
	private ArrayList<MCDynamic> mDynamics;
	private Context mContext;
	private LayoutInflater mInflater;
	private DisplayImageOptions mCircle;
	private ImageLoader mLoader;

	public MCDynamicAdapter(Context context)
	{
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MCDynamicAdapter(Context context, ArrayList<MCDynamic> dynamics)
	{
		this.mDynamics = dynamics;
		init(context);
	}

	public void setData(ArrayList<MCDynamic> dynamics)
	{
		if (null != dynamics && 0 < dynamics.size())
		{
			this.mDynamics = dynamics;
			this.notifyDataSetChanged();
		} else
		{
			this.notifyDataSetInvalidated();
		}
	}

	private void init(Context context)
	{
		mInflater = LayoutInflater.from(context);
		mCircle = MCkuai.getInstance().getCircleOption();
		this.mLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return (null == mDynamics ? 0 : mDynamics.size());
	}

	@Override
	public int getViewTypeCount()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getItemViewType(int position)
	{
		// TODO Auto-generated method stub
		return (null == mDynamics ? 0 : mDynamics.get(position).getTypeEx());
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return (null == mDynamics ? null : mDynamics.get(position));
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
		MCDynamic dynamic = mDynamics.get(position);
		switch (dynamic.getTypeEx())
		{
		case MCDynamic.TYPE_CREATE:
			return handleCreate(dynamic, convertView, parent);
		case MCDynamic.TYPE_REPLY:
			return handleReply(dynamic, convertView, parent);
		default:
			return null;
		}
	}

	private View handleCreate(MCDynamic dynamic, View convertView, ViewGroup parent)
	{
		Create_ViewHolder holder;
		if (null == convertView)
		{
			convertView = mInflater.inflate(R.layout.item_dynamic_create, parent, false);
			holder = new Create_ViewHolder();
			holder.tv_context = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_replyTime);
			convertView.setTag(holder);
		} else
		{
			holder = (Create_ViewHolder) convertView.getTag();
		}
		holder.tv_context.setText(dynamic.getCont() + "");
		holder.tv_time.setText(dynamic.getInsertTime() + "");
		return convertView;
	}

	private View handleReply(MCDynamic dynamic, View convertView, ViewGroup parent)
	{
		Reply_ViewHolder holder;
		if (null == convertView)
		{
			convertView = mInflater.inflate(R.layout.item_dynamic_reply, parent, false);
			holder = new Reply_ViewHolder();
			holder.tv_context = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_replyTime);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_postTitle);
			convertView.setTag(holder);
		} else
		{
			holder = (Reply_ViewHolder) convertView.getTag();
		}
		holder.tv_context.setText(dynamic.getCont() + "");
		holder.tv_time.setText(dynamic.getInsertTime() + "");
		holder.tv_title.setText(dynamic.getTalkTitle() + "");
		return convertView;
	}

	class Reply_ViewHolder
	{
		TextView tv_time;
		TextView tv_title;
		TextView tv_context;
	}

	class Create_ViewHolder
	{
		TextView tv_time;
		TextView tv_context;
	}

}
