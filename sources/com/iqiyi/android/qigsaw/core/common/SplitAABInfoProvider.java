package com.iqiyi.android.qigsaw.core.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public final class SplitAABInfoProvider {
    private static final String TAG = "SplitAABInfoProvider";
    private Context context;
    private final String packageName;

    public SplitAABInfoProvider(Context context) {
        this.packageName = context.getPackageName();
        this.context = context;
    }

    private String cutSplitName(String str) {
        return str.split("\\.config\\.")[0];
    }

    private Set<String> getFusedModules() {
        Bundle bundle;
        HashSet hashSet = new HashSet();
        try {
            ApplicationInfo applicationInfo = this.context.getPackageManager().getApplicationInfo(this.packageName, 128);
            if (applicationInfo == null || (bundle = applicationInfo.metaData) == null) {
                SplitLog.d(TAG, "App has no applicationInfo or metaData", new Object[0]);
                return hashSet;
            }
            String string = bundle.getString("shadow.bundletool.com.android.dynamic.apk.fused.modules");
            if (string == null || string.isEmpty()) {
                SplitLog.d(TAG, "App has no fused modules.", new Object[0]);
                return hashSet;
            }
            Collections.addAll(hashSet, string.split(",", -1));
            hashSet.remove("");
            return hashSet;
        } catch (Throwable th) {
            SplitLog.printErrStackTrace(TAG, th, "App is not found in PackageManager", new Object[0]);
            return hashSet;
        }
    }

    private String[] getSplitInstallInfo() {
        try {
            PackageInfo packageInfo = this.context.getPackageManager().getPackageInfo(this.packageName, 0);
            if (packageInfo != null) {
                return packageInfo.splitNames;
            }
            return null;
        } catch (Throwable th) {
            SplitLog.printErrStackTrace(TAG, th, "App is not found in PackageManager", new Object[0]);
            return null;
        }
    }

    public Set<String> getInstalledSplitsForAAB() {
        Set<String> fusedModules = getFusedModules();
        if (Build.VERSION.SDK_INT < 21) {
            return fusedModules;
        }
        String[] splitInstallInfo = getSplitInstallInfo();
        if (splitInstallInfo == null) {
            SplitLog.d(TAG, "No splits are found or app cannot be found in package manager.", new Object[0]);
            return fusedModules;
        }
        String arrays = Arrays.toString(splitInstallInfo);
        SplitLog.d(TAG, arrays.length() != 0 ? "Split names are: ".concat(arrays) : "Split names are: ", new Object[0]);
        for (String str : splitInstallInfo) {
            if (!str.startsWith("config.")) {
                fusedModules.add(cutSplitName(str));
            }
        }
        return fusedModules;
    }
}
