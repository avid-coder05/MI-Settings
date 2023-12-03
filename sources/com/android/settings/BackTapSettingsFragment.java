package com.android.settings;

import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.stat.commonpreference.KeySettingsStatHelper;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.ArrayList;
import miui.cloud.sync.MiCloudStatusInfo;

/* loaded from: classes.dex */
public class BackTapSettingsFragment extends KeyAndGestureShortcutStatHelperFragment implements Preference.OnPreferenceChangeListener {
    private DropDownPreference mBackDoubleTap;
    private DropDownPreference mBackTripleTap;

    private void updateState() {
        String keyAndGestureShortcutSetFunction = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(getContext(), "back_double_tap");
        String str = MiCloudStatusInfo.QuotaInfo.WARN_NONE;
        if (keyAndGestureShortcutSetFunction == null) {
            keyAndGestureShortcutSetFunction = MiCloudStatusInfo.QuotaInfo.WARN_NONE;
        }
        this.mBackDoubleTap.setValue(keyAndGestureShortcutSetFunction);
        String keyAndGestureShortcutSetFunction2 = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(getContext(), "back_triple_tap");
        if (keyAndGestureShortcutSetFunction2 != null) {
            str = keyAndGestureShortcutSetFunction2;
        }
        this.mBackTripleTap.setValue(str);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return "BackTapSettingsFragment";
    }

    @Override // com.android.settings.KeyAndGestureShortcutStatHelperFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        MiuiShortcut$Key.initFunctionsAndValues(getContext(), arrayList, arrayList2, "back_double_tap", "back_triple_tap");
        addPreferencesFromResource(R.xml.backtap_settings_fragment);
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("back_double_tap");
        this.mBackDoubleTap = dropDownPreference;
        dropDownPreference.setEntries((CharSequence[]) arrayList.toArray(new String[arrayList.size()]));
        this.mBackDoubleTap.setEntryValues((CharSequence[]) arrayList2.toArray(new String[arrayList2.size()]));
        this.mBackDoubleTap.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference2 = (DropDownPreference) findPreference("back_triple_tap");
        this.mBackTripleTap = dropDownPreference2;
        dropDownPreference2.setEntries((CharSequence[]) arrayList.toArray(new String[arrayList.size()]));
        this.mBackTripleTap.setEntryValues((CharSequence[]) arrayList2.toArray(new String[arrayList2.size()]));
        this.mBackTripleTap.setOnPreferenceChangeListener(this);
        this.mPageTitle = KeySettingsStatHelper.GESTURE_BACK_TAP_KEY;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        DropDownPreference dropDownPreference = this.mBackDoubleTap;
        if (preference == dropDownPreference) {
            String str = (String) obj;
            dropDownPreference.setValue(str);
            Settings.System.putStringForUser(getContentResolver(), this.mBackDoubleTap.getKey(), str, -2);
            this.mShortcutMap.put(this.mBackDoubleTap.getKey(), str);
        }
        DropDownPreference dropDownPreference2 = this.mBackTripleTap;
        if (preference == dropDownPreference2) {
            String str2 = (String) obj;
            dropDownPreference2.setValue(str2);
            Settings.System.putStringForUser(getContentResolver(), this.mBackTripleTap.getKey(), str2, -2);
            this.mShortcutMap.put(this.mBackTripleTap.getKey(), str2);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateState();
    }
}
