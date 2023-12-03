package com.android.settings.backup;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.miui.enterprise.RestrictionsHelper;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiLocaleAutoBackupController extends BasePreferenceController {
    private ValuePreference mAutoBackup;

    public MiuiLocaleAutoBackupController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ValuePreference valuePreference = (ValuePreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mAutoBackup = valuePreference;
        valuePreference.setShowRightArrow(true);
        if (Build.IS_TABLET) {
            this.mAutoBackup.setIntent(null);
            this.mAutoBackup.setFragment("com.miui.backup.auto.AutoBackupFragmentPad");
        }
        if (RestrictionsHelper.hasRestriction(this.mContext, "disallow_backup")) {
            Log.d("Enterprise", "Backup is restricted");
            setVisible(preferenceScreen, getPreferenceKey(), false);
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

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mAutoBackup == null) {
            return;
        }
        if (Settings.System.getInt(this.mContext.getContentResolver(), "local_auto_backup", 0) == 1) {
            this.mAutoBackup.setValue(R.string.local_auto_backup_on);
        } else {
            this.mAutoBackup.setValue(R.string.local_auto_backup_off);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
