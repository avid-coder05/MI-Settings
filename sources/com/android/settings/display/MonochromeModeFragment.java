package com.android.settings.display;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.display.PaperModePreference;

/* loaded from: classes.dex */
public class MonochromeModeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, PaperModePreference.OnRightArrowClickListener {
    private Context mContext;
    private ContentObserver mMonochromeModeObserver;
    private CheckBoxPreference monochromeModeEnable;

    /* JADX INFO: Access modifiers changed from: private */
    public int getMonochromeMode() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "screen_monochrome_mode", 2);
    }

    public static boolean isMonochromeModeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_monochrome_mode_enabled", false);
    }

    private void setMonochromeMode(int i) {
        Settings.System.putInt(this.mContext.getContentResolver(), "screen_monochrome_mode", i);
    }

    private void setMonochromeModeEnable(boolean z) {
        MiuiSettings.System.putBoolean(this.mContext.getContentResolver(), "screen_monochrome_mode_enabled", z);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MonochromeModeFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.monochrome_mode_settings);
        this.mContext = getActivity();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("monochrome_mode_enable");
        this.monochromeModeEnable = checkBoxPreference;
        checkBoxPreference.setChecked(isMonochromeModeEnable(this.mContext));
        this.monochromeModeEnable.setOnPreferenceChangeListener(this);
        final PaperModePreference paperModePreference = (PaperModePreference) findPreference("monochrome_mode_global");
        paperModePreference.setChecked(getMonochromeMode() == 1);
        paperModePreference.setOnPreferenceClickListener(this);
        final PaperModePreference paperModePreference2 = (PaperModePreference) findPreference("monochrome_mode_local");
        paperModePreference2.setChecked(getMonochromeMode() == 2);
        paperModePreference2.setOnPreferenceClickListener(this);
        paperModePreference2.setOnRightArrowClickListener(this);
        paperModePreference2.setShowRightArrow(true);
        this.mMonochromeModeObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.display.MonochromeModeFragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                paperModePreference.setChecked(MonochromeModeFragment.this.getMonochromeMode() == 1);
                paperModePreference2.setChecked(MonochromeModeFragment.this.getMonochromeMode() == 2);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_monochrome_mode"), false, this.mMonochromeModeObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mMonochromeModeObserver);
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("monochrome_mode_enable".equals(preference.getKey())) {
            Boolean bool = (Boolean) obj;
            this.monochromeModeEnable.setChecked(bool.booleanValue());
            setMonochromeModeEnable(bool.booleanValue());
            return true;
        }
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if ("monochrome_mode_global".equals(key)) {
            setMonochromeMode(1);
        } else if ("monochrome_mode_local".equals(key)) {
            setMonochromeMode(2);
        }
        this.mMonochromeModeObserver.onChange(false);
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.android.settings.display.PaperModePreference.OnRightArrowClickListener
    public void onRightArrowClick(com.android.settingslib.miuisettings.preference.RadioButtonPreference radioButtonPreference) {
        if ("monochrome_mode_local".equals(radioButtonPreference.getKey())) {
            startFragment(this, MonochromeModeSetAppFragment.class.getName(), 0, (Bundle) null, R.string.screen_monochrome_mode_set_apps_title);
        }
    }
}
