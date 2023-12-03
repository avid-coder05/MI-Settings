package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.BatteryUsageStats;
import android.os.Bundle;
import android.os.UidBatteryConsumer;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.applications.appinfo.AppBatteryPreferenceController;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.AdvancedPowerUsageDetail;
import com.android.settings.fuelgauge.BatteryChartPreferenceController;
import com.android.settings.fuelgauge.BatteryDiffEntry;
import com.android.settings.fuelgauge.BatteryEntry;
import com.android.settings.fuelgauge.BatteryUsageStatsLoader;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.List;
import java.util.function.Predicate;

/* loaded from: classes.dex */
public class AppBatteryPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String KEY_BATTERY = "battery";
    private static final String TAG = "AppBatteryPreferenceController";
    private boolean mBatteryDiffEntriesLoaded;
    BatteryDiffEntry mBatteryDiffEntry;
    private String mBatteryPercent;
    BatteryUsageStats mBatteryUsageStats;
    private boolean mBatteryUsageStatsLoaded;
    final BatteryUsageStatsLoaderCallbacks mBatteryUsageStatsLoaderCallbacks;
    BatteryUtils mBatteryUtils;
    boolean mIsChartGraphEnabled;
    private final String mPackageName;
    private final AppInfoDashboardFragment mParent;
    private Preference mPreference;
    private final int mUid;
    UidBatteryConsumer mUidBatteryConsumer;
    private final int mUserId;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.applications.appinfo.AppBatteryPreferenceController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 extends AsyncTask<Void, Void, BatteryDiffEntry> {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$doInBackground$0(BatteryDiffEntry batteryDiffEntry) {
            return batteryDiffEntry.mBatteryHistEntry.mConsumerType == 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$doInBackground$1(BatteryDiffEntry batteryDiffEntry) {
            return batteryDiffEntry.mBatteryHistEntry.mUserId == ((long) AppBatteryPreferenceController.this.mUserId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$doInBackground$2(BatteryDiffEntry batteryDiffEntry) {
            if (AppBatteryPreferenceController.this.mPackageName.equals(batteryDiffEntry.getPackageName())) {
                Log.i(AppBatteryPreferenceController.TAG, "Return target application: " + batteryDiffEntry.mBatteryHistEntry.mPackageName + " | uid: " + batteryDiffEntry.mBatteryHistEntry.mUid + " | userId: " + batteryDiffEntry.mBatteryHistEntry.mUserId);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public BatteryDiffEntry doInBackground(Void... voidArr) {
            List<BatteryDiffEntry> batteryLast24HrUsageData;
            if (AppBatteryPreferenceController.this.mPackageName == null || (batteryLast24HrUsageData = BatteryChartPreferenceController.getBatteryLast24HrUsageData(((AbstractPreferenceController) AppBatteryPreferenceController.this).mContext)) == null) {
                return null;
            }
            return batteryLast24HrUsageData.stream().filter(new Predicate() { // from class: com.android.settings.applications.appinfo.AppBatteryPreferenceController$1$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$doInBackground$0;
                    lambda$doInBackground$0 = AppBatteryPreferenceController.AnonymousClass1.lambda$doInBackground$0((BatteryDiffEntry) obj);
                    return lambda$doInBackground$0;
                }
            }).filter(new Predicate() { // from class: com.android.settings.applications.appinfo.AppBatteryPreferenceController$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$doInBackground$1;
                    lambda$doInBackground$1 = AppBatteryPreferenceController.AnonymousClass1.this.lambda$doInBackground$1((BatteryDiffEntry) obj);
                    return lambda$doInBackground$1;
                }
            }).filter(new Predicate() { // from class: com.android.settings.applications.appinfo.AppBatteryPreferenceController$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$doInBackground$2;
                    lambda$doInBackground$2 = AppBatteryPreferenceController.AnonymousClass1.this.lambda$doInBackground$2((BatteryDiffEntry) obj);
                    return lambda$doInBackground$2;
                }
            }).findFirst().orElse(null);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(BatteryDiffEntry batteryDiffEntry) {
            AppBatteryPreferenceController appBatteryPreferenceController = AppBatteryPreferenceController.this;
            appBatteryPreferenceController.mBatteryDiffEntry = batteryDiffEntry;
            appBatteryPreferenceController.updateBatteryWithDiffEntry();
        }
    }

    /* loaded from: classes.dex */
    private class BatteryUsageStatsLoaderCallbacks implements LoaderManager.LoaderCallbacks<BatteryUsageStats> {
        private BatteryUsageStatsLoaderCallbacks() {
        }

        /* synthetic */ BatteryUsageStatsLoaderCallbacks(AppBatteryPreferenceController appBatteryPreferenceController, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<BatteryUsageStats> onCreateLoader(int i, Bundle bundle) {
            return new BatteryUsageStatsLoader(((AbstractPreferenceController) AppBatteryPreferenceController.this).mContext, false);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<BatteryUsageStats> loader, BatteryUsageStats batteryUsageStats) {
            AppBatteryPreferenceController appBatteryPreferenceController = AppBatteryPreferenceController.this;
            appBatteryPreferenceController.mBatteryUsageStats = batteryUsageStats;
            appBatteryPreferenceController.onLoadFinished();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<BatteryUsageStats> loader) {
        }
    }

    public AppBatteryPreferenceController(Context context, AppInfoDashboardFragment appInfoDashboardFragment, String str, int i, Lifecycle lifecycle) {
        super(context, KEY_BATTERY);
        this.mBatteryUsageStatsLoaderCallbacks = new BatteryUsageStatsLoaderCallbacks(this, null);
        this.mBatteryUsageStatsLoaded = false;
        this.mBatteryDiffEntriesLoaded = false;
        this.mParent = appInfoDashboardFragment;
        this.mBatteryUtils = BatteryUtils.getInstance(this.mContext);
        this.mPackageName = str;
        this.mUid = i;
        this.mUserId = this.mContext.getUserId();
        refreshFeatureFlag(this.mContext);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    private boolean isWorkProfile(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        return userManager.isManagedProfile() && !userManager.isSystemUser();
    }

    private void loadBatteryDiffEntries() {
        new AnonymousClass1().execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLoadFinished() {
        PackageInfo packageInfo;
        if (this.mBatteryUsageStats == null || (packageInfo = this.mParent.getPackageInfo()) == null) {
            return;
        }
        this.mUidBatteryConsumer = findTargetUidBatteryConsumer(this.mBatteryUsageStats, packageInfo.applicationInfo.uid);
        if (this.mParent.getActivity() != null) {
            updateBattery();
        }
    }

    private void refreshFeatureFlag(Context context) {
        if (isWorkProfile(context)) {
            try {
                context = context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.OWNER);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "context.createPackageContextAsUser() fail: " + e);
            }
        }
        this.mIsChartGraphEnabled = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).isChartGraphEnabled(context);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        findPreference.setEnabled(false);
        loadBatteryDiffEntries();
    }

    UidBatteryConsumer findTargetUidBatteryConsumer(BatteryUsageStats batteryUsageStats, int i) {
        List uidBatteryConsumers = batteryUsageStats.getUidBatteryConsumers();
        int size = uidBatteryConsumers.size();
        for (int i2 = 0; i2 < size; i2++) {
            UidBatteryConsumer uidBatteryConsumer = (UidBatteryConsumer) uidBatteryConsumers.get(i2);
            if (uidBatteryConsumer.getUid() == i) {
                return uidBatteryConsumer;
            }
        }
        return null;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_app_info_settings_battery) ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_BATTERY.equals(preference.getKey())) {
            if (this.mBatteryDiffEntry != null) {
                Log.i(TAG, "BatteryDiffEntry not null, launch : " + this.mBatteryDiffEntry.getPackageName() + " | uid : " + this.mBatteryDiffEntry.mBatteryHistEntry.mUid + " with DiffEntry data");
                FragmentActivity activity = this.mParent.getActivity();
                AppInfoDashboardFragment appInfoDashboardFragment = this.mParent;
                BatteryDiffEntry batteryDiffEntry = this.mBatteryDiffEntry;
                AdvancedPowerUsageDetail.startBatteryDetailPage(activity, appInfoDashboardFragment, batteryDiffEntry, Utils.formatPercentage(batteryDiffEntry.getPercentOfTotal(), true), true, null);
                return true;
            }
            if (isBatteryStatsAvailable()) {
                UserManager userManager = (UserManager) this.mContext.getSystemService("user");
                Context context = this.mContext;
                UidBatteryConsumer uidBatteryConsumer = this.mUidBatteryConsumer;
                BatteryEntry batteryEntry = new BatteryEntry(context, null, userManager, uidBatteryConsumer, false, uidBatteryConsumer.getUid(), null, this.mPackageName);
                Log.i(TAG, "Battery consumer available, launch : " + batteryEntry.getDefaultPackageName() + " | uid : " + batteryEntry.getUid() + " with BatteryEntry data");
                AdvancedPowerUsageDetail.startBatteryDetailPage(this.mParent.getActivity(), this.mParent, batteryEntry, this.mIsChartGraphEnabled ? Utils.formatPercentage(0) : this.mBatteryPercent, !this.mIsChartGraphEnabled);
            } else {
                Log.i(TAG, "Launch : " + this.mPackageName + " with package name");
                AdvancedPowerUsageDetail.startBatteryDetailPage(this.mParent.getActivity(), this.mParent, this.mPackageName);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    boolean isBatteryStatsAvailable() {
        return this.mUidBatteryConsumer != null;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mParent.getLoaderManager().destroyLoader(5);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mParent.getLoaderManager().restartLoader(5, Bundle.EMPTY, this.mBatteryUsageStatsLoaderCallbacks);
    }

    void updateBattery() {
        this.mBatteryUsageStatsLoaded = true;
        this.mPreference.setEnabled(this.mBatteryDiffEntriesLoaded);
        if (this.mIsChartGraphEnabled) {
            return;
        }
        if (!isBatteryStatsAvailable()) {
            this.mPreference.setSummary(this.mContext.getString(R.string.no_battery_summary));
            return;
        }
        String formatPercentage = Utils.formatPercentage((int) this.mBatteryUtils.calculateBatteryPercent(this.mUidBatteryConsumer.getConsumedPower(), this.mBatteryUsageStats.getConsumedPower(), this.mBatteryUsageStats.getDischargePercentage()));
        this.mBatteryPercent = formatPercentage;
        this.mPreference.setSummary(this.mContext.getString(R.string.battery_summary, formatPercentage));
    }

    void updateBatteryWithDiffEntry() {
        if (this.mIsChartGraphEnabled) {
            BatteryDiffEntry batteryDiffEntry = this.mBatteryDiffEntry;
            if (batteryDiffEntry == null || batteryDiffEntry.mConsumePower <= 0.0d) {
                this.mPreference.setSummary(this.mContext.getString(R.string.no_battery_summary_24hr));
            } else {
                String formatPercentage = Utils.formatPercentage(batteryDiffEntry.getPercentOfTotal(), true);
                this.mBatteryPercent = formatPercentage;
                this.mPreference.setSummary(this.mContext.getString(R.string.battery_summary_24hr, formatPercentage));
            }
        }
        this.mBatteryDiffEntriesLoaded = true;
        this.mPreference.setEnabled(this.mBatteryUsageStatsLoaded);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
