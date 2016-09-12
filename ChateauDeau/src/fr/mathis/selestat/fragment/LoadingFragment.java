package fr.mathis.selestat.fragment;

import java.util.Calendar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import fr.mathis.selestat.R;
import fr.mathis.selestat.activities.HomeActivity;
import fr.mathis.selestat.models.Model;
import fr.mathis.selestat.views.CastleAnimationView;

public class LoadingFragment extends Fragment {

	private static int SPLASHSCREEN_DELAY = 2500;

	private boolean _launchAfterDelay = true;

	private ProgressBar _pbLoading;

	public static LoadingFragment newInstance() {
		LoadingFragment f = new LoadingFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_loading, container, false);

		SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
	    tintManager.setStatusBarTintEnabled(true);
	    tintManager.setTintColor(getActivity().getResources().getColor(R.color.accent_color));	    
		
		_pbLoading = (ProgressBar) v.findViewById(R.id.pb_loading_data);

		CastleAnimationView cavLogo = (CastleAnimationView) v.findViewById(R.id.cav_logo);
		cavLogo.setSvgResource(R.raw.logo);
		cavLogo.reveal(v, 0, 0);

		new LoadingDataTask().execute();

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		_launchAfterDelay = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		_launchAfterDelay = false;
	}

	class LoadingDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			long endTime = -1;
			try {
				Model.DownloadThenParseData();

				endTime = Calendar.getInstance().getTimeInMillis();

			} catch (Exception e) {
				e.printStackTrace();
				endTime = Calendar.getInstance().getTimeInMillis();
			}

			publishProgress();
			if (endTime - startTime < SPLASHSCREEN_DELAY) {
				try {
					Thread.sleep(SPLASHSCREEN_DELAY - (endTime - startTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

			_pbLoading.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (_launchAfterDelay) {
				Intent i = new Intent(LoadingFragment.this.getActivity(), HomeActivity.class);
				startActivity(i);
				getActivity().overridePendingTransition(0, 0);
				LoadingFragment.this.getActivity().finish();
				getActivity().overridePendingTransition(0, 0);
			}
		}
	}
}
