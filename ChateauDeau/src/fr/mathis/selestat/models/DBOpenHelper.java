package fr.mathis.selestat.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 6;
	private static final String DATABASE_NAME = "AppSelestat";	
	
	public static final String TABLE_PHOTO = "photo";

	public static final String KEY_PHOTO_ID = "id";
	public static final String KEY_PHOTO_THUMBNAIL_URL = "thumbnailUrl";
	public static final String KEY_PHOTO_FULLSIZE_URL = "fullsizeUrl";
	public static final String KEY_PHOTO_LATITUDE = "lat";
	public static final String KEY_PHOTO_LONGITUDE = "lng";
	public static final String KEY_PHOTO_RATIO = "ratio";
	public static final String KEY_PHOTO_COLOR= "color";
	public static final String KEY_PHOTO_CATEGORY= "categoryId";
	public static final String KEY_PHOTO_ORDER = "orderList";
	
	public static final String TABLE_CATEGORY = "categories";
	
	public static final String KEY_CATEGORY_ID = "id";
	public static final String KEY_CATEGORY_TITLE = "title";
	public static final String KEY_CATEGORY_COUNT = "count";

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PHOTO_TABLE = "CREATE TABLE " + TABLE_PHOTO + "(" + KEY_PHOTO_ID + " INTEGER PRIMARY KEY," + KEY_PHOTO_THUMBNAIL_URL + " TEXT," + KEY_PHOTO_FULLSIZE_URL + " TEXT," + KEY_PHOTO_LATITUDE + " REAL, " + KEY_PHOTO_LONGITUDE + " REAL, " + KEY_PHOTO_RATIO + " REAL, " + KEY_PHOTO_COLOR + " TEXT," + KEY_PHOTO_CATEGORY + " INTEGER," + KEY_PHOTO_ORDER + " INTEGER" + ")";
		db.execSQL(CREATE_PHOTO_TABLE);
		
		String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "(" + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY," + KEY_CATEGORY_TITLE + " TEXT," + KEY_CATEGORY_COUNT + " INTEGER" + ")";
		db.execSQL(CREATE_CATEGORY_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

		onCreate(db);
	}
}
