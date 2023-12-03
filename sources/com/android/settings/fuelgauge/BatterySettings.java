package com.android.settings.fuelgauge;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.os.Build;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class BatterySettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private PreferenceCategory mPerformanceCategory;
    private DropDownPreference mPowerMode;
    private ContentObserver mPowerModeObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.fuelgauge.BatterySettings.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            BatterySettings.this.onPowerModeChanged();
        }
    };
    private PreferenceScreen mPowerUsage;
    private ContentResolver mResolver;

    private boolean hasGeminiFragemnt() {
        try {
            Class.forName("com.mediatek.gemini.GeminiPowerUsageSummary");
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPowerModeChanged() {
        if (this.mPowerMode != null) {
            this.mPowerMode.setValue(SystemProperties.get("persist.sys.aries.power_profile", "middle"));
            DropDownPreference dropDownPreference = this.mPowerMode;
            dropDownPreference.setSummary(dropDownPreference.getEntry());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return BatterySettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.battery_settings);
        this.mResolver = getContentResolver();
        this.mPerformanceCategory = (PreferenceCategory) findPreference("performance_category");
        this.mPowerUsage = (PreferenceScreen) findPreference("power_usage");
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("power_mode");
        this.mPowerMode = dropDownPreference;
        dropDownPreference.setEntryValues(MiuiSettings.System.POWER_MODE_VALUES);
        this.mPowerMode.setOnPreferenceChangeListener(this);
        Utils.updatePreferenceToSpecificActivityOrRemove(getActivity(), getPreferenceScreen(), "power_center", 1);
        Utils.updatePreferenceToSpecificActivityOrRemove(getActivity(), getPreferenceScreen(), "power_hide_mode", 1);
        if (!Build.IS_TABLET && this.mPowerUsage != null) {
            getPreferenceScreen().removePreference(this.mPowerUsage);
        }
        if (FeatureParser.getBoolean("support_power_mode", false)) {
            getContentResolver().registerContentObserver(Settings.System.getUriFor("power_mode"), false, this.mPowerModeObserver);
        } else {
            getPreferenceScreen().removePreference(this.mPowerMode);
            this.mPowerMode = null;
        }
        if (this.mPowerMode == null) {
            getPreferenceScreen().removePreference(this.mPerformanceCategory);
        }
        if (SystemProperties.get("persist.sys.miui_feature_config", "").equals("/system/etc/miui_feature/default.conf")) {
            return;
        }
        Log.d("BatterySettings", "Set the miui_feature_config to default configuration.");
        SystemProperties.set("persist.sys.miui_feature_config", "/system/etc/miui_feature/default.conf");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        if (FeatureParser.getBoolean("support_power_mode", false)) {
            getContentResolver().unregisterContentObserver(this.mPowerModeObserver);
        }
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("power_mode".equals(preference.getKey())) {
            String str = (String) obj;
            SystemProperties.set("persist.sys.aries.power_profile", str);
            Settings.System.putString(this.mResolver, "power_mode", str);
            getActivity().sendBroadcast(new Intent("miui.intent.action.POWER_MODE_CHANGE"));
            this.mPowerMode.setValue(str);
            DropDownPreference dropDownPreference = this.mPowerMode;
            dropDownPreference.setSummary(dropDownPreference.getEntry());
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("power_usage".equals(preference.getKey())) {
            if (hasGeminiFragemnt()) {
                startFragment(this, "com.mediatek.gemini.GeminiPowerUsageSummary", 0, (Bundle) null, R.string.power_usage_history);
                return true;
            }
            Intent intent = new Intent("android.intent.action.POWER_USAGE_SUMMARY");
            intent.putExtra(":miui:starting_window_label", "");
            getActivity().startActivity(intent);
            return true;
        } else if ("power_center".equals(preference.getKey())) {
            Intent intent2 = preference.getIntent();
            if (getActivity().isInMultiWindowMode()) {
                intent2.setFlags(268435456);
            }
            startActivity(intent2);
            return true;
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        onPowerModeChanged();
    }
}
