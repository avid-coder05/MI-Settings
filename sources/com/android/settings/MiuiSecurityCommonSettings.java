package com.android.settings;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.KeyStore;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.KeyguardRestrictedListPreference;
import com.android.settings.compat.LockPatternUtilsCompat;
import com.android.settings.compat.RestrictedLockUtilsCompat;
import com.android.settings.faceunlock.FaceRemoveCallback;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockManager;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.search.SearchUpdater;
import com.android.settingslib.RestrictedLockUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSecurityCommonSettings extends SettingsCompatActivity {
    private static ProgressDialog mLoadingDialog;

    /* loaded from: classes.dex */
    public static class MiuiSecurityCommonSettingsFragment extends KeyguardSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private CheckBoxPreference mBiometricWeakLiveliness;
        private KeyguardRestrictedListPreference mChangePassword;
        private MiuiChooseLockSettingsHelper mChooseLockSettingsHelper;
        private KeyStore mKeyStore;
        private PreferenceCategory mLockScreenCommonOptions;
        private KeyguardRestrictedPreference mSecurityLockToggle;
        private CheckBoxPreference mTactileFeedback;
        private PreferenceCategory mTurnOnOrOffAndChangePassword;
        private String mUserPassword;
        private boolean mPasswordConfirmed = false;
        private final AtomicInteger mFaceAndFingerprintDeletes = new AtomicInteger(0);
        private boolean mAddKeyguardpasswordThenAddFingerprint = false;
        private final Preference.OnPreferenceChangeListener mChangeSecurityLockPreferenceListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiSecurityCommonSettings.MiuiSecurityCommonSettingsFragment.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                if ("change_password".equals(preference.getKey())) {
                    String str = (String) obj;
                    Fragment targetFragment = MiuiSecurityCommonSettingsFragment.this.getTargetFragment();
                    Intent intent = MiuiSecurityCommonSettingsFragment.this.getActivity().getIntent();
                    MiuiChooseLockSettingsHelper unused = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                    intent.putExtra("add_keyguard_password_then_add_fingerprint", false);
                    MiuiChooseLockSettingsHelper unused2 = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                    intent.putExtra("add_keyguard_password_then_add_face_recoginition", false);
                    if ("facial".equals(str)) {
                        MiuiChooseLockSettingsHelper miuiChooseLockSettingsHelper = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                        MiuiSecurityCommonSettingsFragment miuiSecurityCommonSettingsFragment = MiuiSecurityCommonSettingsFragment.this;
                        miuiChooseLockSettingsHelper.startActivityToSetPassword(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON, miuiSecurityCommonSettingsFragment, miuiSecurityCommonSettingsFragment.mPasswordConfirmed, MiuiSecurityCommonSettingsFragment.this.mUserPassword, UserHandle.myUserId());
                    } else if ("pattern".equals(str)) {
                        if (targetFragment == null) {
                            MiuiChooseLockSettingsHelper miuiChooseLockSettingsHelper2 = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                            MiuiSecurityCommonSettingsFragment miuiSecurityCommonSettingsFragment2 = MiuiSecurityCommonSettingsFragment.this;
                            miuiChooseLockSettingsHelper2.startActivityToSetPassword(SearchUpdater.GOOGLE, miuiSecurityCommonSettingsFragment2, miuiSecurityCommonSettingsFragment2.mPasswordConfirmed, MiuiSecurityCommonSettingsFragment.this.mUserPassword, UserHandle.myUserId());
                        } else {
                            MiuiChooseLockSettingsHelper miuiChooseLockSettingsHelper3 = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                            MiuiSecurityCommonSettingsFragment miuiSecurityCommonSettingsFragment3 = MiuiSecurityCommonSettingsFragment.this;
                            miuiChooseLockSettingsHelper3.startFragmentToSetPattern(miuiSecurityCommonSettingsFragment3, 105, miuiSecurityCommonSettingsFragment3.mPasswordConfirmed, MiuiSecurityCommonSettingsFragment.this.mUserPassword, UserHandle.myUserId());
                        }
                    } else if ("numerical".equals(str)) {
                        if (targetFragment == null) {
                            MiuiChooseLockSettingsHelper miuiChooseLockSettingsHelper4 = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                            MiuiSecurityCommonSettingsFragment miuiSecurityCommonSettingsFragment4 = MiuiSecurityCommonSettingsFragment.this;
                            miuiChooseLockSettingsHelper4.startActivityToSetPassword(131072, miuiSecurityCommonSettingsFragment4, miuiSecurityCommonSettingsFragment4.mPasswordConfirmed, MiuiSecurityCommonSettingsFragment.this.mUserPassword, UserHandle.myUserId());
                        } else {
                            MiuiChooseLockSettingsHelper miuiChooseLockSettingsHelper5 = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                            MiuiSecurityCommonSettingsFragment miuiSecurityCommonSettingsFragment5 = MiuiSecurityCommonSettingsFragment.this;
                            miuiChooseLockSettingsHelper5.startFragmentToSetNumericPassword(miuiSecurityCommonSettingsFragment5, 105, miuiSecurityCommonSettingsFragment5.mPasswordConfirmed, MiuiSecurityCommonSettingsFragment.this.mUserPassword, UserHandle.myUserId());
                        }
                    } else if ("mixed".equals(str)) {
                        if (targetFragment == null) {
                            MiuiChooseLockSettingsHelper miuiChooseLockSettingsHelper6 = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                            MiuiSecurityCommonSettingsFragment miuiSecurityCommonSettingsFragment6 = MiuiSecurityCommonSettingsFragment.this;
                            miuiChooseLockSettingsHelper6.startActivityToSetPassword(262144, miuiSecurityCommonSettingsFragment6, miuiSecurityCommonSettingsFragment6.mPasswordConfirmed, MiuiSecurityCommonSettingsFragment.this.mUserPassword, UserHandle.myUserId());
                        } else {
                            MiuiChooseLockSettingsHelper miuiChooseLockSettingsHelper7 = MiuiSecurityCommonSettingsFragment.this.mChooseLockSettingsHelper;
                            MiuiSecurityCommonSettingsFragment miuiSecurityCommonSettingsFragment7 = MiuiSecurityCommonSettingsFragment.this;
                            miuiChooseLockSettingsHelper7.startFragmentToSetMixedPassword(miuiSecurityCommonSettingsFragment7, 105, miuiSecurityCommonSettingsFragment7.mPasswordConfirmed, MiuiSecurityCommonSettingsFragment.this.mUserPassword, UserHandle.myUserId());
                        }
                    }
                }
                return false;
            }
        };
        private FingerprintRemoveCallback callbak = new FingerprintRemoveCallback() { // from class: com.android.settings.MiuiSecurityCommonSettings.MiuiSecurityCommonSettingsFragment.2
            @Override // com.android.settings.FingerprintRemoveCallback
            public void onFailed() {
                Log.e("removeFingerprint", "remove finger failed");
            }

            @Override // com.android.settings.FingerprintRemoveCallback
            public void onRemoved() {
                Log.w("removeFingerprint", "remove finger succeed");
                MiuiSecurityCommonSettingsFragment.this.finish();
            }
        };
        private FaceRemoveCallback mFaceRemovalCallback = new FaceRemoveCallback() { // from class: com.android.settings.MiuiSecurityCommonSettings.MiuiSecurityCommonSettingsFragment.6
            @Override // com.android.settings.faceunlock.FaceRemoveCallback
            public void onFailed() {
                Slog.i("miui_face", "remove all face failed");
                MiuiSecurityCommonSettingsFragment.this.removeBiometricResult();
            }

            @Override // com.android.settings.faceunlock.FaceRemoveCallback
            public void onRemoved() {
                Slog.i("miui_face", "remove all face success");
                MiuiSecurityCommonSettingsFragment.this.removeBiometricResult();
            }
        };
        private FingerprintRemoveCallback removeAllFingerCallbak = new FingerprintRemoveCallback() { // from class: com.android.settings.MiuiSecurityCommonSettings.MiuiSecurityCommonSettingsFragment.7
            @Override // com.android.settings.FingerprintRemoveCallback
            public void onFailed() {
                Slog.i("fingerprint", "remove all finger failed");
                MiuiSecurityCommonSettingsFragment.this.removeBiometricResult();
            }

            @Override // com.android.settings.FingerprintRemoveCallback
            public void onRemoved() {
                Slog.i("fingerprint", "remove finger succeed");
                MiuiSecurityCommonSettingsFragment.this.removeBiometricResult();
            }
        };

        /* JADX INFO: Access modifiers changed from: private */
        public void createNewPassword() {
            Bundle bundle = new Bundle();
            bundle.putBoolean("add_keyguard_password_then_add_fingerprint", this.mAddKeyguardpasswordThenAddFingerprint);
            startFragment(this, "com.android.settings.MiuiSecurityChooseUnlock$MiuiSecurityChooseUnlockFragment", 0, bundle, R.string.password_entrance_title);
        }

        private void disablePrefByPasswordQuality() {
            RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet = RestrictedLockUtilsCompat.checkIfPasswordQualityIsSet(getActivity(), UserHandle.myUserId());
            int passwordQuality = ((DevicePolicyManager) getActivity().getSystemService("device_policy")).getPasswordQuality(null, UserHandle.myUserId());
            if (checkIfPasswordQualityIsSet == null || passwordQuality <= 0) {
                this.mSecurityLockToggle.setDisabledByAdmin(null);
            } else {
                this.mSecurityLockToggle.setDisabledByAdmin(checkIfPasswordQualityIsSet);
            }
            if (checkIfPasswordQualityIsSet != null) {
                CharSequence[] entries = this.mChangePassword.getEntries();
                CharSequence[] entryValues = this.mChangePassword.getEntryValues();
                for (int i = 0; i < entries.length; i++) {
                    boolean z = "pattern".equals(entryValues[i]) && passwordQuality > 65536;
                    boolean z2 = "numerical".equals(entryValues[i]) && passwordQuality > 196608;
                    boolean z3 = "mixed".equals(entryValues[i]) && passwordQuality > 393216;
                    if (z || z2 || z3) {
                        this.mChangePassword.addRestrictedItem(new KeyguardRestrictedListPreference.RestrictedItem(entries[i], entryValues[i], checkIfPasswordQualityIsSet));
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleRemoveAllFingerprints() {
            FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
            List<String> fingerprintIds = fingerprintHelper.getFingerprintIds();
            boolean isHardwareDetected = fingerprintHelper.isHardwareDetected();
            if (fingerprintIds == null || fingerprintIds.size() == 0) {
                removeBiometricResult();
            } else if (!isHardwareDetected || fingerprintIds.size() <= 0) {
            } else {
                fingerprintHelper.removeAllFingerprint(this.removeAllFingerCallbak);
            }
        }

        private void handleSecurityLockToggle() {
            if (keyguardPasswordExisted()) {
                showTurnoffPasswordConfirmDialogAndFinish();
                return;
            }
            final FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
            List<String> fingerprintIds = fingerprintHelper.getFingerprintIds();
            if (!fingerprintHelper.isHardwareDetected() || fingerprintIds.size() <= 0) {
                createNewPassword();
                return;
            }
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecurityCommonSettings.MiuiSecurityCommonSettingsFragment.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -1) {
                        fingerprintHelper.removeAllFingerprint(MiuiSecurityCommonSettingsFragment.this.callbak);
                    }
                    MiuiSecurityCommonSettingsFragment.this.createNewPassword();
                }
            };
            new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setMessage(R.string.delete_or_keep_legacy_passwords_confirm_msg).setPositiveButton(R.string.delete_legacy_fingerprint, onClickListener).setNegativeButton(R.string.keep_legacy_fingerprint, onClickListener).create().show();
        }

        private boolean keyguardPasswordExisted() {
            return this.mChooseLockSettingsHelper.utils().getActivePasswordQuality(UserHandle.myUserId()) != 0;
        }

        private void launchConfirmationFragment(int i) {
            int activePasswordQuality = this.mChooseLockSettingsHelper.utils().getActivePasswordQuality(UserHandle.myUserId());
            Bundle bundle = new Bundle();
            bundle.putBoolean("extra_disable_preview", true);
            if (i == 107) {
                bundle.putBoolean("return_credentials", true);
            }
            if (activePasswordQuality == 65536) {
                startFragment(this, ConfirmLockPattern.ConfirmLockPatternFragment.class.getName(), i, bundle, R.string.empty_title);
            } else if (activePasswordQuality == 131072 || activePasswordQuality == 196608 || activePasswordQuality == 262144 || activePasswordQuality == 327680 || activePasswordQuality == 393216) {
                startFragment(this, ConfirmLockPassword.ConfirmLockPasswordFragment.class.getName(), i, bundle, R.string.empty_title);
            }
        }

        private void processResult(int i, int i2) {
            CheckBoxPreference checkBoxPreference;
            if (i == 101) {
                if (i2 == -1) {
                    turnoffPassword();
                    finish();
                }
            } else if (i == 103) {
                if (i2 == -1) {
                    startBiometricWeakImprove();
                }
            } else if (i == 104) {
                if (i2 != -1 || (checkBoxPreference = this.mBiometricWeakLiveliness) == null) {
                    return;
                }
                checkBoxPreference.setChecked(false);
            } else if (i == 106) {
                if (i2 == -1 && new MiuiLockPatternUtils(getActivity()).getBluetoothUnlockEnabled()) {
                    getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock"));
                }
            } else if (i == 107) {
                if (i2 != -1) {
                    finish();
                } else {
                    this.mPasswordConfirmed = true;
                }
            } else if (i == 201) {
                getActivity().setResult(i2);
                finish();
            } else if (i == 202 && i2 == -1) {
                finish();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeBiometricResult() {
            if (this.mFaceAndFingerprintDeletes.addAndGet(1) >= 2) {
                this.mFaceAndFingerprintDeletes.set(0);
                finish();
            }
        }

        private void showConfirmDialog(DialogInterface.OnClickListener onClickListener, String str) {
            new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setTitle(R.string.turn_off_keyguard_password_alert_title).setMessage(str).setPositiveButton(17039370, onClickListener).setNegativeButton(17039360, onClickListener).create().show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showDialogToWaitTurnOffPassword() {
            ProgressDialog unused = MiuiSecurityCommonSettings.mLoadingDialog = new ProgressDialog(getActivity());
            MiuiSecurityCommonSettings.mLoadingDialog.setCancelable(false);
            MiuiSecurityCommonSettings.mLoadingDialog.setMessage(getResources().getString(R.string.turn_off_keyguard_password_wait_dialog));
            MiuiSecurityCommonSettings.mLoadingDialog.show();
            new Handler().postDelayed(new Runnable() { // from class: com.android.settings.MiuiSecurityCommonSettings.MiuiSecurityCommonSettingsFragment.5
                @Override // java.lang.Runnable
                public void run() {
                    MiuiSecurityCommonSettings.mLoadingDialog.dismiss();
                    ProgressDialog unused2 = MiuiSecurityCommonSettings.mLoadingDialog = null;
                    MiuiSecurityCommonSettingsFragment.this.turnoffPassword();
                    MiuiKeyguardSettingsUtils.saveUpdatepatternTime(MiuiSecurityCommonSettingsFragment.this.getActivity().getApplicationContext());
                    MiuiSecurityCommonSettingsFragment.this.handleRemoveAllFingerprints();
                }
            }, 5000L);
        }

        private void showTurnoffPasswordConfirmDialogAndFinish() {
            FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
            fingerprintHelper.getFingerprintIds();
            boolean isHardwareDetected = fingerprintHelper.isHardwareDetected();
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecurityCommonSettings.MiuiSecurityCommonSettingsFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -1) {
                        if (MiuiKeyguardSettingsUtils.showWaitTurnOffPassword(MiuiSecurityCommonSettingsFragment.this.getActivity().getApplicationContext())) {
                            MiuiSecurityCommonSettingsFragment.this.showDialogToWaitTurnOffPassword();
                            return;
                        }
                        MiuiSecurityCommonSettingsFragment.this.turnoffPassword();
                        MiuiKeyguardSettingsUtils.saveUpdatepatternTime(MiuiSecurityCommonSettingsFragment.this.getActivity().getApplicationContext());
                        MiuiSecurityCommonSettingsFragment.this.handleRemoveAllFingerprints();
                    }
                }
            };
            int i = R.string.turn_off_keyguard_password_confirm_msg;
            if (KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(getActivity().getApplicationContext()) && isHardwareDetected) {
                i = R.string.turn_off_keyguard_password_with_fingerprint_face_confirm_msg;
            } else if (isHardwareDetected) {
                i = R.string.turn_off_keyguard_password_with_fingerprint_confirm_msg;
            } else if (KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(getActivity().getApplicationContext())) {
                i = R.string.turn_off_keyguard_password_with_face_confirm_msg;
            }
            showConfirmDialog(onClickListener, getString(i));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void turnoffPassword() {
            if (KeyguardSettingsFaceUnlockUtils.hasEnrolledFaces(getActivity())) {
                KeyguardSettingsFaceUnlockManager.getInstance(getActivity()).deleteFeature("0", this.mFaceRemovalCallback);
            } else {
                removeBiometricResult();
            }
            LockPatternUtilsCompat.clearLock(this.mChooseLockSettingsHelper.utils(), UserHandle.myUserId(), false, this.mUserPassword);
            this.mChooseLockSettingsHelper.utils().setLockScreenDisabled(false, UserHandle.myUserId());
            MiuiSettings.System.putBooleanForUser(getActivity().getContentResolver(), "new_numeric_password_type", false, UserHandle.myUserId());
            if (new MiuiLockPatternUtils(getActivity()).getBluetoothUnlockEnabled()) {
                getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock.disable"));
            }
            Settings.Secure.getInt(getActivity().getContentResolver(), "vpn_password_enable", 0);
        }

        private void updatePreferencesByPasswordState() {
            if (keyguardPasswordExisted()) {
                this.mSecurityLockToggle.setTitle(R.string.turn_off_security_lock);
                this.mChangePassword.setEnabled(true);
            } else {
                this.mSecurityLockToggle.setTitle(R.string.turn_on_keyguard_password);
                this.mChangePassword.setEnabled(false);
                if (getPreferenceScreen() != null) {
                    getPreferenceScreen().removePreference(this.mLockScreenCommonOptions);
                }
                this.mTurnOnOrOffAndChangePassword.removePreference(this.mChangePassword);
            }
            disablePrefByPasswordQuality();
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return MiuiSecurityCommonSettingsFragment.class.getName();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (intent != null && i == 107) {
                this.mUserPassword = intent.getStringExtra("password");
            }
            processResult(i, i2);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mChooseLockSettingsHelper = new MiuiChooseLockSettingsHelper(getActivity());
            this.mKeyStore = KeyStore.getInstance();
            Bundle arguments = getArguments();
            if (!keyguardPasswordExisted() || bundle != null) {
                if (arguments != null) {
                    this.mAddKeyguardpasswordThenAddFingerprint = arguments.getBoolean("add_keyguard_password_then_add_fingerprint", false);
                }
            } else if (arguments == null || !arguments.getBoolean("password_confirmed", false)) {
                launchConfirmationFragment(107);
            } else {
                this.mUserPassword = arguments.getString("password");
                this.mPasswordConfirmed = true;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public void onFragmentResult(int i, Bundle bundle) {
            processResult(i, bundle != null && bundle.getInt("miui_security_fragment_result", -1) == 0 ? -1 : 0);
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String key = preference.getKey();
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (!"biometric_weak_liveliness".equals(key)) {
                "unlock_tactile_feedback".equals(key);
            } else if (!booleanValue) {
                CheckBoxPreference checkBoxPreference = this.mBiometricWeakLiveliness;
                if (checkBoxPreference != null) {
                    checkBoxPreference.setChecked(true);
                }
                this.mChooseLockSettingsHelper.launchConfirmationActivity(getActivity(), this, 104, (CharSequence) null, (CharSequence) null);
            }
            return true;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            String key = preference.getKey();
            if ("turn_off_security_lock".equals(key)) {
                handleSecurityLockToggle();
            } else if (!"biometric_weak_improve_matching".equals(key)) {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            } else {
                if (!this.mChooseLockSettingsHelper.launchConfirmationActivity(getActivity(), this, 103, (CharSequence) null, (CharSequence) null)) {
                    startBiometricWeakImprove();
                }
            }
            return true;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            int i;
            String str;
            PreferenceCategory preferenceCategory;
            CheckBoxPreference checkBoxPreference;
            super.onResume();
            KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService("keyguard");
            if (keyguardManager == null || !keyguardManager.isKeyguardLocked()) {
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.removeAll();
                }
                addPreferencesFromResource(R.xml.security_settings_unlock_common);
                this.mSecurityLockToggle = (KeyguardRestrictedPreference) findPreference("turn_off_security_lock");
                this.mChangePassword = (KeyguardRestrictedListPreference) findPreference("change_password");
                this.mLockScreenCommonOptions = (PreferenceCategory) findPreference("lock_screen_common_options");
                this.mTurnOnOrOffAndChangePassword = (PreferenceCategory) findPreference("turn_and_change_password");
                int activePasswordQuality = this.mChooseLockSettingsHelper.utils().getActivePasswordQuality(UserHandle.myUserId());
                if (activePasswordQuality == 32768) {
                    i = R.xml.security_settings_biometric_weak;
                    str = "facial";
                } else if (activePasswordQuality == 65536) {
                    i = R.xml.security_settings_pattern;
                    str = "pattern";
                } else if (activePasswordQuality == 131072 || activePasswordQuality == 196608) {
                    i = R.xml.security_settings_pin;
                    str = "numerical";
                } else {
                    str = (activePasswordQuality == 262144 || activePasswordQuality == 327680 || activePasswordQuality == 393216) ? "mixed" : null;
                    i = -1;
                }
                if (i != -1) {
                    PreferenceScreen inflateFromResource = getPreferenceManager().inflateFromResource(getPreferenceManager().getContext(), i, null);
                    for (int i2 = 0; i2 < inflateFromResource.getPreferenceCount(); i2++) {
                        Preference preference = inflateFromResource.getPreference(i2);
                        inflateFromResource.removePreference(preference);
                        this.mLockScreenCommonOptions.addPreference(preference);
                    }
                }
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("biometric_weak_liveliness");
                this.mBiometricWeakLiveliness = checkBoxPreference2;
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setOnPreferenceChangeListener(this);
                }
                CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("unlock_tactile_feedback");
                this.mTactileFeedback = checkBoxPreference3;
                if (checkBoxPreference3 != null) {
                    checkBoxPreference3.setOnPreferenceChangeListener(this);
                }
                if (!((Vibrator) getSystemService("vibrator")).hasVibrator() && (preferenceCategory = this.mLockScreenCommonOptions) != null && (checkBoxPreference = this.mTactileFeedback) != null) {
                    preferenceCategory.removePreference(checkBoxPreference);
                }
                LockPatternUtils utils = this.mChooseLockSettingsHelper.utils();
                CheckBoxPreference checkBoxPreference4 = this.mTactileFeedback;
                if (checkBoxPreference4 != null) {
                    checkBoxPreference4.setChecked(utils.isTactileFeedbackEnabled());
                }
                if (this.mLockScreenCommonOptions.getPreferenceCount() == 0 && getPreferenceScreen() != null) {
                    getPreferenceScreen().removePreference(this.mLockScreenCommonOptions);
                }
                findPreference("change_password").setOnPreferenceChangeListener(this.mChangeSecurityLockPreferenceListener);
                if (!TextUtils.isEmpty(str)) {
                    ((KeyguardRestrictedListPreference) findPreference("change_password")).setValue(str);
                }
                KeyguardRestrictedListPreference keyguardRestrictedListPreference = (KeyguardRestrictedListPreference) findPreference("change_password");
                CharSequence[] entries = keyguardRestrictedListPreference.getEntries();
                CharSequence[] entryValues = keyguardRestrictedListPreference.getEntryValues();
                int i3 = 0;
                while (true) {
                    if (i3 >= entryValues.length) {
                        i3 = -1;
                        break;
                    } else if (entryValues[i3].equals("facial")) {
                        break;
                    } else {
                        i3++;
                    }
                }
                if (i3 != -1) {
                    CharSequence[] charSequenceArr = new CharSequence[entries.length - 1];
                    CharSequence[] charSequenceArr2 = new CharSequence[entryValues.length - 1];
                    for (int i4 = 0; i4 < entries.length; i4++) {
                        if (i4 != i3) {
                            if (i4 < i3) {
                                charSequenceArr[i4] = entries[i4];
                                charSequenceArr2[i4] = entryValues[i4];
                            } else {
                                int i5 = i4 - 1;
                                charSequenceArr[i5] = entries[i4];
                                charSequenceArr2[i5] = entryValues[i4];
                            }
                        }
                    }
                    keyguardRestrictedListPreference.setEntries(charSequenceArr);
                    keyguardRestrictedListPreference.setEntryValues(charSequenceArr2);
                }
                updatePreferencesByPasswordState();
            }
        }

        public void startBiometricWeakImprove() {
            Intent intent = new Intent();
            intent.setClassName("com.android.facelock", "com.android.facelock.AddToSetup");
            startActivity(intent);
        }
    }

    public static void setFragmentResultOnDetach(final Fragment fragment, final int i, final String str, final Bundle bundle) {
        if (fragment.getTargetFragment() == null || !MiuiKeyguardSettingsUtils.instanceofSettingsPreFragment(fragment.getTargetFragment())) {
            return;
        }
        new Handler().post(new Runnable() { // from class: com.android.settings.MiuiSecurityCommonSettings.1
            @Override // java.lang.Runnable
            public void run() {
                Bundle bundle2 = new Bundle();
                Bundle bundle3 = bundle;
                if (bundle3 != null) {
                    bundle2.putAll(bundle3);
                }
                bundle2.putInt("miui_security_fragment_result", i);
                bundle2.putString("password", str);
                MiuiKeyguardSettingsUtils.onFragmentResult(fragment.getTargetFragment(), fragment.getTargetRequestCode(), bundle2);
            }
        });
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", MiuiSecurityCommonSettingsFragment.class.getName());
        return intent;
    }
}
