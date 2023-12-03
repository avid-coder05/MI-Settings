package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import androidx.preference.Preference;
import com.android.settings.MiuiValuePreference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;

/* loaded from: classes.dex */
public class ScreenReaderController extends BasePreferenceController {
    public static final String ACCESSIBILITY_SCREEN_READER_SP = "ACCESSIBILITY_SCREEN_READER_SP";
    public static final String IS_ACCESSIBILITY_SCREEN_READER_OPEN = "is_accessibility_screen_reader_open";
    private SharedPreferences mSharedPrefs;

    public ScreenReaderController(Context context, String str) {
        super(context, str);
        this.mSharedPrefs = context.getSharedPreferences(ACCESSIBILITY_SCREEN_READER_SP, 0);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return SettingsFeatures.isSupportAccessibilityHaptic(this.mContext) ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return isScreenReaderCheckboxOpen() ? this.mContext.getResources().getString(R.string.accessibility_summary_state_enabled) : this.mContext.getResources().getString(R.string.accessibility_summary_state_disabled);
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

    public boolean isScreenReaderCheckboxOpen() {
        return this.mSharedPrefs.getInt(IS_ACCESSIBILITY_SCREEN_READER_OPEN, 0) == 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        MiuiValuePreference miuiValuePreference = (MiuiValuePreference) preference;
        miuiValuePreference.setShowRightArrow(true);
        miuiValuePreference.setValue(isScreenReaderCheckboxOpen() ? R.string.accessibility_summary_state_enabled : R.string.accessibility_summary_state_disabled);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
