package com.btn.pronotes.utils;


import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceHelper {

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private static final String SHARED_PREFERENCE_NAME = "SharedPreference";
    public static final String COLOR_CHANGING_TILE = "color changing tile";
    public static final String SELECT_COLOR = "select color";
    public static final String WIDGET_ID = "widget id";
    private static SharedPreferenceHelper helperInstance = null;
    private final SharedPreferences sharedPreferences;

    public boolean isColorChangingTiles() {
        return sharedPreferences.getBoolean(COLOR_CHANGING_TILE, false);
    }

    public void setColorChangingTile(Boolean start) {
        sharedPreferences.edit().putBoolean(COLOR_CHANGING_TILE, start).apply();
    }
    public String getSelectedColor() {
        return sharedPreferences.getString(SELECT_COLOR,"");
    }

    public void setSelectedColor(String hexColor) {
        sharedPreferences.edit().putString(SELECT_COLOR, hexColor).apply();
    }

    public int getWidgetID() {
        return sharedPreferences.getInt(WIDGET_ID,0);
    }

    public void setWidgetId(int id) {
        sharedPreferences.edit().putInt(WIDGET_ID, id).apply();
    }

    public void deleteSharedPreference() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}