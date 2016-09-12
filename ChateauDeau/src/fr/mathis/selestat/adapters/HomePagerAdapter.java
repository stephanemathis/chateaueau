package fr.mathis.selestat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import fr.mathis.selestat.R;
import fr.mathis.selestat.app.App;

public class HomePagerAdapter extends FragmentPagerAdapter {

	private static int NB_PAGE = 2;
	private Fragment _f0;
	private Fragment _f1;

	public HomePagerAdapter(FragmentManager fm, Fragment f0, Fragment f1) {
		super(fm);
		_f0 = f0;
		_f1 = f1;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return _f0;
		case 1:
			return _f1;
		}
		return null;
	}

	@Override
	public int getCount() {
		return NB_PAGE;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return App.getContext().getString(R.string.tab_gallery);
		case 1:
			return App.getContext().getString(R.string.tab_map);
		}
		return null;
	}
}
