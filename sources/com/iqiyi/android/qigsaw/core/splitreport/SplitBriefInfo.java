package com.iqiyi.android.qigsaw.core.splitreport;

import androidx.annotation.Keep;

@Keep
/* loaded from: classes2.dex */
public class SplitBriefInfo {
    public static final int ALREADY_INSTALLED = 2;
    public static final int FIRST_INSTALLED = 1;
    public static final int UNKNOWN = 0;
    public final boolean builtIn;
    public final String splitName;
    public final String version;
    private long timeCost = -1;
    private int installFlag = 0;

    public SplitBriefInfo(String str, String str2, boolean z) {
        this.splitName = str;
        this.version = str2;
        this.builtIn = z;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SplitBriefInfo) {
            SplitBriefInfo splitBriefInfo = (SplitBriefInfo) obj;
            if (this.splitName.equals(splitBriefInfo.splitName) && this.version.equals(splitBriefInfo.version) && this.builtIn == splitBriefInfo.builtIn) {
                return true;
            }
            return super.equals(obj);
        }
        return false;
    }

    public int getInstallFlag() {
        return this.installFlag;
    }

    public long getTimeCost() {
        return this.timeCost;
    }

    public SplitBriefInfo setInstallFlag(int i) {
        this.installFlag = i;
        return this;
    }

    public SplitBriefInfo setTimeCost(long j) {
        this.timeCost = j;
        return this;
    }

    public String toString() {
        return "{\"splitName\":\"" + this.splitName + "\",\"version\":\"" + this.version + "\",\"builtIn\":" + this.builtIn + "}";
    }
}
