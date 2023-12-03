package com.android.settings.fuelgauge;

import android.os.BatteryStats;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.settings.fuelgauge.BatteryActiveView;
import com.android.settings.fuelgauge.BatteryInfo;

/* loaded from: classes.dex */
public class BatteryFlagParser implements BatteryInfo.BatteryDataParser, BatteryActiveView.BatteryActiveProvider {
    private final int mAccentColor;
    private final SparseBooleanArray mData;
    private final int mFlag;
    private boolean mLastSet;
    private long mLastTime;
    private long mLength;
    private final boolean mState2;

    private int getColor(boolean z) {
        if (z) {
            return this.mAccentColor;
        }
        return 0;
    }

    @Override // com.android.settings.fuelgauge.BatteryActiveView.BatteryActiveProvider
    public SparseIntArray getColorArray() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        for (int i = 0; i < this.mData.size(); i++) {
            sparseIntArray.put(this.mData.keyAt(i), getColor(this.mData.valueAt(i)));
        }
        return sparseIntArray;
    }

    @Override // com.android.settings.fuelgauge.BatteryActiveView.BatteryActiveProvider
    public long getPeriod() {
        return this.mLength;
    }

    protected boolean isSet(BatteryStats.HistoryItem historyItem) {
        return (this.mFlag & (this.mState2 ? historyItem.states2 : historyItem.states)) != 0;
    }

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onDataGap() {
        if (this.mLastSet) {
            this.mData.put((int) this.mLastTime, false);
            this.mLastSet = false;
        }
    }

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onDataPoint(long j, BatteryStats.HistoryItem historyItem) {
        boolean isSet = isSet(historyItem);
        if (isSet != this.mLastSet) {
            this.mData.put((int) j, isSet);
            this.mLastSet = isSet;
        }
        this.mLastTime = j;
    }

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onParsingDone() {
        if (this.mLastSet) {
            this.mData.put((int) this.mLastTime, false);
            this.mLastSet = false;
        }
    }

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onParsingStarted(long j, long j2) {
        this.mLength = j2 - j;
    }
}
