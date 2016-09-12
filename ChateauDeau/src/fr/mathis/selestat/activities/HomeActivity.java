package fr.mathis.selestat.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;

import com.astuetz.PagerSlidingTabStrip;

import fr.mathis.selestat.R;
import fr.mathis.selestat.adapters.HomePagerAdapter;
import fr.mathis.selestat.adapters.HomeSpinnerAdapter;
import fr.mathis.selestat.fragment.GalleryFragment;
import fr.mathis.selestat.fragment.MapWrapperFragment;
import fr.mathis.selestat.models.Category;
import fr.mathis.selestat.tools.Tools;
import fr.mathis.selestat.views.fab.FloatingActionButton;

public class HomeActivity extends AppActivity implements OnPageChangeListener, OnItemSelectedListener {

	public static int RESULT_CODE_MAP = 0;
	public static String RESULT_EXTRA_ID = "id";

	private Toolbar _toolbar;
	private Spinner _sp_sections;
	private ViewPager _vpPager;
	private FloatingActionButton _cbAdd;
	private int _marginDiff;

	private HomePagerAdapter _pagerAdapter;
	private HomeSpinnerAdapter _spinnerAdapter;
	private int _currentCategoryeId = 1;
	private int _currentTabPosition = -1;

	private GalleryFragment _galleryFragment;
	private MapWrapperFragment _mapFragment;

