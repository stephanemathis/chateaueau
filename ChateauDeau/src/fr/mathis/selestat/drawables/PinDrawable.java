package fr.mathis.selestat.drawables;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

public class PinDrawable extends Drawable {

	Paint _paint;
	int _fillColor;
	int _strokeColor;
	int _textColor;
	int _number;
	Rect _bounds;
	Resources _r;

	public PinDrawable(int number, int fillColor, int strokeColor, int textColor, Resources r) {
		_number = number;
		_fillColor = fillColor;
		_strokeColor = strokeColor;
		_textColor = textColor;
		_r = r;

		_paint = new Paint();
		_paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, _r.getDisplayMetrics()));
		_paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17, _r.getDisplayMetrics()));
		_paint.setAntiAlias(true);
	}

	@Override
	public void draw(Canvas canvas) {
		setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

		float cx = canvas.getWidth() / 2;
		float cy = canvas.getHeight() / 2;
		float r = Math.min(cx, cy) - _paint.getStrokeWidth();

		// Fill
		_paint.setStyle(Paint.Style.FILL);
		_paint.setColor(_fillColor);
		canvas.drawCircle(cx, cy, r, _paint);

		// Stroke
		_paint.setStyle(Paint.Style.STROKE);
		_paint.setColor(_strokeColor);
		canvas.drawCircle(cx, cy, r, _paint);

		// Text
		if (_number > 1) {
			_paint.setStyle(Paint.Style.FILL);
			_paint.setColor(_textColor);
			_bounds = new Rect();
			_paint.getTextBounds(_number + "", 0, (_number + "").length(), _bounds);
			float txtW = _paint.measureText(_number + "");
			float tx = cx - txtW / 2;
			float ty = cy + (float) (_bounds.height()) / 2;
			canvas.drawText(_number + "", tx, ty, _paint);
		}
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 255;
	}

	public Bitmap ToBitmap(int width, int height) {
		int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, _r.getDisplayMetrics());
		int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, _r.getDisplayMetrics());
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		return bitmap;
	}

}
