package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class PowerUsageAdvanced extends PowerUsageBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.fuelgauge.PowerUsageAdvanced.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new BatteryAppListPreferenceController(context, "app_list", null, null, null));
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R.xml.power_usage_advanced;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private BatteryAppListPreferenceController mBatteryAppListPreferenceController;
    private BatteryChartPreferenceController mBatteryChartPreferenceController;
    Map<Long, Map<String, BatteryHistEntry>> mBatteryHistoryMap;
    BatteryHistoryPreference mHistPref;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    final BatteryHistoryLoaderCallbacks mBatteryHistoryLoaderCallbacks = new BatteryHistoryLoaderCallbacks();
    private boolean mIsChartDataLoaded = false;
    private boolean mIsChartGraphEnabled = false;

    /* loaded from: classes.dex */
    private class BatteryHistoryLoaderCallbacks implements LoaderManager.LoaderCallbacks<Map<Long, Map<String, BatteryHistEntry>>> {
        private int mRefreshType;

        private BatteryHistoryLoaderCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<Map<Long, Map<String, BatteryHistEntry>>> onCreateLoader(int i, Bundle bundle) {
            this.mRefreshType = bundle.getInt("refresh_type");
            return new BatteryHistoryLoader(PowerUsageAdvanced.this.getContext());
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<Map<Long, Map<String, BatteryHistEntry>>> loader, Map<Long, Map<String, BatteryHistEntry>> map) {
            PowerUsageAdvanced powerUsageAdvanced = PowerUsageAdvanced.this;
            powerUsageAdvanced.mBatteryHistoryMap = map;
            powerUsageAdvanced.onLoadFinished(this.mRefreshType);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<Map<Long, Map<String, BatteryHistEntry>>> loader) {
        }
    }

    private void refreshFeatureFlag(Context context) {
        if (this.mPowerUsageFeatureProvider == null) {
            PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
            this.mPowerUsageFeatureProvider = powerUsageFeatureProvider;
            this.mIsChartGraphEnabled = powerUsageFeatureProvider.isChartGraphEnabled(context);
        }
    }

    private void setBatteryChartPreferenceController() {
        BatteryChartPreferenceController batteryChartPreferenceController;
        BatteryHistoryPreference batteryHistoryPreference = this.mHistPref;
        if (batteryHistoryPreference == null || (batteryChartPreferenceController = this.mBatteryChartPreferenceController) == null) {
            return;
        }
        batteryHistoryPreference.setChartPreferenceController(batteryChartPreferenceController);
    }

    private void updateHistPrefSummary(Context context) {
        boolean z = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("plugged", -1) != 0;
        if (!this.mPowerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context) || z) {
            this.mHistPref.hideBottomSummary();
        } else {
            this.mHistPref.setBottomSummary(this.mPowerUsageFeatureProvider.getAdvancedUsageScreenInfoString());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        refreshFeatureFlag(context);
        ArrayList arrayList = new ArrayList();
        if (this.mIsChartGraphEnabled) {
            BatteryChartPreferenceController batteryChartPreferenceController = new BatteryChartPreferenceController(context, "app_list", getSettingsLifecycle(), (SettingsActivity) getActivity(), this);
            this.mBatteryChartPreferenceController = batteryChartPreferenceController;
            arrayList.add(batteryChartPreferenceController);
            setBatteryChartPreferenceController();
        } else {
            BatteryAppListPreferenceController batteryAppListPreferenceController = new BatteryAppListPreferenceController(context, "app_list", getSettingsLifecycle(), (SettingsActivity) getActivity(), this);
            this.mBatteryAppListPreferenceController = batteryAppListPreferenceController;
            arrayList.add(batteryAppListPreferenceController);
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AdvancedBatteryUsage";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 51;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.power_usage_advanced;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageBase
    protected boolean isBatteryHistoryNeeded() {
        return true;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        refreshFeatureFlag(context);
        this.mHistPref = (BatteryHistoryPreference) findPreference("battery_graph");
        if (this.mIsChartGraphEnabled) {
            setBatteryChartPreferenceController();
        } else {
            updateHistPrefSummary(context);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (getActivity().isChangingConfigurations()) {
            BatteryEntry.clearUidCache();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mIsChartDataLoaded = false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageBase
    protected void refreshUi(int i) {
        Map<Long, Map<String, BatteryHistEntry>> map;
        Context context = getContext();
        if (context == null) {
            return;
        }
        updatePreference(this.mHistPref);
        if (this.mBatteryAppListPreferenceController != null && this.mBatteryUsageStats != null) {
            updateHistPrefSummary(context);
            this.mBatteryAppListPreferenceController.refreshAppListGroup(this.mBatteryUsageStats, true);
        }
        BatteryChartPreferenceController batteryChartPreferenceController = this.mBatteryChartPreferenceController;
        if (batteryChartPreferenceController == null || (map = this.mBatteryHistoryMap) == null) {
            return;
        }
        batteryChartPreferenceController.setBatteryHistoryMap(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.fuelgauge.PowerUsageBase
    public void restartBatteryStatsLoader(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("refresh_type", i);
        boolean z = this.mIsChartGraphEnabled;
        if (z && !this.mIsChartDataLoaded) {
            this.mIsChartDataLoaded = true;
            getLoaderManager().restartLoader(2, bundle, this.mBatteryHistoryLoaderCallbacks);
        } else if (z) {
        } else {
            super.restartBatteryStatsLoader(i);
        }
    }
}
