package fr.mathis.selestat.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.Surface;

import com.nostra13.universalimageloader.core.ImageLoader;

import fr.mathis.selestat.R;
import fr.mathis.selestat.fragment.PhotoFragment;

public class PhotoActivity extends AppActivity {

	PhotoFragment _f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pane);

		setRequestedOrientation(getScreenOrientation(this));

		int photoId = getIntent().getIntExtra("photoId", -1);

		_f = PhotoFragment.newInstance(photoId);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, _f);
		ft.commit();
	}

	public static int getScreenOrientation(Activity activity) {
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int orientation = activity.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
				return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			} else {
				return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
			}
		}
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
				return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			} else {
				return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
			}
		}
		return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			initClose();
			ActivityCompat.finishAfterTransition(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		initClose();
	}

	private void initClose() {
		ImageLoader.getInstance().cancelDisplayTask(_f._ivPhoto);
		_f._ivPhoto.normalizedScale = 1;
		_f._ivPhoto.invalidate();
	}

	@Override
	protected void onStop() {
		_f = null;
		super.onStop();
	}
}
