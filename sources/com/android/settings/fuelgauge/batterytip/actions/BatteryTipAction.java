package com.android.settings.fuelgauge.batterytip.actions;

import android.content.Context;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

/* loaded from: classes.dex */
public abstract class BatteryTipAction {
    protected Context mContext;
    protected MetricsFeatureProvider mMetricsFeatureProvider;

    public BatteryTipAction(Context context) {
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public abstract void handlePositiveAction(int i);
}
