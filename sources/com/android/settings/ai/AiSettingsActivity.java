package com.android.settings.ai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ai.PreferenceHelper;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AiSettingsActivity extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class AiSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
        private ValuePreference mClickPref;
        private ValuePreference mDoubleTaskPref;
        private ValuePreference mLongTaskPref;

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            ValuePreference valuePreference = (ValuePreference) findPreference("key_click_task");
            this.mClickPref = valuePreference;
            valuePreference.setOnPreferenceClickListener(this);
            this.mClickPref.setShowRightArrow(true);
            ValuePreference valuePreference2 = (ValuePreference) findPreference("key_long_task");
            this.mLongTaskPref = valuePreference2;
            valuePreference2.setOnPreferenceClickListener(this);
            this.mLongTaskPref.setShowRightArrow(true);
            ValuePreference valuePreference3 = (ValuePreference) findPreference("key_double_task");
            this.mDoubleTaskPref = valuePreference3;
            valuePreference3.setOnPreferenceClickListener(this);
            this.mDoubleTaskPref.setShowRightArrow(true);
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.ai_settings);
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(getActivity(), AiSettingsSubActivity.class);
            intent.putExtra("type", this.mLongTaskPref == preference ? "key_long_press_down_ai_button_settings" : this.mDoubleTaskPref == preference ? "key_double_click_ai_button_settings" : "key_single_click_ai_button_settings");
            getActivity().startActivity(intent);
            return true;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            updateValues();
        }

        public void updateValues() {
            String[] strArr = {"key_single_click_ai_button_settings", "key_long_press_down_ai_button_settings", "key_double_click_ai_button_settings"};
            String[] stringArray = getResources().getStringArray(R.array.ai_key_item_name);
            for (int i = 0; i < 3; i++) {
                String str = strArr[i];
                AiSettingsItem pressAiButtonSettings = PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(getActivity(), str);
                if (!TextUtils.isEmpty(pressAiButtonSettings.name)) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= 8) {
                            break;
                        } else if (TextUtils.equals(pressAiButtonSettings.name, stringArray[i2])) {
                            pressAiButtonSettings.mIndex = i2;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (TextUtils.equals(str, "key_single_click_ai_button_settings")) {
                        this.mClickPref.setValue(pressAiButtonSettings.name);
                    } else if (TextUtils.equals(str, "key_long_press_down_ai_button_settings")) {
                        this.mLongTaskPref.setValue(pressAiButtonSettings.name);
                    } else if (TextUtils.equals(str, "key_double_click_ai_button_settings")) {
                        this.mDoubleTaskPref.setValue(pressAiButtonSettings.name);
                    }
                    PreferenceHelper.AiSettingsPreferenceHelper.setPressAiButtonSettings(getActivity(), str, pressAiButtonSettings);
                } else if (TextUtils.equals(str, "key_single_click_ai_button_settings")) {
                    this.mClickPref.setValue(stringArray[pressAiButtonSettings.mIndex]);
                } else if (TextUtils.equals(str, "key_long_press_down_ai_button_settings")) {
                    this.mLongTaskPref.setValue(stringArray[pressAiButtonSettings.mIndex]);
                } else if (TextUtils.equals(str, "key_double_click_ai_button_settings")) {
                    this.mDoubleTaskPref.setValue(stringArray[pressAiButtonSettings.mIndex]);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, new AiSettingsFragment()).commit();
        }
        if (getAppCompatActionBar() != null) {
            getAppCompatActionBar().setTitle(getResources().getString(R.string.ai_button_title_global));
        }
    }
}
