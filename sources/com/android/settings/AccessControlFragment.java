package com.android.settings;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.SystemSettings$Secure;
import android.security.ChooseLockSettingsHelper;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.device.MiuiAboutPhoneUtils;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class AccessControlFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mAcPrivacylEnabled;
    private CheckBoxPreference mAcessControlEnabled;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private boolean mIsPad;
    private CheckBoxPreference mPrivacyEnabled;
    private final ContentObserver mPrivacyModeObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.AccessControlFragment.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            AccessControlFragment.this.updatePrivacyMode();
        }
    };
    private ContentResolver mResolver;
    private CheckBoxPreference mVisiblePatternEnabled;

    private void initAcPrivacy(PreferenceScreen preferenceScreen) {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen.findPreference("privacy_mode_enable");
        this.mPrivacyEnabled = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        ContentResolver contentResolver = getContentResolver();
        this.mResolver = contentResolver;
        contentResolver.registerContentObserver(Settings.Secure.getUriFor(SystemSettings$Secure.PRIVACY_MODE_ENABLED), false, this.mPrivacyModeObserver);
    }

    private boolean isVisibilePattern() {
        return MiuiAboutPhoneUtils.getBooleanPreference(getActivity(), "ac_visiblepattern");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVisibilePattern(boolean z) {
        MiuiAboutPhoneUtils.setBooleanPreference(getActivity(), "ac_visiblepattern", z);
    }

    private void updateAcPrivacylEnabledPreference() {
        boolean isPasswordForPrivacyModeEnabled = this.mChooseLockSettingsHelper.isPasswordForPrivacyModeEnabled();
        Intent intent = new Intent(getActivity(), ConfirmAccessControl.class);
        if (isPasswordForPrivacyModeEnabled) {
            intent.putExtra("confirm_purpose", 3);
        } else {
            intent.putExtra("confirm_purpose", 2);
        }
        startActivity(intent);
    }

    private void updateAcessControlEnabledPreference() {
        if (!this.mChooseLockSettingsHelper.isACLockEnabled()) {
            startActivityForResult(new Intent(getActivity(), ChooseAccessControl.class), 100);
            return;
        }
        Intent intent = new Intent(getActivity(), ConfirmAccessControl.class);
        intent.putExtra("confirm_purpose", 1);
        startActivity(intent);
    }

    private void updatePrivacyEnabledPreference(Preference preference) {
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
        if (!this.mChooseLockSettingsHelper.isPrivacyModeEnabled()) {
            checkBoxPreference.setChecked(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.privacy_mode_dialog_title);
            builder.setMessage(R.string.privacy_mode_dialog_message);
            builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.AccessControlFragment.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    AccessControlFragment.this.mChooseLockSettingsHelper.setPrivacyModeEnabled(true);
                    checkBoxPreference.setChecked(true);
                }
            });
            builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.AccessControlFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        } else if (!this.mChooseLockSettingsHelper.isACLockEnabled() || !this.mChooseLockSettingsHelper.isPasswordForPrivacyModeEnabled()) {
            this.mChooseLockSettingsHelper.setPrivacyModeEnabled(false);
        } else {
            Intent intent = new Intent("android.app.action.CONFIRM_ACCESS_CONTROL");
            intent.putExtra("confirm_purpose", 4);
            intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
            intent.addFlags(536870912);
            getActivity().startActivity(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePrivacyMode() {
        CheckBoxPreference checkBoxPreference = this.mPrivacyEnabled;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(this.mChooseLockSettingsHelper.isPrivacyModeEnabled());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return AccessControlFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIsPad = FeatureParser.getBoolean("is_pad", false);
        this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
        addPreferencesFromResource(R.xml.access_control);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen.findPreference("ac_enable");
        this.mAcessControlEnabled = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) preferenceScreen.findPreference("ac_visiblepattern");
        this.mVisiblePatternEnabled = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.AccessControlFragment.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                AccessControlFragment.this.setVisibilePattern(((Boolean) obj).booleanValue());
                return true;
            }
        });
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) preferenceScreen.findPreference("ac_privacy_mode");
        this.mAcPrivacylEnabled = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        initAcPrivacy(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("ac_privacy_mode_category");
        if (preferenceCategory != null) {
            preferenceScreen.removePreference(preferenceCategory);
        }
        if (this.mIsPad) {
            preferenceScreen.removePreference(preferenceScreen.findPreference("ac_enable_phone"));
            return;
        }
        Preference findPreference = preferenceScreen.findPreference("ac_set_apps");
        Preference findPreference2 = preferenceScreen.findPreference("ac_visiblepattern");
        preferenceScreen.removePreference(this.mAcessControlEnabled);
        preferenceScreen.removePreference(findPreference);
        preferenceScreen.removePreference(findPreference2);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mResolver.unregisterContentObserver(this.mPrivacyModeObserver);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("privacy_mode_enable".equals(preference.getKey())) {
            updatePrivacyEnabledPreference(preference);
            return true;
        } else if ("ac_enable".equals(preference.getKey())) {
            updateAcessControlEnabledPreference();
            return true;
        } else if ("ac_privacy_mode".equals(preference.getKey())) {
            updateAcPrivacylEnabledPreference();
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("ac_enable_phone".equals(preference.getKey())) {
            Intent intent = new Intent("com.miui.securitycenter.action.TRANSITION");
            intent.putExtra("enter_way", "00005");
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updatePrivacyMode();
        boolean isACLockEnabled = this.mChooseLockSettingsHelper.isACLockEnabled();
        this.mAcPrivacylEnabled.setEnabled(isACLockEnabled);
        if (this.mIsPad) {
            this.mAcessControlEnabled.setChecked(isACLockEnabled);
        }
        this.mAcPrivacylEnabled.setChecked(this.mChooseLockSettingsHelper.isPasswordForPrivacyModeEnabled());
        this.mVisiblePatternEnabled.setChecked(isVisibilePattern());
    }
}
