package fr.mathis.selestat.fragment;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import fr.mathis.selestat.R;
import fr.mathis.selestat.activities.AppActivity;
import fr.mathis.selestat.activities.HomeActivity;
import fr.mathis.selestat.models.Photo;
import fr.mathis.selestat.views.TouchImageView;

public class PhotoFragment extends Fragment {

	public static int MENU_WALLPAPER = 0;
	public static int MENU_MAP = 1;

	private Toolbar _toolbar;
	private View _vLoading;
	public TouchImageView _ivPhoto;
	private Photo p;

	public static PhotoFragment newInstance(int id) {
		PhotoFragment f = new PhotoFragment();

		Bundle b = new Bundle();
		b.putInt("id", id);
		f.setArguments(b);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_photo, container, false);

		_toolbar = (Toolbar) v.findViewById(R.id.toolbar);
		_vLoading = v.findViewById(R.id.lv_loading_photo);
		_ivPhoto = (TouchImageView) v.findViewById(R.id.iv_photo_detail);

		((AppActivity) getActivity()).setSupportActionBar(_toolbar);
		((AppActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((AppActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
		((AppActivity) getActivity()).getSupportActionBar().setTitle("");

		int photoId = getArguments().getInt("id");
		p = Photo.getPhoto(photoId);

		boolean isInCache = DiskCacheUtils.findInCache(p.getThumbnailUrl(), ImageLoader.getInstance().getDiskCache()) != null;

		if (isInCache) {
			DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).build();
			_ivPhoto.setImageBitmap(ImageLoader.getInstance().loadImageSync(p.getThumbnailUrl(), options));
			
			DisplayImageOptions optionsFull = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).delayBeforeLoading(1000).build();
			_vLoading.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(p.getFullsizeUrl(), _ivPhoto, optionsFull);
		} else {
			DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).build();
			ImageLoader.getInstance().displayImage(p.getFullsizeUrl(), _ivPhoto, options, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String arg0, View arg1) {
				}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					if (getActivity() != null)
						getActivity().finish();
				}

				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					Toast.makeText(getActivity(), R.string.photo_detail_loading_fail, Toast.LENGTH_SHORT).show();
					if (getActivity() != null)
						getActivity().finish();
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					_ivPhoto.invalidate();
					_vLoading.setVisibility(View.GONE);
					Log.d("SELESTAT", "Fullsize loaded");
				}
			});
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			v.findViewById(R.id.iv_photo_detail).setTransitionName("TransImg" + p.getId());
		}

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItemCompat.setShowAsAction(menu.add(0, MENU_WALLPAPER, 0, R.string.photo_menu_wallpaper), MenuItemCompat.SHOW_AS_ACTION_NEVER);

		MenuItemCompat.setShowAsAction(menu.add(0, MENU_MAP, 0, R.string.photo_menu_map), MenuItemCompat.SHOW_AS_ACTION_NEVER);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_WALLPAPER) {
			new SetWallpaperTask().execute();
			return true;
		} else if (item.getItemId() == MENU_MAP) {
			Intent b = new Intent();
			b.putExtra(HomeActivity.RESULT_EXTRA_ID, p.getId());
			getActivity().setResult(Activity.RESULT_OK, b);
			getActivity().finish();
			return true;
		}

		return super.onContextItemSelected(item);
	}

	class SetWallpaperTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			((ActionBarActivity) PhotoFragment.this.getActivity()).setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				return ((BitmapDrawable) _ivPhoto.getDrawable()).getBitmap();
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);

			if (getActivity() != null) {
				if (result != null) {
					WallpaperManager m = WallpaperManager.getInstance(getActivity());
					try {
						m.setBitmap(result);
						Toast.makeText(getActivity(), R.string.photo_menu_wallpaper_succes, Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), R.string.photo_menu_wallpaper_fail, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getActivity(), R.string.photo_menu_wallpaper_fail, Toast.LENGTH_SHORT).show();
				}
				((ActionBarActivity) PhotoFragment.this.getActivity()).setSupportProgressBarIndeterminateVisibility(false);
			}
		}
	}

}
