package fr.mathis.selestat.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.mathis.selestat.app.App;

public class Photo {

	private int _id;
	private String _thumbnailUrl;
	private String _fullsizeUrl;
	private float _latitude;
	private float _longitude;
	private float _ratio;
	private String _color;
	private int _category;
	private int _order;

	public Photo() {
		setId(-1);
		setThumbnailUrl(null);
		setFullsizeUrl(null);
		setLatitude(-200.0f);
		setLongitude(-200.0f);
		setRatio(0.0f);
		setColor(null);
		setCategory(-1);
		setOrder(-1);

	}

	public Photo(int id, String thumbnailUrl, String fullsizeUrl, float latitude, float longitude, float ratio, String color, int category, int order) {
		setId(id);
		setThumbnailUrl(thumbnailUrl);
		setFullsizeUrl(fullsizeUrl);
		setLatitude(latitude);
		setLongitude(longitude);
		setRatio(ratio);
		setColor(color);
		setCategory(category);
		setOrder(order);
	}

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}

	public void setThumbnailUrl(String _thumbnailUrl) {
		this._thumbnailUrl = _thumbnailUrl;
	}

	public String getFullsizeUrl() {
		return _fullsizeUrl;
	}

	public void setFullsizeUrl(String _fullsizeUrl) {
		this._fullsizeUrl = _fullsizeUrl;
	}

	public float getLatitude() {
		return _latitude;
	}

	public void setLatitude(float _latitude) {
		this._latitude = _latitude;
	}

	public float getLongitude() {
		return _longitude;
	}

	public void setLongitude(float _longitude) {
		this._longitude = _longitude;
	}

	public float getRatio() {
		return _ratio;
	}

	public void setRatio(float _ratio) {
		this._ratio = _ratio;
	}

	public String getColor() {
		return _color;
	}

	public void setColor(String _color) {
		this._color = _color;
	}

	public int getCategory() {
		return _category;
	}

	public void setCategory(int _category) {
		this._category = _category;
	}

	public int getOrder() {
		return _order;
	}

	public void setOrder(int _order) {
		this._order = _order;
	}

	public static void deleteAll() {
		SQLiteDatabase db = App.getDb().getWritableDatabase();

		db.delete(DBOpenHelper.TABLE_PHOTO, null, null);

		db.close();
	}

	public static void addPhoto(Photo p) {
		SQLiteDatabase db = App.getDb().getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.KEY_PHOTO_ID, p.getId());
		values.put(DBOpenHelper.KEY_PHOTO_FULLSIZE_URL, p.getFullsizeUrl());
		values.put(DBOpenHelper.KEY_PHOTO_THUMBNAIL_URL, p.getThumbnailUrl());
		values.put(DBOpenHelper.KEY_PHOTO_LATITUDE, p.getLatitude());
		values.put(DBOpenHelper.KEY_PHOTO_LONGITUDE, p.getLongitude());
		values.put(DBOpenHelper.KEY_PHOTO_RATIO, p.getRatio());
		values.put(DBOpenHelper.KEY_PHOTO_COLOR, p.getColor());
		values.put(DBOpenHelper.KEY_PHOTO_CATEGORY, p.getCategory());
		values.put(DBOpenHelper.KEY_PHOTO_ORDER, p.getOrder());

		db.replace(DBOpenHelper.TABLE_PHOTO, null, values);
		db.close();
	}

	public static void deletePhoto(int photoId) {
		SQLiteDatabase db = App.getDb().getWritableDatabase();

		db.delete(DBOpenHelper.TABLE_PHOTO, DBOpenHelper.KEY_PHOTO_ID + "=?", new String[] { photoId + "" });

		db.close();
	}

	public static ArrayList<Photo> getAllPhotos(int categoryId) {
		ArrayList<Photo> photos = new ArrayList<Photo>();

		String where = "";
		String orderBy = "";

		if (categoryId > 0) {
			where = "WHERE " + DBOpenHelper.KEY_PHOTO_CATEGORY + " = " + categoryId;
		}

		if (categoryId > 0) {
			orderBy = "ORDER BY "+DBOpenHelper.KEY_PHOTO_ORDER;
		}
		else {
			orderBy = "ORDER BY RANDOM()";
		}

		String selectQuery = "SELECT  * FROM " + DBOpenHelper.TABLE_PHOTO + " " + where + " " + orderBy;

		SQLiteDatabase db = App.getDb().getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Photo p = new Photo(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getFloat(3), cursor.getFloat(4), cursor.getFloat(5), cursor.getString(6), Integer.parseInt(cursor.getString(7)), Integer.parseInt(cursor.getString(8)));
				photos.add(p);
			} while (cursor.moveToNext());
		}

		return photos;
	}

	public static Photo getPhoto(int id) {
		SQLiteDatabase db = App.getDb().getReadableDatabase();

		Cursor cursor = db.query(DBOpenHelper.TABLE_PHOTO, new String[] { DBOpenHelper.KEY_PHOTO_ID, DBOpenHelper.KEY_PHOTO_THUMBNAIL_URL, DBOpenHelper.KEY_PHOTO_FULLSIZE_URL, DBOpenHelper.KEY_PHOTO_LATITUDE, DBOpenHelper.KEY_PHOTO_LONGITUDE, DBOpenHelper.KEY_PHOTO_RATIO, DBOpenHelper.KEY_PHOTO_COLOR, DBOpenHelper.KEY_PHOTO_CATEGORY, DBOpenHelper.KEY_PHOTO_ORDER }, DBOpenHelper.KEY_PHOTO_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Photo p = new Photo(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getFloat(3), cursor.getFloat(4), cursor.getFloat(5), cursor.getString(6), Integer.parseInt(cursor.getString(7)), Integer.parseInt(cursor.getString(8)));

		return p;
	}
}
