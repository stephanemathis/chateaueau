package fr.mathis.selestat.app;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import fr.mathis.selestat.models.DBOpenHelper;

public class App extends Application {

	private static Context _context;
	private static DBOpenHelper _db;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		App._context = this;
		
		App._db = new DBOpenHelper(this);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.memoryCache(new WeakMemoryCache())
		        .build();		
		ImageLoader.getInstance().init(config);
	}

	public static Context getContext() {
		return _context;
	}
	
	public static DBOpenHelper getDb()
	{
		return _db;
	}
}
