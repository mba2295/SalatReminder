package com.mubilal.salatreminder.classes;

import android.content.Context;
import android.util.Log;

import com.mubilal.salatreminder.utils.SettingsConstants;
import com.mubilal.salatreminder.models.PrayerTime;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PrayerTimeManager {

    private final DataStorePreferences dataStorePreferences;
    private @NonNull Disposable subscription; // Field to store the Disposable


    public PrayerTimeManager(Context context) {
        this.dataStorePreferences =  DataStorePreferences.getInstance(context);
    }

    public void calculatePrayerTimes(Calendar calendar, PrayerTimesCallback callback) {
        // Initialize PreferencesData only once
        PreferencesData preferencesData = new PreferencesData();
        List<PrayerTime> prayerTimes = new ArrayList<>();

        // Fetch the first preference (time format) and handle the rest sequentially
        subscription = dataStorePreferences.readBoolean(SettingsConstants.TIME_FORMAT_KEY)
                .subscribeOn(Schedulers.io()) // Running on IO thread
                .observeOn(AndroidSchedulers.mainThread()) // Observing on the main thread for UI updates
                .flatMap(timeFormat -> {
                    preferencesData.timeFormat = timeFormat;
                    return dataStorePreferences.readString(SettingsConstants.CALC_METHOD_KEY)
                            .map(calcMethod -> {
                                preferencesData.calcMethod = calcMethod;
                                return preferencesData;
                            });
                })
                .flatMap(updatedPreferencesData -> {
                    return dataStorePreferences.readString(SettingsConstants.JURI_METHOD_KEY)
                            .map(juriMethod -> {
                                updatedPreferencesData.juriMethod = juriMethod;
                                return updatedPreferencesData;
                            });
                })
                .flatMap(updatedPreferencesData -> {
                    return dataStorePreferences.readString(SettingsConstants.LATITUDE_METHOD_KEY)
                            .map(latitudeMethod -> {
                                updatedPreferencesData.latitudeMethod = latitudeMethod;
                                return updatedPreferencesData;
                            });
                })
                .flatMap(updatedPreferencesData -> {
                    return dataStorePreferences.readDouble(SettingsConstants.LATITUDE_KEY)
                            .map(latitude -> {
                                updatedPreferencesData.latitude = latitude;
                                return updatedPreferencesData;
                            });
                })
                .flatMap(updatedPreferencesData -> {
                    return dataStorePreferences.readDouble(SettingsConstants.LONGITUDE_KEY)
                            .map(longitude -> {
                                updatedPreferencesData.longitude = longitude;
                                return updatedPreferencesData;
                            });
                })
                .subscribe(updatedPreferencesData -> {
                    Log.d("LatLONG at PrayerTimeManager", updatedPreferencesData.latitude + " latitude " + updatedPreferencesData.longitude + " Longitude");

                    double timezone = (double) (Calendar.getInstance().getTimeZone().getOffset(Calendar.getInstance().getTimeInMillis())) / (1000 * 60 * 60);
                    PrayerCalculator prayers = new PrayerCalculator();

                    // Set time format
                    if (!updatedPreferencesData.timeFormat)
                        prayers.setTimeFormat(prayers.Time12);
                    else
                        prayers.setTimeFormat(prayers.Time24);

                    // Set calculation method
                    setCalculationMethod(prayers, updatedPreferencesData.calcMethod);

                    // Set juristic method
                    setJuristicMethod(prayers, updatedPreferencesData.juriMethod);

                    // Set latitude method
                    setLatitudeMethod(prayers, updatedPreferencesData.latitudeMethod);

                    // Tune prayer calculation offsets
                    int[] offsets = {0, 0, 0, 0, 0, 0, 0};
                    prayers.tune(offsets);

                    // Get the prayer times
                    ArrayList<String> calculatedPrayerTimes = prayers.getPrayerTimes(calendar, updatedPreferencesData.latitude, updatedPreferencesData.longitude, timezone);
                    ArrayList<String> prayerNames = prayers.getTimeNames();

                    // Ensure both lists have the same size
                    if (calculatedPrayerTimes.size() == prayerNames.size()) {
                        // Populate prayerTimes list
                        for (int i = 0; i < prayerNames.size(); i++) {
                            String prayerName = prayerNames.get(i);
                            String prayerTime = calculatedPrayerTimes.get(i);
                            PrayerTime prayerTimeObject = new PrayerTime(prayerName, prayerTime);
                            prayerTimes.add(prayerTimeObject);
                        }

                        // Notify the callback with the result
                        callback.onPrayerTimesCalculated(prayerTimes);
                    } else {
                        // Handle error if lists are of different sizes
                        System.out.println("Error: Prayer names and times lists are of different sizes.");
                        callback.onError("Prayer names and times lists are of different sizes.");
                    }

                }, throwable -> {
                    // Handle any errors
                    callback.onError(throwable.getMessage());
                });
    }


    public void dispose() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    public interface PrayerTimesCallback {
        void onPrayerTimesCalculated(List<PrayerTime> prayerTimes);
        void onError(String error);
    }

    private void setCalculationMethod(PrayerCalculator prayers, String calcMethod) {
        switch (calcMethod) {
            case "0":
                prayers.setCalcMethod(prayers.Karachi);
                break;
            case "1":
                prayers.setCalcMethod(prayers.ISNA);
                break;
            case "2":
                prayers.setCalcMethod(prayers.MWL);
                break;
            case "3":
                prayers.setCalcMethod(prayers.Makkah);
                break;
            case "4":
                prayers.setCalcMethod(prayers.Jafari);
                break;
            case "5":
                prayers.setCalcMethod(prayers.Egypt);
                break;
            case "6":
                prayers.setCalcMethod(prayers.Tehran);
                break;
        }
    }

    private void setJuristicMethod(PrayerCalculator prayers, String juriMethod) {
        switch (juriMethod) {
            case "0":
                prayers.setAsrJuristic(prayers.Shafii);
                break;
            case "1":
                prayers.setAsrJuristic(prayers.Hanafi);
                break;
        }
    }

    private void setLatitudeMethod(PrayerCalculator prayers, String latitudeMethod) {
        switch (latitudeMethod) {
            case "0":
                prayers.setAdjustHighLats(prayers.None);
                break;
            case "1":
                prayers.setAdjustHighLats(prayers.MidNight);
                break;
            case "2":
                prayers.setAdjustHighLats(prayers.OneSeventh);
                break;
            case "3":
                prayers.setAdjustHighLats(prayers.AngleBased);
                break;
        }
    }


    // Data class to hold all preferences in one object
    private static class PreferencesData {
        boolean timeFormat;
        String calcMethod;
        String juriMethod;
        String latitudeMethod;
        double latitude;
        double longitude;

        PreferencesData() {
        }

    }
}
