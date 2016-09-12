package fr.mathis.selestat.activities;

import java.lang.reflect.Field;
import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import fr.mathis.selestat.R;

public class AppActivity extends ActionBarActivity {

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(getResources().getColor(R.color.accent_dark));
		}

		super.onCreate(savedInstanceState);

		if (getSupportActionBar() != null) {
			setSupportProgressBarIndeterminateVisibility(false);
			getSupportActionBar().setIcon(android.R.color.transparent);
		}

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		int glowDrawableId = getResources().getIdentifier("overscroll_glow", "drawable", "android");
		if (glowDrawableId > 0) {
			Drawable androidGlow = getResources().getDrawable(glowDrawableId);
			androidGlow.setColorFilter(getResources().getColor(R.color.accent_color), PorterDuff.Mode.SRC_ATOP);
		}

		// change the default blue scroll edge
		int edgeDrawableId = getResources().getIdentifier("overscroll_edge", "drawable", "android");
		if (edgeDrawableId > 0) {
			Drawable androidEdge = getResources().getDrawable(edgeDrawableId);
			androidEdge.setColorFilter(getResources().getColor(R.color.accent_color), PorterDuff.Mode.SRC_ATOP);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
