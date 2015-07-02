/**
 * 
 */
package com.mckuai.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import com.mckuai.imc.MCkuai;
import com.google.gson.Gson;
import com.mckuai.imc.R;
import com.mckuai.bean.ForumBean;
import com.mckuai.bean.ForumInfo;

/**
 * @author kyly
 *
 */
public class ForumAdapter_Publish extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<ForumInfo> mFroumInfos;
	private android.widget.CompoundButton.OnCheckedChangeListener mListener;
	private boolean isFirst = true;
	private String forums;
	
	/**
	 * 
	 */
	public ForumAdapter_Publish(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setOnCheckedChangeListener(android.widget.CompoundButton.OnCheckedChangeListener l){
		this.mListener = l;
	}

	
	public void setData(ArrayList<ForumInfo> forums)
	{
		if (null != forums && !forums.isEmpty()) {
			this.mFroumInfos = forums;
			notifyDataSetChanged();
		}
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null == mFroumInfos ? 0 : mFroumInfos.size();
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public ForumInfo getItem(int position) {
		// TODO Auto-generated method stub
		return null == mFroumInfos ? null : mFroumInfos.get(position);
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_publish_froum, parent, false);
		}
		convertView.setTag(getItem(position));
		RadioButton textView = (RadioButton) convertView;
		textView.setText(getItem(position).getName());
		
		if (null != mListener) {
			textView.setOnCheckedChangeListener(mListener);
		}
		return convertView;
	}
}
