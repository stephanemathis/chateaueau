package fr.mathis.selestat.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An {@link android.widget.ImageView} layout that maintains a consistent width to height aspect ratio.
 */
public class DynamicWidthImageView extends ImageView {

	private double mHeightRatio;

	public DynamicWidthImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DynamicWidthImageView(Context context) {
		super(context);
	}

	public void setHeightRatio(double ratio) {
		if (ratio != mHeightRatio) {
			mHeightRatio = ratio;
			requestLayout();
		}
	}

	public double getHeightRatio() {
		return mHeightRatio;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mHeightRatio > 0.0) {
			// set the image views size
			int height = MeasureSpec.getSize(heightMeasureSpec);
			int width = (int) (height / mHeightRatio);
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void drawableHotspotChanged(float x, float y) {
		super.drawableHotspotChanged(x, y);
	}
}
