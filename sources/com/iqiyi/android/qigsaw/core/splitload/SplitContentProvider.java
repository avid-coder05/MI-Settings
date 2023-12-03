package com.iqiyi.android.qigsaw.core.splitload;

import com.iqiyi.android.qigsaw.core.extension.ContentProviderProxy;

/* loaded from: classes2.dex */
public abstract class SplitContentProvider extends ContentProviderProxy {
    @Override // com.iqiyi.android.qigsaw.core.extension.ContentProviderProxy
    protected boolean checkRealContentProviderInstallStatus(String str) {
        if (getRealContentProvider() != null) {
            return true;
        }
        if (SplitLoadManagerService.hasInstance()) {
            SplitLoadManagerService.getInstance().loadInstalledSplits();
            return getRealContentProvider() != null;
        }
        return false;
    }
}
