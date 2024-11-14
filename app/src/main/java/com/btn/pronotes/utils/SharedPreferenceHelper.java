package com.btn.pronotes.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    private static final String SHARED_PREFERENCE_NAME = "SharedPreference";
    private static final String COLOR_CHANGING_TILE = "color changing tile";
    private static final String SELECT_COLOR = "select color";
    private static final String WIDGET_ID = "widget id";
    private static final String KEY_AUTOSAVE_ENABLED = "autosave_enabled"; // Added this line

    private static SharedPreferenceHelper helperInstance = null;
    private final SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    // **Color Changing Tiles Preference Methods**

    public boolean isColorChangingTiles() {
        return sharedPreferences.getBoolean(COLOR_CHANGING_TILE, false);
    }

    public void setColorChangingTile(Boolean start) {
        sharedPreferences.edit().putBoolean(COLOR_CHANGING_TILE, start).apply();
    }

    // **Selected Color Preference Methods**

    public String getSelectedColor() {
        return sharedPreferences.getString(SELECT_COLOR, "");
    }

    public void setSelectedColor(String hexColor) {
        sharedPreferences.edit().putString(SELECT_COLOR, hexColor).apply();
    }

    // **Widget ID Methods**

    public int getWidgetID() {
        return sharedPreferences.getInt(WIDGET_ID, 0);
    }

    public void setWidgetId(int id) {
        sharedPreferences.edit().putInt(WIDGET_ID, id).apply();
    }

    // **Delete Preferences**

    public void deleteSharedPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // **Autosave Preference Methods**

    /**
     * Checks if autosave is enabled.
     *
     * @return true if autosave is enabled, false otherwise.
     */
    public boolean isAutosaveEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTOSAVE_ENABLED, true); // Default is true
    }

    /**
     * Sets the autosave preference.
     *
     * @param enabled true to enable autosave, false to disable.
     */
    public void setAutosaveEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AUTOSAVE_ENABLED, enabled);
        editor.apply();
    }
}
