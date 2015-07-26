package com.mckuai.adapter;

import java.util.ArrayList;

import com.mckuai.bean.MCUser;
import com.mckuai.bean.Post;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.UserCenter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.mckuai.imc.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserCentPostAdapter extends BaseAdapter implements View.OnClickListener
{
	private ArrayList<Post> mPostList = new ArrayList<Post>(10);
	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader mLoader;
	private DisplayImageOptions normal;
	private DisplayImageOptions circle;

	private static final String TAG = "UserCentPostAdapter";


	public UserCentPostAdapter(Context context, ArrayList<Post> data)
	{
		init(context);
		this.mPostList = data;
	}

	public void refresh()
	{
		this.notifyDataSetChanged();
	}

	protected void init(Context context)
	{
		this.mContext = context;
		this.mLoader = ImageLoader.getInstance();
		this.mInflater = (LayoutInflater) context.getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.normal = MCkuai.getInstance().getNormalOption();
		this.circle = MCkuai.getInstance().getCircleOption();
	}

	public void setData(ArrayList<Post> data)
	{
		if (null != data)
		{
			this.mPostList = data;
			notifyDataSetChanged();
		} else
		{
			notifyDataSetInvalidated();
		}
	}

	@Override
	public int getCount()
	{
		return null == mPostList ? 0:mPostList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return  null == mPostList ? null:mPostList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		return handleNormalPost(position, convertView, parent);
	}



	private View handleNormalPost(int position, View view, ViewGroup parent)
	{
		if (null != mPostList && -1 < position && position < mPostList.size())
		{
			Post_ViewHolder holder;
			Post post = mPostList.get(position);
			if (null == view)
			{
				view = mInflater.inflate(R.layout.item_post_normal, parent, false);
				holder = new Post_ViewHolder();
				holder.owner_bottom = (TextView) view.findViewById(R.id.tv_postOwner_bottom);
				holder.owner_bottom_cover = (ImageView) view.findViewById(R.id.civ_postOwner_bottom);
				holder.typeTop = (ImageView) view.findViewById(R.id.tv_typeTop);
				holder.typeEssence = (ImageView) view.findViewById(R.id.tv_typeEssence);
				holder.title = (TextView) view.findViewById(R.id.tv_postTitle);
				holder.replyCount = (TextView) view.findViewById(R.id.tv_postReply);
				holder.replyTime = (TextView) view.findViewById(R.id.tv_postrepayTime);
				view.setTag(holder);
			} else
			{
				holder = (Post_ViewHolder) view.getTag();
			}
			holder.title.setText(post.getTalkTitle() + "");
			holder.replyCount.setText(post.getReplyNum() + "");
			holder.replyTime.setText(post.getLastReplyTime());

			if (null != post.getUserName())
			{
				holder.owner_bottom.setText(post.getUserName() + "");
				if (null != post.getHeadImg() && 10 < post.getHeadImg().length())
				{
					mLoader.displayImage(post.getHeadImg(), holder.owner_bottom_cover, circle);
				}
				// 设置用户头像和名字点击跳转到其个人中心
				holder.owner_bottom.setTag(R.id.key_USERID, post.getUserId());
				holder.owner_bottom_cover.setTag(R.id.key_USERID, post.getUserId());
				holder.owner_bottom.setOnClickListener(this);
				holder.owner_bottom_cover.setOnClickListener(this);
			} else
			{
				holder.owner_bottom.setText(post.getForumName());
				if (null != post.getIcon() && 10 < post.getIcon().length())
				{
					mLoader.displayImage(post.getIcon(), holder.owner_bottom_cover, circle);
				}
			}
		}
		return view;
	}


	private View handlePostHeader(View view, ViewGroup parent)
	{
		if (null == view)
		{
			view = mInflater.inflate(R.layout.item_post_header, parent, false);
		}
		return view;
	}


	class Post_ViewHolder
	{
		ImageView cover;
		ImageView owner_bottom_cover;
		TextView owner_bottom;
		TextView title;
		ImageView typeTop;
		ImageView typeEssence;
		TextView replyTime;
		TextView replyCount;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		int userId = 0, forumId = 0;
		try
		{
			userId = (Integer) v.getTag(R.id.key_USERID);
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		if (0 != userId)
		{
			Intent intent = new Intent(mContext, UserCenter.class);
			MCUser user = new MCUser();
			user.setId(userId);
			Bundle bundle = new Bundle();
			bundle.putSerializable(mContext.getString(R.string.user), user);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		} else
		{
			Toast.makeText(mContext, "版块ID" + forumId, Toast.LENGTH_SHORT).show();
		}
	}

}
