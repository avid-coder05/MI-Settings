package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.util.Collection;

/* loaded from: classes2.dex */
final class SplitDeferredInstallTask extends SplitInstallTask {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDeferredInstallTask(SplitInstaller splitInstaller, Collection<SplitInfo> collection) {
        super(splitInstaller, collection);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallTask
    boolean isStartInstallOperation() {
        return false;
    }
}
