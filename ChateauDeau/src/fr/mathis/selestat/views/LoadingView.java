package fr.mathis.selestat.views;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;

import fr.mathis.selestat.R;
import fr.mathis.selestat.tools.SvgHelper;
import fr.mathis.selestat.tools.Tools;

public class LoadingView extends View {
	private static final String LOG_TAG = "LoadingView";

	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private final SvgHelper mSvg = new SvgHelper(mPaint);
	private int mSvgResource;

	private final Object mSvgLock = new Object();
	private List<SvgHelper.SvgPath> mPaths = new ArrayList<SvgHelper.SvgPath>(0);
	private Thread mLoader;

	private float mPhase;
	private int mDuration;
	private float mParallax = 1.0f;
	private float mOffsetY;

	private ObjectAnimator mSvgAnimator;

	public LoadingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mPaint.setStyle(Paint.Style.STROKE);

		mPaint.setStrokeWidth(Tools.convertDpToPixel(2));
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPhase = 2.0f;
		mDuration = 4000;
		setSvgResource(R.raw.logo_sans_detail);
	}

	@SuppressLint("NewApi")
	public void reset() {
		if (mSvgAnimator != null)
			mSvgAnimator.cancel();
		mSvgAnimator = null;
		mPhase = 2.0f;
	}

	private void updatePathsPhaseLocked() {
		final int count = mPaths.size();
		for (int i = 0; i < count; i++) {
			SvgHelper.SvgPath svgPath = mPaths.get(i);
			svgPath.paint.setPathEffect(createPathEffect(svgPath.length, mPhase, 0.0f));
		}
	}

	public float getParallax() {
		return mParallax;
	}

	public void setParallax(float parallax) {
		mParallax = parallax;
		invalidate();
	}

	public float getPhase() {
		return mPhase;
	}

	public void setPhase(float phase) {
		mPhase = phase;
		synchronized (mSvgLock) {
			updatePathsPhaseLocked();
		}
		invalidate();
	}

	public int getSvgResource() {
		return mSvgResource;
	}

	public void setSvgResource(int svgResource) {
		mSvgResource = svgResource;
	}

	@Override
	protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (mLoader != null) {
			try {
				mLoader.join();
			} catch (InterruptedException e) {
				Log.e(LOG_TAG, "Unexpected error", e);
			}
		}

		mLoader = new Thread(new Runnable() {
			@Override
			public void run() {
				mSvg.load(getContext(), mSvgResource);
				synchronized (mSvgLock) {
					mPaths = mSvg.getPathsForViewport(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom());
					updatePathsPhaseLocked();
				}
			}
		}, "SVG Loader");
		mLoader.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		synchronized (mSvgLock) {
			canvas.save();
			canvas.translate(getPaddingLeft(), getPaddingTop() + mOffsetY);
			final int count = mPaths.size();
			for (int i = 0; i < count; i++) {
				SvgHelper.SvgPath svgPath = mPaths.get(i);
				canvas.drawPath(svgPath.path, svgPath.paint);
			}
			canvas.restore();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(hasWindowFocus)
			reveal(getRootView(), 0);
		else reset();
		super.onWindowFocusChanged(hasWindowFocus);
	}
	
	@Override
	public void setVisibility(int visibility) {
		if(visibility == View.GONE)
		{
			reset();
		}
		else {
			reveal(getRootView(), 0);
		}
		super.setVisibility(visibility);
	}
	
	private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
		return new DashPathEffect(new float[] { pathLength, pathLength }, Math.max(phase * pathLength, offset));
	}

	public void reveal(View scroller, int parentBottom) {
		if (mSvgAnimator == null) {
			mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", mPhase, 0.0f);
			mSvgAnimator.setDuration(mDuration);
			mSvgAnimator.setInterpolator(new LinearInterpolator());
			mSvgAnimator.setRepeatMode(Animation.RESTART);
			mSvgAnimator.setRepeatCount(Animation.INFINITE);
			mSvgAnimator.start();
		}

		float previousOffset = mOffsetY;
		mOffsetY = Math.min(0, scroller.getHeight() - (parentBottom - scroller.getScrollY()));
		if (previousOffset != mOffsetY)
			invalidate();
	}
}