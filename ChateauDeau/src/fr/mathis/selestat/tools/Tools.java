package fr.mathis.selestat.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.mathis.selestat.R;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

public class Tools {

	public static int GetToolbarHeight(Activity a)
	{
		int height = 0;
		TypedValue tv = new TypedValue();
		if (a.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
			height = TypedValue.complexToDimensionPixelSize(tv.data, a.getResources().getDisplayMetrics());
		}
		return height;
	}
	
	public static int getViewDiagonal(View v)
	{
		int d = 0;
		
		d = (int) Math.sqrt(v.getHeight() * v.getHeight() + v.getWidth() * v.getWidth());
		
		return d;
	}
	
	public static int convertDpToPixel(float dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}
	
	public static Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.WHITE);
		view.draw(canvas);
		return returnedBitmap;
	}
	
	public static String downloadJson(String url) throws IOException
	{
		StringBuilder response = new StringBuilder();
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
			String line = null;
			while ((line = input.readLine()) != null) {
				response.append(line);
			}
			input.close();
		}
		return response.toString();
	}
}
