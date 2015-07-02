package com.mckuai.widget;

import com.mckuai.imc.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView
{
	private int mBorderThickness = 0;
	private Context mContext;
	private int defaultColor = 0xFFFFFFFF;
	// 如果只有其中一个有值，则只画一个圆形边框
	private int mBorderOutsideColor = 0;
	private int mBorderInsideColor = 0;
	// 控件默认长、宽
	private int defaultWidth = 0;
	private int defaultHeight = 0;
	private float mProgress = 0;
	private float mTextSize = 0;
	private int mStart = -90;
	private int mTextColor;
	private String mText = "lv88";

	public CircleImageView(Context context)
	{
		super(context);
		mContext = context;
	}

	public CircleImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		setCustomAttributes(attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		setCustomAttributes(attrs);
	}

	private void setCustomAttributes(AttributeSet attrs)
	{
		TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
		if (null != a)
		{
			mBorderThickness = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, 0);
			mBorderOutsideColor = a.getColor(R.styleable.CircleImageView_progress_color, defaultColor);
			mBorderInsideColor = a.getColor(R.styleable.CircleImageView_border_color, defaultColor);
			mTextSize = a.getDimension(R.styleable.CircleImageView_text_size, 0);
			mStart = a.getInteger(R.styleable.CircleImageView_start_point, -90);// 默认是从上方开始
			mTextColor = a.getColor(R.styleable.CircleImageView_text_color, mTextColor);
			a.recycle();
		}
	}

	public void setProgress(float progress)
	{
		this.mProgress = progress;
		 postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		Drawable drawable = getDrawable();
		if (drawable == null)
		{
			return;
		}

		if (getWidth() == 0 || getHeight() == 0)
		{
			return;
		}
		this.measure(0, 0);
		if (drawable == null || drawable instanceof NinePatchDrawable)
			return;
		// Bitmap b = ((Drawable) drawable).getBitmap();
		Bitmap b = drawableToBitamp(drawable, getWidth(), getHeight());
		Bitmap bitmap = b.copy(Config.ARGB_8888, true);
		if (defaultWidth == 0)
		{
			defaultWidth = getWidth();

		}
		if (defaultHeight == 0)
		{
			defaultHeight = getHeight();
		}
		// 保证重新读取图片后不会因为图片大小而改变控件宽、高的大小（针对宽、高为wrap_content布局的imageview，但会导致margin无效）
		// if (defaultWidth != 0 && defaultHeight != 0) {
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// defaultWidth, defaultHeight);
		// setLayoutParams(params);
		// }
		int radius = 0;
		radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
		
		Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
		canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
		drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
		if (0 != mProgress)
		{
			drawProgress(canvas, radius + mBorderThickness / 2);
		}
		
		if (null != mText)
		{
			drawtext(canvas);
		}
	}

	/**
	 * 获取裁剪后的圆形图片
	 * 
	 * @param radius
	 *            半径
	 */
	public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius)
	{
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;

		// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth)
		{// 高大于宽
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 截取正方形图片
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
		} else if (bmpHeight < bmpWidth)
		{// 宽大于高
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
		} else
		{
			squareBitmap = bmp;
		}

		if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter)
		{
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);

		} else
		{
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		// bitmap回收(recycle导致在布局文件XML看不到效果)
//		 bmp.recycle();
//		 squareBitmap.recycle();
//		 scaledSrcBmp.recycle();
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}

	/**
	 * 边缘画圆
	 */
	private void drawCircleBorder(Canvas canvas, int radius, int color)
	{
		Paint paint = new Paint();
		/* 去锯齿 */
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setColor(color);
		/* 设置paint的　style　为STROKE：空心 */
		paint.setStyle(Paint.Style.STROKE);
		/* 设置paint的外框宽度 */
		paint.setStrokeWidth(mBorderThickness);
		canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
	}

	private void drawProgress(Canvas canvas, int radius)
	{
		Paint paint = new Paint();
		/* 去锯齿 */
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setColor(mBorderOutsideColor);
		/* 设置paint的　style　为STROKE：空心 */
		paint.setStyle(Paint.Style.STROKE);
		/* 设置paint的外框宽度 */
		paint.setStrokeWidth(mBorderThickness);
		RectF rectF = new RectF(mBorderThickness /2, mBorderThickness /2 , defaultWidth - mBorderThickness /2 ,defaultHeight - mBorderThickness / 2);
		canvas.drawArc(rectF, mStart, mProgress * 360, false, paint);
	}
	
	private void drawtext(Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(mTextColor);
		paint.setTextSize(mTextSize);
		canvas.drawText(mText, 0, mText.length() - 1, 1, 40, paint);
	}

	private Bitmap drawableToBitamp(Drawable drawable, int width, int height)
	{
		// 6 System.out.println("Drawable转Bitmap");
		Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
				: Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		// 注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
		Canvas canvas = new Canvas(bitmap);
		drawable.draw(canvas);
		return bitmap;
	}
}
