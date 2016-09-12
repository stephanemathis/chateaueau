package fr.mathis.selestat.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView.OnScrollListener;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import fr.mathis.selestat.R;
import fr.mathis.selestat.activities.HomeActivity;
import fr.mathis.selestat.activities.PhotoActivity;
import fr.mathis.selestat.adapters.GalleryRecyclerAdapter;
import fr.mathis.selestat.models.Model;
import fr.mathis.selestat.models.Photo;
import fr.mathis.selestat.tools.Tools;
import fr.mathis.selestat.views.fab.FloatingActionButton;

public class GalleryFragment extends Fragment implements OnRefreshListener, OnClickListener {

	private ScrollListener _scrollListener;
	private RecyclerView _rvGallery;
	private GalleryRecyclerAdapter _adapter;
	private SwipeRefreshLayout _swipeLayout;
	private int _currentCategoryeId = -1;

	public static GalleryFragment newInstance(int categoryId) {
		GalleryFragment f = new GalleryFragment();

		Bundle b = new Bundle();
		b.putInt("id", categoryId);
		f.setArguments(b);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		_currentCategoryeId = getArguments().getInt("id");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_gallery, container, false);

		_rvGallery = (RecyclerView) v.findViewById(R.id.rv_gallery);
		_swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);

		_scrollListener = new ScrollListener();

		_rvGallery.setHasFixedSize(false);
		_rvGallery.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.num_cols), StaggeredGridLayoutManager.VERTICAL));
		_rvGallery.setOnScrollListener(_scrollListener);
		_rvGallery.setItemAnimator(new DefaultItemAnimator());
		_rvGallery.setPadding(Tools.convertDpToPixel(2), Tools.convertDpToPixel(2), Tools.convertDpToPixel(2), Tools.convertDpToPixel(2));
		_swipeLayout.setOnRefreshListener(this);
		_swipeLayout.setProgressViewOffset(false, Tools.GetToolbarHeight(getActivity()), (int) (Tools.GetToolbarHeight(getActivity()) * 2.5));
		_swipeLayout.setColorSchemeResources(R.color.accent_inverse, R.color.accent_color, R.color.accent_inverse, R.color.accent_color);

		loadPhotosAndDisplay();

		return v;
	}

	private void loadPhotosAndDisplay() {
		ArrayList<Photo> photos = Photo.getAllPhotos(_currentCategoryeId);

		UpdateFakeItems(photos);
		if (_adapter == null) {
			_adapter = new GalleryRecyclerAdapter(this);

			_rvGallery.setAdapter(_adapter);
		}

		_adapter.update(photos);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void UpdateFakeItems(ArrayList<Photo> photos) {

		while (photos.get(0).getId() == -1) {
			photos.remove(0);
		}

		int screenWidth = 0;
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
		} else {
			screenWidth = display.getWidth();
		}

		int numColumns = getResources().getInteger(R.integer.num_cols);

		for (int i = 0; i < numColumns; i++) {
			photos.add(0, new Photo(-1, "", "", 0, 0, (float) (Tools.GetToolbarHeight(getActivity()) * 2 - Tools.convertDpToPixel(4)) / (float) (screenWidth / numColumns - Tools.convertDpToPixel(4 + 2 * numColumns)), "#ffffff", _currentCategoryeId, 9999));
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		final View view = _rvGallery;

		ViewTreeObserver observer = view.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				UpdateFakeItems(_adapter.getItems());

				_scrollListener.init();
				ViewHelper.setTranslationY(getActivity().findViewById(R.id.ll_head), 0);

				_rvGallery.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.num_cols), StaggeredGridLayoutManager.VERTICAL));
				_adapter.notifyDataSetChanged();

				((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();

				view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	public void updateCategory(int categoryId) {
		_currentCategoryeId = categoryId;
		loadPhotosAndDisplay();
	}

	@Override
	public void onClick(View v) {
		int position = _rvGallery.getChildPosition(v);

		Photo p = _adapter.getItem(position);

		boolean isInCache = DiskCacheUtils.findInCache(p.getThumbnailUrl(), ImageLoader.getInstance().getDiskCache()) != null;

		Intent i = new Intent(GalleryFragment.this.getActivity(), PhotoActivity.class);
		i.putExtra("photoId", p.getId());

		if (isInCache) {
			ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "TransImg" + p.getId());
			ActivityCompat.startActivityForResult(getActivity(), i, HomeActivity.RESULT_CODE_MAP, options.toBundle());
		} else {
			getActivity().startActivityForResult(i, HomeActivity.RESULT_CODE_MAP);
		}
	}

	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

		final float densityMultiplier = context.getResources().getDisplayMetrics().density;

		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

		photo = Bitmap.createScaledBitmap(photo, w, h, true);

		return photo;
	}

	@Override
	public void onRefresh() {
		new LoadingDataTask().execute();
	}

	class ScrollListener extends RecyclerView.OnScrollListener {

		ImageLoader instance;
		View toolbarContainer;
		int toolbarHeight;
		int scrollY;
		boolean isFullyVisible = true;
		FloatingActionButton cbAdd;

		public ScrollListener() {
			instance = ImageLoader.getInstance();
			toolbarContainer = getActivity().findViewById(R.id.ll_head);
			cbAdd = (FloatingActionButton) getActivity().findViewById(R.id.cb_add);

			init();
		}

		public void init() {
			toolbarHeight = Tools.GetToolbarHeight(getActivity());
			scrollY = 0;
		}

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);

			switch (newState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				instance.resume();

				int from = (int) ViewHelper.getTranslationY(toolbarContainer);
				int to = 0;

				if (isFullyVisible) {
					if (from < -(toolbarHeight / 2)) {
						to = -toolbarHeight;
					} else {
						to = 0;
					}
				} else {
					if (from < -(toolbarHeight / 2)) {
						to = -toolbarHeight;
					} else {
						to = 0;
					}
				}

				isFullyVisible = to == 0;

				if (scrollY < toolbarHeight) {
					to = 0;
				}

				ObjectAnimator anim = ObjectAnimator.ofFloat(toolbarContainer, "translationY", from, to);
				anim.setDuration(200);
				anim.start();

				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				instance.pause();
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				instance.pause();
				break;
			}
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);

			scrollY += dy;

			if (toolbarContainer.getAnimation() != null)
				toolbarContainer.getAnimation().cancel();

			int nextPosition = (int) (ViewHelper.getTranslationY(toolbarContainer) - dy);

			if (nextPosition < -toolbarHeight)
				nextPosition = -toolbarHeight;
			if (nextPosition > 0)
				nextPosition = 0;

			ViewHelper.setTranslationY(toolbarContainer, nextPosition);

			if (dy > Tools.convertDpToPixel(4)) {
				cbAdd.hide(true);
			} else if (dy < 0) {
				cbAdd.show(true);
			}
		}
	}

	class LoadingDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
				Model.DownloadThenParseData();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			loadPhotosAndDisplay();
			if (getActivity() != null) {
				((HomeActivity) getActivity()).refreshViews();
			}
			_swipeLayout.setRefreshing(false);

		}
	}
}
