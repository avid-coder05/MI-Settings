package com.android.settings.ad;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.report.InternationalCompat;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import miui.os.Build;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AdServiceSettings extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class AdServiceSettingsFragment extends SettingsPreferenceFragment {
        private ContentResolver mContentResolver;
        CheckBoxPreference mPersonalizedAdSwitch;

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mContentResolver = getContentResolver();
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("personalized_ad_switch");
            this.mPersonalizedAdSwitch = checkBoxPreference;
            checkBoxPreference.setSummary(Build.IS_INTERNATIONAL_BUILD ? R.string.use_personalized_ad_service_summary_for_global : R.string.use_personalized_ad_service_summary);
            this.mPersonalizedAdSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.ad.AdServiceSettings.AdServiceSettingsFragment.1
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    try {
                        MiuiSettings.Ad.setPersonalizedAdEnable(AdServiceSettingsFragment.this.mContentResolver, ((Boolean) obj).booleanValue());
                        MiuiSettings.Ad.setPersonalizedAdEnableTime(AdServiceSettingsFragment.this.mContentResolver, System.currentTimeMillis());
                        OneTrackInterfaceUtils.trackSwitchEvent("AdServiceSettings_switch", ((Boolean) obj).booleanValue());
                        InternationalCompat.trackReportSwitchStatus("setting_Passwords_sec_ad", obj);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return true;
                    }
                }
            });
            this.mPersonalizedAdSwitch.setChecked(MiuiSettings.Ad.isPersonalizedAdEnabled(this.mContentResolver));
            AdFooterPreference adFooterPreference = new AdFooterPreference(this, getPrefContext());
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                preferenceScreen.addPreference(adFooterPreference);
            }
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.ad_service_settings);
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, new AdServiceSettingsFragment()).commit();
        }
    }
}
