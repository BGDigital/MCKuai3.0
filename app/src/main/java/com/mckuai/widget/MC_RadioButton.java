package com.mckuai.widget;

import com.mckuai.imc.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MC_RadioButton extends FrameLayout implements OnClickListener
{
	private boolean checked = false;
	private Context mContext;
	private LayoutInflater mInflater;
	private TextView mCount;
	private TextView mTitle;
	private ImageView mLayout;
	private String text;
	private int count;
	private OnCheckChangeListener onCheckChangeListener;
	private int mChecked;
	private int mNormal;
	private View view;

	public interface OnCheckChangeListener
	{
		public void onCheckedChang(int id);
	}

	public MC_RadioButton(Context context)
	{
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	public MC_RadioButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		mChecked = mContext.getResources().getColor(R.color.font_radio_hint_checked);
		mNormal = mContext.getResources().getColor(R.color.font_radio_hint_normal);
		initView();
	}

	public MC_RadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		// TODO Auto-generated constructor stub
		this(context, attrs);
	}

	private void initView()
	{
		if (null == view)
		{
			view = mInflater.inflate(R.layout.mc_radiobutton, null);
		}
		mCount = (TextView) view.findViewById(R.id.tv_count);
		mTitle = (TextView) view.findViewById(R.id.tv_title);
		mLayout = (ImageView) view.findViewById(R.id.iv_background);
		mTitle.setTextColor(mNormal);
		removeAllViews();
		setClickable(true);
		view.setOnClickListener(this);
		addView(view);
	}

	private void changeTextColor()
	{
		if (checked)
		{
			mTitle.setTextColor(mChecked);
			mLayout.setVisibility(VISIBLE);
		} else
		{
			mTitle.setTextColor(mNormal);
			mLayout.setVisibility(INVISIBLE);
		}
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		boolean change = checked ^ this.checked;

		if (change)
		{
			this.checked = checked;
			changeTextColor();
			//postInvalidate();
			if (null != onCheckChangeListener && checked)
			{
				onCheckChangeListener.onCheckedChang(getId());
			}
		}

	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
		mTitle.setText(text);
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
		mCount.setText(count + "");
	}

	public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener)
	{
		this.onCheckChangeListener = onCheckChangeListener;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		setChecked(true);
	}

}
