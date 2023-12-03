package com.android.settings;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.compat.RestrictedLockUtilsCompat;
import com.android.settingslib.MiuiRestrictedPreference;
import com.android.settingslib.RestrictedLockUtils;

/* loaded from: classes.dex */
public class MiuiChangeScreenLockPreferenceController {
    protected Context mContext;
    protected final DevicePolicyManager mDPM;
    protected final LockPatternUtils mLockPatternUtils;
    protected MiuiRestrictedPreference mPreference;
    protected final int mProfileChallengeUserId;
    protected final UserManager mUm;
    protected final int mUserId;

    public MiuiChangeScreenLockPreferenceController(Context context) {
        int myUserId = UserHandle.myUserId();
        this.mUserId = myUserId;
        this.mContext = context;
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mUm = userManager;
        this.mDPM = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mProfileChallengeUserId = Utils.getManagedProfileId(userManager, myUserId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableIfPasswordQualityManaged(int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet = RestrictedLockUtilsCompat.checkIfPasswordQualityIsSet(this.mContext, i);
        if (checkIfPasswordQualityIsSet == null || this.mDPM.getPasswordQuality(checkIfPasswordQualityIsSet.component, i) != 524288) {
            return;
        }
        this.mPreference.setDisabledByAdmin(checkIfPasswordQualityIsSet);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = (MiuiRestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public String getPreferenceKey() {
        return "unlock_set_or_change";
    }

    public boolean isAvailable() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateSummary(int i) {
        if (this.mLockPatternUtils.isSecure(i)) {
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i);
            if (keyguardStoredPasswordQuality == 65536) {
                this.mPreference.setSummary(R.string.unlock_set_unlock_mode_pattern);
            } else if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                this.mPreference.setSummary(R.string.unlock_set_unlock_mode_pin);
            } else if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
                this.mPreference.setSummary(R.string.unlock_set_unlock_mode_password);
            }
        } else if (i == this.mProfileChallengeUserId || this.mLockPatternUtils.isLockScreenDisabled(i)) {
            this.mPreference.setSummary(R.string.unlock_set_unlock_mode_off);
        } else {
            this.mPreference.setSummary(R.string.unlock_set_unlock_mode_none);
        }
        this.mPreference.setEnabled(true);
    }
}
