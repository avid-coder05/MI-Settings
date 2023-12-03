package com.android.settings;

import android.os.Bundle;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AODSettingActivity extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class SettingsFragment extends SettingsPreferenceFragment {
        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            addPreferencesFromResource(R.xml.aod_fake_settings);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getSupportFragmentManager().beginTransaction().replace(16908290, new SettingsFragment()).commit();
    }
}
