package com.android.settings.fuelgauge;

import android.app.Activity;
import android.os.BatteryUsageStats;
import android.os.Bundle;
import android.os.UserManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.fuelgauge.BatteryBroadcastReceiver;

/* loaded from: classes.dex */
public abstract class PowerUsageBase extends DashboardFragment {
    private BatteryBroadcastReceiver mBatteryBroadcastReceiver;
    BatteryUsageStats mBatteryUsageStats;
    protected UserManager mUm;
    protected boolean mIsBatteryPresent = true;
    final BatteryUsageStatsLoaderCallbacks mBatteryUsageStatsLoaderCallbacks = new BatteryUsageStatsLoaderCallbacks();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BatteryUsageStatsLoaderCallbacks implements LoaderManager.LoaderCallbacks<BatteryUsageStats> {
        private int mRefreshType;

        private BatteryUsageStatsLoaderCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<BatteryUsageStats> onCreateLoader(int i, Bundle bundle) {
            this.mRefreshType = bundle.getInt("refresh_type");
            return new BatteryUsageStatsLoader(PowerUsageBase.this.getContext(), bundle.getBoolean("include_history"));
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<BatteryUsageStats> loader, BatteryUsageStats batteryUsageStats) {
            PowerUsageBase powerUsageBase = PowerUsageBase.this;
            powerUsageBase.mBatteryUsageStats = batteryUsageStats;
            powerUsageBase.onLoadFinished(this.mRefreshType);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<BatteryUsageStats> loader) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(int i) {
        if (i == 5) {
            this.mIsBatteryPresent = false;
        }
        restartBatteryStatsLoader(i);
    }

    protected abstract boolean isBatteryHistoryNeeded();

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mUm = (UserManager) activity.getSystemService("user");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BatteryBroadcastReceiver batteryBroadcastReceiver = new BatteryBroadcastReceiver(getContext());
        this.mBatteryBroadcastReceiver = batteryBroadcastReceiver;
        batteryBroadcastReceiver.setBatteryChangedListener(new BatteryBroadcastReceiver.OnBatteryChangedListener() { // from class: com.android.settings.fuelgauge.PowerUsageBase$$ExternalSyntheticLambda0
            @Override // com.android.settings.fuelgauge.BatteryBroadcastReceiver.OnBatteryChangedListener
            public final void onBatteryChanged(int i) {
                PowerUsageBase.this.lambda$onCreate$0(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onLoadFinished(int i) {
        refreshUi(i);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mBatteryBroadcastReceiver.register();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mBatteryBroadcastReceiver.unRegister();
    }

    protected abstract void refreshUi(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public void restartBatteryStatsLoader(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("refresh_type", i);
        bundle.putBoolean("include_history", isBatteryHistoryNeeded());
        getLoaderManager().restartLoader(1, bundle, this.mBatteryUsageStatsLoaderCallbacks);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePreference(BatteryHistoryPreference batteryHistoryPreference) {
        long currentTimeMillis = System.currentTimeMillis();
        batteryHistoryPreference.setBatteryUsageStats(this.mBatteryUsageStats);
        BatteryUtils.logRuntime("PowerUsageBase", "updatePreference", currentTimeMillis);
    }
}
