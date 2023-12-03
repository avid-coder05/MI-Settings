package com.android.settings.display;

import com.miui.darkmode.DarkModeAppDetailInfo;

/* loaded from: classes.dex */
public class DarkModeAppInfo {
    private boolean enabled;
    private String label;
    private long lastTimeUsed;
    private String pkgName;

    public DarkModeAppInfo(DarkModeAppDetailInfo darkModeAppDetailInfo) {
        this.pkgName = darkModeAppDetailInfo.getPkgName();
        this.label = darkModeAppDetailInfo.getLabel();
        this.enabled = darkModeAppDetailInfo.isEnabled();
    }

    public boolean equals(Object obj) {
        if (obj instanceof DarkModeAppInfo) {
            return this.pkgName.equals(((DarkModeAppInfo) obj).getPkgName());
        }
        return false;
    }

    public String getLabel() {
        return this.label;
    }

    public long getLastTimeUsed() {
        return this.lastTimeUsed;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean z) {
        this.enabled = z;
    }

    public void setLastTimeUsed(long j) {
        this.lastTimeUsed = j;
    }
}
