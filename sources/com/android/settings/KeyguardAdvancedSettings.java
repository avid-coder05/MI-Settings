package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.compat.RestrictedLockUtilsCompat;
import miui.content.res.ThemeResources;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class KeyguardAdvancedSettings extends KeyguardSettingsPreferenceFragment {
    private Preference mBluetoothUnlock;
    private boolean mHasPassword;
    private LockPatternUtils mLockPatternUtils;
    private Preference mSmartCoverPref;
    private boolean mSupportHallSensor;

    public static boolean isEllipticProximity(Context context) {
        return SystemProperties.getBoolean("ro.vendor.audio.us.proximity", false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return KeyguardAdvancedSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        this.mLockPatternUtils = new LockPatternUtils(getActivity());
        addPreferencesFromResource(R.xml.keyguard_advanced_settings);
        this.mSmartCoverPref = findPreference("smartcover_sensitive_small_win_sensor");
        this.mBluetoothUnlock = findPreference("bluetooth_unlock");
        if (UserHandle.myUserId() != 0) {
            getPreferenceScreen().removePreference(this.mBluetoothUnlock);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if ("lock_screen_signature".equals(key)) {
            Bundle bundle = new Bundle();
            bundle.putString(":miui:starting_window_label", "");
            startFragment(this, "com.android.settings.OwnerInfoSettings", 0, bundle, 0);
        } else if ("bluetooth_unlock".equals(key)) {
            startFragment(this, "com.android.settings.MiuiSecurityBluetoothSettingsFragment", -1, (Bundle) null, R.string.bluetooth_unlock_title);
        } else if ("smartcover_lock_or_unlock_screen".equals(key)) {
            MiuiSettings.System.setSmartCoverMode(((CheckBoxPreference) preference).isChecked());
        } else if ("smartcover_sensitive_small_win_sensor".equals(key)) {
            startFragment(this, "com.android.settings.MiuiSmartCoverSettingsFragment", -1, (Bundle) null, R.string.smartcover_sensitive_title);
        } else if ("choose_keyguard_clock".equals(key)) {
            Intent intent = new Intent();
            intent.setClassName(ThemeResources.SYSTEMUI_NAME, "com.android.keyguard.settings.ChooseKeyguardClockActivity");
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setOwnerInfoDisabledIfNeed();
        this.mHasPassword = MiuiSettings.Secure.hasCommonPassword(getActivity());
        this.mSupportHallSensor = FeatureParser.getBoolean("support_hall_sensor", false);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }
        if (!this.mSupportHallSensor) {
            preferenceScreen.removePreference(this.mSmartCoverPref);
        } else if (!FeatureParser.getBoolean("support_multiple_small_win_cover", false)) {
            preferenceScreen.removePreference(this.mSmartCoverPref);
            MiuiSettings.System.setSmartCoverMode(SystemProperties.getInt("persist.sys.smartcover_mode", -1) != 0);
        }
        MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(getActivity());
        this.mBluetoothUnlock.setEnabled(this.mHasPassword);
        if (miuiLockPatternUtils.getBluetoothUnlockEnabled()) {
            this.mBluetoothUnlock.setSummary(R.string.bluetooth_unlock_turned_on);
        } else {
            this.mBluetoothUnlock.setSummary(R.string.bluetooth_unlock_turned_off);
        }
        if (getActivity().getPackageManager().hasSystemFeature("android.hardware.sensor.proximity") && !isEllipticProximity(getActivity()) && Settings.Global.getInt(getContentResolver(), "enable_screen_on_proximity_sensor", -1) == -1) {
            MiuiSettings.Global.putBoolean(getContentResolver(), "enable_screen_on_proximity_sensor", MiuiSettings.System.getBoolean(getContentResolver(), "enable_screen_on_proximity_sensor", getResources().getBoolean(285540416)));
        }
    }

    public void setOwnerInfoDisabledIfNeed() {
        KeyguardRestrictedPreference keyguardRestrictedPreference = (KeyguardRestrictedPreference) findPreference("lock_screen_signature");
        if (keyguardRestrictedPreference != null) {
            if (this.mLockPatternUtils.isDeviceOwnerInfoEnabled()) {
                keyguardRestrictedPreference.setDisabledByAdmin(RestrictedLockUtilsCompat.getDeviceOwner(getActivity()));
                return;
            }
            keyguardRestrictedPreference.setDisabledByAdmin(null);
            keyguardRestrictedPreference.setEnabled(!this.mLockPatternUtils.isLockScreenDisabled(UserHandle.myUserId()));
        }
    }
}
