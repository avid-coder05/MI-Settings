package com.android.settings.applications;

import android.app.Application;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.AppPreference;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AppsPreferenceController extends BasePreferenceController implements LifecycleObserver {
    static final String KEY_ALL_APP_INFO = "all_app_infos";
    static final String KEY_GENERAL_CATEGORY = "general_category";
    static final String KEY_RECENT_APPS_CATEGORY = "recent_apps_category";
    static final String KEY_SEE_ALL = "see_all_apps";
    public static final int SHOW_RECENT_APP_COUNT = 4;
    Preference mAllAppsInfoPref;
    private final ApplicationsState mApplicationsState;
    PreferenceCategory mGeneralCategory;
    private Fragment mHost;
    private boolean mInitialLaunch;
    List<UsageStats> mRecentApps;
    PreferenceCategory mRecentAppsCategory;
    Preference mSeeAllPref;
    private final int mUserId;

    public AppsPreferenceController(Context context) {
        super(context, KEY_RECENT_APPS_CATEGORY);
        this.mInitialLaunch = false;
        this.mApplicationsState = ApplicationsState.getInstance((Application) this.mContext.getApplicationContext());
        this.mUserId = UserHandle.myUserId();
    }

    private void displayRecentApps() {
        boolean z;
        if (this.mRecentAppsCategory != null) {
            ArrayMap arrayMap = new ArrayMap();
            int preferenceCount = this.mRecentAppsCategory.getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                Preference preference = this.mRecentAppsCategory.getPreference(i);
                String key = preference.getKey();
                if (!TextUtils.equals(key, KEY_SEE_ALL)) {
                    arrayMap.put(key, preference);
                }
            }
            Iterator<UsageStats> it = this.mRecentApps.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                final String packageName = it.next().getPackageName();
                final ApplicationsState.AppEntry entry = this.mApplicationsState.getEntry(packageName, this.mUserId);
                if (entry != null) {
                    Preference preference2 = (Preference) arrayMap.remove(packageName);
                    if (preference2 == null) {
                        preference2 = new AppPreference(this.mContext);
                        z = false;
                    } else {
                        z = true;
                    }
                    preference2.setKey(packageName);
                    preference2.setTitle(entry.label);
                    preference2.setIcon(Utils.getBadgedIcon(this.mContext, entry.info));
                    preference2.setSummary(StringUtil.formatRelativeTime(this.mContext, System.currentTimeMillis() - r4.getLastTimeUsed(), false, RelativeDateTimeFormatter.Style.SHORT));
                    int i3 = i2 + 1;
                    preference2.setOrder(i2);
                    preference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.AppsPreferenceController$$ExternalSyntheticLambda0
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference3) {
                            boolean lambda$displayRecentApps$0;
                            lambda$displayRecentApps$0 = AppsPreferenceController.this.lambda$displayRecentApps$0(packageName, entry, preference3);
                            return lambda$displayRecentApps$0;
                        }
                    });
                    if (!z) {
                        this.mRecentAppsCategory.addPreference(preference2);
                    }
                    i2 = i3;
                }
            }
            Iterator it2 = arrayMap.values().iterator();
            while (it2.hasNext()) {
                this.mRecentAppsCategory.removePreference((Preference) it2.next());
            }
        }
    }

    private void initPreferences(PreferenceScreen preferenceScreen) {
        this.mRecentAppsCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_RECENT_APPS_CATEGORY);
        this.mGeneralCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_GENERAL_CATEGORY);
        this.mAllAppsInfoPref = preferenceScreen.findPreference(KEY_ALL_APP_INFO);
        this.mSeeAllPref = preferenceScreen.findPreference(KEY_SEE_ALL);
        this.mRecentAppsCategory.setVisible(false);
        this.mGeneralCategory.setVisible(false);
        this.mAllAppsInfoPref.setVisible(false);
        this.mSeeAllPref.setVisible(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayRecentApps$0(String str, ApplicationsState.AppEntry appEntry, Preference preference) {
        AppInfoBase.startAppInfoFragment(AppInfoDashboardFragment.class, R.string.application_info_label, str, appEntry.info.uid, this.mHost, 1001, getMetricsCategory());
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        initPreferences(preferenceScreen);
        refreshUi();
        this.mInitialLaunch = true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    void loadAllAppsCount() {
        Context context = this.mContext;
        new InstalledAppCounter(context, -1, context.getPackageManager()) { // from class: com.android.settings.applications.AppsPreferenceController.1
            @Override // com.android.settings.applications.AppCounter
            protected void onCountComplete(int i) {
                if (AppsPreferenceController.this.mRecentApps.isEmpty()) {
                    AppsPreferenceController appsPreferenceController = AppsPreferenceController.this;
                    appsPreferenceController.mAllAppsInfoPref.setSummary(((AbstractPreferenceController) appsPreferenceController).mContext.getString(R.string.apps_summary, Integer.valueOf(i)));
                    return;
                }
                AppsPreferenceController appsPreferenceController2 = AppsPreferenceController.this;
                appsPreferenceController2.mSeeAllPref.setTitle(((AbstractPreferenceController) appsPreferenceController2).mContext.getResources().getQuantityString(R.plurals.see_all_apps_title, i, Integer.valueOf(i)));
            }
        }.execute(new Void[0]);
    }

    List<UsageStats> loadRecentApps() {
        RecentAppStatsMixin recentAppStatsMixin = new RecentAppStatsMixin(this.mContext, 4);
        recentAppStatsMixin.loadDisplayableRecentApps(4);
        return recentAppStatsMixin.mRecentApps;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mInitialLaunch = false;
    }

    void refreshUi() {
        loadAllAppsCount();
        List<UsageStats> loadRecentApps = loadRecentApps();
        this.mRecentApps = loadRecentApps;
        if (loadRecentApps.isEmpty()) {
            this.mAllAppsInfoPref.setVisible(true);
            this.mRecentAppsCategory.setVisible(false);
            this.mGeneralCategory.setVisible(false);
            this.mSeeAllPref.setVisible(false);
            return;
        }
        displayRecentApps();
        this.mAllAppsInfoPref.setVisible(false);
        this.mRecentAppsCategory.setVisible(true);
        this.mGeneralCategory.setVisible(true);
        this.mSeeAllPref.setVisible(true);
    }

    public void setFragment(Fragment fragment) {
        this.mHost = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mInitialLaunch) {
            return;
        }
        refreshUi();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
