package com.mubilal.salatreminder.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.mubilal.salatreminder.R;
import com.mubilal.salatreminder.classes.DataStorePreferences;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsFragment extends Fragment {

    private DataStorePreferences dataStorePreferences;

    private SwitchCompat switch24hFormat;
    private Spinner spinnerCalculationMethod;
    private Spinner spinnerJuristicMethod;
    private Spinner spinnerLatitudeAdjustment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize DataStorePreferences instance
        dataStorePreferences = DataStorePreferences.getInstance(requireActivity().getBaseContext());

        // Initialize views
        switch24hFormat = view.findViewById(R.id.switch_24h_format);
        spinnerCalculationMethod = view.findViewById(R.id.spinner_calculation_method);
        spinnerJuristicMethod = view.findViewById(R.id.spinner_juristic_method);
        spinnerLatitudeAdjustment = view.findViewById(R.id.spinner_latitude_adjustment);

        // Populate spinners first
        //populateSpinners();

        // Read preferences from DataStore and update UI
        readPreferences();

        // Set listeners
        setListeners();

        return view;
    }

    // Method to read preferences from DataStore
    private void readPreferences() {
        // Read 24H format (boolean)
        dataStorePreferences.readBoolean("pref_24h_format")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(is24hFormat -> {
                    switch24hFormat.setChecked(is24hFormat);
                },
                throwable -> {
                    // onError (error handling)
                    Log.e("SettingsFragment", "Error saving preference", throwable);
                    Toast.makeText(getContext(), "Failed to save preference", Toast.LENGTH_SHORT).show();
                });

        // Read Calculation Method (String)
        dataStorePreferences.readString("pref_calculation_method")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(calculationMethod -> {
                            // Ensure the adapter is set before trying to select the item
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerCalculationMethod.getAdapter();

                            // If the adapter is null, initialize it (this is the first time the adapter is created)
                            if (adapter == null) {
                                adapter = ArrayAdapter.createFromResource(getContext(),
                                        R.array.calculation_methods, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCalculationMethod.setAdapter(adapter);
                            }

                            // Get the position of the stored value
                            int position = adapter.getPosition(calculationMethod);

                            // Only set the selection if the position is valid
                            if (position >= 0) {
                                spinnerCalculationMethod.setSelection(position);
                            }
                        },
                        throwable -> {
                            // Handle errors in reading preferences
                            Log.e("SettingsFragment", "Error reading preference", throwable);
                            Toast.makeText(getContext(), "Failed to load preferences", Toast.LENGTH_SHORT).show();
                        });

        // Read Juristic Method (String)
        dataStorePreferences.readString("pref_juristic_method")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(juristicMethod -> {
                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerJuristicMethod.getAdapter();

                            // If the adapter is null, initialize it (this is the first time the adapter is created)
                            if (adapter == null) {
                                adapter = ArrayAdapter.createFromResource(getContext(),
                                        R.array.juristic_methods, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerJuristicMethod.setAdapter(adapter);
                            }

                            // Get the position of the stored value
                            int position = adapter.getPosition(juristicMethod);

                            // Only set the selection if the position is valid
                            if (position >= 0) {
                                spinnerJuristicMethod.setSelection(position);
                            }
                },
                throwable -> {
                    // onError (error handling)
                    Log.e("SettingsFragment", "Error saving preference", throwable);
                    Toast.makeText(getContext(), "Failed to save preference", Toast.LENGTH_SHORT).show();
                });

        // Read Latitude Adjustment (String)
        dataStorePreferences.readString("pref_latitude_adjustment")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(latitudeAdjustment -> {
                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerLatitudeAdjustment.getAdapter();
                            // If the adapter is null, initialize it (this is the first time the adapter is created)
                            if (adapter == null) {
                                adapter = ArrayAdapter.createFromResource(getContext(),
                                        R.array.latitude_adjustments, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerLatitudeAdjustment.setAdapter(adapter);
                            }

                            // Get the position of the stored value
                            int position = adapter.getPosition(latitudeAdjustment);

                            // Only set the selection if the position is valid
                            if (position >= 0) {
                                spinnerLatitudeAdjustment.setSelection(position);
                            }
                },
                throwable -> {
                    // onError (error handling)
                    Log.e("SettingsFragment", "Error saving preference", throwable);
                    Toast.makeText(getContext(), "Failed to save preference", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to set listeners on the UI elements (Switch and Spinners)
    private void setListeners() {
        // Listener for 24H Format switch
        switch24hFormat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataStorePreferences.writeBoolean("pref_24h_format", isChecked)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((value) -> {
                        String message = isChecked ? "24H Format Enabled" : "24H Format Disabled";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    },
                    throwable -> {
                        // onError (error handling)
                        Log.e("SettingsFragment", "Error saving preference", throwable);
                        Toast.makeText(getContext(), "Failed to save preference", Toast.LENGTH_SHORT).show();
                    });
        });

        // Listener for Calculation Method spinner
        spinnerCalculationMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMethod = parent.getItemAtPosition(position).toString();
                dataStorePreferences.writeString("pref_calculation_method", selectedMethod)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((value) -> {
                            Toast.makeText(getContext(), "Selected: " + selectedMethod, Toast.LENGTH_SHORT).show();
                        },
                        throwable -> {
                            // onError (error handling)
                            Log.e("SettingsFragment", "Error saving preference", throwable);
                            Toast.makeText(getContext(), "Failed to save preference", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener for Juristic Method spinner
        spinnerJuristicMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMethod = parent.getItemAtPosition(position).toString();
                dataStorePreferences.writeString("pref_juristic_method", selectedMethod)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((value) -> {
                            Toast.makeText(getContext(), "Selected: " + selectedMethod, Toast.LENGTH_SHORT).show();
                        },
                        throwable -> {
                            // onError (error handling)
                            Log.e("SettingsFragment", "Error saving preference", throwable);
                            Toast.makeText(getContext(), "Failed to save preference", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener for Latitude Adjustment spinner
        spinnerLatitudeAdjustment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAdjustment = parent.getItemAtPosition(position).toString();
                dataStorePreferences.writeString("pref_latitude_adjustment", selectedAdjustment)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((value) -> {
                            Toast.makeText(getContext(), "Selected: " + selectedAdjustment, Toast.LENGTH_SHORT).show();
                        },
                        throwable -> {
                            // onError (error handling)
                            Log.e("SettingsFragment", "Error saving preference", throwable);
                            Toast.makeText(getContext(), "Failed to save preference", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
