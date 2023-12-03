package com.android.settings.accessibility.haptic;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import miui.os.Build;

/* loaded from: classes.dex */
public class ScreenReaderHapticController extends BasePreferenceController {
    private CheckBoxPreference mPreference;

    public ScreenReaderHapticController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen.findPreference(this.mPreferenceKey);
            this.mPreference = checkBoxPreference;
            checkBoxPreference.setChecked(MiuiAccessibilityUtils.isRemoveScreenReaderVibrator(this.mContext));
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (Build.IS_GLOBAL_BUILD || !MiuiAccessibilityUtils.isServiceInstalled(this.mContext, "com.miui.accessibility/com.miui.accessibility.haptic.HapticAccessibilityService")) ? 2 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        CheckBoxPreference checkBoxPreference = this.mPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(MiuiAccessibilityUtils.isTallBackActive(this.mContext));
            this.mPreference.setChecked(MiuiAccessibilityUtils.isRemoveScreenReaderVibrator(this.mContext));
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
