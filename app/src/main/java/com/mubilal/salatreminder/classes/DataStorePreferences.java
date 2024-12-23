package com.mubilal.salatreminder.classes;

import android.content.Context;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * DataStorePreferences is a wrapper for interacting with Android's DataStore to persist
 * and retrieve user preferences using RxJava3 for reactive operations.
 */
public class DataStorePreferences {
    private final RxDataStore<Preferences> dataStore;
    private static DataStorePreferences instance;
    /**
    /**
     * Private constructor to initialize DataStore.
     * @param context The application context.
     */
    private DataStorePreferences(Context context) {
        // Initialize DataStore with the name "settings"
        dataStore = new RxPreferenceDataStoreBuilder(context, "settings").build();
    }

    /**
     * Get the singleton instance of DataStorePreferences.
     * @param context The application context.
     * @return The DataStorePreferences instance.
     */
    public static DataStorePreferences getInstance(Context context) {
        if (instance == null) {
            synchronized (DataStorePreferences.class) {
                if (instance == null) {
                    instance = new DataStorePreferences(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Reads an integer value from DataStore.
     *
     * @param key The key for the integer value.
     * @return A Flowable that emits the stored integer value.
     */
    public Flowable<Integer> readInt(String key) {
        Preferences.Key<Integer> integerKey = PreferencesKeys.intKey(key);
        return dataStore.data()
                .map(prefs -> prefs.get(integerKey) != null ? prefs.get(integerKey) : 0);
    }

    /**
     * Writes an integer value to DataStore.
     *
     * @param key The key to store the integer value under.
     * @param value The integer value to store.
     * @return A Completable that signals the operation completion.
     */
    public Single<Preferences> writeInt(String key, int value) {
        Preferences.Key<Integer> integerKey = PreferencesKeys.intKey(key);
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(integerKey, value);
            return Single.just(mutablePreferences);
        });
    }

    /**
     * Reads a string value from DataStore.
     *
     * @param key The key for the string value.
     * @return A Flowable that emits the stored string value.
     */
    public Flowable<String> readString(String key) {
        Preferences.Key<String> stringKey = PreferencesKeys.stringKey(key);
        return dataStore.data()
                .map(prefs -> prefs.get(stringKey) != null ? prefs.get(stringKey) : "");
    }

    /**
     * Writes a string value to DataStore.
     *
     * @param key The key to store the string value under.
     * @param value The string value to store.
     * @return A Completable that signals the operation completion.
     */
    public  Single<Preferences>  writeString(String key, String value) {
        Preferences.Key<String> stringKey = PreferencesKeys.stringKey(key);
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(stringKey, value);
            return Single.just(mutablePreferences);  // Corrected return type: Single<Preferences>
        });
    }

    /**
     * Reads a boolean value from DataStore.
     *
     * @param key The key for the boolean value.
     * @return A Flowable that emits the stored boolean value.
     */
    public Flowable<Boolean> readBoolean(String key) {
        Preferences.Key<Boolean> booleanKey = PreferencesKeys.booleanKey(key);
        return dataStore.data()
                .map(prefs -> prefs.get(booleanKey) != null ? prefs.get(booleanKey) : false);
    }

    /**
     * Writes a boolean value to DataStore.
     *
     * @param key The key to store the boolean value under.
     * @param value The boolean value to store.
     * @return A Completable that signals the operation completion.
     */
    public  Single<Preferences> writeBoolean(String key, boolean value) {
        Preferences.Key<Boolean> booleanKey = PreferencesKeys.booleanKey(key);
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(booleanKey, value);
            return Single.just(mutablePreferences);
        });
    }

    /**
     * Reads a double value from DataStore.
     *
     * @param key The key for the double value.
     * @return A Flowable that emits the stored double value.
     */
    public Flowable<Double> readDouble(String key) {
        Preferences.Key<Double> doubleKey = PreferencesKeys.doubleKey(key);
        return dataStore.data()
                .map(prefs -> prefs.get(doubleKey) != null ? prefs.get(doubleKey) : 0.0);
    }

    /**
     * Writes a double value to DataStore.
     *
     * @param key The key to store the double value under.
     * @param value The double value to store.
     * @return A Completable that signals the operation completion.
     */
    public Single<Preferences> writeDouble(String key, double value) {
        Preferences.Key<Double> doubleKey = PreferencesKeys.doubleKey(key);
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(doubleKey, value);
            return Single.just(mutablePreferences);  // No result needed, just a completion signal
        });
    }
}
