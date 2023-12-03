package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.SparseIntArray;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.fuelgauge.Estimate;
import java.util.Map;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class PowerUsageFeatureProviderImpl implements PowerUsageFeatureProvider {
    private static final String[] PACKAGES_SYSTEM = {"com.android.providers.media", "com.android.providers.calendar", ThemeResources.SYSTEMUI_NAME};
    protected Context mContext;
    protected PackageManager mPackageManager;

    public PowerUsageFeatureProviderImpl(Context context) {
        this.mPackageManager = context.getPackageManager();
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext != null ? applicationContext : context;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public String getAdvancedUsageScreenInfoString() {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Map<Long, Map<String, BatteryHistEntry>> getBatteryHistory(Context context) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean getEarlyWarningSignal(Context context, String str) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Estimate getEnhancedBatteryPrediction(Context context) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public SparseIntArray getEnhancedBatteryPredictionCurve(Context context, long j) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isChartGraphEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isChartGraphSlotsEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isEnhancedBatteryPredictionEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isSmartBatterySupported() {
        return this.mContext.getResources().getBoolean(17891641);
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isTypeSystem(int i, String[] strArr) {
        if (i < 0 || i >= 10000) {
            if (strArr != null) {
                for (String str : strArr) {
                    if (ArrayUtils.contains(PACKAGES_SYSTEM, str)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }
}
