package com.example.scorely.util;

import android.content.Context;
import android.content.SharedPreferences;

/** עטיפה ל-SharedPreferences: ערכת נושא, שפה, מיון אחרון ומשתמש מחובר. */
public class PrefsManager {

    private static final String FILE = "scorely_prefs";
    private final SharedPreferences prefs;

    public PrefsManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public boolean isDarkMode() { return prefs.getBoolean("dark", false); }
    public void setDarkMode(boolean v) { prefs.edit().putBoolean("dark", v).apply(); }

    public String getLanguage() { return prefs.getString("lang", "he"); }
    public void setLanguage(String l) { prefs.edit().putString("lang", l).apply(); }

    public String getLastSort() { return prefs.getString("sort", "title"); }
    public void setLastSort(String s) { prefs.edit().putString("sort", s).apply(); }

    public String getUserId() { return prefs.getString("uid", null); }
    public void setUserId(String uid) { prefs.edit().putString("uid", uid).apply(); }
}
