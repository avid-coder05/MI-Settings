package com.android.settings.privacy;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.DeviceConfig;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public class MicToggleController extends SensorToggleController {
    public MicToggleController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.privacy.SensorToggleController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mSensorPrivacyManagerHelper.supportsSensorToggle(getSensor()) && DeviceConfig.getBoolean("privacy", "mic_toggle_enabled", true)) ? 1 : 3;
    }

    @Override // com.android.settings.privacy.SensorToggleController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.privacy.SensorToggleController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.privacy.SensorToggleController
    protected String getRestriction() {
        return "disallow_microphone_toggle";
    }

    @Override // com.android.settings.privacy.SensorToggleController
    public int getSensor() {
        return 1;
    }

    @Override // com.android.settings.privacy.SensorToggleController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.privacy.SensorToggleController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.privacy.SensorToggleController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
