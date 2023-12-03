package com.android.settings.sound;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemProperties;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public class BacktouchSoundsController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String DEVICE_CENTAUR = "centaur";
    private static final String PERSIST_SYS_BACKTOUCH_PROPERTY = "persist.sys.backtouch";
    private CheckBoxPreference mPreference;

    public BacktouchSoundsController(Context context, String str) {
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
            checkBoxPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return DEVICE_CENTAUR.equals(Build.DEVICE) ? 0 : 2;
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
            SystemProperties.set(PERSIST_SYS_BACKTOUCH_PROPERTY, (((Boolean) obj).booleanValue() ? 1 : 0) + "");
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        CheckBoxPreference checkBoxPreference = this.mPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked("1".equals(SystemProperties.get(PERSIST_SYS_BACKTOUCH_PROPERTY)));
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