	private boolean isTablet;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pane_pager);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

			final View vReveal = findViewById(R.id.v_reveal);

			vReveal.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
				@TargetApi(Build.VERSION_CODES.LOLLIPOP)
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
					v.removeOnLayoutChangeListener(this);

					Animator anim = ViewAnimationUtils.createCircularReveal(vReveal, vReveal.getWidth() / 2, Tools.convertDpToPixel(60), vReveal.getHeight(), 0);
					anim.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							super.onAnimationEnd(animation);
							vReveal.setVisibility(View.GONE);
						}
					});

					anim.start();
				}
			});

		} else {
			findViewById(R.id.v_reveal).setVisibility(View.GONE);
		}

		isTablet = findViewById(R.id.vp_pager) == null;

		_galleryFragment = GalleryFragment.newInstance(_currentCategoryeId);
		_mapFragment = MapWrapperFragment.newInstance(_currentCategoryeId);
		_toolbar = (Toolbar) findViewById(R.id.toolbar);
		_sp_sections = (Spinner) findViewById(R.id.sp_sections);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		_cbAdd = (FloatingActionButton) findViewById(R.id.cb_add);

		if (!isTablet()) {
			_vpPager = (ViewPager) findViewById(R.id.vp_pager);

			_pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), _galleryFragment, _mapFragment);
			_vpPager.setAdapter(_pagerAdapter);
		} else {
			/*
			 * FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); ft.add(R.id.fragment_container_left, _galleryFragment); ft.commit();
			 * 
			 * ft = getSupportFragmentManager().beginTransaction(); ft.add(R.id.fragment_container_right, _mapFragment); ft.commit();
			 */
		}

		getSupportActionBar().setTitle(null);
		getSupportActionBar().setIcon(android.R.color.transparent);

		_spinnerAdapter = new HomeSpinnerAdapter(LayoutInflater.from(this), Category.getAllCategories(), _currentCategoryeId);
		_sp_sections.setAdapter(_spinnerAdapter);
		_sp_sections.setOnItemSelectedListener(this);
		_sp_sections.setSelection(_currentCategoryeId);

		if (!isTablet()) {
			PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.psts_tabs);
			tabs.setIndicatorHeight(Tools.convertDpToPixel(4));
			tabs.setIndicatorColorResource(R.color.accent_inverse);
			tabs.setViewPager(_vpPager);
			tabs.setOnPageChangeListener(this);
		}

		_currentTabPosition = 0;

		_cbAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, AddActivity.class);
				Bundle b = null;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					Bitmap bitmap = Bitmap.createBitmap(_cbAdd.getWidth(), _cbAdd.getHeight(), Bitmap.Config.ARGB_8888);
					bitmap.eraseColor(getResources().getColor(R.color.accent_color));
					b = ActivityOptions.makeThumbnailScaleUpAnimation(_cbAdd, bitmap, 0, 0).toBundle();
					startActivityForResult(intent, 0, b);
					overridePendingTransition(0, 0);
				} else {
					startActivityForResult(intent, 0);
					overridePendingTransition(0, 0);
				}
			}
		});

		Display display = getWindowManager().getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			_marginDiff = size.x - Tools.convertDpToPixel(64 + 20 + 20);
		} else {
			_marginDiff = display.getWidth() - Tools.convertDpToPixel(64 + 20 + 20);
		}
	}

	public boolean isTablet() {
		return isTablet;
	}

	public int getCurrentCategory() {
		return _currentCategoryeId;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (position == 0) {
			LayoutParams params = (LayoutParams) _cbAdd.getLayoutParams();
			params.rightMargin = Tools.convertDpToPixel(20) + (int) (_marginDiff * Math.abs(positionOffset));
			_cbAdd.setLayoutParams(params);
		} else {
			LayoutParams params = (LayoutParams) _cbAdd.getLayoutParams();
			params.rightMargin = (int) (_marginDiff + Tools.convertDpToPixel(20));
			_cbAdd.setLayoutParams(params);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if (_currentTabPosition != position) {
			_vpPager.setCurrentItem(position);
			_currentTabPosition = position;
		}
		_cbAdd.show(true);
	}

	@SuppressLint("NewApi")
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (position != _currentCategoryeId) {
			_currentCategoryeId = position;
			_spinnerAdapter.UpdateIndicator(_currentCategoryeId);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				final View vRevealOverContent = findViewById(R.id.v_reveal_over_content);
				Animator anim = ViewAnimationUtils.createCircularReveal(vRevealOverContent, 0, 0, 0, Tools.getViewDiagonal(vRevealOverContent));
				vRevealOverContent.setVisibility(View.VISIBLE);
				anim.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);

						vRevealOverContent.setVisibility(View.GONE);
						refreshViews();

						Animator animPager = ViewAnimationUtils.createCircularReveal(_vpPager, 0, 0, 0, Tools.getViewDiagonal(_vpPager));
						animPager.start();
					}
				});

				anim.start();

			} else {
				refreshViews();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		final View view = findViewById(R.id.psts_tabs);
		if (view != null) {
			ViewTreeObserver observer = view.getViewTreeObserver();
			observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@SuppressLint("NewApi")
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					// Change tabs height
					view.getLayoutParams().height = Tools.GetToolbarHeight(HomeActivity.this);
					_toolbar.getLayoutParams().height = Tools.GetToolbarHeight(HomeActivity.this);

					// change scrolling distance for Add button
					Display display = getWindowManager().getDefaultDisplay();
					if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
						Point size = new Point();
						display.getSize(size);
						_marginDiff = size.x - Tools.convertDpToPixel(64 + 20 + 20);
					} else {
						_marginDiff = display.getWidth() - Tools.convertDpToPixel(64 + 20 + 20);
					}

					// change app button position
					LayoutParams params = (LayoutParams) _cbAdd.getLayoutParams();
					if (params.rightMargin > Tools.convertDpToPixel(20)) {
						params.rightMargin = (int) (display.getWidth() - Tools.convertDpToPixel(64 + 20));
						_cbAdd.setLayoutParams(params);
					}

					view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				}
			});
		}
	}

	public void refreshViews() {
		_spinnerAdapter.UpdateData(Category.getAllCategories());
		if (_galleryFragment != null) {
			_galleryFragment.updateCategory(_currentCategoryeId);
		}
		if (_mapFragment != null) {
			if (_mapFragment.getMapFragment() != null)
				_mapFragment.getMapFragment().updateCategory(_currentCategoryeId);
		}
	}

	public void displayPhotoOnMap(int photoId) {
		if (_vpPager != null)
			_vpPager.setCurrentItem(1, true);
		_mapFragment.getMapFragment().zoomOnPhoto(photoId);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == RESULT_CODE_MAP && resultCode == Activity.RESULT_OK) {
			displayPhotoOnMap(data.getExtras().getInt(RESULT_EXTRA_ID));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
