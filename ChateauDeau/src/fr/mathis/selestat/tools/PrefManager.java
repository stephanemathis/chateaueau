package fr.mathis.selestat.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefManager {
	public static void SaveBool(String name, boolean value, Context c) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	public static boolean GetBool(String name, Context c) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
		return settings.getBoolean(name, false);
	}

	public static void SaveInt(String name, int value, Context c) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	public static int GetInt(String name, Context c) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
		return settings.getInt(name, -1);
	}
	
	public static void SaveString(String name, String value, Context c) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(name, value);
		editor.commit();
	}

	public static String GetString(String name, Context c) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(c);
		return settings.getString(name, "");
	}
}
