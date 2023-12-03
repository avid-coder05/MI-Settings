package com.android.settings.personal;

import android.os.Bundle;
import android.os.SystemProperties;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.miuisettings.preference.SwitchPreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;

/* loaded from: classes2.dex */
public class BeautyCameraSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    private SwitchPreference mBeautyCameraPreference;

    private void updateStatus() {
        if (this.mBeautyCameraPreference != null) {
            this.mBeautyCameraPreference.setChecked(Boolean.parseBoolean(SystemProperties.get("persist.vendor.vcb.enable", "false")));
            this.mBeautyCameraPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BeautyCameraSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.beauty_camera_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBeautyCameraPreference = (SwitchPreference) findPreference("beauty_camera_switch_preference");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("beauty_camera_switch_preference".equals(preference.getKey())) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            MiStatInterfaceUtils.trackSwitchEvent("beauty_camera_switch_preference", booleanValue);
            SystemProperties.set("persist.vendor.vcb.enable", String.valueOf(booleanValue));
            OneTrackInterfaceUtils.trackSwitchEvent("beauty_camera_switch_preference", booleanValue);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateStatus();
    }
}
