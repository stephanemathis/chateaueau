package fr.mathis.selestat.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import fr.mathis.selestat.R;
import fr.mathis.selestat.fragment.LoadingFragment;

public class LoadingActivity extends AppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pane);

		LoadingFragment newFragment = LoadingFragment.newInstance();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, newFragment);
		ft.commit();
	}
}
