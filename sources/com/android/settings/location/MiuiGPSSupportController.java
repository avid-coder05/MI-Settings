package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.Locale;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiGPSSupportController extends BasePreferenceController {
    private static final String KEY_GPS_SUPPORT = "gps_support";
    private boolean gpsSupport;
    private boolean indiaBuild;

    public MiuiGPSSupportController(Context context) {
        super(context, KEY_GPS_SUPPORT);
        this.gpsSupport = false;
        this.indiaBuild = false;
        init();
    }

    private void init() {
        this.indiaBuild = SystemProperties.get("ro.product.mod_device", "").endsWith("in_global");
        this.gpsSupport = SystemProperties.getBoolean("ro.config.gnss.support", false);
    }

    private boolean isZh() {
        return Locale.getDefault().toString().endsWith("zh_CN");
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.indiaBuild && this.gpsSupport) {
            return 0;
        }
        return (Build.IS_INTERNATIONAL_BUILD || !isZh()) ? 3 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_GPS_SUPPORT;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
