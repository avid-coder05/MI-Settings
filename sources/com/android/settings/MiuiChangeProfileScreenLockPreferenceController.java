package com.android.settings;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.android.settings.MiuiSecurityChooseUnlock;

/* loaded from: classes.dex */
public class MiuiChangeProfileScreenLockPreferenceController extends MiuiChangeScreenLockPreferenceController {
    public MiuiChangeProfileScreenLockPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settings.MiuiChangeScreenLockPreferenceController
    public String getPreferenceKey() {
        return "unlock_set_or_change_profile";
    }

    public boolean handlePreferenceTreeClick(Fragment fragment) {
        if (TextUtils.equals(this.mPreference.getKey(), getPreferenceKey()) && !Utils.startQuietModeDialogIfNecessary(this.mContext, this.mUm, this.mProfileChallengeUserId)) {
            Bundle bundle = new Bundle();
            bundle.putInt("android.intent.extra.USER_ID", this.mProfileChallengeUserId);
            MiuiKeyguardSettingsUtils.startFragment(fragment, MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.class.getName(), 0, bundle, R.string.lock_settings_picker_update_profile_lock_title);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.MiuiChangeScreenLockPreferenceController
    public boolean isAvailable() {
        int keyguardStoredPasswordQuality;
        int i = this.mProfileChallengeUserId;
        if (i == -10000 || !this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i)) {
            return false;
        }
        return !this.mLockPatternUtils.isSecure(this.mProfileChallengeUserId) || (keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId)) == 65536 || keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608 || keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288;
    }

    public void updateState() {
        updateSummary(this.mProfileChallengeUserId);
        if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId)) {
            disableIfPasswordQualityManaged(this.mProfileChallengeUserId);
            return;
        }
        this.mPreference.setSummary(this.mContext.getString(R.string.lock_settings_profile_unified_summary));
        this.mPreference.setEnabled(false);
    }
}
