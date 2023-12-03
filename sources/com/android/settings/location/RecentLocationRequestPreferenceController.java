package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.applications.InstalledAppDetailsFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.FunctionColumns;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.location.RecentLocationApps;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class RecentLocationRequestPreferenceController extends LocationBasePreferenceController {
    public static final int MAX_APPS = 3;
    private PreferenceCategory mCategoryRecentLocationRequests;
    RecentLocationApps mRecentLocationApps;
    private int mType;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class PackageEntryClickedListener implements Preference.OnPreferenceClickListener {
        private final DashboardFragment mFragment;
        private final String mPackage;
        private final UserHandle mUserHandle;

        public PackageEntryClickedListener(DashboardFragment dashboardFragment, String str, UserHandle userHandle) {
            this.mFragment = dashboardFragment;
            this.mPackage = str;
            this.mUserHandle = userHandle;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Bundle bundle;
            Bundle bundle2 = new Bundle();
            bundle2.putString(FunctionColumns.PACKAGE, this.mPackage);
            UserHandle userHandle = this.mUserHandle;
            if (userHandle.getIdentifier() == 999) {
                Bundle bundle3 = new Bundle();
                bundle3.putBoolean("is_xspace_app", true);
                bundle = bundle3;
                userHandle = UserHandle.CURRENT;
            } else {
                bundle = null;
            }
            new SubSettingLauncher(this.mFragment.getContext()).setDestination(InstalledAppDetailsFragment.class.getName()).setArguments(bundle2).setTitleRes(R.string.application_info_label).setUserHandle(userHandle).setSourceMetricsCategory(this.mFragment.getMetricsCategory()).setExtras(bundle).launch();
            return true;
        }
    }

    public RecentLocationRequestPreferenceController(Context context, String str) {
        super(context, str);
        this.mType = 3;
        this.mRecentLocationApps = new RecentLocationApps(context);
    }

    public static AppPreference createAppPreference(Context context, RecentLocationApps.Request request, DashboardFragment dashboardFragment) {
        AppPreference appPreference = new AppPreference(context);
        appPreference.setIcon(request.icon);
        appPreference.setTitle(request.label);
        appPreference.setOnPreferenceClickListener(new PackageEntryClickedListener(dashboardFragment, request.packageName, request.userHandle));
        return appPreference;
    }

    public static boolean isRequestMatchesProfileType(UserManager userManager, RecentLocationApps.Request request, int i) {
        boolean isManagedProfile = userManager.isManagedProfile(request.userHandle.getIdentifier());
        if (!isManagedProfile || (i & 2) == 0) {
            return (isManagedProfile || (i & 1) == 0) ? false : true;
        }
        return true;
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mCategoryRecentLocationRequests = preferenceCategory;
        Context context = preferenceCategory.getContext();
        ArrayList arrayList = new ArrayList();
        UserManager userManager = UserManager.get(this.mContext);
        for (RecentLocationApps.Request request : this.mRecentLocationApps.getAppListSorted(false)) {
            if (isRequestMatchesProfileType(userManager, request, this.mType)) {
                arrayList.add(request);
                if (arrayList.size() == 3) {
                    break;
                }
            }
        }
        if (arrayList.size() > 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                this.mCategoryRecentLocationRequests.addPreference(createAppPreference(context, (RecentLocationApps.Request) it.next(), this.mFragment));
            }
            return;
        }
        AppPreference appPreference = new AppPreference(context);
        appPreference.setTitle(R.string.location_no_recent_apps);
        appPreference.setSelectable(false);
        this.mCategoryRecentLocationRequests.addPreference(appPreference);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        this.mCategoryRecentLocationRequests.setEnabled(this.mLocationEnabler.isEnabled(i));
    }

    public void setProfileType(int i) {
        this.mType = i;
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
