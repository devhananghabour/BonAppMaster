package app.bonapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by admin on 11/21/16.
 */

public class AppSharedPrefs {

    private static AppSharedPrefs mInstance;
    private static Context mContext;
    private SharedPreferences sharedPref;

    private AppSharedPrefs(Context context) {
        mContext = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static AppSharedPrefs getInstance(Context context) {
        if (mInstance == null || mContext == null)
            mInstance = new AppSharedPrefs(context);
        return mInstance;
    }

    public enum PREF_KEY {
        IS_LOGIN("is_login"),USER_TYPE("user_type"),ACCESS_TOKEN("access_token"), USER_ID("id"), USER_NAME("username"),
        USER_IMAGE("userimage"),USER_EMAIL("useremail"), USER_MOBILE("mob_no"),IS_FIRST_TIME("is_first"), ADDRESS("address"), COUNTRY_NAME("country"),
        STATE_NAME("state"), CITY_NAME("city"),DEVICE_TOKEN("device_token"), SOCIAL_TYPE("social_type"), POSTAL_CODE("postal_code"),COUNTRY_ID("country_id"),
        CITY_ID("city_id"),STATE_ID("state_id"), ISO_CODE("iso_code");

        public final String KEY;

        PREF_KEY(String key) {
            this.KEY = key;
        }
    }

    public void putInt(PREF_KEY key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key.KEY, value);
        editor.commit();
    }

    public int getInt(PREF_KEY key, int defaultValue) {
        return sharedPref.getInt(key.KEY, defaultValue);
    }

    public void putLong(PREF_KEY key, long value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key.KEY, value);
        editor.commit();
    }

    public long getLong(PREF_KEY key, long defaultValue) {
        return sharedPref.getLong(key.KEY, defaultValue);
    }

    public void putString(PREF_KEY key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key.KEY, value);
        editor.commit();
    }

    public String getString(PREF_KEY key, String defaultValue) {
        return sharedPref.getString(key.KEY, defaultValue);
    }

    public void putBoolean(PREF_KEY key, boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key.KEY, value);
        editor.commit();
    }

    public boolean getBoolean(PREF_KEY key, boolean defaultValue) {
        return sharedPref.getBoolean(key.KEY, defaultValue);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return sharedPref.getString(key, defaultValue);
    }

    public void clearPrefs(Context context) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

}
