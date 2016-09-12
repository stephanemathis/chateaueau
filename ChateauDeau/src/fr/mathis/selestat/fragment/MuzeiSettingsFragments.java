package fr.mathis.selestat.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import fr.mathis.selestat.R;
import fr.mathis.selestat.models.Category;
import fr.mathis.selestat.models.Model;
import fr.mathis.selestat.models.Photo;
import fr.mathis.selestat.services.MuzeiService;
import fr.mathis.selestat.tools.PrefManager;
import fr.mathis.selestat.views.LoadingView;

public class MuzeiSettingsFragments extends Fragment {

	public static int MENU_DELAY = 0;

	public static int INTERVAL_NEVER = 0;
	public static int INTERVAL_ONE_HOUR = 1;
	public static int INTERVAL_THEE_HOUR = 2;
	public static int INTERVAL_SIX_HOUR = 3;
	public static int INTERVAL_ONE_DAY = 4;
	public static int INTERVAL_THREE_DAY = 5;

	private LinearLayout _llLoaded;
	private LoadingView _lvLoading;
	private StickyGridHeadersGridView _grid;
	private MuzeiGridAdpater _adapter;

	private ArrayList<Integer> _photosSelectedIds;

	public static MuzeiSettingsFragments newInstance() {
		MuzeiSettingsFragments f = new MuzeiSettingsFragments();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_muzei_settings, container, false);

		_llLoaded = (LinearLayout) v.findViewById(R.id.ll_muzei_loaded);
		_lvLoading = (LoadingView) v.findViewById(R.id.lv_muzei_loading);
		_grid = (StickyGridHeadersGridView) v.findViewById(R.id.sghgv_muzei);
		_adapter = new MuzeiGridAdpater(LayoutInflater.from(getActivity()));

		_grid.setAdapter(_adapter);

		_photosSelectedIds = new ArrayList<Integer>();

