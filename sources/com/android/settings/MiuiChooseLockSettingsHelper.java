package com.android.settings;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.security.ChooseLockSettingsHelper;
import android.security.KeyStore;
import android.security.MiuiLockPatternUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.ChooseLockPassword;
import com.android.settings.ChooseLockPattern;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.MiuiSecurityChooseUnlock;
import com.android.settings.ProvisionSetUpMiuiSecurityChooseUnlock;
import com.android.settings.ProvisionSetupChooseLockPassword;
import com.android.settings.ProvisionSetupChooseLockPattern;
import com.android.settings.SetUpMiuiSecurityChooseUnlock;
import com.android.settings.SetupChooseLockPassword;
import com.android.settings.SetupChooseLockPattern;
import com.android.settings.compat.QualityCompat;
import com.android.settings.search.SearchUpdater;
import com.android.settings.utils.TabletUtils;
import miui.securityspace.CrossUserUtils;

/* loaded from: classes.dex */
public final class MiuiChooseLockSettingsHelper {
    private DevicePolicyManager mDpm;
    @VisibleForTesting
    LockPatternUtils mLockPatternUtils;

    public MiuiChooseLockSettingsHelper(Context context) {
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mDpm = (DevicePolicyManager) context.getSystemService("device_policy");
    }

    private Intent getBiometricSensorIntent(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment) {
        Intent intent = new Intent().setClass(keyguardSettingsPreferenceFragment.getActivity(), MiuiSecurityChooseUnlock.class);
        intent.putExtra("lockscreen.biometric_weak_fallback", true);
        intent.putExtra(":settings:show_fragment_title", R.string.lockpassword_choose_your_password_header);
        Intent intent2 = new Intent();
        intent2.setClassName("com.android.facelock", "com.android.facelock.SetupIntro");
        intent2.putExtra("showTutorial", false);
        intent2.putExtra("PendingIntent", PendingIntent.getActivity(keyguardSettingsPreferenceFragment.getActivity(), 0, intent, 0));
        return intent2;
    }

    private Intent getConfirmationActivityIntent(int i, int i2, String str) {
        Intent intent = new Intent();
        intent.putExtra("return_credentials", true);
        intent.putExtra("android.intent.extra.USER_ID", i);
        intent.putExtra("com.android.settings.ConfirmLockPattern.header", str);
        intent.putExtra("com.android.settings.forgetPassword", false);
        intent.setClassName("com.android.settings", (i2 != 65536 ? (i2 == 131072 || i2 == 196608 || i2 == 262144 || i2 == 327680 || i2 == 393216) ? ConfirmLockPassword.InternalActivity.class : null : ConfirmLockPattern.InternalActivity.class).getName());
        return intent;
    }

