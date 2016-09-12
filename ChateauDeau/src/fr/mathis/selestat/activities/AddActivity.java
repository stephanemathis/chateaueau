package fr.mathis.selestat.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import fr.mathis.selestat.R;
import fr.mathis.selestat.fragment.AddFragment;

public class AddActivity extends AppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pane);

		AddFragment newFragment = AddFragment.newInstance();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, newFragment);
		ft.commit();
	}

}
