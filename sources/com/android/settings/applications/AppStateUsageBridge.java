package com.android.settings.applications;

import android.content.Context;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;

/* loaded from: classes.dex */
public class AppStateUsageBridge extends AppStateAppOpsBridge {
    private static final int[] APP_OPS_OP_CODES = {43, 95};
    private static final String[] PM_PERMISSIONS = {"android.permission.PACKAGE_USAGE_STATS", "android.permission.LOADER_USAGE_STATS"};
    public static final ApplicationsState.AppFilter FILTER_APP_USAGE = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateUsageBridge.1
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return appEntry.extraInfo != null;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }
    };

    /* loaded from: classes.dex */
    public static class UsageState extends AppStateAppOpsBridge.PermissionState {
        public UsageState(AppStateAppOpsBridge.PermissionState permissionState) {
            super(permissionState.packageName, permissionState.userHandle);
            this.packageInfo = permissionState.packageInfo;
            this.appOpMode = permissionState.appOpMode;
            this.permissionDeclared = permissionState.permissionDeclared;
            this.staticPermissionGranted = permissionState.staticPermissionGranted;
        }
    }

    public AppStateUsageBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(context, applicationsState, callback, APP_OPS_OP_CODES, PM_PERMISSIONS);
    }

    public UsageState getUsageInfo(String str, int i) {
        return new UsageState(super.getPermissionInfo(str, i));
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = getUsageInfo(str, i);
    }
}
