package com.septeni.polaris.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefs {

	public static final String DEFAULT_BLANK = "";
	public static final String SUID = "suid"; // Key to save "suid" value.
	public static final String SALES = "sales"; // Key to save "sales" value.
	public static final String VOLUME = "volume"; // Key to save "volume" value.
	public static final String PROFIT = "profit"; // Key to save "profit" value.
	public static final String OTHERS = "others"; // Key to save "others" value.
	public static final String TRACK_DAU = "track_dau"; // Key to save time when track DAU.
	public static final String MODE = "mode"; // Key to save test MODE.
	public static final String STEP = "step"; // Key to save test STEP.
	public static final String TYPE = "type"; // Key to save test TYPE.
	public static final int UNKNOWN_MODE = 0; // Unknown testing mode.
	public static final int AUTO_MODE = 1; // Auto mode.
	public static final int NORMAL_MODE = 2; // Normal mode.
	public static final int PREPARE_STEP = 0; // Prepare step.
	public static final int FIRST_STEP = 1; // First step.
	public static final int SECOND_STEP = 2; // Second step.
	public static final int THIRD_STEP = 3; // Third step.
	public static final int FOURTH_STEP = 4; // Fourth step.
	public static final int FIFTH_STEP = 5; // Fifth step.
	public static final int UNKNOWN_TYPE = 0; // Unknown testing type.
	public static final int CONVERSION_TESTING = 1; // Conversion testing.
	public static final int INSTALL_TESTING = 2; // Install testing.
	public static final int ONLINE_OFFLINE_TESTING = 3; // Online/Offline testing.
	public static final int DUPLICATION_TESTING = 4; // Duplication testing.
	public static final int STATE_ON = 1; // State is on.
	public static final int STATE_OFF = 0; // State is off.

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
	 * Save long value to shared preferences.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference
	 */
	public void save(String key, long value) {
		sharedPreferences.edit().putLong(key, value).commit();
	}

	/**
	 * Retrieve a long value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param _defValue
	 *            Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue
	 */
	public long get(String key, long _defValue) {
		return sharedPreferences.getLong(key, _defValue);
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

	/**
	 * Save time when track DAU in preferences.
	 * 
	 * @param timeInMillis
	 *            time in milliseconds to save.
	 */
	public void setTrackDAUTime(long timeInMillis) {
		save(TRACK_DAU, timeInMillis);
	}

	/**
	 * Get time when track DAU from preferences.
	 * 
	 * @return time in milliseconds when track DAU from preferences.
	 */
	public long getTrackDAUTime() {
		return get(TRACK_DAU, (long) 0);
	}

	/**
	 * Save testing mode to the preferences.
	 * 
	 * @param mode
	 *            mode to set.
	 */
	public void setMode(int mode) {
		save(MODE, mode);
	}

	/**
	 * Get testing mode from the preferences.
	 * 
	 * @return testing mode.
	 */
	public int getMode() {
		return get(MODE, UNKNOWN_MODE);
	}

	/**
	 * Save testing type to the preferences.
	 * 
	 * @param type
	 *            type to set.
	 */
	public void setType(int type) {
		save(TYPE, type);
	}

	/**
	 * Get testing type from the preferences.
	 * 
	 * @return testing type.
	 */
	public int getType() {
		return get(TYPE, UNKNOWN_TYPE);
	}

	/**
	 * Save testing step to the preferences.
	 * 
	 * @param step
	 *            step to set.
	 */
	public void setStep(int step) {
		save(STEP, step);
	}

	/**
	 * Get testing step from the preferences.
	 * 
	 * @return testing step.
	 */
	public int getStep() {
		return get(STEP, PREPARE_STEP);
	}
}