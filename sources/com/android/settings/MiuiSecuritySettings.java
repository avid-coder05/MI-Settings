package com.android.settings;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.faceunlock.MiuiFaceDataInput;
import com.android.settings.privacypassword.PrivacyPasswordManager;
import com.android.settings.privacypassword.TransparentHelper;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.securityspace.CrossUserUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSecuritySettings extends KeyguardSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Preference mAddOrManageFaceData;
    private Preference mAddOrManageFingerprint;
    private MiuiChooseLockSettingsHelper mChooseLockSettingsHelper;
    private DevicePolicyManager mDPM;
    private PreferenceCategory mFingerprintAndFaceUnlock;
    private boolean mFingerprintHardwareAvailable;
    private boolean mHasClickFaceUnlockOrFinger;
    private boolean mIsOled;
    private ListPreference mKeyguardNotificationStatusPref;
    private ValuePreference mLockScreenMagazine;
    private PackageManager mPackageManager;
    private Preference mPrivacyPassword;
    private KeyguardTimeoutListPreference mScreenTimeout;
    private PreferenceCategory mSecurityCategory;
    private CheckBoxPreference mWakeupForKeyguardNotificationPref;

    /* JADX INFO: Access modifiers changed from: private */
    public void addFaceData(int i) {
        startActivityForResult(new Intent(getActivity(), MiuiFaceDataInput.class), 108);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addFingerprint(int i) {
        if (i >= 5) {
            showInformationDialog(R.string.max_fingerprint_number_reached);
        } else {
            startActivityForResult(new Intent(getActivity(), NewFingerprintInternalActivity.class), 107);
        }
    }

    private int convertNotificationStatusToPrefIndex(int i) {
        String[] stringArray = this.mKeyguardNotificationStatusPref.getContext().getResources().getStringArray(R.array.keyguard_notification_status_values);
        for (int i2 = 0; i2 < stringArray.length; i2++) {
            if (i == Integer.valueOf(stringArray[i2]).intValue()) {
                return i2;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createNewPassword() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("add_keyguard_password_then_add_fingerprint", !keyguardPasswordExisted());
        startFragment(this, "com.android.settings.MiuiSecurityChooseUnlock$MiuiSecurityChooseUnlockFragment", 0, bundle, R.string.password_entrance_title);
    }

    private static ComponentName getSettingsComponent(Context context, String str) {
        try {
            String string = context.getContentResolver().call(Uri.parse("content://" + str), "getSettingsComponent", (String) null, (Bundle) null).getString("result_string");
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            return ComponentName.unflattenFromString(string);
        } catch (Exception unused) {
            return null;
        }
    }

    public static Intent getWallpaperIntent(Context context) {
        Intent intent;
        Iterator<ResolveInfo> it = context.getPackageManager().queryIntentContentProviders(new Intent("miui.intent.action.LOCKWALLPAPER_PROVIDER"), 0).iterator();
        while (true) {
            if (!it.hasNext()) {
                return null;
            }
            String str = it.next().providerInfo.authority;
            try {
            } catch (Exception e) {
                Log.e(MiuiSecuritySettings.class.getName(), "call lockscreen magazine provider  throw an exception" + e);
            }
            if (isProviderEnabled(context, str)) {
                if ("com.xiaomi.tv.gallerylockscreen.lockscreen_magazine_provider".equals(str)) {
                    intent = new Intent("android.intent.action.VIEW", Uri.parse("mifg://fashiongallery/jump_setting"));
                } else {
                    if ("IN".equalsIgnoreCase(Build.getRegion())) {
                        intent = new Intent("com.miui.android.fashiongallery.setting.SETTING");
                    }
                    intent = null;
                }
                if ((intent != null ? context.getPackageManager().resolveActivity(intent, 64) : null) != null) {
                    break;
                }
                intent = new Intent();
                intent.setComponent(getSettingsComponent(context, str));
                if (context.getPackageManager().resolveActivity(intent, 64) != null) {
                    break;
                }
            }
        }
        return intent;
    }

    private void handleSecurityLockToggle() {
        if (keyguardPasswordExisted()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("add_keyguard_password_then_add_fingerprint", !keyguardPasswordExisted());
            startFragment(this, "com.android.settings.MiuiSecurityCommonSettings$MiuiSecurityCommonSettingsFragment", -1, bundle, R.string.password_entrance_title);
            return;
        }
        final FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
        List<String> fingerprintIds = fingerprintHelper.getFingerprintIds();
        if (!fingerprintHelper.isHardwareDetected() || fingerprintIds.size() <= 0) {
            createNewPassword();
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecuritySettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    fingerprintHelper.removeAllFingerprint(null);
                }
                MiuiSecuritySettings.this.createNewPassword();
            }
        };
        new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setMessage(R.string.delete_or_keep_legacy_passwords_confirm_msg).setPositiveButton(R.string.delete_legacy_fingerprint, onClickListener).setNegativeButton(R.string.keep_legacy_fingerprint, onClickListener).create().show();
    }

    private void initKeyguardNotificationPref() {
        if (!MiuiKeyguardSettingsUtils.isSupportAodAnimateDevice(getActivity())) {
            this.mWakeupForKeyguardNotificationPref.setChecked(MiuiKeyguardSettingsUtils.isWakeupForNotification(getActivity(), getContentResolver()));
            ListPreference listPreference = this.mKeyguardNotificationStatusPref;
            if (listPreference != null) {
                this.mSecurityCategory.removePreference(listPreference);
                return;
            }
            return;
        }
        if (this.mKeyguardNotificationStatusPref != null) {
            this.mKeyguardNotificationStatusPref.setValueIndex(convertNotificationStatusToPrefIndex(MiuiKeyguardSettingsUtils.getKeyguardNotificationStatus(getActivity(), getContentResolver())));
            ListPreference listPreference2 = this.mKeyguardNotificationStatusPref;
            listPreference2.setSummary(listPreference2.getEntry());
            this.mKeyguardNotificationStatusPref.setOnPreferenceChangeListener(this);
        }
        CheckBoxPreference checkBoxPreference = this.mWakeupForKeyguardNotificationPref;
        if (checkBoxPreference != null) {
            this.mSecurityCategory.removePreference(checkBoxPreference);
        }
    }

    private void initLockScreenMagazine() {
        if (Build.IS_TABLET) {
            this.mSecurityCategory.removePreference(this.mLockScreenMagazine);
        } else {
            new AsyncTask<Void, Void, Intent>() { // from class: com.android.settings.MiuiSecuritySettings.4
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Intent doInBackground(Void... voidArr) {
                    if (MiuiSecuritySettings.this.getActivity() == null) {
                        return null;
                    }
                    return MiuiSecuritySettings.getWallpaperIntent(MiuiSecuritySettings.this.getActivity());
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Intent intent) {
                    if (MiuiSecuritySettings.this.getActivity() != null) {
                        if (intent != null) {
                            MiuiSecuritySettings.this.mLockScreenMagazine.setIntent(intent);
                        } else {
                            MiuiSecuritySettings.this.mSecurityCategory.removePreference(MiuiSecuritySettings.this.mLockScreenMagazine);
                        }
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public static boolean isLockScreenMagazineAvailable(Context context) {
        return (Build.IS_TABLET || getWallpaperIntent(context) == null) ? false : true;
    }

    public static boolean isMiShowMode(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "disable_security_by_mishow", 0) == 1;
    }

    private static boolean isProviderEnabled(Context context, String str) {
        return "com.xiaomi.tv.gallerylockscreen.lockscreen_magazine_provider".equals(str) || "com.miui.android.fashiongallery.lockscreen_magazine_provider".equals(str);
    }

    private boolean isToggled(Preference preference) {
        return ((CheckBoxPreference) preference).isChecked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean keyguardPasswordExisted() {
        return this.mChooseLockSettingsHelper.utils().getActivePasswordQuality(UserHandle.myUserId()) != 0;
    }

    private void setupTimeoutPreference() {
        this.mScreenTimeout.setValue(String.valueOf(Settings.System.getLong(getActivity().getContentResolver(), "screen_off_timeout", 30000L)));
        this.mScreenTimeout.disableUnusableTimeouts();
    }

    private void showInformationDialog(int i) {
        showInformationDialog(getString(i));
    }

    private void showInformationDialog(String str) {
        new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setMessage(str).setPositiveButton(R.string.information_dialog_button_text, (DialogInterface.OnClickListener) null).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toFaceManageFragment() {
        startFragment(this, "com.android.settings.faceunlock.FaceManageFragment$FaceManageFragment", -1, (Bundle) null, R.string.privacy_password_use_finger_dialog_title);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toFingerprintManageFragment() {
        startFragment(this, "com.android.settings.FingerprintManageSetting$FingerprintManageFragment", -1, (Bundle) null, R.string.privacy_password_use_finger_dialog_title);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 87;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSecuritySettings.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1001) {
            if (i2 == -1) {
                toFingerprintManageFragment();
            }
        } else if (i == 1002 && i2 == -1) {
            toFaceManageFragment();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mChooseLockSettingsHelper = new MiuiChooseLockSettingsHelper(getActivity());
        this.mDPM = (DevicePolicyManager) getSystemService("device_policy");
        this.mPackageManager = getPackageManager();
        this.mIsOled = "oled".equals(SystemProperties.get("ro.vendor.display.type")) || "oled".equals(SystemProperties.get("ro.display.type"));
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mKeyguardNotificationStatusPref) {
            int parseInt = Integer.parseInt((String) obj);
            int convertNotificationStatusToPrefIndex = convertNotificationStatusToPrefIndex(parseInt);
            this.mKeyguardNotificationStatusPref.setSummary(preference.getContext().getResources().getStringArray(R.array.aod_notification_status_entries)[convertNotificationStatusToPrefIndex]);
            Settings.System.putInt(getContentResolver(), "wakeup_for_keyguard_notification", parseInt);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        LockPatternUtils utils = this.mChooseLockSettingsHelper.utils();
        if ("unlock_set_or_change".equals(key)) {
            handleSecurityLockToggle();
        } else if (!"lockenabled".equals(key)) {
            if ("power_button_instantly_locks".equals(key)) {
                utils.setPowerButtonInstantlyLocks(isToggled(preference), UserHandle.myUserId());
            } else if (preference == this.mWakeupForKeyguardNotificationPref) {
                Settings.System.putInt(getContentResolver(), "wakeup_for_keyguard_notification", ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            } else if ("privacy_password".equals(key)) {
                startActivity(new Intent(getActivity(), TransparentHelper.class));
            } else if ("keyguard_privacy_policy".equals(key)) {
                String locale = Locale.getDefault().toString();
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.keyguard_setting_privacy_policy_url, Build.getRegion(), locale))));
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        boolean z;
        super.onResume();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        addPreferencesFromResource(R.xml.security_settings);
        PreferenceScreen preferenceScreen2 = getPreferenceScreen();
        addPreferencesFromResource(R.xml.security_settings_common);
        this.mSecurityCategory = (PreferenceCategory) findPreference("security_category");
        KeyguardTimeoutListPreference keyguardTimeoutListPreference = (KeyguardTimeoutListPreference) preferenceScreen2.findPreference("screen_timeout");
        this.mScreenTimeout = keyguardTimeoutListPreference;
        if (keyguardTimeoutListPreference != null) {
            setupTimeoutPreference();
            this.mScreenTimeout.updateTimeoutPreferenceSummary();
        }
        boolean z2 = getActivity() instanceof MiuiPasswordGuardActivity;
        this.mHasClickFaceUnlockOrFinger = false;
        FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
        this.mFingerprintHardwareAvailable = fingerprintHelper.isHardwareDetected();
        this.mFingerprintAndFaceUnlock = (PreferenceCategory) findPreference("fingerprint_and_faceunlock");
        this.mAddOrManageFingerprint = findPreference("add_or_manage_fingerprint");
        if (this.mFingerprintHardwareAvailable && UserHandle.myUserId() == 0) {
            List<String> fingerprintIds = fingerprintHelper.getFingerprintIds();
            final int size = fingerprintIds == null ? 0 : fingerprintIds.size();
            this.mAddOrManageFingerprint.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.MiuiSecuritySettings.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    int i = size;
                    if (i == 0) {
                        MiuiSecuritySettings.this.addFingerprint(i);
                        return true;
                    } else if (!MiuiSecuritySettings.this.keyguardPasswordExisted()) {
                        MiuiSecuritySettings.this.toFingerprintManageFragment();
                        return true;
                    } else {
                        Intent intent = new Intent(MiuiSecuritySettings.this.getActivity(), MiuiConfirmCommonPassword.class);
                        intent.putExtra(":android:show_fragment_title", R.string.empty_title);
                        MiuiSecuritySettings.this.startActivityForResult(intent, 1001);
                        return true;
                    }
                }
            });
            if (size != 0) {
                this.mAddOrManageFingerprint.setTitle(R.string.manage_fingerprint_text);
            } else {
                this.mAddOrManageFingerprint.setTitle(R.string.add_fingerprint_text);
            }
            z = true;
        } else {
            this.mFingerprintAndFaceUnlock.removePreference(this.mAddOrManageFingerprint);
            z = false;
        }
        this.mAddOrManageFaceData = findPreference("add_or_manage_face_recoginition");
        if (KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(getActivity()) && UserHandle.myUserId() == 0) {
            this.mFingerprintAndFaceUnlock.setTitle(R.string.fingerprint_first_open_screen_password_face_tittle);
            List<String> enrolledFaceList = KeyguardSettingsFaceUnlockUtils.getEnrolledFaceList(getActivity());
            final int size2 = enrolledFaceList == null ? 0 : enrolledFaceList.size();
            this.mAddOrManageFaceData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.MiuiSecuritySettings.2
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    int i = size2;
                    if (i == 0) {
                        MiuiSecuritySettings.this.addFaceData(i);
                        return true;
                    } else if (!MiuiSecuritySettings.this.keyguardPasswordExisted()) {
                        MiuiSecuritySettings.this.toFaceManageFragment();
                        return true;
                    } else {
                        Intent intent = new Intent(MiuiSecuritySettings.this.getActivity(), MiuiConfirmCommonPassword.class);
                        intent.putExtra(":android:show_fragment_title", R.string.empty_title);
                        MiuiSecuritySettings.this.startActivityForResult(intent, 1002);
                        return true;
                    }
                }
            });
            if (size2 != 0) {
                this.mAddOrManageFaceData.setTitle(R.string.rgb_manage_facerecoginition_text);
            } else {
                this.mAddOrManageFaceData.setTitle(R.string.add_facerecoginition_text);
            }
            z = true;
        } else {
            this.mFingerprintAndFaceUnlock.removePreference(this.mAddOrManageFaceData);
        }
        if (!z) {
            getPreferenceScreen().removePreference(this.mFingerprintAndFaceUnlock);
        }
        Preference findPreference = findPreference("unlock_set_or_change");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("security_second_category");
        if (UserHandle.myUserId() != 0 && !CrossUserUtils.isAirSpace(getActivity(), UserHandle.myUserId())) {
            preferenceCategory.removePreference(findPreference);
        } else if (keyguardPasswordExisted()) {
            findPreference.setSummary(R.string.password_turned_on_text);
        } else {
            findPreference.setSummary(R.string.password_turned_off_text);
        }
        this.mPrivacyPassword = findPreference("privacy_password");
        if (PrivacyPasswordManager.getInstance(getActivity()).havePattern()) {
            this.mPrivacyPassword.setSummary(R.string.password_turned_on_text);
        } else {
            this.mPrivacyPassword.setSummary(R.string.password_turned_off_text);
        }
        this.mWakeupForKeyguardNotificationPref = (CheckBoxPreference) findPreference("wakeup_for_keyguard_notification");
        this.mKeyguardNotificationStatusPref = (ListPreference) findPreference("keyguard_notification_status");
        initKeyguardNotificationPref();
        ValuePreference valuePreference = (ValuePreference) findPreference("lockscreen_magazine");
        this.mLockScreenMagazine = valuePreference;
        valuePreference.setShowRightArrow(true);
        initLockScreenMagazine();
        if (Settings.Secure.getInt(getContentResolver(), "disable_security_by_mishow", 0) == 1) {
            this.mFingerprintAndFaceUnlock.setEnabled(false);
            preferenceCategory.setEnabled(false);
        }
    }
}
