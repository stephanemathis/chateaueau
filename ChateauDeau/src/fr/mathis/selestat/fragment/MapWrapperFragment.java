package fr.mathis.selestat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.mathis.selestat.R;

public class MapWrapperFragment extends Fragment {

	MapFragment _mapFragment;

	public static MapWrapperFragment newInstance(int categoryId) {
		MapWrapperFragment f = new MapWrapperFragment();

		Bundle b = new Bundle();
		b.putInt("id", categoryId);
		f.setArguments(b);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_mapFragment = MapFragment.getNewInstance(getArguments().getInt("id"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_map_wrapper, container, false);

		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.add(R.id.map_container, _mapFragment);
		ft.commit();

		return v;
	}

	public MapFragment getMapFragment() {
		return _mapFragment;
	}

}
