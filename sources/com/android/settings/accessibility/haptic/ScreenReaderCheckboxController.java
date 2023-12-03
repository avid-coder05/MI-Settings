package com.android.settings.accessibility.haptic;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.accessibility.ScreenReaderController;
import com.android.settings.accessibility.VisualAccessibilitySettings;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.Iterator;

/* loaded from: classes.dex */
public class ScreenReaderCheckboxController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mPreference;
    private SharedPreferences mSharedPrefs;

    public ScreenReaderCheckboxController(Context context, String str) {
        super(context, str);
        this.mSharedPrefs = context.getSharedPreferences(ScreenReaderController.ACCESSIBILITY_SCREEN_READER_SP, 0);
    }

    public static boolean isScreenReaderCheckboxOpen(Context context) {
        return context.getSharedPreferences(ScreenReaderController.ACCESSIBILITY_SCREEN_READER_SP, 0).getInt(ScreenReaderController.IS_ACCESSIBILITY_SCREEN_READER_OPEN, 0) == 1;
    }

    private void updateStatus() {
        boolean isTallBackActive = MiuiAccessibilityUtils.isTallBackActive(this.mContext);
        if (isTallBackActive && !isScreenReaderCheckboxOpen(this.mContext)) {
            this.mSharedPrefs.edit().putInt(ScreenReaderController.IS_ACCESSIBILITY_SCREEN_READER_OPEN, 1).apply();
        }
        if (isTallBackActive || isScreenReaderCheckboxOpen(this.mContext)) {
            this.mPreference.setChecked(true);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = (CheckBoxPreference) preferenceScreen.findPreference(this.mPreferenceKey);
            updateStatus();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mPreferenceKey.equals(preference.getKey())) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            this.mSharedPrefs.edit().putInt(ScreenReaderController.IS_ACCESSIBILITY_SCREEN_READER_OPEN, booleanValue ? 1 : 0).apply();
            if (booleanValue) {
                return true;
            }
            Iterator<String> it = VisualAccessibilitySettings.SCREEN_READER_SERVICES_LIST.iterator();
            while (it.hasNext()) {
                AccessibilityUtils.setAccessibilityServiceState(this.mContext, ComponentName.unflattenFromString(it.next()), false);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mPreference != null) {
            updateStatus();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
