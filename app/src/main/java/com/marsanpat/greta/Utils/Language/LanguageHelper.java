package com.marsanpat.greta.Utils.Language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class LanguageHelper {
    private static final String KEY_USER_LANGUAGE = "language_preference";

    /**
     * Update the app language
     * @param language Language to switch to.
     */
    public static void updateLanguage(Context context, String language) {
        final Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration cfg = new Configuration(res.getConfiguration());
        cfg.locale = locale;
        res.updateConfiguration(cfg, res.getDisplayMetrics());
        Log.d("debug", "Language updated to: "+language);
    }

    /**
     * Store the language selected by the user.
     */
    public static void storeUserLanguage(Context context, String language) {
        context.getSharedPreferences(context.getPackageName()+"_preferences", MODE_PRIVATE)
                .edit()
                .putString(KEY_USER_LANGUAGE, language)
                .apply();
        Log.d("debug", "Language stored as "+language);
    }

    /**
     * @return The stored user language or null if not found.
     */
    public static String getUserLanguage(Context context) {
        String userLanguage = context.getSharedPreferences(context.getPackageName()+"_preferences", MODE_PRIVATE)
                .getString(KEY_USER_LANGUAGE, null);
        Log.d("d", "getUserLanguage: "+userLanguage);
        return userLanguage;
    }
}