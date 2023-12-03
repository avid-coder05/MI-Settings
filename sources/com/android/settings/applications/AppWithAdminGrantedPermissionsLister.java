package com.android.settings.applications;

import android.app.admin.DevicePolicyManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.os.UserManager;

/* loaded from: classes.dex */
public abstract class AppWithAdminGrantedPermissionsLister extends AppLister {
    private final DevicePolicyManager mDevicePolicyManager;
    private final IPackageManager mPackageManagerService;
    private final String[] mPermissions;

    public AppWithAdminGrantedPermissionsLister(String[] strArr, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager, UserManager userManager) {
        super(packageManager, userManager);
        this.mPermissions = strArr;
        this.mPackageManagerService = iPackageManager;
        this.mDevicePolicyManager = devicePolicyManager;
    }

    @Override // com.android.settings.applications.AppLister
    protected boolean includeInCount(ApplicationInfo applicationInfo) {
        return AppWithAdminGrantedPermissionsCounter.includeInCount(this.mPermissions, this.mDevicePolicyManager, this.mPm, this.mPackageManagerService, applicationInfo);
    }
}
