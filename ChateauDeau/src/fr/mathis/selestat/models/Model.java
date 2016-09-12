package fr.mathis.selestat.models;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import fr.mathis.selestat.app.App;
import fr.mathis.selestat.tools.PrefManager;
import fr.mathis.selestat.tools.Tools;

public class Model {

	@SuppressLint("UseSparseArrays")
	public static void DownloadThenParseData() throws IOException, JSONException {
		String jsonData = Tools.downloadJson("http://erudite-harbor-719.appspot.com/img/data.json");
		JSONObject data = new JSONObject(jsonData);

		int version = (Integer) data.get("update_version");

		if (version > PrefManager.GetInt("update_version", App.getContext())) {
			HashMap<Integer, Integer> countForId = new HashMap<Integer, Integer>();

			JSONArray photos = data.getJSONArray("photos");

			int photosCount = photos.length();
			for (int i = 0; i < photosCount; i++) {
				JSONObject jsonPhoto = photos.getJSONObject(i);

				int categoryId = jsonPhoto.getInt("category");
				if (!countForId.containsKey(categoryId))
					countForId.put(categoryId, 0);

				countForId.put(categoryId, countForId.get(categoryId) + 1);

				Photo.addPhoto(new Photo(jsonPhoto.getInt("id"), jsonPhoto.getString("thumbnailUrl"), jsonPhoto.getString("fullsizeUrl"), Float.parseFloat(jsonPhoto.getString("lat")), Float.parseFloat(jsonPhoto.getString("lng")), Float.parseFloat(jsonPhoto.getString("height")) / Float.parseFloat(jsonPhoto.getString("width")), jsonPhoto.getString("color"), jsonPhoto.getInt("category"), jsonPhoto.getInt("order")));
			}

			countForId.put(0, photosCount);
			
			JSONArray deleted = data.getJSONArray("deleted_photos");
			for (int i = 0; i < deleted.length(); i++) {
				int id = (Integer) deleted.get(i);
				Photo.deletePhoto(id);
			}

			Category.deleteAll();
			JSONArray categories = data.getJSONArray("categories");

			int categoriesCount = categories.length();
			for(int i = 0 ; i < categoriesCount ; i++)
			{
				JSONObject jsonCategory = categories.getJSONObject(i);
				
				int categoryId = jsonCategory.getInt("id");
				if (!countForId.containsKey(categoryId))
					countForId.put(categoryId, 0);
				
				Category.addCategory(new Category(categoryId, jsonCategory.getString("title"), countForId.get(categoryId)));
			}

			PrefManager.SaveInt("update_version", version, App.getContext());
		}
	}
}
