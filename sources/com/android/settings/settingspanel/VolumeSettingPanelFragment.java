package com.android.settings.settingspanel;

import android.os.Bundle;
import android.view.View;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.sound.SeekBarVolumizer;
import com.android.settings.sound.VolumeSeekBarPreference;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes2.dex */
public class VolumeSettingPanelFragment extends SettingsPreferenceFragment {
    private ArrayList<VolumeSeekBarPreference> mVolumePrefs = new ArrayList<>();

    private void initVolume() {
        initVolumePreference("ring_volume", 2, R.drawable.ring_volume_icon);
        initVolumePreference("alarm_volume", 4, R.drawable.alarm_volume_icon);
        initVolumePreference("media_volume", 3, R.drawable.media_volume_icon);
    }

    private void initVolumePreference(String str, int i, int i2) {
        VolumeSeekBarPreference volumeSeekBarPreference = (VolumeSeekBarPreference) findPreference(str);
        volumeSeekBarPreference.setStream(i);
        volumeSeekBarPreference.setIcon(i2);
        volumeSeekBarPreference.setSeekBarVolumizer(new SeekBarVolumizer(volumeSeekBarPreference));
        this.mVolumePrefs.add(volumeSeekBarPreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_Provision_Notitle_WifiSettings);
        addPreferencesFromResource(R.xml.miui_volume_settings_panel);
        initVolume();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Iterator<VolumeSeekBarPreference> it = this.mVolumePrefs.iterator();
        while (it.hasNext()) {
            it.next().getSeekBarVolumizer().pause();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Iterator<VolumeSeekBarPreference> it = this.mVolumePrefs.iterator();
        while (it.hasNext()) {
            it.next().getSeekBarVolumizer().resume();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        View view2 = (View) getListView().getParent();
        if (view2 instanceof SpringBackLayout) {
            view2.setEnabled(false);
        }
    }
}
