package com.android.settings.personal;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.MiuiShortcut$System;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.tree.OtherPersonalSettingsTree;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class OtherPersonalSettings extends DashboardFragment {
    private Context mContext;
    private ValuePreference mLocaleSettings;

    private List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        if (!OtherPersonalSettingsTree.isExcludeEnterpriseMode()) {
            arrayList.add(new EnterpriseModeController(context, getActivity(), "enterprise_mode_settings"));
        }
        arrayList.add(new MiuiAccessibilityController(context, getActivity(), "accessibility_settings"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OtherPersonalSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return 4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.other_personal_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        Preference findPreference = findPreference("key_shortcut_settings");
        Preference findPreference2 = findPreference("gesture_shortcut_settings");
        this.mLocaleSettings = (ValuePreference) findPreference("locale_settings");
        if (RegionUtils.IS_LM_CLARO) {
            getPreferenceScreen().removePreference(this.mLocaleSettings);
        }
        if (MiuiShortcut$System.isSupportNewVersionKeySettings(this.mContext)) {
            getPreferenceScreen().removePreference(findPreference);
        } else {
            getPreferenceScreen().removePreference(findPreference2);
        }
        if (OtherPersonalSettingsTree.isExcludeEnterpriseMode()) {
            ((PreferenceCategory) findPreference("development_settings_category")).removePreference(findPreference("enterprise_mode_settings"));
        }
        Preference findPreference3 = findPreference("screen_recorder");
        if (findPreference3 != null) {
            findPreference3.setTitle(ScreenRecorderController.getTitle(this.mContext));
        }
    }
}
