package com.septeni.polaris.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefs {

	public static final String DEFAULT_BLANK = "";
	public static final String SUID = "suid";
	public static final String SALES = "sales";
	public static final String VOLUME = "volume";
	public static final String PROFIT = "profit";
	public static final String OTHERS = "others";

	private static SharePrefs instance = new SharePrefs();
	private SharedPreferences sharedPreferences;

	public static SharePrefs getInstance() {
		return instance;
	}

	public void init(Context ctx) {
		if (sharedPreferences == null) {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		}
	}

	/**
	 * Clear shared preferences.
	 */
	public void clear() {
		sharedPreferences.edit().clear().commit();
	}

	/**
	 * Save string value to shared preferences.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference
	 */
	public void save(String key, String value) {
		sharedPreferences.edit().putString(key, value).commit();
	}

	/**
	 * Retrieve a String value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param _defValue
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue
	 */
	public String get(String key, String _defValue) {
		return sharedPreferences.getString(key, _defValue);
	}

	/**
	 * Save integer value to shared preferences.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference
	 */
	public void save(String key, int value) {
		sharedPreferences.edit().putInt(key, value).commit();
	}

	/**
	 * Retrieve a integer value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param _defValue
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue
	 */
	public int get(String key, int _defValue) {
		return sharedPreferences.getInt(key, _defValue);
	}

	/**
	 * Save boolean value to shared preferences.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference
	 */
	public void save(String key, boolean value) {
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	/**
	 * Retrieve a boolean value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param _defValue
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue
	 */
	public boolean get(String key, boolean _defValue) {
		return sharedPreferences.getBoolean(key, _defValue);
	}

	/**
	 * Save suid value to shared preferences.
	 * 
	 * @param suid
	 *            suid want to save.
	 */
	public void saveSUID(String suid) {
		save(SUID, suid);
	}

	/**
	 * Retrieve a suid value from the preferences.
	 * 
	 * @return suid if exists, or blank.
	 */
	public String getSUID() {
		return get(SUID, DEFAULT_BLANK);
	}

	/**
	 * Save sales value to shared preferences.
	 * 
	 * @param sales
	 *            sales want to save.
	 */
	public void saveSales(String sales) {
		save(SALES, sales);
	}

	/**
	 * Retrieve a sales value from the preferences.
	 * 
	 * @return sales if exists, or blank.
	 */
	public String getSales() {
		return get(SALES, DEFAULT_BLANK);
	}

	/**
	 * Save volume value to shared preferences.
	 * 
	 * @param volume
	 *            volume want to save.
	 */
	public void saveVolume(String volume) {
		save(VOLUME, volume);
	}

	/**
	 * Retrieve a volume value from the preferences.
	 * 
	 * @return volume if exists, or blank.
	 */
	public String getVolume() {
		return get(VOLUME, DEFAULT_BLANK);
	}

	/**
	 * Save profit value to shared preferences.
	 * 
	 * @param profit
	 *            profit want to save.
	 */
	public void saveProfit(String profit) {
		save(PROFIT, profit);
	}

	/**
	 * Retrieve a profit value from the preferences.
	 * 
	 * @return profit if exists, or blank.
	 */
	public String getProfit() {
		return get(PROFIT, DEFAULT_BLANK);
	}

	/**
	 * Save others value to shared preferences.
	 * 
	 * @param others
	 *            others want to save.
	 */
	public void saveOthers(String others) {
		save(OTHERS, others);
	}

	/**
	 * Retrieve a others value from the preferences.
	 * 
	 * @return others if exists, or blank.
	 */
	public String getOthers() {
		return get(OTHERS, DEFAULT_BLANK);
	}
}