package com.android.settings.cloud.push;

import java.util.Set;

/* loaded from: classes.dex */
public class RunningCompatibility {
    private String mMessage;
    private String mPackageName;
    private boolean mPrecise;
    private Set<Integer> mVersions;

    public String getMessage() {
        return this.mMessage;
    }

    public String getPackageName() {
        return this.mPackageName;
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

    public void setVersions(Set<Integer> set) {
        this.mVersions = set;
    }

    public String toString() {
        return "RunningCompatibility : PackageName = " + this.mPackageName + " Message = " + this.mMessage + " Precise = " + this.mPrecise + " Versions = " + this.mVersions.toString();
    }
}