    private static boolean isDeviceProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
    }

    private void launchConfirmActivity(Fragment fragment, Activity activity, int i, int i2, int i3) {
        Class cls;
        if (i == 65536) {
            cls = ConfirmLockPattern.class;
            if (CrossUserUtils.isAirSpace(activity, i3)) {
                cls = ConfirmSpacePatternActivity.class;
            }
        } else if (i == 131072 || i == 196608 || i == 262144 || i == 327680 || i == 393216) {
            cls = ConfirmLockPassword.class;
            if (CrossUserUtils.isAirSpace(activity, i3)) {
                cls = ConfirmSpacePasswordActivity.class;
            }
        } else {
            cls = null;
        }
        if (cls != null) {
            Intent intent = new Intent(activity, cls);
            if (i3 == Settings.Secure.getIntForUser(activity.getContentResolver(), "second_user_id", -10000, 0)) {
                intent.putExtra("com.android.settings.ConfirmLockPattern.header", activity.getResources().getString(R.string.lockpassword_confirm_for_second_user));
                intent.putExtra("com.android.settings.titleColor", activity.getResources().getColor(17170443));
                intent.putExtra("com.android.settings.bgColor", activity.getResources().getColor(R.color.set_second_space_background));
                intent.putExtra("com.android.settings.lockBtnWhite", true);
                intent.putExtra("com.android.settings.forgetPatternColor", activity.getResources().getColor(17170443));
                intent.putExtra("com.android.settings.footerTextColor", activity.getResources().getColor(17170443));
            }
            if (CrossUserUtils.isAirSpace(activity, i3)) {
                intent.putExtra("com.android.settings.userIdToConfirm", i3);
                intent.putExtra("com.android.settings.ConfirmLockPattern.header", activity.getResources().getString(R.string.lockpassword_confirm_workspace_password));
                intent.putExtra("com.android.settings.forgetPassword", false);
            }
            if (i2 == 107) {
                intent.putExtra("return_credentials", true);
            }
            if (fragment != null) {
                fragment.startActivityForResult(intent, i2);
            } else {
                activity.startActivityForResult(intent, i2);
            }
        }
    }

    private int upgradeQualityForKeyStore(int i) {
        KeyStore keyStore = KeyStore.getInstance();
        return (keyStore == null || keyStore.isEmpty() || i >= 65536) ? i : SearchUpdater.GOOGLE;
    }

    public void launchConfirmFragment(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, int i2) {
        Bundle bundle;
        if (i2 == 107) {
            bundle = new Bundle();
            bundle.putBoolean("return_credentials", true);
        } else {
            bundle = null;
        }
        if (i == 65536) {
            MiuiKeyguardSettingsUtils.startFragment(keyguardSettingsPreferenceFragment, ConfirmLockPattern.ConfirmLockPatternFragment.class.getName(), i2, bundle, R.string.empty_title);
        } else if (i == 131072 || i == 196608 || i == 262144 || i == 327680 || i == 393216) {
            MiuiKeyguardSettingsUtils.startFragment(keyguardSettingsPreferenceFragment, ConfirmLockPassword.ConfirmLockPasswordFragment.class.getName(), i2, bundle, R.string.empty_title);
        }
    }

    public void launchConfirmWhenNecessary(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, int i2) {
        Bundle extras = keyguardSettingsPreferenceFragment.getActivity().getIntent().getExtras();
        if (keyguardSettingsPreferenceFragment.getActivity() instanceof MiuiSettings) {
            extras = keyguardSettingsPreferenceFragment.getArguments();
        }
        if (extras.getBoolean("confirm_credentials", true)) {
            int activePasswordQuality = new MiuiLockPatternUtils(keyguardSettingsPreferenceFragment.getActivity()).getActivePasswordQuality(i2);
            if (keyguardSettingsPreferenceFragment.getTargetFragment() == null) {
                launchConfirmActivity(keyguardSettingsPreferenceFragment, keyguardSettingsPreferenceFragment.getActivity(), activePasswordQuality, i, i2);
            } else {
                launchConfirmFragment(keyguardSettingsPreferenceFragment, activePasswordQuality, i);
            }
        }
    }

    public boolean launchConfirmationActivity(Activity activity, Fragment fragment, int i, CharSequence charSequence, CharSequence charSequence2) {
        return new ChooseLockSettingsHelper(activity).launchConfirmationActivity(i, charSequence, charSequence2);
    }

    public boolean launchConfirmationActivity(Fragment fragment, int i, int i2, int i3, String str) {
        if (fragment != null) {
            fragment.startActivityForResult(getConfirmationActivityIntent(i, i2, str), i3);
            return true;
        }
        return true;
    }

    public void startActivityToSetPassword(int i, KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, boolean z, String str, int i2) {
        startActivityToSetPassword(i, keyguardSettingsPreferenceFragment, z, str, i2, false);
    }

    public void startActivityToSetPassword(int i, KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, boolean z, String str, int i2, boolean z2) {
        boolean z3;
        String str2;
        long j;
        int i3;
        boolean z4;
        Intent intent;
        boolean booleanExtra = keyguardSettingsPreferenceFragment.getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false);
        boolean booleanExtra2 = keyguardSettingsPreferenceFragment.getActivity().getIntent().getBooleanExtra("use_lock_password_to_encrypt_device", false);
        boolean booleanExtra3 = keyguardSettingsPreferenceFragment.getActivity().getIntent().getBooleanExtra("add_keyguard_password_then_add_fingerprint", false);
        boolean booleanExtra4 = keyguardSettingsPreferenceFragment.getActivity().getIntent().getBooleanExtra("add_keyguard_password_then_add_face_recoginition", false);
        FragmentActivity activity = keyguardSettingsPreferenceFragment.getActivity();
        if (!new FingerprintHelper(activity).isHardwareDetected() || (((intent = activity.getIntent()) == null || !((activity instanceof MiuiSecurityChooseUnlock.InternalActivity) || (activity instanceof SetUpMiuiSecurityChooseUnlock.InternalActivity))) && !(activity instanceof ProvisionSetUpMiuiSecurityChooseUnlock.InternalActivity))) {
            z3 = booleanExtra4;
            str2 = "add_keyguard_password_then_add_face_recoginition";
            j = 0;
            i3 = 0;
            z4 = false;
        } else {
            i3 = 0;
            z4 = intent.getBooleanExtra("has_challenge", false);
            z3 = booleanExtra4;
            str2 = "add_keyguard_password_then_add_face_recoginition";
            j = 0;
            if (z4) {
                j = intent.getLongExtra("challenge", 0L);
            }
        }
        long j2 = j;
        int intExtra = keyguardSettingsPreferenceFragment.getActivity().getIntent().getIntExtra("requested_min_complexity", i3);
        int upgradeQuality = upgradeQuality(i, keyguardSettingsPreferenceFragment, i2, intExtra);
        if (upgradeQuality >= 131072) {
            int passwordMinimumLength = this.mDpm.getPasswordMinimumLength(null, i2);
            if (passwordMinimumLength < 4) {
                passwordMinimumLength = 4;
            }
            if (upgradeQuality > 262144) {
                upgradeQuality = 262144;
            }
            int passwordMaximumLength = this.mDpm.getPasswordMaximumLength(upgradeQuality);
            Intent intent2 = new Intent().setClass(keyguardSettingsPreferenceFragment.getActivity(), z2 ? isDeviceProvisioned(keyguardSettingsPreferenceFragment.getActivity()) ? SetupChooseLockPassword.class : ProvisionSetupChooseLockPassword.class : ChooseLockPassword.class);
            intent2.putExtra("lockscreen.password_type", upgradeQuality);
            intent2.putExtra("lockscreen.password_min", passwordMinimumLength);
            intent2.putExtra("lockscreen.password_max", passwordMaximumLength);
            MiuiKeyguardSettingsUtils.putExtraUserId(intent2, i2);
            intent2.putExtra("lockscreen.biometric_weak_fallback", booleanExtra);
            intent2.putExtra("user_id_to_set_password", keyguardSettingsPreferenceFragment.getActivity().getIntent().getIntExtra("user_id_to_set_password", -10000));
            intent2.putExtra("use_lock_password_to_encrypt_device", booleanExtra2);
            intent2.putExtra("set_keyguard_password", activity.getIntent().getBooleanExtra("set_keyguard_password", true));
            intent2.putExtra("add_keyguard_password_then_add_fingerprint", booleanExtra3);
            intent2.putExtra(str2, z3);
            intent2.putExtra("confirm_credentials", !z);
            intent2.putExtra("password", str);
            intent2.putExtra("requested_min_complexity", intExtra);
            if (z4) {
                intent2.putExtra("has_challenge", true);
                intent2.putExtra("challenge", j2);
            }
            if (booleanExtra) {
                keyguardSettingsPreferenceFragment.startActivityForResult(intent2, 201);
                return;
            } else {
                keyguardSettingsPreferenceFragment.startActivityForResult(intent2, 202);
                return;
            }
        }
        String str3 = str2;
        boolean z5 = z3;
        if (upgradeQuality != 65536) {
            if (upgradeQuality == 32768) {
                keyguardSettingsPreferenceFragment.startActivityForResult(getBiometricSensorIntent(keyguardSettingsPreferenceFragment), 202);
                return;
            }
            return;
        }
        Intent intent3 = new Intent();
        intent3.setClass(keyguardSettingsPreferenceFragment.getActivity(), z2 ? isDeviceProvisioned(keyguardSettingsPreferenceFragment.getActivity()) ? SetupChooseLockPattern.class : ProvisionSetupChooseLockPattern.class : ChooseLockPattern.class);
        intent3.putExtra("key_lock_method", "pattern");
        MiuiKeyguardSettingsUtils.putExtraUserId(intent3, i2);
        intent3.putExtra("use_lock_password_to_encrypt_device", booleanExtra2);
        intent3.putExtra("lockscreen.biometric_weak_fallback", booleanExtra);
        intent3.putExtra("user_id_to_set_password", keyguardSettingsPreferenceFragment.getActivity().getIntent().getIntExtra("user_id_to_set_password", -10000));
        intent3.putExtra("set_keyguard_password", activity.getIntent().getBooleanExtra("set_keyguard_password", true));
        intent3.putExtra("add_keyguard_password_then_add_fingerprint", booleanExtra3);
        intent3.putExtra(str3, z5);
        intent3.putExtra(":android:show_fragment_title", R.string.empty_title);
        intent3.putExtra("confirm_credentials", !z);
        intent3.putExtra("password", str);
        if (z4) {
            intent3.putExtra("has_challenge", true);
            intent3.putExtra("challenge", j2);
        }
        if (booleanExtra) {
            keyguardSettingsPreferenceFragment.startActivityForResult(intent3, 201);
        } else {
            keyguardSettingsPreferenceFragment.startActivityForResult(intent3, 202);
        }
    }

    public void startFragmentToSetMixedPassword(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, boolean z, String str, int i2) {
        startFragmentToSetMixedPassword(keyguardSettingsPreferenceFragment, i, z, str, i2, false);
    }

    public void startFragmentToSetMixedPassword(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, boolean z, String str, int i2, boolean z2) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) keyguardSettingsPreferenceFragment.getSystemService("device_policy");
        int passwordMinimumLength = devicePolicyManager.getPasswordMinimumLength(null);
        startFragmentToSetPassword(keyguardSettingsPreferenceFragment, i, passwordMinimumLength < 4 ? 4 : passwordMinimumLength, devicePolicyManager.getPasswordMaximumLength(262144), 262144, z, str, i2, z2);
    }

    public void startFragmentToSetNumericPassword(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, boolean z, String str, int i2) {
        startFragmentToSetNumericPassword(keyguardSettingsPreferenceFragment, i, z, str, i2, false);
    }

    public void startFragmentToSetNumericPassword(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, boolean z, String str, int i2, boolean z2) {
        startFragmentToSetPassword(keyguardSettingsPreferenceFragment, i, this.mDpm.getPasswordMinimumLength(null), this.mDpm.getPasswordMaximumLength(131072), 131072, z, str, i2, z2);
    }

    public void startFragmentToSetPassword(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, int i2, int i3, int i4, boolean z, String str, int i5, boolean z2) {
        Intent intent = keyguardSettingsPreferenceFragment.getActivity().getIntent();
        Bundle bundle = new Bundle();
        if (keyguardSettingsPreferenceFragment.getArguments() != null) {
            bundle.putAll(keyguardSettingsPreferenceFragment.getArguments());
        }
        bundle.putInt("lockscreen.password_min", i2);
        bundle.putInt("lockscreen.password_max", i3);
        bundle.putInt("lockscreen.password_type", i4);
        bundle.putBoolean("confirm_credentials", !z);
        bundle.putString("password", str);
        bundle.putInt("android.intent.extra.USER_ID", i5);
        bundle.putBoolean("use_lock_password_to_encrypt_device", intent.getBooleanExtra("use_lock_password_to_encrypt_device", false));
        bundle.putBoolean("set_keyguard_password", intent.getBooleanExtra("set_keyguard_password", true));
        bundle.putBoolean("add_keyguard_password_then_add_fingerprint", intent.getBooleanExtra("add_keyguard_password_then_add_fingerprint", false));
        bundle.putBoolean("add_keyguard_password_then_add_face_recoginition", intent.getBooleanExtra("add_keyguard_password_then_add_face_recoginition", false));
        bundle.putInt("requested_min_complexity", intent.getIntExtra("requested_min_complexity", 0));
        if (TabletUtils.IS_TABLET) {
            keyguardSettingsPreferenceFragment.getActivity().getIntent().getExtras().clear();
            MiuiKeyguardSettingsUtils.startFragment(keyguardSettingsPreferenceFragment, z2 ? isDeviceProvisioned(keyguardSettingsPreferenceFragment.getActivity()) ? SetupChooseLockPassword.SetupChooseLockPasswordFragment.class.getName() : ProvisionSetupChooseLockPassword.ProvisionSetupChooseLockPasswordFragment.class.getName() : ChooseLockPassword.ChooseLockPasswordFragment.class.getName(), i, bundle, R.string.lockpassword_choose_your_password_header);
            return;
        }
        Intent intent2 = new Intent(keyguardSettingsPreferenceFragment.getActivity(), z2 ? isDeviceProvisioned(keyguardSettingsPreferenceFragment.getActivity()) ? SetupChooseLockPassword.class : ProvisionSetupChooseLockPassword.class : ChooseLockPassword.class);
        intent2.putExtras(bundle);
        keyguardSettingsPreferenceFragment.startActivityForResult(intent2, i);
    }

    public void startFragmentToSetPattern(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, boolean z, String str, int i2) {
        startFragmentToSetPattern(keyguardSettingsPreferenceFragment, i, z, str, i2, false);
    }

    public void startFragmentToSetPattern(KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i, boolean z, String str, int i2, boolean z2) {
        Intent intent = keyguardSettingsPreferenceFragment.getActivity().getIntent();
        Bundle bundle = new Bundle();
        if (keyguardSettingsPreferenceFragment.getArguments() != null) {
            bundle.putAll(keyguardSettingsPreferenceFragment.getArguments());
        }
        bundle.putBoolean("use_lock_password_to_encrypt_device", intent.getBooleanExtra("use_lock_password_to_encrypt_device", false));
        bundle.putBoolean("set_keyguard_password", intent.getBooleanExtra("set_keyguard_password", true));
        bundle.putBoolean("add_keyguard_password_then_add_fingerprint", intent.getBooleanExtra("add_keyguard_password_then_add_fingerprint", false));
        bundle.putBoolean("add_keyguard_password_then_add_face_recoginition", intent.getBooleanExtra("add_keyguard_password_then_add_face_recoginition", false));
        bundle.putBoolean("confirm_credentials", !z);
        bundle.putString("password", str);
        bundle.putInt("android.intent.extra.USER_ID", i2);
        int i3 = R.string.empty_title;
        bundle.putInt(":android:show_fragment_title", i3);
        if (TabletUtils.IS_TABLET) {
            keyguardSettingsPreferenceFragment.getActivity().getIntent().getExtras().clear();
            MiuiKeyguardSettingsUtils.startFragment(keyguardSettingsPreferenceFragment, z2 ? isDeviceProvisioned(keyguardSettingsPreferenceFragment.getActivity()) ? SetupChooseLockPattern.SetupChooseLockPatternFragment.class.getName() : ProvisionSetupChooseLockPattern.ProvisionSetupChooseLockPatternFragment.class.getName() : ChooseLockPattern.ChooseLockPatternFragment.class.getName(), i, bundle, i3);
            return;
        }
        Intent intent2 = new Intent(keyguardSettingsPreferenceFragment.getActivity(), z2 ? isDeviceProvisioned(keyguardSettingsPreferenceFragment.getActivity()) ? SetupChooseLockPattern.class : ProvisionSetupChooseLockPattern.class : ChooseLockPattern.class);
        intent2.putExtras(bundle);
        keyguardSettingsPreferenceFragment.startActivityForResult(intent2, i);
    }

    public int upgradeQuality(int i, KeyguardSettingsPreferenceFragment keyguardSettingsPreferenceFragment, int i2, int i3) {
        int upgradeQuality = QualityCompat.upgradeQuality(i, this.mDpm, i2, i3);
        return Settings.Secure.getInt(keyguardSettingsPreferenceFragment.getActivity().getContentResolver(), "vpn_password_enable", 0) > 0 ? upgradeQualityForKeyStore(upgradeQuality) : upgradeQuality;
    }

    public LockPatternUtils utils() {
        return this.mLockPatternUtils;
    }
}
