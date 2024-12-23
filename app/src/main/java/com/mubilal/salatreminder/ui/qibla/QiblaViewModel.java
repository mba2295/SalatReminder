package com.mubilal.salatreminder.ui.qibla;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QiblaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public QiblaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Qibla fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}