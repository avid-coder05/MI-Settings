package com.android.settings.accessibility;

import android.hardware.display.ColorDisplayManager;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

/* loaded from: classes.dex */
public class TextAndDisplayFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.accessibility_text_and_display);
    private Preference mDisplayDaltonizerPreferenceScreen;
    private SwitchPreference mToggleDisableAnimationsPreference;
    private SwitchPreference mToggleLargePointerIconPreference;

    private void initializeAllPreferences() {
        this.mDisplayDaltonizerPreferenceScreen = findPreference("daltonizer_preference");
        this.mToggleDisableAnimationsPreference = (SwitchPreference) findPreference("toggle_disable_animations");
        this.mToggleLargePointerIconPreference = (SwitchPreference) findPreference("toggle_large_pointer_icon");
    }

    private void updateSystemPreferences() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("experimental_category");
        if (ColorDisplayManager.isColorTransformAccelerated(getContext())) {
            this.mDisplayDaltonizerPreferenceScreen.setSummary(AccessibilityUtil.getSummary(getContext(), "accessibility_display_daltonizer_enabled"));
            getPreferenceScreen().removePreference(preferenceCategory);
            return;
        }
        getPreferenceScreen().removePreference(this.mDisplayDaltonizerPreferenceScreen);
        getPreferenceScreen().removePreference(this.mToggleDisableAnimationsPreference);
        getPreferenceScreen().removePreference(this.mToggleLargePointerIconPreference);
        preferenceCategory.addPreference(this.mDisplayDaltonizerPreferenceScreen);
        preferenceCategory.addPreference(this.mToggleDisableAnimationsPreference);
        preferenceCategory.addPreference(this.mToggleLargePointerIconPreference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "TextAndDisplayFragment";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1858;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.accessibility_text_and_display;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeAllPreferences();
        updateSystemPreferences();
    }
}
