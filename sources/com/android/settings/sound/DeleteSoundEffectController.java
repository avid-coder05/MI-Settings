package com.android.settings.sound;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import miui.settings.commonlib.SoundDefaultValueUtil;

/* loaded from: classes2.dex */
public class DeleteSoundEffectController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "DeleteSoundEffectController";
    private boolean DEFAULT_FILE_DELETE_SOUND;
    private CheckBoxPreference mPreference;

    public DeleteSoundEffectController(Context context, String str) {
        super(context, str);
        this.DEFAULT_FILE_DELETE_SOUND = SoundDefaultValueUtil.getDeleteSoundEffectDefaultValue();
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
            MiuiSettings.System.putBooleanForUser(this.mContext.getContentResolver(), "delete_sound_effect", ((Boolean) obj).booleanValue(), UserHandle.myUserId());
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        CheckBoxPreference checkBoxPreference = this.mPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(MiuiSettings.System.getBooleanForUser(this.mContext.getContentResolver(), "delete_sound_effect", this.DEFAULT_FILE_DELETE_SOUND, UserHandle.myUserId()));
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
