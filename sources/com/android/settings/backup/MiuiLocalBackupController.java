package com.android.settings.backup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;
import com.miui.enterprise.RestrictionsHelper;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiLocalBackupController extends BasePreferenceController {
    private static final String LOCAL_BACKUP = "local_backup";
    private static final String LOCAL_BACKUP_CATEGORY = "local_backup_category";

    public MiuiLocalBackupController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(LOCAL_BACKUP);
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.backup.MiuiLocalBackupController.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = preference.getIntent();
                    if (!Build.IS_TABLET && (((AbstractPreferenceController) MiuiLocalBackupController.this).mContext instanceof Activity) && ((Activity) ((AbstractPreferenceController) MiuiLocalBackupController.this).mContext).isInMultiWindowMode()) {
                        intent.setFlags(268435456);
                    }
                    ((AbstractPreferenceController) MiuiLocalBackupController.this).mContext.startActivity(intent);
                    return true;
                }
            });
        }
        if (Build.IS_TABLET) {
            setVisible(preferenceScreen, LOCAL_BACKUP_CATEGORY, false);
            preferenceScreen.findPreference(LOCAL_BACKUP).setIntent(null);
            preferenceScreen.findPreference(LOCAL_BACKUP).setFragment("com.miui.backup.ui.MainFragmentPad");
        }
        if (RestrictionsHelper.hasRestriction(this.mContext, "disallow_backup")) {
            setVisible(preferenceScreen, LOCAL_BACKUP_CATEGORY, false);
            setVisible(preferenceScreen, LOCAL_BACKUP, false);
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
