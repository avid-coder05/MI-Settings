package com.android.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.lab.MiuiAiAsstCallScreenController;
import com.android.settings.lab.MiuiAiPreloadController;
import com.android.settings.lab.MiuiDriveModeController;
import com.android.settings.lab.MiuiFlashbackController;
import com.android.settings.lab.MiuiLabBaseController;
import com.android.settings.lab.MiuiLabGestureController;
import com.android.settings.lab.MiuiVoipAssistantController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MiuiLabSettings extends SettingsPreferenceFragment {
    private List<MiuiLabBaseController> mMiuiLabBaseControllers = new ArrayList();

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiLabSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        addPreferencesFromResource(R.xml.miui_lab_settings);
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("miui_lab_features");
        this.mMiuiLabBaseControllers.add(new MiuiAiPreloadController(preferenceCategory));
        this.mMiuiLabBaseControllers.add(new MiuiDriveModeController(preferenceCategory));
        this.mMiuiLabBaseControllers.add(new MiuiLabGestureController(preferenceCategory));
        this.mMiuiLabBaseControllers.add(new MiuiAiAsstCallScreenController(preferenceCategory));
        this.mMiuiLabBaseControllers.add(new MiuiFlashbackController(preferenceCategory));
        this.mMiuiLabBaseControllers.add(new MiuiVoipAssistantController(preferenceCategory));
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Iterator<MiuiLabBaseController> it = this.mMiuiLabBaseControllers.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        Iterator<MiuiLabBaseController> it = this.mMiuiLabBaseControllers.iterator();
        while (it.hasNext()) {
            it.next().dipatchClick(key);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (getActivity() == null) {
            return;
        }
        Iterator<MiuiLabBaseController> it = this.mMiuiLabBaseControllers.iterator();
        while (it.hasNext()) {
            it.next().resume();
        }
    }
}
