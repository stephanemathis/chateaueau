package fr.mathis.selestat.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import fr.mathis.selestat.R;
import fr.mathis.selestat.fragment.MuzeiSettingsFragments;

public class MuzeiSettingsActivity extends AppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pane_toolbar);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		getSupportActionBar().setTitle(R.string.muzei_settings_title);
		getSupportActionBar().setIcon(android.R.color.transparent);

		MuzeiSettingsFragments newFragment = MuzeiSettingsFragments.newInstance();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, newFragment);
		ft.commit();
	}
}
