package com.android.settings.enterprise;

import android.content.Context;
import com.android.settings.R;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.enterprise.ApplicationListPreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ApplicationListFragment extends DashboardFragment implements ApplicationListPreferenceController.ApplicationListBuilder {

    /* loaded from: classes.dex */
    private static abstract class AdminGrantedPermission extends ApplicationListFragment {
        private final String[] mPermissions;

        public AdminGrantedPermission(String[] strArr) {
            this.mPermissions = strArr;
        }

        @Override // com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            FeatureFactory.getFactory(context).getApplicationFeatureProvider(context).listAppsWithAdminGrantedPermissions(this.mPermissions, listOfAppsCallback);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 939;
        }
    }

    /* loaded from: classes.dex */
    public static class AdminGrantedPermissionCamera extends AdminGrantedPermission {
        public AdminGrantedPermissionCamera() {
            super(new String[]{"android.permission.CAMERA"});
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public /* bridge */ /* synthetic */ void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super.buildApplicationList(context, listOfAppsCallback);
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
        public /* bridge */ /* synthetic */ int getMetricsCategory() {
            return super.getMetricsCategory();
        }
    }

    /* loaded from: classes.dex */
    public static class AdminGrantedPermissionLocation extends AdminGrantedPermission {
        public AdminGrantedPermissionLocation() {
            super(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"});
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public /* bridge */ /* synthetic */ void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super.buildApplicationList(context, listOfAppsCallback);
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
        public /* bridge */ /* synthetic */ int getMetricsCategory() {
            return super.getMetricsCategory();
        }
    }

    /* loaded from: classes.dex */
    public static class AdminGrantedPermissionMicrophone extends AdminGrantedPermission {
        public AdminGrantedPermissionMicrophone() {
            super(new String[]{"android.permission.RECORD_AUDIO"});
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public /* bridge */ /* synthetic */ void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super.buildApplicationList(context, listOfAppsCallback);
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
        public /* bridge */ /* synthetic */ int getMetricsCategory() {
            return super.getMetricsCategory();
        }
    }

    /* loaded from: classes.dex */
    public static class EnterpriseInstalledPackages extends ApplicationListFragment {
        @Override // com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            FeatureFactory.getFactory(context).getApplicationFeatureProvider(context).listPolicyInstalledApps(listOfAppsCallback);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 938;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ApplicationListPreferenceController(context, this, context.getPackageManager(), this));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "EnterprisePrivacySettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.app_list_disclosure_settings;
    }
}
