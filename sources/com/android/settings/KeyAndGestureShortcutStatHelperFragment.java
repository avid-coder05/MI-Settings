package com.android.settings;

import android.os.Bundle;
import android.util.ArrayMap;
import com.android.settings.stat.commonpreference.KeySettingsStatHelper;
import java.util.Map;

/* loaded from: classes.dex */
public class KeyAndGestureShortcutStatHelperFragment extends SettingsPreferenceFragment {
    private KeySettingsStatHelper mKeySettingsStatHelper;
    public String mPageTitle;
    public Map<String, String> mShortcutMap = new ArrayMap();

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mKeySettingsStatHelper = KeySettingsStatHelper.getInstance(getActivity());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mKeySettingsStatHelper.traceUserSetting(this.mShortcutMap, this.mPageTitle);
    }
}
