package com.android.settings.usagestats.model;

/* loaded from: classes2.dex */
public class AppUsageStats extends AppUsageInfo {
    private int foregroundCount;
    private long lastUsageTime;
    private long totalForegroundTime;

    public AppUsageStats(String str) {
        super(str);
        this.totalForegroundTime = 0L;
        this.lastUsageTime = 0L;
        this.foregroundCount = 0;
    }

    public void addForegroundTime(long j) {
        this.totalForegroundTime += j;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || AppUsageStats.class != obj.getClass()) {
            return false;
        }
        return this.pkgName.equals(((AppUsageStats) obj).pkgName);
    }

    public long getLastUsageTime() {
        return this.lastUsageTime;
    }

    public long getTotalForegroundTime() {
        return this.totalForegroundTime;
    }

    public int hashCode() {
        return this.pkgName.hashCode();
    }

    public void increaseForegroundCount() {
        this.foregroundCount++;
    }

    public boolean isValid() {
        return this.totalForegroundTime > 0;
    }

    public void setLastUsageTime(long j) {
        this.lastUsageTime = j;
    }

    public void setTotalForegroundTime(long j) {
        this.totalForegroundTime = j;
    }

    public String toString() {
        return "AppUsageStats{pkgName='" + this.pkgName + "', totalForegroundTime=" + this.totalForegroundTime + ", lastUsageTime=" + this.lastUsageTime + ", foregroundCount=" + this.foregroundCount + '}';
    }

    public void updateLastUsageTime(long j) {
        if (j > this.lastUsageTime) {
            this.lastUsageTime = j;
        }
    }

    public void updateStats(long j, long j2) {
        if (j > this.lastUsageTime) {
            this.lastUsageTime = j;
            this.totalForegroundTime = j2;
        }
    }
}
