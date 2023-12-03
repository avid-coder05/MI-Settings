package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.SearchIndexableResource;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.linkturbo.LinkTurboClient;
import com.android.settings.wifi.linkturbo.WifiLinkTurboController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.wifi.SlaveWifiUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class MiuiWifiAssistSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.wifi.MiuiWifiAssistSettings.2
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R.xml.miui_wifi_assist_settings;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private DualWifiController mDualWifiController;
    boolean mDualWifiSupported;
    private CheckBoxPreference mEnableTrafficPriority;
    private RadioButtonPreference mExtremeTrafficPriority;
    private PreferenceCategory mMultiNetworkAcceleration;
    private PreferenceCategory mNetworkOptimization;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.MiuiWifiAssistSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (("android.net.wifi.WIFI_STATE_CHANGED".equals(action) || "android.net.wifi.WIFI_SLAVE_STATE_CHANGED".equals(action)) && MiuiWifiAssistSettings.this.mDualWifiController != null) {
                DualWifiController dualWifiController = MiuiWifiAssistSettings.this.mDualWifiController;
                MiuiWifiAssistSettings miuiWifiAssistSettings = MiuiWifiAssistSettings.this;
                dualWifiController.updateState(miuiWifiAssistSettings.findPreference(miuiWifiAssistSettings.mDualWifiController.getPreferenceKey()));
            }
        }
    };
    private RadioButtonPreference mRegularTrafficPriority;
    private CheckBoxPreference mWifiEnhancedHandoverPreference;
    private MiuiWifiWakeupPreferenceController mWifiWakeupPreferenceController;

    private void initMultiNetwork() {
        this.mMultiNetworkAcceleration = (PreferenceCategory) findPreference("multi_network_acceleration");
        if (getActivity() == null || this.mMultiNetworkAcceleration == null) {
            return;
        }
        if (LinkTurboClient.isLinkTurboSupported(getActivity())) {
            this.mMultiNetworkAcceleration.removePreference(findPreference(this.mDualWifiController.getPreferenceKey()));
            return;
        }
        boolean isUiVisible = SlaveWifiUtils.getInstance(getActivity()).isUiVisible(getActivity());
        this.mDualWifiSupported = isUiVisible;
        if (!isUiVisible) {
            getPreferenceScreen().removePreference(this.mMultiNetworkAcceleration);
            return;
        }
        Preference findPreference = this.mMultiNetworkAcceleration.findPreference("link_turbo");
        if (findPreference != null) {
            this.mMultiNetworkAcceleration.removePreference(findPreference);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_SLAVE_STATE_CHANGED");
        getActivity().registerReceiver(this.mReceiver, intentFilter);
    }

    private void initNetworkOptimization() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("wifi_network_optimization");
        this.mNetworkOptimization = preferenceCategory;
        if (preferenceCategory != null) {
            initWifiEnhancedHandover();
            initTrafficeUI();
        }
    }

    private void initTrafficeUI() {
        this.mEnableTrafficPriority = (CheckBoxPreference) this.mNetworkOptimization.findPreference("enable_wifi_traffic_priority");
        this.mRegularTrafficPriority = (RadioButtonPreference) this.mNetworkOptimization.findPreference("wifi_traffic_priority_regular");
        this.mExtremeTrafficPriority = (RadioButtonPreference) this.mNetworkOptimization.findPreference("wifi_traffic_priority_extreme");
        if (WifiTrafficUtils.isTrafficPrioritySupport()) {
            this.mEnableTrafficPriority.setOnPreferenceChangeListener(this);
            return;
        }
        this.mNetworkOptimization.removePreference(this.mEnableTrafficPriority);
        this.mNetworkOptimization.removePreference(this.mRegularTrafficPriority);
        this.mNetworkOptimization.removePreference(this.mExtremeTrafficPriority);
    }

    private void initWifiEnhancedHandover() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.mNetworkOptimization.findPreference("enable_enhanced_handover");
        this.mWifiEnhancedHandoverPreference = checkBoxPreference;
        if (checkBoxPreference == null || getActivity() == null) {
            return;
        }
        if (!getActivity().getResources().getBoolean(R.bool.config_show_enhanced_handover_swith)) {
            this.mNetworkOptimization.removePreference(this.mWifiEnhancedHandoverPreference);
            return;
        }
        this.mWifiEnhancedHandoverPreference.setOnPreferenceChangeListener(this);
        this.mWifiEnhancedHandoverPreference.setChecked(MiuiSettings.System.getBoolean(getActivity().getApplicationContext().getContentResolver(), "enhanced_wifi_handover_enabled", false));
    }

    private void updateTrafficeUi() {
        if (!WifiTrafficUtils.isTrafficPrioritySupport() || getActivity() == null || this.mEnableTrafficPriority == null || this.mRegularTrafficPriority == null || this.mExtremeTrafficPriority == null) {
            return;
        }
        boolean z = WifiTrafficUtils.getTrafficPriority(getActivity()) != 0;
        this.mEnableTrafficPriority.setChecked(z);
        if (!z) {
            this.mNetworkOptimization.removePreference(this.mRegularTrafficPriority);
            this.mNetworkOptimization.removePreference(this.mExtremeTrafficPriority);
            return;
        }
        int lastSelectedTrafficPriority = WifiTrafficUtils.getLastSelectedTrafficPriority(getActivity());
        this.mNetworkOptimization.addPreference(this.mRegularTrafficPriority);
        this.mNetworkOptimization.addPreference(this.mExtremeTrafficPriority);
        this.mRegularTrafficPriority.setChecked(lastSelectedTrafficPriority == 1);
        this.mExtremeTrafficPriority.setChecked(lastSelectedTrafficPriority == 2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiAssistantController(context));
        arrayList.add(new WifiLinkTurboController(context));
        DualWifiController dualWifiController = new DualWifiController(context);
        this.mDualWifiController = dualWifiController;
        arrayList.add(dualWifiController);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MiuiWifiAssistSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 338;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.miui_wifi_assist_settings;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 600) {
            this.mWifiWakeupPreferenceController.onActivityResult(i, i2);
        } else {
            super.onActivityResult(i, i2, intent);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        MiuiWifiWakeupPreferenceController miuiWifiWakeupPreferenceController = (MiuiWifiWakeupPreferenceController) use(MiuiWifiWakeupPreferenceController.class);
        this.mWifiWakeupPreferenceController = miuiWifiWakeupPreferenceController;
        miuiWifiWakeupPreferenceController.setFragment(this);
        this.mWifiWakeupPreferenceController.setWifiWakeupEnabled(((WifiManager) context.getSystemService(WifiManager.class)).isAutoWakeupEnabled());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initNetworkOptimization();
        initMultiNetwork();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() == null || !this.mDualWifiSupported) {
            return;
        }
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if (getActivity() != null) {
            if (TextUtils.equals("enable_wifi_traffic_priority", preference.getKey())) {
                WifiTrafficUtils.setTrafficPriority(getActivity(), ((Boolean) obj).booleanValue() ? WifiTrafficUtils.getLastSelectedTrafficPriority(getActivity()) : 0);
                updateTrafficeUi();
                return true;
            } else if ("enable_enhanced_handover".equals(key)) {
                MiuiSettings.System.putBoolean(getActivity().getContentResolver(), "enhanced_wifi_handover_enabled", ((Boolean) obj).booleanValue());
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (getActivity() != null && this.mRegularTrafficPriority != null && this.mExtremeTrafficPriority != null) {
            if (TextUtils.equals("wifi_traffic_priority_regular", preference.getKey())) {
                WifiTrafficUtils.setTrafficPriority(getActivity(), 1);
                WifiTrafficUtils.setLastSelectedTrafficPriority(getActivity(), 1);
                this.mRegularTrafficPriority.setChecked(true);
                this.mExtremeTrafficPriority.setChecked(false);
                return true;
            } else if (TextUtils.equals("wifi_traffic_priority_extreme", preference.getKey())) {
                WifiTrafficUtils.setTrafficPriority(getActivity(), 2);
                WifiTrafficUtils.setLastSelectedTrafficPriority(getActivity(), 2);
                this.mRegularTrafficPriority.setChecked(false);
                this.mExtremeTrafficPriority.setChecked(true);
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateTrafficeUi();
    }
}
