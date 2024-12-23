package com.mubilal.salatreminder.utils;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;

public class SettingsConstants {
    // Keys for the Preferences
    // SharedPreferences keys
    public static final String TIME_FORMAT_KEY = "TimeFormat";
    public static final String CALC_METHOD_KEY = "CalMethod";
    public static final String JURI_METHOD_KEY = "JuriMethod";
    public static final String LATITUDE_METHOD_KEY = "latitudeMethod";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";

    // Default values
    public static final boolean DEFAULT_TIME_FORMAT = false;
    public static final String DEFAULT_CALC_METHOD = "0";
    public static final String DEFAULT_JURI_METHOD = "0";
    public static final String DEFAULT_LATITUDE_METHOD = "3";
    public static final String DEFAULT_LATITUDE = "0";
    public static final String DEFAULT_LONGITUDE = "0";
}
