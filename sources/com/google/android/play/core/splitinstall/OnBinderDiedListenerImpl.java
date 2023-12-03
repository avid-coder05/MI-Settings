package com.google.android.play.core.splitinstall;

import com.google.android.play.core.remote.OnBinderDiedListener;

/* loaded from: classes2.dex */
final class OnBinderDiedListenerImpl implements OnBinderDiedListener {
    private final SplitInstallService mSplitInstallService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnBinderDiedListenerImpl(SplitInstallService splitInstallService) {
        this.mSplitInstallService = splitInstallService;
    }

    @Override // com.google.android.play.core.remote.OnBinderDiedListener
    public void onBinderDied() {
        this.mSplitInstallService.onBinderDied();
    }
}
