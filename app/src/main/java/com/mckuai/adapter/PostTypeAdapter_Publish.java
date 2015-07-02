/**
 * 
 */
package com.mckuai.adapter;

import java.util.ArrayList;

import com.mckuai.bean.PostType;
import com.mckuai.imc.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author kyly
 *
 */
public class PostTypeAdapter_Publish extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<PostType> mTypes;
	private OnCheckedChangeListener mListener;
	private boolean isFirst = true;
	
	/**
	 * 
	 */
	public PostTypeAdapter_Publish(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setOnCheckedChangeListiner(OnCheckedChangeListener l){
		if (null != l)
		{
			this.mListener = l;
		}
	}
	
	public void show(ArrayList<PostType> types)
	{
		this.mTypes = types;
		notifyDataSetChanged();
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null == mTypes ? 0 : mTypes.size();
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public PostType getItem(int position) {
		// TODO Auto-generated method stub
		return null == mTypes ? null : mTypes.get(position);
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
		textView.setText(getItem(position).getSmallName());
//		textView.setTextColor(mContext.getResources().getColorStateList(R.color.item_publish));
		if (null != mListener) {
			textView.setOnCheckedChangeListener(mListener);
		}
//		if (isFirst && 0 == position) { 
//			textView.setChecked(true);
//			isFirst = false;
//		}
		return convertView;
	}
}
