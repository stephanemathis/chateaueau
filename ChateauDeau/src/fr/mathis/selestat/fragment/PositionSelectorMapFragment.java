package fr.mathis.selestat.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PositionSelectorMapFragment extends SupportMapFragment {

	private boolean _onReadyEventConsumed;

	private double _lat = 48.2586555d;
	private double _lng = 7.4537702d;

	public static PositionSelectorMapFragment getNewInstance() {
		PositionSelectorMapFragment f = new PositionSelectorMapFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
						init();
						_onReadyEventConsumed = true;
					}
				}
			});
		}
	}

	private void init() {
		getMap().getUiSettings().setZoomControlsEnabled(false);
		getMap().getUiSettings().setCompassEnabled(false);
		getMap().getUiSettings().setMyLocationButtonEnabled(false);
		getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_lat, _lng), 14));
		getMap().addMarker(new MarkerOptions().draggable(true).position(new LatLng(_lat, _lng)));

		getMap().setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng position) {
				_lat = position.latitude;
				_lng = position.longitude;

				getMap().clear();
				getMap().addMarker(new MarkerOptions().draggable(true).position(new LatLng(_lat, _lng)));
			}
		});

		getMap().setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker arg0) {
			}

			@Override
			public void onMarkerDragEnd(Marker m) {
				_lat = m.getPosition().latitude;
				_lng = m.getPosition().longitude;

			}

			@Override
			public void onMarkerDrag(Marker arg0) {
			}
		});

		getMap().setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng position) {
				_lat = position.latitude;
				_lng = position.longitude;

				getMap().clear();
				getMap().addMarker(new MarkerOptions().draggable(true).position(new LatLng(_lat, _lng)));
			}
		});
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

	public double getLat() {
		return _lat;
	}

	public double getLng() {
		return _lng;
	}
}
