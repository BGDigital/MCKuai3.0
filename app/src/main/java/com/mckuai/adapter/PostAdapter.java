package com.mckuai.adapter;

import java.util.ArrayList;

import com.mckuai.bean.Post;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.mckuai.imc.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PostAdapter extends BaseAdapter
{
	private ArrayList<Post> mPostList = new ArrayList<Post>(10);
	private Context mContext;
	private LayoutInflater mInflater;
//	private boolean isRecommend = false;
	private ImageLoader mLoader;
//	private DisplayImageOptions normal;
//	private DisplayImageOptions circle;

	private static final String TAG = "PostAdapter";

	public PostAdapter(Context context)
	{
		this(context,null);
	}

	public PostAdapter(Context context, ArrayList<Post> post)
	{
		init(context);
		this.mPostList = post;
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
//		this.normal = MyApplication.getInstance().getNormalOptions();
//		this.circle = MyApplication.getInstance().getCircleOptions();
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
			return (null == mPostList ? 0 : mPostList.size());
	}

	@Override
	public Object getItem(int position)
	{
			return mPostList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (null != mPostList && -1 < position && position < mPostList.size())
		{
			Post_ViewHolder holder;
			Post post = mPostList.get(position);
			if (null == convertView)
			{
				convertView = mInflater.inflate(R.layout.item_post_normal, parent, false);
				holder = new Post_ViewHolder();
				holder.owner_bottom = (TextView) convertView.findViewById(R.id.tv_postOwner_bottom);
				holder.owner_bottom_cover = (ImageView) convertView.findViewById(R.id.civ_postOwner_bottom);
				holder.typeTop = (ImageView) convertView.findViewById(R.id.tv_typeTop);
				holder.typeEssence = (ImageView) convertView.findViewById(R.id.tv_typeEssence);
				holder.title = (TextView) convertView.findViewById(R.id.tv_postTitle);
				holder.replyCount = (TextView) convertView.findViewById(R.id.v_postReply);
				holder.replyTime = (TextView) convertView.findViewById(R.id.tv_postrepayTime);
				convertView.setTag(holder);
			} else
			{
				holder = (Post_ViewHolder) convertView.getTag();
			}
			holder.title.setText(post.getTalkTitle() + "");
			holder.replyCount.setText(post.getReplyNum() + "");
			holder.replyTime.setText(post.getLastReplyTime());
			if (post.isTop())
			{
				holder.typeTop.setVisibility(View.VISIBLE);
			} else
			{
				holder.typeTop.setVisibility(View.GONE);
			}
			if (post.isEssence())
			{
				holder.typeEssence.setVisibility(View.VISIBLE);
			} else
			{
				holder.typeEssence.setVisibility(View.GONE);
			}
			if (null != post.getUserName())
			{
				holder.owner_bottom.setText(post.getUserName() + "");
				if (null != post.getHeadImg() && 10 < post.getHeadImg().length())
				{
					mLoader.displayImage(post.getHeadImg(), holder.owner_bottom_cover);
				}
				// 设置用户头像和名字点击跳转到其个人中心
				holder.owner_bottom.setTag(R.id.key_USERID, post.getUserId());
				holder.owner_bottom_cover.setTag(R.id.key_USERID, post.getUserId());
//				holder.owner_bottom.setOnClickListener(this);
//				holder.owner_bottom_cover.setOnClickListener(this);
			} else
			{
				holder.owner_bottom.setText(post.getForumName());
				if (null != post.getIcon() && 10 < post.getIcon().length())
				{
					mLoader.displayImage(post.getIcon(), holder.owner_bottom_cover);
				}
			}
		}
		return convertView;
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


}
