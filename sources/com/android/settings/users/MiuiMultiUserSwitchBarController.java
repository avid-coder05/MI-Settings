package com.android.settings.users;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.users.MultiUserSwitchBarController;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;

/* loaded from: classes2.dex */
public class MiuiMultiUserSwitchBarController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "MultiUserSwitchBar";
    private MultiUserSwitchBarController.OnMultiUserSwitchChangedListener mListener;
    private final UserCapabilities mUserCapabilities;

    public MiuiMultiUserSwitchBarController(Context context, MultiUserSwitchBarController.OnMultiUserSwitchChangedListener onMultiUserSwitchChangedListener) {
        super(context, "multiuser_switch");
        this.mUserCapabilities = UserCapabilities.create(context);
        this.mListener = onMultiUserSwitchChangedListener;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("multiuser_switch");
        if (findPreference instanceof RestrictedSwitchPreference) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference;
            UserCapabilities userCapabilities = this.mUserCapabilities;
            boolean z = userCapabilities.mDisallowSwitchUser;
            if (z) {
                restrictedSwitchPreference.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_user_switch", UserHandle.myUserId()));
            } else {
                restrictedSwitchPreference.setEnabled((z || userCapabilities.mIsGuest || !userCapabilities.isAdmin()) ? false : true);
            }
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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
        MultiUserSwitchBarController.OnMultiUserSwitchChangedListener onMultiUserSwitchChangedListener;
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Log.d(TAG, "Toggling multi-user feature enabled state to: " + booleanValue);
        boolean putInt = Settings.Global.putInt(this.mContext.getContentResolver(), "user_switcher_enabled", booleanValue ? 1 : 0);
        if (putInt && (onMultiUserSwitchChangedListener = this.mListener) != null) {
            onMultiUserSwitchChangedListener.onMultiUserSwitchChanged(booleanValue);
        }
        if (preference instanceof RestrictedSwitchPreference) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setTitle(restrictedSwitchPreference.isChecked() ? R.string.switch_on_text : R.string.switch_off_text);
        }
        return putInt;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (isAvailable() && (preference instanceof RestrictedSwitchPreference)) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            UserCapabilities userCapabilities = this.mUserCapabilities;
            restrictedSwitchPreference.setEnabled((userCapabilities.mDisallowSwitchUser || userCapabilities.mIsGuest || !userCapabilities.isAdmin()) ? false : true);
            restrictedSwitchPreference.setChecked(this.mUserCapabilities.mUserSwitcherEnabled);
            Settings.Global.putInt(this.mContext.getContentResolver(), "user_switcher_enabled", restrictedSwitchPreference.isChecked() ? 1 : 0);
            restrictedSwitchPreference.setTitle(restrictedSwitchPreference.isChecked() ? R.string.switch_on_text : R.string.switch_off_text);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
