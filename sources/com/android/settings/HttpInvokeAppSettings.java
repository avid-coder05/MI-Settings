package com.android.settings;

import android.os.Bundle;
import android.provider.MiuiSettings;
import android.view.View;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class HttpInvokeAppSettings extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class HttpInvokeAppSettingsFragment extends SettingsPreferenceFragment {
        private CheckBoxPreference mHttpInvokeAppCheckBox;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.core.InstrumentedPreferenceFragment
        public int getPreferenceScreenResId() {
            return R.xml.http_invoke_app_settings;
        }

        public boolean isHttpInvokeAppEnabled() {
            return MiuiSettings.Secure.isHttpInvokeAppEnable(getContentResolver());
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("http_invoke_app");
            this.mHttpInvokeAppCheckBox = checkBoxPreference;
            checkBoxPreference.setChecked(isHttpInvokeAppEnabled());
            this.mHttpInvokeAppCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.HttpInvokeAppSettings.HttpInvokeAppSettingsFragment.1
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    Boolean bool = (Boolean) obj;
                    MiuiSettings.Secure.enableHttpInvokeApp(HttpInvokeAppSettingsFragment.this.getContentResolver(), bool.booleanValue());
                    OneTrackInterfaceUtils.trackSwitchEvent("http_invoke_app", bool.booleanValue());
                    return true;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, new HttpInvokeAppSettingsFragment()).commit();
        }
    }
}
