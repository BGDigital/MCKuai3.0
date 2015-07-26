package com.mckuai.adapter;

import java.util.ArrayList;

import com.mckuai.bean.MCMessage;
import com.mckuai.imc.MCkuai;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.mckuai.imc.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MCMessageAdapter extends BaseAdapter
{
	private ArrayList<MCMessage> mMessages;
	private Context mContext;
	private LayoutInflater mInflater;
	private DisplayImageOptions mCircle;
	private ImageLoader mLoader;

	public MCMessageAdapter(Context context)
	{
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MCMessageAdapter(Context context, ArrayList<MCMessage> messages)
	{
		this.mMessages = messages;
		init(context);
	}

	public void setData(ArrayList<MCMessage> messages)
	{
		if (null != messages && 0 < messages.size())
		{
			this.mMessages = messages;
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
		return (null == mMessages ? 0 : mMessages.size());
	}

	@Override
	public int getViewTypeCount()
	{
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getItemViewType(int position)
	{
		// TODO Auto-generated method stub
		return (null == mMessages ? 0 : mMessages.get(position).getTypeEx());
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return (null == mMessages ? null : mMessages.get(position));
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
		MCMessage msg = mMessages.get(position);
		switch (msg.getTypeEx())
		{
		case MCMessage.TYPE_AT:
			return handleAtMsg(msg, convertView, parent);
		case MCMessage.TYPE_REPLY:
			return handleReplyMsg(msg, convertView, parent);
		case MCMessage.TYPE_SYSTEM:
			return handleSysMsg(msg, convertView, parent);
		default:
			return null;
		}
	}

	private View handleSysMsg(MCMessage msg, View convertView, ViewGroup parent)
	{
		Sys_ViewHolder holder;
		if (null == convertView)
		{
			convertView = mInflater.inflate(R.layout.item_message_system, parent, false);
			holder = new Sys_ViewHolder();
			holder.tv_context = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_replyTime);
			convertView.setTag(holder);
		} else
		{
			holder = (Sys_ViewHolder) convertView.getTag();
		}
		holder.tv_context.setText(msg.getShowText() + "");
		holder.tv_time.setText(msg.getInsertTime() + "");
		return convertView;
	}

	private View handleAtMsg(MCMessage msg, View convertView, ViewGroup parent)
	{
		At_ViewHolder holder;
		if (null == convertView)
		{
			convertView = mInflater.inflate(R.layout.item_message_at, parent, false);
			holder = new At_ViewHolder();
			holder.tv_context = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_replyTime);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_userName);
			holder.iv_user = (ImageView) convertView.findViewById(R.id.iv_userCover);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_postTitle);
			convertView.setTag(holder);
		} else
		{
			holder = (At_ViewHolder) convertView.getTag();
		}
		holder.tv_context.setText(msg.getCont() + "");
		holder.tv_time.setText(msg.getInsertTime() + "");
		holder.tv_name.setText(msg.getUserName().toString() + "");
		holder.tv_title.setText(msg.getTalkTitle() + "");
		if (null != msg.getHeadImg() && 10 < msg.getHeadImg().length())
		{
			mLoader.displayImage(msg.getHeadImg(), holder.iv_user, mCircle);
		}
		return convertView;
	}

	private View handleReplyMsg(MCMessage msg, View convertView, ViewGroup parent)
	{
		Reply_ViewHolder holder;
		if (null == convertView)
		{
			convertView = mInflater.inflate(R.layout.item_message_reply, parent, false);
			holder = new Reply_ViewHolder();
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_postTitle);
			holder.tv_context = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_replyTime);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_userName);
			holder.iv_user = (ImageView) convertView.findViewById(R.id.iv_userCover);
			convertView.setTag(holder);
		} else
		{
			holder = (Reply_ViewHolder) convertView.getTag();
		}
		holder.tv_context.setText(msg.getCont() + "");
		holder.tv_time.setText(msg.getInsertTime() + "");
		holder.tv_name.setText(msg.getUserName() + "");
		holder.tv_title.setText(msg.getTalkTitle() + "");
		if (null != msg.getHeadImg() && 10 < msg.getHeadImg().length())
		{
			mLoader.displayImage(msg.getHeadImg(), holder.iv_user, mCircle);
		}
		return convertView;
	}

	class Sys_ViewHolder
	{
		TextView tv_time;
		TextView tv_context;
	}

	class At_ViewHolder
	{
		TextView tv_name;
		TextView tv_time;
		TextView tv_context;
		TextView tv_title;
		ImageView iv_user;
	}

	class Reply_ViewHolder
	{
		TextView tv_name;
		TextView tv_time;
		TextView tv_context;
		TextView tv_title;
		ImageView iv_user;
	}

}
