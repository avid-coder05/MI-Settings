package com.android.settings;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.AodPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AodUtils;

/* loaded from: classes.dex */
public class AodNotificationPrefController extends AodPreferenceController {
    public static final String AOD_KEYGUARD_NOTIFICATION_STATUS = "aod_notification_status";
    private boolean mAodShowModeStyleSelectAvaliable;
    private boolean mNotificationStyleSelectAvaliable;

    public AodNotificationPrefController(Context context) {
        super(context, AOD_KEYGUARD_NOTIFICATION_STATUS);
        this.mNotificationStyleSelectAvaliable = false;
        this.mAodShowModeStyleSelectAvaliable = false;
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (MiuiKeyguardSettingsUtils.isSupportAodAnimateDevice(this.mContext) && this.mNotificationStyleSelectAvaliable) ? 0 : 3;
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public void setAodShowModeStyleSelectAvaliable(boolean z) {
        this.mAodShowModeStyleSelectAvaliable = z;
    }

    public void setNotificationStyleSelectAvaliable(boolean z) {
        this.mNotificationStyleSelectAvaliable = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        if (preference instanceof KeyguardRestrictedPreference) {
            ((KeyguardRestrictedPreference) preference).setValue(AodUtils.getNotificationWakeUpStyle(this.mContext));
        }
        preference.setEnabled(getAvailabilityStatus() != 5);
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
