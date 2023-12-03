package com.google.android.play.core.splitinstall;

import android.os.IBinder;
import com.google.android.play.core.remote.IRemote;
import com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceHolder;
import com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy;

/* loaded from: classes2.dex */
public class SplitRemoteImpl implements IRemote<ISplitInstallServiceProxy> {
    static final IRemote sInstance = new SplitRemoteImpl();

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.play.core.remote.IRemote
    public ISplitInstallServiceProxy asInterface(IBinder iBinder) {
        return ISplitInstallServiceHolder.queryLocalInterface(iBinder);
    }
}
