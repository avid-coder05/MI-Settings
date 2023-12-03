package com.android.settings.locale;

/* loaded from: classes.dex */
public class LocaleInfo {
    private String mCountryCode;
    private String mDisplayName;

    public LocaleInfo() {
    }

    public LocaleInfo(String str, String str2) {
        this.mCountryCode = str;
        this.mDisplayName = str2;
    }

    public String getCountryCode() {
        return this.mCountryCode;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }
}
