package com.android.settings;

import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class LockSecureAfterTimeout extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class LockSecureAfterTimeoutFragment extends SettingsPreferenceFragment {
        private final List<RadioButtonPreference> mPrefs = new ArrayList();
        private final Preference.OnPreferenceChangeListener mPrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.LockSecureAfterTimeout.LockSecureAfterTimeoutFragment.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.Secure.putLong(LockSecureAfterTimeoutFragment.this.getContentResolver(), "enable_lock_screen_secure_after_timeout", Integer.parseInt(preference.getKey()));
                Iterator it = LockSecureAfterTimeoutFragment.this.mPrefs.iterator();
                while (true) {
                    boolean z = false;
                    if (!it.hasNext()) {
                        return false;
                    }
                    RadioButtonPreference radioButtonPreference = (RadioButtonPreference) it.next();
                    if (radioButtonPreference == preference) {
                        z = true;
                    }
                    radioButtonPreference.setChecked(z);
                }
            }
        };

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.lock_secure_after_timeout_settings);
            long j = Settings.Secure.getLong(getContentResolver(), "enable_lock_screen_secure_after_timeout", 0L);
            String[] stringArray = getResources().getStringArray(R.array.lock_screen_secure_after_timeout_values);
            String[] stringArray2 = getResources().getStringArray(R.array.lock_screen_secure_after_timeout_entries);
            for (int i = 0; i < stringArray.length; i++) {
                RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
                radioButtonPreference.setKey(stringArray[i]);
                radioButtonPreference.setTitle(stringArray2[i]);
                radioButtonPreference.setPersistent(false);
                radioButtonPreference.setOnPreferenceChangeListener(this.mPrefChangeListener);
                getPreferenceScreen().addPreference(radioButtonPreference);
                radioButtonPreference.setChecked(j == ((long) Integer.parseInt(stringArray[i])));
                this.mPrefs.add(radioButtonPreference);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, new LockSecureAfterTimeoutFragment()).commit();
        }
    }
}
