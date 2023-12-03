package com.google.android.play.core.splitinstall.protocol;

import android.os.IBinder;
import android.os.IInterface;
import com.google.android.binder.BinderWrapper;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService;

/* loaded from: classes2.dex */
public abstract class ISplitInstallServiceHolder extends BinderWrapper implements ISplitInstallServiceProxy {
    protected ISplitInstallServiceHolder(String str) {
        super(str);
    }

    public static ISplitInstallServiceProxy queryLocalInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface(ISplitInstallService.DESCRIPTOR);
        return queryLocalInterface instanceof ISplitInstallServiceProxy ? (ISplitInstallServiceProxy) queryLocalInterface : new ISplitInstallServiceImpl(iBinder);
    }
}
