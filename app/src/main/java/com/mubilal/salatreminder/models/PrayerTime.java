package com.mubilal.salatreminder.models;

public class PrayerTime {
    private String prayerName;
    private String prayerTime;

    public PrayerTime(String prayerName, String prayerTime) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
    }

    public String getPrayerName() {
        return prayerName;
    }

    public String getPrayerTime() {
        return prayerTime;
    }

    public void setPrayerName(String value) {
        this.prayerName = value;
    }

    public void setPrayerTime(String value) {
        this.prayerTime = value;
    }
}