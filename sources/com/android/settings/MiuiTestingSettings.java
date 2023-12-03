package com.android.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import miui.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class MiuiTestingSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private PreferenceScreen mPhone1Preference;
    private PreferenceScreen mPhone2Preference;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 89;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.testing_settings);
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("phone1");
        this.mPhone1Preference = preferenceScreen;
        preferenceScreen.setOnPreferenceClickListener(this);
        PreferenceScreen preferenceScreen2 = (PreferenceScreen) findPreference("phone2");
        this.mPhone2Preference = preferenceScreen2;
        preferenceScreen2.setOnPreferenceClickListener(this);
        int i = SystemProperties.getInt("ro.miui.singlesim", 0);
        if (!TelephonyManager.getDefault().isMultiSimEnabled() || i == 1) {
            getPreferenceScreen().removePreference(this.mPhone2Preference);
        } else {
            this.mPhone1Preference.setTitle(((Object) this.mPhone1Preference.getTitle()) + "1");
            this.mPhone2Preference.setTitle(((Object) this.mPhone2Preference.getTitle()) + "2");
        }
        if (UserManager.get(getContext()).isSystemUser()) {
            return;
        }
        getPreferenceScreen().removePreference(this.mPhone1Preference);
        getPreferenceScreen().removePreference(this.mPhone2Preference);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mPhone1Preference || preference == this.mPhone2Preference) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.phone", "com.android.phone.settings.RadioInfo"));
            intent.setAction("android.intent.action.MAIN");
            intent.putExtra("phone_id", preference == this.mPhone1Preference ? "phone1" : "phone2");
            startActivity(intent);
            return true;
        }
        return false;
    }
}
