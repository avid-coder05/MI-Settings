package com.android.settings.applications;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.apphibernation.AppHibernationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.DeviceConfig;
import android.util.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class HibernatedAppsPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final long DEFAULT_UNUSED_THRESHOLD_MS = TimeUnit.DAYS.toMillis(90);
    private static final String PROPERTY_HIBERNATION_UNUSED_THRESHOLD_MILLIS = "auto_revoke_unused_threshold_millis2";
    private static final String TAG = "HibernatedAppsPrefController";
    private final Executor mBackgroundExecutor;
    private boolean mLoadedUnusedCount;
    private boolean mLoadingUnusedApps;
    private final Executor mMainExecutor;
    private PreferenceScreen mScreen;
    private int mUnusedCount;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface UnusedCountLoadedCallback {
        void onUnusedCountLoaded(int i);
    }

    public HibernatedAppsPreferenceController(Context context, String str) {
        this(context, str, Executors.newSingleThreadExecutor(), context.getMainExecutor());
    }

    HibernatedAppsPreferenceController(Context context, String str, Executor executor, Executor executor2) {
        super(context, str);
        this.mUnusedCount = 0;
        this.mBackgroundExecutor = executor;
        this.mMainExecutor = executor2;
    }

    private int getUnusedCount() {
        List list;
        String[] strArr;
        PackageManager packageManager = this.mContext.getPackageManager();
        List hibernatingPackagesForUser = ((AppHibernationManager) this.mContext.getSystemService(AppHibernationManager.class)).getHibernatingPackagesForUser();
        int size = hibernatingPackagesForUser.size();
        UsageStatsManager usageStatsManager = (UsageStatsManager) this.mContext.getSystemService(UsageStatsManager.class);
        long currentTimeMillis = System.currentTimeMillis();
        long j = DeviceConfig.getLong("permissions", PROPERTY_HIBERNATION_UNUSED_THRESHOLD_MILLIS, DEFAULT_UNUSED_THRESHOLD_MS);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(2, currentTimeMillis - j, currentTimeMillis);
        ArrayMap arrayMap = new ArrayMap();
        for (UsageStats usageStats : queryUsageStats) {
            arrayMap.put(usageStats.mPackageName, usageStats);
        }
        int i = 0;
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(4608)) {
            String str = packageInfo.packageName;
            UsageStats usageStats2 = (UsageStats) arrayMap.get(str);
            boolean z = usageStats2 != null && (currentTimeMillis - usageStats2.getLastTimeAnyComponentUsed() < j || currentTimeMillis - usageStats2.getLastTimeVisible() < j);
            if (!hibernatingPackagesForUser.contains(str) && (strArr = packageInfo.requestedPermissions) != null && !z) {
                int length = strArr.length;
                int i2 = 0;
                while (i2 < length) {
                    list = hibernatingPackagesForUser;
                    if ((packageManager.getPermissionFlags(strArr[i2], str, this.mContext.getUser()) & 131072) != 0) {
                        i++;
                        break;
                    }
                    i2++;
                    hibernatingPackagesForUser = list;
                }
            }
            list = hibernatingPackagesForUser;
            hibernatingPackagesForUser = list;
        }
        return size + i;
    }

    private static boolean isHibernationEnabled() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_enabled", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadUnusedCount$2(UnusedCountLoadedCallback unusedCountLoadedCallback) {
        unusedCountLoadedCallback.onUnusedCountLoaded(getUnusedCount());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePreference$0() {
        refreshSummary(this.mScreen.findPreference(this.mPreferenceKey));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePreference$1(int i) {
        this.mUnusedCount = i;
        this.mLoadingUnusedApps = false;
        this.mLoadedUnusedCount = true;
        this.mMainExecutor.execute(new Runnable() { // from class: com.android.settings.applications.HibernatedAppsPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                HibernatedAppsPreferenceController.this.lambda$updatePreference$0();
            }
        });
    }

    private void loadUnusedCount(final UnusedCountLoadedCallback unusedCountLoadedCallback) {
        this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.settings.applications.HibernatedAppsPreferenceController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                HibernatedAppsPreferenceController.this.lambda$loadUnusedCount$2(unusedCountLoadedCallback);
            }
        });
    }

    private void updatePreference() {
        if (this.mScreen == null || this.mLoadingUnusedApps) {
            return;
        }
        loadUnusedCount(new UnusedCountLoadedCallback() { // from class: com.android.settings.applications.HibernatedAppsPreferenceController$$ExternalSyntheticLambda0
            @Override // com.android.settings.applications.HibernatedAppsPreferenceController.UnusedCountLoadedCallback
            public final void onUnusedCountLoaded(int i) {
                HibernatedAppsPreferenceController.this.lambda$updatePreference$1(i);
            }
        });
        this.mLoadingUnusedApps = true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isHibernationEnabled() ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mLoadedUnusedCount) {
            Resources resources = this.mContext.getResources();
            int i = R.plurals.unused_apps_summary;
            int i2 = this.mUnusedCount;
            return resources.getQuantityString(i, i2, Integer.valueOf(i2));
        }
        return this.mContext.getResources().getString(R.string.summary_placeholder);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        updatePreference();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
