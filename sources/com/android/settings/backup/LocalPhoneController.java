package com.android.settings.backup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public class LocalPhoneController extends BasePreferenceController {
    private static final String KEY_PHONE = "phone_backup";
    private static final String TAG = "phoneBackupController";
    private Preference.OnPreferenceChangeListener mChangeListener;
    private Context mContext;
    private CustomRadioButtonPreference mPhoneBackup;

    public LocalPhoneController(Context context, String str) {
        super(context, str);
        this.mChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.backup.LocalPhoneController.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                ((CustomRadioButtonPreference) preference).setChecked(false);
                return false;
            }
        };
        this.mContext = context;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    @SuppressLint({"NewApi"})
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        CustomRadioButtonPreference customRadioButtonPreference = (CustomRadioButtonPreference) preferenceScreen.findPreference(KEY_PHONE);
        this.mPhoneBackup = customRadioButtonPreference;
        customRadioButtonPreference.setCustomItemIcon(this.mContext.getResources().getDrawable(R.drawable.ic_mobile));
        this.mPhoneBackup.setOnPreferenceChangeListener(this.mChangeListener);
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
