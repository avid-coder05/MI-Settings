package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.os.storage.StorageManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes2.dex */
public class SecurityEncryptionPreferenceController extends BasePreferenceController {
    public static final String PREF_KEY_SECURITY_ENCRYPTION = "security_encryption";

    public SecurityEncryptionPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mContext.getResources().getBoolean(R.bool.config_show_encryption_and_credentials_encryption_status) || StorageManager.isFileEncryptedNativeOrEmulated()) {
            return 3;
        }
        return LockPatternUtils.isDeviceEncryptionEnabled() ? 0 : 5;
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
        ValuePreference valuePreference = (ValuePreference) preference;
        valuePreference.setShowRightArrow(true);
        valuePreference.setValue(Settings.System.getInt(this.mContext.getContentResolver(), "is_security_encryption_enabled", 0) > 0 ? this.mContext.getString(R.string.security_encryption_is_used) : "");
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
