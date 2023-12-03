package com.android.settings;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class UsageAndDiagnosticsActivity extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class UsageAndDiagnosticsFragment extends SettingsPreferenceFragment {
        private ContentResolver mContentResolver;
        CheckBoxPreference mPersonalizedAdSwitch;

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mContentResolver = getContentResolver();
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("uplaod_log_switch");
            this.mPersonalizedAdSwitch = checkBoxPreference;
            checkBoxPreference.setChecked(MiuiSettings.Secure.isUploadDebugLogEnable(this.mContentResolver));
            this.mPersonalizedAdSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.UsageAndDiagnosticsActivity.UsageAndDiagnosticsFragment.1
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    try {
                        MiuiSettings.Secure.enableUploadDebugLog(UsageAndDiagnosticsFragment.this.mContentResolver, ((Boolean) obj).booleanValue());
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return true;
                    }
                }
            });
            UsageAndDiagnosticsFooterPreference usageAndDiagnosticsFooterPreference = new UsageAndDiagnosticsFooterPreference(this, getPrefContext());
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                preferenceScreen.addPreference(usageAndDiagnosticsFooterPreference);
            }
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.usage_and_diagnostics);
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, new UsageAndDiagnosticsFragment()).commit();
        }
    }
}
