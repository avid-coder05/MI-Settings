package com.android.settings.usagestats.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import com.android.settings.usagestats.utils.AppCategory;
import java.io.Serializable;

/* loaded from: classes2.dex */
public class AppUsageInfo implements Serializable {
    protected String pkgName;
    protected String appName = "";
    protected PackageInfo packageInfo = null;
    protected int category = -1;

    public AppUsageInfo(String str) {
        this.pkgName = str;
    }

    public int getCategory() {
        return this.category;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setAppName(String str) {
        this.appName = str;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo;
        this.packageInfo = packageInfo;
        if (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null) {
            return;
        }
        this.category = AppCategory.transferCategory(packageInfo.packageName, applicationInfo.category);
    }
}
