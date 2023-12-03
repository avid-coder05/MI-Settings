package com.android.settings.cloud.push;

import java.util.Set;

/* loaded from: classes.dex */
public class ExistCompatibility {
    private String mMessage;
    private String mPackageName;
    private boolean mPrecise;
    private String mTicker;
    private String mTitle;
    private Set<Integer> mVersions;

    public String getMessage() {
        return this.mMessage;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getTicker() {
        return this.mTicker;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public Set<Integer> getVersions() {
        return this.mVersions;
    }

    public boolean isPrecise() {
        return this.mPrecise;
    }

    public void setMessage(String str) {
        this.mMessage = str;
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }

    public void setPrecise(boolean z) {
        this.mPrecise = z;
    }

    public void setTicker(String str) {
        this.mTicker = str;
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }

    public void setVersions(Set<Integer> set) {
        this.mVersions = set;
    }

    public String toString() {
        return "ExistCompatibility : PackageName = " + this.mPackageName + " Message = " + this.mMessage + " Title = " + this.mTitle + " Tricker = " + this.mTicker + " Precise = " + this.mPrecise + " Versions = " + this.mVersions.toString();
    }
}
