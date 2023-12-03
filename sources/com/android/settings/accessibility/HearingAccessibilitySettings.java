package com.android.settings.accessibility;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.content.PackageMonitor;
import com.android.settings.MiuiValuePreference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import miui.os.Build;

/* loaded from: classes.dex */
public class HearingAccessibilitySettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    private static final String[] CATEGORIES = {"audio_and_captions_category"};
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.accessibility_settings);
    private MiuiValuePreference mEnvironmentSoundRecognition;
    private final SettingsContentObserver mSettingsContentObserver;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() { // from class: com.android.settings.accessibility.HearingAccessibilitySettings.1
        @Override // java.lang.Runnable
        public void run() {
            if (HearingAccessibilitySettings.this.getActivity() != null) {
                HearingAccessibilitySettings.this.updateServicePreferences();
            }
        }
    };
    private final BroadcastReceiver mUpdateVoiceAccess = new BroadcastReceiver() { // from class: com.android.settings.accessibility.HearingAccessibilitySettings.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !action.equals("INTENT_ACTION_EXIT") || HearingAccessibilitySettings.this.mEnvironmentSoundRecognition == null) {
                return;
            }
            HearingAccessibilitySettings.this.mEnvironmentSoundRecognition.setSummary(HearingAccessibilitySettings.this.getPrefContext().getResources().getString(R.string.accessibility_feature_state_off));
        }
    };
    private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor() { // from class: com.android.settings.accessibility.HearingAccessibilitySettings.3
        private void sendUpdate() {
            HearingAccessibilitySettings.this.mHandler.postDelayed(HearingAccessibilitySettings.this.mUpdateRunnable, 1000L);
        }

        public void onPackageAdded(String str, int i) {
            sendUpdate();
        }

        public void onPackageAppeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageDisappeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageRemoved(String str, int i) {
            sendUpdate();
        }
    };
    private final Map<String, PreferenceCategory> mCategoryToPrefCategoryMap = new ArrayMap();
    private final Map<Preference, PreferenceCategory> mServicePreferenceToPreferenceCategoryMap = new ArrayMap();
    private final Map<ComponentName, PreferenceCategory> mPreBundledServiceComponentToCategoryMap = new ArrayMap();

    public HearingAccessibilitySettings() {
        Collection values = AccessibilityShortcutController.getFrameworkShortcutFeaturesMap().values();
        ArrayList arrayList = new ArrayList(values.size());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            arrayList.add(((AccessibilityShortcutController.ToggleableFrameworkFeatureInfo) it.next()).getSettingKey());
        }
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler, arrayList) { // from class: com.android.settings.accessibility.HearingAccessibilitySettings.4
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                HearingAccessibilitySettings.this.updateAllPreferences();
            }
        };
    }

    private void initializeAllPreferences() {
        int i = 0;
        while (true) {
            String[] strArr = CATEGORIES;
            if (i >= strArr.length) {
                this.mEnvironmentSoundRecognition = (MiuiValuePreference) findPreference("environment_sound_recognition");
                return;
            }
            this.mCategoryToPrefCategoryMap.put(strArr[i], (PreferenceCategory) findPreference(strArr[i]));
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAllPreferences() {
        updateServicePreferences();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_accessibility;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "HearingAccessibilitySettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return HearingAccessibilitySettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.accessibility_settings_hearing;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AccessibilityHearingAidPreferenceController) use(AccessibilityHearingAidPreferenceController.class)).setFragmentManager(getFragmentManager());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeAllPreferences();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mHandler.removeMessages(0);
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateAllPreferences();
        this.mSettingsPackageMonitor.register(getActivity(), getActivity().getMainLooper(), false);
        this.mSettingsContentObserver.register(getContentResolver());
        getPrefContext().registerReceiver(this.mUpdateVoiceAccess, new IntentFilter("INTENT_ACTION_EXIT"));
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        this.mSettingsPackageMonitor.unregister();
        this.mSettingsContentObserver.unregister(getContentResolver());
        getPrefContext().unregisterReceiver(this.mUpdateVoiceAccess);
        super.onStop();
    }

    protected void updateServicePreferences() {
        PreferenceCategory preferenceCategory;
        ArrayList arrayList = new ArrayList(this.mServicePreferenceToPreferenceCategoryMap.keySet());
        for (int i = 0; i < arrayList.size(); i++) {
            Preference preference = (Preference) arrayList.get(i);
            this.mServicePreferenceToPreferenceCategoryMap.get(preference).removePreference(preference);
        }
        if (!Build.IS_INTERNATIONAL_BUILD || (preferenceCategory = (PreferenceCategory) findPreference("hearing_aids_tools_category")) == null) {
            return;
        }
        getPreferenceScreen().removePreference(preferenceCategory);
    }
}
