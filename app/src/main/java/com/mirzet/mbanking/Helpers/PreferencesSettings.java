package com.mirzet.mbanking.Helpers;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesSettings {

    private static final String PREF_FILE = "settings_pref";

    public static void saveToPref(Context context, String str) {
        final SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("code", str);
        editor.apply();
    }
    public static void saveToPref(Context context,String prefName, String str) {
        final SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefName, str);
        editor.apply();
    }
    public static String getCode(Context context) {
        final SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        final String defaultValue = "";
        return sharedPref.getString("code", defaultValue);
    }
    public static String getFullName(Context context) {
        final SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        final String defaultValue = "mBanking";
        return sharedPref.getString("Full_Name", defaultValue);
    }
}
