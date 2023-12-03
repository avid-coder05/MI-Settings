package com.android.settings.inputmethod;

import android.os.Bundle;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miuix.visual.check.VisualCheckGroup;

/* loaded from: classes.dex */
public class SecurityIMESettingFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private FullScreenInputMethodPreference mNumKeyboardPreference;
    private CheckBoxPreference mSimeEnablePreference;

    private boolean isSecImeNumRandomOder() {
        return MiuiSettings.Secure.getBoolean(getContentResolver(), "security_ime_num_is_random", false);
    }

    private void updateSecIMEPreference(boolean z) {
        MiuiSettings.Secure.putBoolean(getContentResolver(), "enable_miui_security_ime", z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSecImeNumRandomOrder(boolean z) {
        this.mNumKeyboardPreference.setHighKeyboardChecked(!z);
        MiuiSettings.Secure.putBoolean(getContentResolver(), "security_ime_num_is_random", z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.security_keyboard_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSimeEnablePreference = (CheckBoxPreference) findPreference("miui_security_ime_enable");
        this.mNumKeyboardPreference = (FullScreenInputMethodPreference) findPreference("num_keyboard_order_setting");
        this.mSimeEnablePreference.setChecked(MiuiSettings.Secure.getBoolean(getContentResolver(), "enable_miui_security_ime", true));
        this.mSimeEnablePreference.setOnPreferenceChangeListener(this);
        this.mNumKeyboardPreference.setHighText(R.string.security_input_num_order);
        this.mNumKeyboardPreference.setDefaultText(R.string.security_input_num_random_order);
        this.mNumKeyboardPreference.setHighImage(R.drawable.security_input_num_order_icon);
        this.mNumKeyboardPreference.setDefaultImage(R.drawable.security_input_num_random_order_icon);
        this.mNumKeyboardPreference.setHighKeyboardChecked(!isSecImeNumRandomOder());
        this.mNumKeyboardPreference.setOnCheckedChangeListener(new VisualCheckGroup.OnCheckedChangeListener() { // from class: com.android.settings.inputmethod.SecurityIMESettingFragment.1
            @Override // miuix.visual.check.VisualCheckGroup.OnCheckedChangeListener
            public void onCheckedChanged(VisualCheckGroup visualCheckGroup, int i) {
                if (i == R.id.high_keyboard) {
                    SecurityIMESettingFragment.this.updateSecImeNumRandomOrder(false);
                } else if (i == R.id.default_keyboard) {
                    SecurityIMESettingFragment.this.updateSecImeNumRandomOrder(true);
                }
            }
        });
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("miui_security_ime_enable".equals(preference.getKey()) && (obj instanceof Boolean)) {
            updateSecIMEPreference(((Boolean) obj).booleanValue());
            return true;
        }
        return true;
    }
}
