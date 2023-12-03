package com.google.android.play.core.splitinstall;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes2.dex */
final class SplitInstalledDisposer implements Runnable {
    private final SplitInstallManagerImpl splitInstallManager;
    private final SplitInstallRequest splitInstallRequest;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstalledDisposer(SplitInstallManagerImpl splitInstallManagerImpl, SplitInstallRequest splitInstallRequest) {
        this.splitInstallManager = splitInstallManagerImpl;
        this.splitInstallRequest = splitInstallRequest;
    }

    private Bundle makeInstalledSessionState(String[] strArr) {
        Bundle bundle = new Bundle();
        bundle.putInt("session_id", 0);
        bundle.putInt("status", 5);
        bundle.putInt("error_code", 0);
        bundle.putStringArrayList("module_names", new ArrayList<>(Arrays.asList(strArr)));
        bundle.putLong("total_bytes_to_download", 0L);
        bundle.putLong("bytes_downloaded", 0L);
        return bundle;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.splitInstallManager.getRegistry().notifyListeners(SplitInstallSessionState.createFrom(makeInstalledSessionState((String[]) this.splitInstallRequest.getModuleNames().toArray(new String[0]))));
    }
}
