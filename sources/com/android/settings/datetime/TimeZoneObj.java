package com.android.settings.datetime;

/* loaded from: classes.dex */
public class TimeZoneObj {
    private String mCityName;
    private String mGmtName;
    private String mID;
    private int mOffset;

    public TimeZoneObj(String str, String str2, String str3, int i) {
        this.mCityName = str;
        this.mGmtName = str2;
        this.mID = str3;
        this.mOffset = i;
    }

    public String getCityName() {
        return this.mCityName;
    }

    public String getGmtName() {
        return this.mGmtName;
    }

    public String getID() {
        return this.mID;
    }

    public int getOffset() {
        return this.mOffset;
    }

    public String toString() {
        return " timezoneID = " + this.mID + " cityName = " + this.mCityName + " gmtName = " + this.mGmtName + " offset = " + this.mOffset;
    }
}
