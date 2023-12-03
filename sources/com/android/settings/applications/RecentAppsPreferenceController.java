package com.android.settings.applications;

import android.app.Application;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.UserHandle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.applications.RecentAppStatsMixin;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.AppEntitiesHeaderController;
import com.android.settingslib.widget.AppEntityInfo;
import com.android.settingslib.widget.LayoutPreference;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class RecentAppsPreferenceController extends BasePreferenceController implements RecentAppStatsMixin.RecentAppStatsListener {
    static final String KEY_DIVIDER = "recent_apps_divider";
    AppEntitiesHeaderController mAppEntitiesController;
    private final ApplicationsState mApplicationsState;
    Preference mDivider;
    private Fragment mHost;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private List<UsageStats> mRecentApps;
    LayoutPreference mRecentAppsPreference;
    private final int mUserId;

    public RecentAppsPreferenceController(Context context, String str) {
        super(context, str);
        this.mApplicationsState = ApplicationsState.getInstance((Application) this.mContext.getApplicationContext());
        this.mUserId = UserHandle.myUserId();
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
    }

    private AppEntityInfo createAppEntity(UsageStats usageStats) {
        final String packageName = usageStats.getPackageName();
        final ApplicationsState.AppEntry entry = this.mApplicationsState.getEntry(packageName, this.mUserId);
        if (entry == null) {
            return null;
        }
        return new AppEntityInfo.Builder().setIcon(Utils.getBadgedIcon(this.mContext, entry.info)).setTitle(entry.label).setSummary(StringUtil.formatRelativeTime(this.mContext, System.currentTimeMillis() - usageStats.getLastTimeUsed(), false, RelativeDateTimeFormatter.Style.SHORT)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.RecentAppsPreferenceController$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RecentAppsPreferenceController.this.lambda$createAppEntity$1(packageName, entry, view);
            }
        }).build();
    }

    private void displayRecentApps() {
        Iterator<UsageStats> it = this.mRecentApps.iterator();
        int i = 0;
        while (it.hasNext()) {
            AppEntityInfo createAppEntity = createAppEntity(it.next());
            if (createAppEntity != null) {
                this.mAppEntitiesController.setAppEntity(i, createAppEntity);
                i++;
            }
            if (i == 3) {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createAppEntity$1(String str, ApplicationsState.AppEntry appEntry, View view) {
        this.mMetricsFeatureProvider.logClickedPreference(this.mRecentAppsPreference, getMetricsCategory());
        AppInfoBase.startAppInfoFragment(AppInfoDashboardFragment.class, R.string.application_info_label, str, appEntry.info.uid, this.mHost, 1001, getMetricsCategory());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        this.mMetricsFeatureProvider.logClickedPreference(this.mRecentAppsPreference, getMetricsCategory());
        new SubSettingLauncher(this.mContext).setDestination(ManageApplications.class.getName()).setArguments(null).setTitleRes(R.string.application_info_label).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    private void refreshUi() {
        if (this.mRecentApps.isEmpty()) {
            setVisible(this.mDivider, false);
            this.mRecentAppsPreference.setVisible(false);
            return;
        }
        displayRecentApps();
        this.mRecentAppsPreference.setVisible(true);
        setVisible(this.mDivider, true);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mDivider = preferenceScreen.findPreference(KEY_DIVIDER);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mRecentAppsPreference = layoutPreference;
        this.mAppEntitiesController = AppEntitiesHeaderController.newInstance(this.mContext, layoutPreference.findViewById(R.id.app_entities_header)).setHeaderTitleRes(R.string.recent_app_category_title).setHeaderDetailsClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.RecentAppsPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RecentAppsPreferenceController.this.lambda$displayPreference$0(view);
            }
        });
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

    @Override // com.android.settings.applications.RecentAppStatsMixin.RecentAppStatsListener
    public void onReloadDataCompleted(List<UsageStats> list) {
        this.mRecentApps = list;
        refreshUi();
        Context context = this.mContext;
        new InstalledAppCounter(context, -1, context.getPackageManager()) { // from class: com.android.settings.applications.RecentAppsPreferenceController.1
            @Override // com.android.settings.applications.AppCounter
            protected void onCountComplete(int i) {
                RecentAppsPreferenceController recentAppsPreferenceController = RecentAppsPreferenceController.this;
                recentAppsPreferenceController.mAppEntitiesController.setHeaderDetails(((AbstractPreferenceController) recentAppsPreferenceController).mContext.getResources().getQuantityString(R.plurals.see_all_apps_title, i, Integer.valueOf(i)));
                RecentAppsPreferenceController.this.mAppEntitiesController.apply();
            }
        }.execute(new Void[0]);
    }

    public void setFragment(Fragment fragment) {
        this.mHost = fragment;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
