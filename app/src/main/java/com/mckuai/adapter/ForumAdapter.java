/**
 * 
 */
package com.mckuai.adapter;

import java.util.ArrayList;

import com.mckuai.imc.R;
import com.mckuai.bean.ForumInfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author kyly
 * 
 */
public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder>
{
	private ArrayList<ForumInfo> forumInfos;
	// private String TAG = "ForumAdapter";
	private static OnItemClickListener mListener;
	private static int checkedForumId;
	private Context mContext;

	public interface OnItemClickListener
	{
		public void onItemClick(ForumInfo forumInfo);
	}

	public ForumAdapter()
	{
		// TODO Auto-generated constructor stub
//		this.forumInfos = forumInfos;
//		checkedForumId = forumInfos.get(0).getId();
	}

	public void setOnItemClickListener(OnItemClickListener l)
	{
		if (null != l)
		{
			this.mListener = l;
		}
	}

	public void setData(ArrayList<ForumInfo> forumInfos)
	{
		if (null != forumInfos)
		{
			this.forumInfos = forumInfos;
			checkedForumId = forumInfos.get(0).getId();
			notifyDataSetChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android
	 * .view.ViewGroup, int)
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1)
	{
		// TODO Auto-generated method stub
		if (null == mContext)
		{
			this.mContext = arg0.getContext();
		}
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_forum, null, false);
		ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	/**
	 * @author kyly
	 * 
	 */
	public static class ViewHolder extends RecyclerView.ViewHolder
	{
		private RadioButton mbackground;
		private TextView mtop;

		/**
		 * @param itemView
		 */
		public ViewHolder(View itemView)
		{
			super(itemView);
			// TODO Auto-generated constructor stub
			mbackground = (RadioButton) itemView.findViewById(R.id.rb_background);
			mtop = (TextView) itemView.findViewById(R.id.tv_top);
			mbackground.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					ForumInfo forum = (ForumInfo) v.getTag();
					if (null != forum)
					{
						checkedForumId = forum.getId();
						if (null != mListener)
						{
							mListener.onItemClick(forum);
						}
					}
				}
			});
		}
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
	 */
	@Override
	public int getItemCount()
	{
		// TODO Auto-generated method stub
		return null == forumInfos ? 0 : forumInfos.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android
	 * .support.v7.widget.RecyclerView.ViewHolder, int)
	 */
	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1)
	{
		// TODO Auto-generated method stub
		ForumInfo forum = forumInfos.get(arg1 % forumInfos.size());
		arg0.mbackground.setTag(forum);
		if (checkedForumId == forum.getId())
		{
			arg0.mbackground.setChecked(true);
			arg0.mtop.setTextColor(mContext.getResources().getColor(R.color.font_white));
		} else
		{
			arg0.mbackground.setChecked(false);
			arg0.mtop.setTextColor(mContext.getResources().getColor(R.color.forum_unselected));
		}

		String name = forum.getName();
		int length = name.length();
		if (1 < length)
		{
			int top = length / 2 + length % 2;
			name = name.substring(0,top) + "\n"+name.substring(top,length);
			arg0.mtop.setText(name);
		} else
		{
			arg0.mtop.setText(name);
		}
	}

}
