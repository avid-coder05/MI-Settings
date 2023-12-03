package com.android.settings.fuelgauge;

import android.content.Context;

/* loaded from: classes.dex */
public class BatteryStatusFeatureProviderImpl implements BatteryStatusFeatureProvider {
    protected Context mContext;

    public BatteryStatusFeatureProviderImpl(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.fuelgauge.BatteryStatusFeatureProvider
    public boolean triggerBatteryStatusUpdate(BatteryPreferenceController batteryPreferenceController, BatteryInfo batteryInfo) {
        return false;
    }
}
