package fr.mathis.selestat.fragment;

import java.io.File;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import fr.mathis.selestat.R;
import fr.mathis.selestat.adapters.AddRecyclerAdapter;
import fr.mathis.selestat.models.Photo;
import fr.mathis.selestat.tools.Tools;

public class AddFragment extends Fragment {

	public static int RESULT_PHOTO = 0;
	public static int MENU_MAP = 0;

	TextView _tvAddPhoto;
	TextView _tvAddGallery;
	View _revealRv;

	RecyclerView _rv;
	AddRecyclerAdapter _adapter;

	PositionSelectorMapFragment _mapFragment;

	ArrayList<Photo> _newPhotos;

	public static AddFragment newInstance() {
		AddFragment f = new AddFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		_newPhotos = new ArrayList<Photo>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_add, container, false);

		((ActionBarActivity) getActivity()).setSupportActionBar((Toolbar) v.findViewById(R.id.toolbar));
		((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("");
		((ActionBarActivity) getActivity()).getSupportActionBar().setIcon(android.R.color.transparent);

		_mapFragment = PositionSelectorMapFragment.getNewInstance();
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.add(R.id.fl_map_container, _mapFragment);
		ft.commit();

		_tvAddPhoto = (TextView) v.findViewById(R.id.tv_add_photo);
		_tvAddGallery = (TextView) v.findViewById(R.id.tv_add_gallery);
		_revealRv = v.findViewById(R.id.v_mask_rv);

		_rv = (RecyclerView) v.findViewById(R.id.rv_add_gallery);
		_rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		_rv.setItemAnimator(new DefaultItemAnimator());
		_rv.setHasFixedSize(false);
		_adapter = new AddRecyclerAdapter(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		_rv.setAdapter(_adapter);

		_tvAddPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		_tvAddGallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_PHOTO);
			}
		});

		/*
		 * tvSend.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); emailIntent.setType("plain/text"); emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "mathis.steph@gmail.com" }); emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, AddFragment.this.getString(R.string.add_mail_subject)); if (uriPhoto != null) { emailIntent.putExtra(Intent.EXTRA_STREAM, uriPhoto); } emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, AddFragment.this.getString(R.string.add_mail_message).replace("%1", "" + _mapFragment.getLat()).replace("%2", "" + _mapFragment.getLng())); AddFragment.this.startActivity(Intent.createChooser(emailIntent, AddFragment.this.getString(R.string.add_mail_chooser)));
		 * 
		 * } });
		 */

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItemCompat.setShowAsAction(menu.add(0, MENU_MAP, 0, R.string.add_menu_map_type), MenuItemCompat.SHOW_AS_ACTION_NEVER);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_MAP) {
			if (_mapFragment.getMap().getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
				_mapFragment.getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
			} else {
				_mapFragment.getMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}
			return true;
		}

		return super.onContextItemSelected(item);
	}

	@SuppressLint("NewApi")
	private void addNewPhoto(String path) {

		_newPhotos.add(new Photo(-1, path, path, 0.0f, 0.0f, getImageFileRatio(path), null, 0, 0));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Animator anim = ViewAnimationUtils.createCircularReveal(_revealRv, 0, 0, 0, Tools.getViewDiagonal(_revealRv));
			anim.setDuration(400);
			_revealRv.setVisibility(View.VISIBLE);
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);

					_revealRv.setVisibility(View.GONE);

					_adapter.add(_newPhotos.get(_newPhotos.size() - 1));

					Animator animPager = ViewAnimationUtils.createCircularReveal(_rv, 0, 0, 0, Tools.getViewDiagonal(_rv));
					animPager.setDuration(400);
					animPager.start();
				}
			});

			anim.start();
		} else {
			_adapter.add(_newPhotos.get(_newPhotos.size() - 1));
		}
	}

	private float getImageFileRatio(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;

		return imageHeight / imageWidth;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_PHOTO && data != null && data.getData() != null) {
			Uri _uri = data.getData();
			Cursor cursor = getActivity().getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
			cursor.moveToFirst();

			String imageFilePath = cursor.getString(0);

			try {
				if (new File(imageFilePath).exists()) {
					addNewPhoto(imageFilePath);
				} else {
					Toast.makeText(getActivity(), R.string.add_error, Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				Toast.makeText(getActivity(), R.string.add_error, Toast.LENGTH_LONG).show();
			}
			cursor.close();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
