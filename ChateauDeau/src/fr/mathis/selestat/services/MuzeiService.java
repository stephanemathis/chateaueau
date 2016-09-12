package fr.mathis.selestat.services;

import java.util.ArrayList;
import java.util.Random;

import android.net.Uri;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

import fr.mathis.selestat.fragment.MuzeiSettingsFragments;
import fr.mathis.selestat.models.Category;
import fr.mathis.selestat.models.Photo;
import fr.mathis.selestat.tools.PrefManager;

public class MuzeiService extends MuzeiArtSource {

	static MuzeiService instance;

	public MuzeiService() {
		super(MuzeiService.class.toString());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		setUserCommands(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
	}

	protected void onUpdate(int reason) {

		Photo p = null;

		ArrayList<Integer> photoIds = new ArrayList<Integer>();
		String savedIds = PrefManager.GetString("muzei", this);
		String[] ids = savedIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] != null && ids[i].compareTo("") != 0) {
				photoIds.add(Integer.parseInt(ids[i]));
			}
		}

		Random r = new Random();

		if (photoIds.size() > 0) {
			p = Photo.getPhoto(photoIds.get(r.nextInt(photoIds.size())));
		} else {
			ArrayList<Photo> allPhotos = Photo.getAllPhotos(0);
			if (allPhotos.size() > 0)
				p = allPhotos.get(r.nextInt(allPhotos.size()));
		}

		if (p != null) {
			PrefManager.SaveInt("lastPhotoId", p.getId(), this);
			
			Category c = Category.getCategory(p.getCategory());
			publishArtwork(new Artwork.Builder().imageUri(Uri.parse(p.getFullsizeUrl())).title(c.getTitle()).build());
		}

		int interval = PrefManager.GetInt("refreshInterval", this);
		if (interval < MuzeiSettingsFragments.INTERVAL_NEVER)
			interval = MuzeiSettingsFragments.INTERVAL_ONE_DAY;
		
		if(interval == MuzeiSettingsFragments.INTERVAL_NEVER)
			;
		if(interval == MuzeiSettingsFragments.INTERVAL_ONE_HOUR)
			scheduleUpdate(System.currentTimeMillis() + 1000 * 60 * 60);
		if(interval == MuzeiSettingsFragments.INTERVAL_THEE_HOUR)
			scheduleUpdate(System.currentTimeMillis() + 1000 * 60 * 60 * 3);
		if(interval == MuzeiSettingsFragments.INTERVAL_SIX_HOUR)
			scheduleUpdate(System.currentTimeMillis() + 1000 * 60 * 60 * 6);
		if(interval == MuzeiSettingsFragments.INTERVAL_ONE_DAY)
			scheduleUpdate(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
		if(interval == MuzeiSettingsFragments.INTERVAL_THREE_DAY)
			scheduleUpdate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3);
	}

	public static MuzeiService getInstance() {
		return instance;
	}

	public void forceUpdate() {
		scheduleUpdate(System.currentTimeMillis() + 1000);
	}
}