		String savedIds = PrefManager.GetString("muzei", getActivity());
		String[] ids = savedIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] != null && ids[i].compareTo("") != 0) {
				_photosSelectedIds.add(Integer.parseInt(ids[i]));
			}
		}

		new LoadingDataTask().execute();

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem m = menu.add(0, MENU_DELAY, 0, R.string.muzei_settings_menu_delay);
		m.setIcon(R.drawable.ic_action_rotate_interval);
		MenuItemCompat.setShowAsAction(m, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_DELAY) {

			int interval = PrefManager.GetInt("refreshInterval", getActivity());
			if (interval < INTERVAL_NEVER)
				interval = INTERVAL_ONE_DAY;

			PrefManager.SaveInt("refreshIntervalTemporary", interval, getActivity());

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.muzei_settings_menu_delay).setSingleChoiceItems(R.array.muzei_settings_intervals, interval, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					PrefManager.SaveInt("refreshIntervalTemporary", which, getActivity());
				}
			}).setPositiveButton(R.string.OK, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PrefManager.SaveInt("refreshInterval", PrefManager.GetInt("refreshIntervalTemporary", getActivity()), getActivity());
					MuzeiService.getInstance().forceUpdate();
				}
			}).setNegativeButton(R.string.cancel, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.create().show();

			return true;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();

		StringBuilder sb = new StringBuilder();
		int photoCount = _photosSelectedIds.size();

		for (int i = 0; i < photoCount; i++) {
			sb.append("," + _photosSelectedIds.get(i));
		}

		String toSave = sb.toString();

		if (toSave.length() > 0)
			toSave = toSave.substring(1);

		PrefManager.SaveString("muzei", toSave, getActivity());

		if (MuzeiService.getInstance() != null) {
			int lastPhotoId = PrefManager.GetInt("lastPhotoId", getActivity());
			if (lastPhotoId == -1 || !_photosSelectedIds.contains(lastPhotoId)) {
				MuzeiService.getInstance().forceUpdate();
			}
		}
	}

	class LoadingDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Model.DownloadThenParseData();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			_llLoaded.setVisibility(View.VISIBLE);
			_lvLoading.setVisibility(View.GONE);

			ArrayList<Category> categories = Category.getAllCategories();
			ArrayList<Photo> photos = new ArrayList<Photo>();

			for (int i = 1; i < categories.size(); i++) {
				photos.addAll(Photo.getAllPhotos(categories.get(i).getId()));
			}

			if (_photosSelectedIds.size() == 0) {
				for (int i = 0; i < photos.size(); i++) {
					_photosSelectedIds.add(photos.get(i).getId());
				}
			}

			categories.remove(0);

			_adapter.Update(categories, photos);
		}
	}

	class MuzeiGridAdpater extends BaseAdapter implements StickyGridHeadersBaseAdapter {

		LayoutInflater _inflater;
		ArrayList<Category> _categories;
		ArrayList<Photo> _photos;
		private DisplayImageOptions _options;

		public MuzeiGridAdpater(LayoutInflater inflater) {
			_inflater = inflater;
			_categories = new ArrayList<Category>();
			_photos = new ArrayList<Photo>();
			_options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).displayer(new FadeInBitmapDisplayer(300)).build();
		}

		public void Update(ArrayList<Category> categories, ArrayList<Photo> photos) {
			_categories = categories;
			_photos = photos;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return _photos.size();
		}

		@Override
		public Object getItem(int position) {
			return _photos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getCountForHeader(int header) {
			return _categories.get(header).getCount();
		}

		@Override
		public int getNumHeaders() {
			return _categories.size();
		}

		@Override
		public View getHeaderView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (convertView == null) {
				v = _inflater.inflate(R.layout.template_muzei_settings_header, parent, false);
			}

			Category c = _categories.get(position);

			TextView tvTitle = (TextView) v.findViewById(R.id.tv_muzei_header_title);
			TextView tvCount = (TextView) v.findViewById(R.id.tv_muzei_header_count);

			tvTitle.setText(c.getTitle());
			tvCount.setText(c.getCount() + "");

			v.setOnClickListener(new HeaderClickListener(c));

			return v;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (convertView == null) {
				v = _inflater.inflate(R.layout.template_muzei_settings_item, parent, false);
			}

			Photo p = _photos.get(position);

			ImageView iv = (ImageView) v.findViewById(R.id.iv_gallery_photo);
			View vCache = v.findViewById(R.id.v_muzei_cache);

			iv.setImageDrawable(null);

			if (_photosSelectedIds.contains(p.getId())) {
				v.setBackgroundColor(Color.parseColor(p.getColor()));
				vCache.setBackgroundResource(R.drawable.bg_transparent_selector);
			} else {
				v.setBackgroundColor(Color.DKGRAY);
				vCache.setBackgroundResource(R.drawable.bg_grey_selector);
			}

			ImageLoader.getInstance().displayImage(p.getThumbnailUrl(), iv, _options);

			vCache.setOnClickListener(new ItemClickListener(p));

			return v;
		}

		class ItemClickListener implements View.OnClickListener {
			Photo _p;

			public ItemClickListener(Photo p) {
				_p = p;
			}

			@Override
			public void onClick(View v) {
				if (_photosSelectedIds.contains(_p.getId())) {
					_photosSelectedIds.remove(Integer.valueOf(_p.getId()));
				} else {
					_photosSelectedIds.add(Integer.valueOf(_p.getId()));
				}

				if (_photosSelectedIds.contains(_p.getId())) {
					v.setBackgroundResource(R.drawable.bg_transparent_selector);
				} else {
					v.setBackgroundResource(R.drawable.bg_grey_selector);
				}
			}
		}

		class HeaderClickListener implements View.OnClickListener {

			Category _c;

			public HeaderClickListener(Category c) {
				_c = c;
			}

			@Override
			public void onClick(View v) {
				int startOffset = 0;

				int cCount = _categories.size();

				for (int i = 0; i < cCount; i++) {
					if (_c.getId() == _categories.get(i).getId()) {
						break;
					} else {
						startOffset += _categories.get(i).getCount();
					}
				}

				boolean addAll = !_photosSelectedIds.contains(_photos.get(startOffset).getId());

				cCount = _c.getCount();

				if (addAll) {
					for (int i = startOffset; i < startOffset + cCount; i++) {
						if (_photosSelectedIds.contains(_photos.get(i).getId())) {

						} else {
							_photosSelectedIds.add(Integer.valueOf(_photos.get(i).getId()));
						}
					}
				} else {
					for (int i = startOffset; i < startOffset + cCount; i++) {
						if (_photosSelectedIds.contains(_photos.get(i).getId())) {
							_photosSelectedIds.remove(Integer.valueOf(_photos.get(i).getId()));
						}
					}
				}

				_adapter.notifyDataSetChanged();
			}
		}
	}

}
