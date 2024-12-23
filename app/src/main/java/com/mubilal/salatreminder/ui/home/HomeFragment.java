package com.mubilal.salatreminder.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.mubilal.salatreminder.R;
import com.mubilal.salatreminder.adapters.PrayerTimeAdapter;
import com.mubilal.salatreminder.classes.DataStorePreferences;
import com.mubilal.salatreminder.classes.PrayerCalculator;
import com.mubilal.salatreminder.classes.PrayerTimeManager;
import com.mubilal.salatreminder.models.PrayerTime;
import com.mubilal.salatreminder.utils.SettingsConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment implements PrayerTimeManager.PrayerTimesCallback {
    private RecyclerView prayerRecyclerView;
    private PrayerTimeAdapter prayerAdapter;
    private List<PrayerTime> prayerTimes;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double latitude = 0;
    private double longitude = 0;
    private PrayerTimeManager prayerTimeManager;

    private DataStorePreferences dataStorePreferences;
    Calendar calendar = Calendar.getInstance();

    // Declare the ActivityResultLauncher to handle the permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        // Permission granted, start location updates
                        startLocationUpdates();
                    } else {
                        // Permission denied, show a message to the user
                        Toast.makeText(requireContext(), R.string.location_permission_required, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        dataStorePreferences = DataStorePreferences.getInstance(requireContext());
        // Initialize location callback (this will handle the location updates)
        locationCallback = new LocationCallback() {
            @SuppressLint("CheckResult")
            @Override
            public void onLocationResult(@NonNull com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    dataStorePreferences.writeDouble(SettingsConstants.LATITUDE_KEY, latitude)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(value -> {
                                // This is the value that was written (true in this case)
                                Toast.makeText(requireActivity(), "Latitude value written: " + value, Toast.LENGTH_SHORT).show();
                                // Example usage: Reading data
                            }, throwable -> {
                                // Handle error
                                Toast.makeText(requireActivity(), "Error writing Latitude", Toast.LENGTH_SHORT).show();
                            });

                    dataStorePreferences.writeDouble(SettingsConstants.LONGITUDE_KEY, longitude)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(value -> {
                                // This is the value that was written (true in this case)
                                Toast.makeText(requireActivity(), "Longitude value written: " + value, Toast.LENGTH_SHORT).show();
                                // Example usage: Reading data
                            }, throwable -> {
                                // Handle error
                                Toast.makeText(requireActivity(), "Error writing Longitude", Toast.LENGTH_SHORT).show();
                            });

                    // Show location in a Toast (or handle it as needed)
                    Toast.makeText(requireContext(), "New Location: " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();
                    // Call getTime() to update prayer times after receiving location
                    getTime();
                }
            }
        };

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        prayerRecyclerView = rootView.findViewById(R.id.prayerRecyclerView);
        prayerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Prepare sample data (initially empty or default values)
        prayerTimes = new ArrayList<>();
        prayerTimes.add(new PrayerTime(getString(R.string.fajr), ""));
        prayerTimes.add(new PrayerTime(getString(R.string.sunrise), ""));
        prayerTimes.add(new PrayerTime(getString(R.string.dhuhr), ""));
        prayerTimes.add(new PrayerTime(getString(R.string.asr), ""));
        prayerTimes.add(new PrayerTime(getString(R.string.sunset), ""));
        prayerTimes.add(new PrayerTime(getString(R.string.maghrib), ""));
        prayerTimes.add(new PrayerTime(getString(R.string.isha), ""));


        // Set the adapter for RecyclerView
        prayerAdapter = new PrayerTimeAdapter(prayerTimes);
        prayerRecyclerView.setAdapter(prayerAdapter);

        // Check for location permissions and request them if not granted
        checkLocationPermission();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove location updates when the fragment is paused to conserve battery
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // Create a LocationRequest to specify how often to receive updates
    private LocationRequest createLocationRequest() {
        return new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMaxUpdates(1)
                //.setMinUpdateIntervalMillis(5000)
                .setWaitForAccurateLocation(false)
                .build();
    }

    // Check for location permissions and request them if not granted
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission using the ActivityResultLauncher
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // Permission is granted, start location updates
            startLocationUpdates();
        }
    }

    // Start location updates once permissions are granted
    private void startLocationUpdates() {
        // Create a LocationRequest object
        locationRequest = createLocationRequest();

        // Start receiving location updates
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (prayerTimeManager != null)
            prayerTimeManager.dispose();
    }

    public void getTime() {
        prayerTimeManager = new PrayerTimeManager(requireActivity().getBaseContext());

        // Pass this fragment as the callback
        prayerTimeManager.calculatePrayerTimes(calendar, this);
    }

    @Override
    public void onPrayerTimesCalculated(List<PrayerTime> prayerTimes) {
        // Update the prayerTimes list and notify the adapter
        requireActivity().runOnUiThread(() -> {
            prayerAdapter.updatePrayerTimes(prayerTimes);
        });
    }
    private void runOnMainThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    @Override
    public void onError(String error) {
        // Handle any errors (e.g., show a toast or log the error)
        Log.e("PrayerTimeManager", error);
        runOnMainThread(() -> {
            Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show();
        });
    }
}
