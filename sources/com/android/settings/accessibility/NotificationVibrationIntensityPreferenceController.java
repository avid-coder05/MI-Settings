package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public class NotificationVibrationIntensityPreferenceController extends VibrationIntensityPreferenceController {
    static final String PREF_KEY = "notification_vibration_preference_screen";

    public NotificationVibrationIntensityPreferenceController(Context context) {
        super(context, PREF_KEY, "notification_vibration_intensity", "");
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController
    protected int getDefaultIntensity() {
        return this.mVibrator.getDefaultNotificationVibrationIntensity();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
