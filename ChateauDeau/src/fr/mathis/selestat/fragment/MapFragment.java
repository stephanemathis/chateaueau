package fr.mathis.selestat.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.mathis.selestat.R;
import fr.mathis.selestat.activities.HomeActivity;
import fr.mathis.selestat.activities.PhotoActivity;
import fr.mathis.selestat.drawables.PinDrawable;
import fr.mathis.selestat.models.Photo;
import fr.mathis.selestat.tools.Tools;

public class MapFragment extends SupportMapFragment implements OnMarkerClickListener {

	private boolean _onReadyEventConsumed;
	private HashMap<String, Integer> markerIdForPhotoId;

	private int _currentCategoryeId = -1;

	public static MapFragment getNewInstance(int categoryId) {
		MapFragment f = new MapFragment();

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
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setMapTransparent((ViewGroup) view);

		return view;
	}

	private void setMapTransparent(ViewGroup group) {
		int childCount = group.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = group.getChildAt(i);
			if (child instanceof ViewGroup) {
				setMapTransparent((ViewGroup) child);
			} else if (child instanceof SurfaceView) {
				child.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_onReadyEventConsumed = false;
		if (getMap() != null) {
			getMap().setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChange(CameraPosition arg0) {
					if (!_onReadyEventConsumed) {
						_onReadyEventConsumed = true;
						init();
					}
				}
			});
		}
	}

	private void init() {
		if (getMap() != null) {
			getMap().clear();
			getMap().setPadding(0, Tools.GetToolbarHeight(getActivity()) * 2, 0, 0);
			getMap().setOnMarkerClickListener(this);
			// getMap().getUiSettings().setZoomControlsEnabled(false);
			// getMap().getUiSettings().setCompassEnabled(false);
			// getMap().getUiSettings().setMyLocationButtonEnabled(false);

			ArrayList<Photo> photos = Photo.getAllPhotos(_currentCategoryeId);
			int count = photos.size();
			markerIdForPhotoId = new HashMap<String, Integer>();

			int strokeColor = getResources().getColor(R.color.accent_color);
			int fillColor = Color.parseColor(getString(R.color.accent_color).replace("#ff", "#55"));

			LatLngBounds.Builder builder = new Builder();
			for (int i = 0; i < count; i++) {
				Photo p = photos.get(i);
				LatLng ll = new LatLng(p.getLatitude(), p.getLongitude());
				builder.include(ll);
			}
			try {
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), Tools.convertDpToPixel(80));
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					getMap().moveCamera(cu);
				} else {
					getMap().animateCamera(cu);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (int i = 0; i < count; i++) {
				Photo p = photos.get(i);
				LatLng ll = new LatLng(p.getLatitude(), p.getLongitude());
				markerIdForPhotoId.put(getMap().addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(new PinDrawable(1, fillColor, strokeColor, strokeColor, getResources()).ToBitmap(20, 20))).position(ll)).getId(), Integer.valueOf(p.getId()));
			}
		}
	}

	public void updateCategory(int categoryId) {
		_currentCategoryeId = categoryId;
		init();
	}

	public void zoomOnPhoto(int photoId) {
		Photo photo = Photo.getPhoto(photoId);
		getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(photo.getLatitude(), photo.getLongitude()), 20));
	}

	@Override
	public boolean onMarkerClick(Marker m) {
		if (markerIdForPhotoId.containsKey(m.getId())) {
			Intent i = new Intent(MapFragment.this.getActivity(), PhotoActivity.class);
			i.putExtra("photoId", markerIdForPhotoId.get(m.getId()));
			getActivity().startActivityForResult(i, HomeActivity.RESULT_CODE_MAP);

			return true;
		} else
			return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getMap() != null) {
			getMap().setMyLocationEnabled(true);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getMap() != null) {
			getMap().setMyLocationEnabled(false);
		}
	}

}
