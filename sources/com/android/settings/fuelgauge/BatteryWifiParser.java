package com.android.settings.fuelgauge;

import android.os.BatteryStats;

/* loaded from: classes.dex */
public class BatteryWifiParser extends BatteryFlagParser {
    @Override // com.android.settings.fuelgauge.BatteryFlagParser
    protected boolean isSet(BatteryStats.HistoryItem historyItem) {
        int i = (historyItem.states2 & 15) >> 0;
        return (i == 0 || i == 1 || i == 2 || i == 3 || i == 11 || i == 12) ? false : true;
    }
}
