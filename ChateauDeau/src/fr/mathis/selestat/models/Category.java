package fr.mathis.selestat.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.mathis.selestat.app.App;

public class Category {

	private int _id;
	private String _title;
	private int _count;
	public ArrayList<Photo> photos;

	public Category() {
		_id = -1;
		_title = "";
		_count = 0;
	}

	public Category(int id, String title, int count) {
		_id = id;
		_title = title;
		_count = count;
	}

	public static void deleteAll() {
		SQLiteDatabase db = App.getDb().getWritableDatabase();

		db.delete(DBOpenHelper.TABLE_CATEGORY, null, null);

		db.close();
	}

	public static void addCategory(Category c) {
		SQLiteDatabase db = App.getDb().getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.KEY_CATEGORY_ID, c.getId());
		values.put(DBOpenHelper.KEY_CATEGORY_TITLE, c.getTitle());
		values.put(DBOpenHelper.KEY_CATEGORY_COUNT, c.getCount());

		db.replace(DBOpenHelper.TABLE_CATEGORY, null, values);
		db.close();
	}

	public static void deleteCategory(int categoryId) {
		SQLiteDatabase db = App.getDb().getWritableDatabase();

		db.delete(DBOpenHelper.TABLE_CATEGORY, DBOpenHelper.KEY_CATEGORY_ID + "=?", new String[] { categoryId + "" });

		db.close();
	}

	public static ArrayList<Category> getAllCategories() {
		ArrayList<Category> categories = new ArrayList<Category>();

		String selectQuery = "SELECT  * FROM " + DBOpenHelper.TABLE_CATEGORY;

		SQLiteDatabase db = App.getDb().getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Category c = new Category(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
				categories.add(c);
			} while (cursor.moveToNext());
		}

		return categories;
	}

	public static Category getCategory(int categoryId) {
		Category c = null;

		String selectQuery = "SELECT  * FROM " + DBOpenHelper.TABLE_CATEGORY + " WHERE id=" + categoryId;

		SQLiteDatabase db = App.getDb().getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				c = new Category(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
			} while (cursor.moveToNext());
		}

		return c;
	}

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String _title) {
		this._title = _title;
	}

	public int getCount() {
		return _count;
	}

	public void setCount(int _count) {
		this._count = _count;
	}
}
