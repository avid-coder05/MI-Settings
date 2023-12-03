package com.google.android.play.core.splitinstall;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.android.play.core.tasks.Task;
import com.google.android.play.core.tasks.Tasks;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInstallManagerImpl implements SplitInstallManager {
    private static final String TAG = "SplitInstallManagerImpl";
    private final Context context;
    private final SplitInstallService mInstallService;
    private final Handler mMainHandler;
    private SplitInstallListenerRegistry mRegistry;
    private final String packageName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallManagerImpl(SplitInstallService splitInstallService, Context context) {
        this(splitInstallService, context, context.getPackageName());
    }

    private SplitInstallManagerImpl(SplitInstallService splitInstallService, Context context, String str) {
        this.context = context;
        this.packageName = str;
        this.mInstallService = splitInstallService;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mRegistry = new SplitInstallListenerRegistry(context);
    }

    private String cutSplitName(String str) {
        return str.split("\\.config\\.")[0];
    }

    private Set<String> getFusedModules() {
        HashSet hashSet = new HashSet();
        try {
            Bundle bundle = this.context.getPackageManager().getApplicationInfo(this.packageName, 128).metaData;
            if (bundle == null) {
                Log.d(TAG, "App has no applicationInfo or metaData");
                return hashSet;
            }
            String string = bundle.getString("shadow.bundletool.com.android.dynamic.apk.fused.modules");
            if (string == null || string.isEmpty()) {
                Log.d(TAG, "App has no fused modules.");
                return hashSet;
            }
            Collections.addAll(hashSet, string.split(",", -1));
            hashSet.remove("");
            return hashSet;
        } catch (Throwable unused) {
            Log.w(TAG, "App is not found in PackageManager");
            return hashSet;
        }
    }

    private Set<String> getInstalledSplitInstallInfo() {
        Set<String> fusedModules = getFusedModules();
        if (Build.VERSION.SDK_INT < 21) {
            return fusedModules;
        }
        String[] splitInstallInfo = getSplitInstallInfo();
        if (splitInstallInfo == null) {
            Log.d(TAG, "No splits are found or app cannot be found in package manager.");
            return fusedModules;
        }
        String arrays = Arrays.toString(splitInstallInfo);
        Log.d(TAG, arrays.length() != 0 ? "Split names are: ".concat(arrays) : "Split names are: ");
        for (String str : splitInstallInfo) {
            if (!str.startsWith("config.")) {
                fusedModules.add(cutSplitName(str));
            }
        }
        return fusedModules;
    }

    private String[] getSplitInstallInfo() {
        try {
            PackageInfo packageInfo = this.context.getPackageManager().getPackageInfo(this.packageName, 0);
            if (packageInfo != null) {
                return packageInfo.splitNames;
            }
            return null;
        } catch (Throwable unused) {
            Log.d(TAG, "App is not found in PackageManager");
            return null;
        }
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public Task<Void> cancelInstall(int i) {
        return this.mInstallService.cancelInstall(i);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public Task<Void> deferredInstall(List<String> list) {
        return this.mInstallService.deferredInstall(list);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public Task<Void> deferredUninstall(List<String> list) {
        return this.mInstallService.deferredUninstall(list);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public Set<String> getInstalledModules() {
        Set<String> installedSplitInstallInfo = getInstalledSplitInstallInfo();
        return (installedSplitInstallInfo == null || installedSplitInstallInfo.isEmpty()) ? LoadedSplitFetcherSingleton.get().loadedSplits() : installedSplitInstallInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallListenerRegistry getRegistry() {
        return this.mRegistry;
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public Task<SplitInstallSessionState> getSessionState(int i) {
        return this.mInstallService.getSessionState(i);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public Task<List<SplitInstallSessionState>> getSessionStates() {
        return this.mInstallService.getSessionStates();
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public void registerListener(SplitInstallStateUpdatedListener splitInstallStateUpdatedListener) {
        getRegistry().registerListener(splitInstallStateUpdatedListener);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public boolean startConfirmationDialogForResult(SplitInstallSessionState splitInstallSessionState, Activity activity, int i) throws IntentSender.SendIntentException {
        if (splitInstallSessionState.status() != 8 || splitInstallSessionState.resolutionIntent() == null) {
            return false;
        }
        activity.startIntentSenderForResult(splitInstallSessionState.resolutionIntent().getIntentSender(), i, null, 0, 0, 0);
        return true;
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public Task<Integer> startInstall(SplitInstallRequest splitInstallRequest) {
        if (getInstalledModules().containsAll(splitInstallRequest.getModuleNames())) {
            this.mMainHandler.post(new SplitInstalledDisposer(this, splitInstallRequest));
            return Tasks.createTaskAndSetResult(0);
        }
        return this.mInstallService.startInstall(splitInstallRequest.getModuleNames());
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallManager
    public void unregisterListener(SplitInstallStateUpdatedListener splitInstallStateUpdatedListener) {
        getRegistry().unregisterListener(splitInstallStateUpdatedListener);
    }
}
