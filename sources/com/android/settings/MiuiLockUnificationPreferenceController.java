package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.MiuiLockPatternUtils;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.MiuiSecurityChooseUnlock;
import com.android.settings.compat.LockPatternUtilsCompat;
import com.android.settings.compat.RestrictedLockUtilsCompat;
import com.android.settings.security.MiuiSecurityAndPrivacySettings;
import com.android.settingslib.MiuiRestrictedSwitchPreference;

/* loaded from: classes.dex */
public class MiuiLockUnificationPreferenceController implements Preference.OnPreferenceChangeListener {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private MiuiChooseLockSettingsHelper mChooseLockSettingsHelper;
    private Context mContext;
    private String mCurrentDevicePassword;
    private String mCurrentProfilePassword;
    private Fragment mFragment;
    private final MiuiSecurityAndPrivacySettings mHost;
    private final LockPatternUtils mLockPatternUtils;
    private final int mProfileChallengeUserId;
    private final UserManager mUm;
    private MiuiRestrictedSwitchPreference mUnifyProfile;

    public MiuiLockUnificationPreferenceController(Context context) {
        this(context, null, null);
    }

    public MiuiLockUnificationPreferenceController(Context context, MiuiSecurityAndPrivacySettings miuiSecurityAndPrivacySettings, Fragment fragment) {
        this.mContext = context;
        this.mFragment = fragment;
        this.mHost = miuiSecurityAndPrivacySettings;
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mUm = userManager;
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mChooseLockSettingsHelper = new MiuiChooseLockSettingsHelper(context);
        this.mProfileChallengeUserId = Utils.getManagedProfileId(userManager, MY_USER_ID);
    }

    private void launchConfirmProfileLockForUnification() {
        Fragment fragment;
        String string = this.mContext.getString(R.string.unlock_set_unlock_launch_picker_title_profile);
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId);
        if (keyguardStoredPasswordQuality == 0 || (fragment = this.mFragment) == null) {
            unifyLocks();
        } else {
            this.mChooseLockSettingsHelper.launchConfirmationActivity(fragment, this.mProfileChallengeUserId, keyguardStoredPasswordQuality, 129, string);
        }
    }

    private void unifyLocks() {
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId);
        boolean z = 262144 == keyguardStoredPasswordQuality || 327680 == keyguardStoredPasswordQuality || 393216 == keyguardStoredPasswordQuality;
        if (keyguardStoredPasswordQuality == 65536) {
            LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
            LockPatternUtilsCompat.saveLockPattern(lockPatternUtils, LockPatternUtilsCompat.stringToPattern(lockPatternUtils, this.mCurrentProfilePassword), this.mCurrentDevicePassword, MY_USER_ID, false);
        } else {
            LockPatternUtilsCompat.saveLockPassword(new MiuiLockPatternUtils(this.mContext), this.mCurrentProfilePassword, true ^ z, this.mCurrentDevicePassword, keyguardStoredPasswordQuality, MY_USER_ID);
        }
        LockPatternUtilsCompat.setSeparateProfileChallengeEnabled(this.mLockPatternUtils, this.mProfileChallengeUserId, false, this.mCurrentProfilePassword);
        this.mLockPatternUtils.setVisiblePatternEnabled(this.mLockPatternUtils.isVisiblePatternEnabled(this.mProfileChallengeUserId), MY_USER_ID);
        this.mCurrentDevicePassword = null;
        this.mCurrentProfilePassword = null;
    }

    private void ununifyLocks() {
        if (this.mFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("android.intent.extra.USER_ID", this.mProfileChallengeUserId);
            MiuiKeyguardSettingsUtils.startFragment(this.mFragment, MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.class.getName(), 0, bundle, R.string.lock_settings_picker_update_profile_lock_title);
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        MiuiRestrictedSwitchPreference miuiRestrictedSwitchPreference = (MiuiRestrictedSwitchPreference) preferenceScreen.findPreference("unification");
        this.mUnifyProfile = miuiRestrictedSwitchPreference;
        miuiRestrictedSwitchPreference.setOnPreferenceChangeListener(this);
    }

    public boolean handleActivityResult(int i, int i2, Intent intent) {
        if (i == 130 && i2 == -1) {
            ununifyLocks();
            return true;
        } else if (i == 128 && i2 == -1) {
            this.mCurrentDevicePassword = intent.getStringExtra("password");
            launchConfirmProfileLockForUnification();
            return true;
        } else if (i == 129 && i2 == -1) {
            this.mCurrentProfilePassword = intent.getStringExtra("password");
            unifyLocks();
            return true;
        } else {
            return false;
        }
    }

    public boolean isAvailable() {
        int i = this.mProfileChallengeUserId;
        return i != -10000 && this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i);
    }

    public void launchConfirmDeviceLockForUnification() {
        Fragment fragment;
        String string = this.mContext.getString(R.string.unlock_set_unlock_launch_picker_title);
        LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
        int i = MY_USER_ID;
        int keyguardStoredPasswordQuality = lockPatternUtils.getKeyguardStoredPasswordQuality(i);
        if (keyguardStoredPasswordQuality == 0 || (fragment = this.mFragment) == null) {
            launchConfirmProfileLockForUnification();
        } else {
            this.mChooseLockSettingsHelper.launchConfirmationActivity(fragment, i, keyguardStoredPasswordQuality, 128, string);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Fragment fragment;
        boolean z = false;
        if (Utils.startQuietModeDialogIfNecessary(this.mContext, this.mUm, this.mProfileChallengeUserId)) {
            return false;
        }
        if (!((Boolean) obj).booleanValue() || this.mHost == null) {
            String string = this.mContext.getString(R.string.unlock_set_unlock_launch_picker_title);
            LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
            int i = MY_USER_ID;
            int keyguardStoredPasswordQuality = lockPatternUtils.getKeyguardStoredPasswordQuality(i);
            if (keyguardStoredPasswordQuality == 0 || (fragment = this.mFragment) == null) {
                ununifyLocks();
            } else {
                this.mChooseLockSettingsHelper.launchConfirmationActivity(fragment, i, keyguardStoredPasswordQuality, 130, string);
            }
        } else {
            if (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId) >= 65536 && !this.mUm.hasUserRestriction("no_unified_password", UserHandle.of(this.mProfileChallengeUserId))) {
                z = true;
            }
            MiuiUnificationConfirmationDialog.newInstance(z).show(this.mHost);
        }
        return true;
    }

    public void unifyUncompliantLocks() {
        LockPatternUtilsCompat.setSeparateProfileChallengeEnabled(this.mLockPatternUtils, this.mProfileChallengeUserId, false, this.mCurrentProfilePassword);
        Fragment fragment = this.mFragment;
        if (fragment != null) {
            MiuiKeyguardSettingsUtils.startFragment(fragment, MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.class.getName(), 0, null, R.string.lock_settings_picker_title);
        }
    }

    public void updateState() {
        if (this.mUnifyProfile != null) {
            boolean isSeparateProfileChallengeEnabled = this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId);
            this.mUnifyProfile.setChecked(!isSeparateProfileChallengeEnabled);
            if (isSeparateProfileChallengeEnabled) {
                this.mUnifyProfile.setDisabledByAdmin(RestrictedLockUtilsCompat.checkIfRestrictionEnforced(this.mContext, "no_unified_password", this.mProfileChallengeUserId));
            }
        }
    }
}
