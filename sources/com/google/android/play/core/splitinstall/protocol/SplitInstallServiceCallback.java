package com.google.android.play.core.splitinstall.protocol;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.binder.BinderWrapper;
import com.google.android.binder.ParcelHelper;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;

/* loaded from: classes2.dex */
public abstract class SplitInstallServiceCallback extends BinderWrapper implements ISplitInstallServiceCallbackProxy {
    /* JADX INFO: Access modifiers changed from: protected */
    public SplitInstallServiceCallback() {
        super(ISplitInstallServiceCallback.DESCRIPTOR);
    }

    @Override // com.google.android.binder.BinderWrapper
    protected final boolean dispatchTransact(int i, Parcel parcel) {
        switch (i) {
            case 1:
                onStartInstall(parcel.readInt(), (Bundle) ParcelHelper.createFromParcel(parcel, Bundle.CREATOR));
                return true;
            case 2:
                int readInt = parcel.readInt();
                ParcelHelper.createFromParcel(parcel, Bundle.CREATOR);
                onCompleteInstall(readInt);
                return true;
            case 3:
                onCancelInstall(parcel.readInt(), (Bundle) ParcelHelper.createFromParcel(parcel, Bundle.CREATOR));
                return true;
            case 4:
                onGetSession(parcel.readInt(), (Bundle) ParcelHelper.createFromParcel(parcel, Bundle.CREATOR));
                return true;
            case 5:
                onDeferredUninstall((Bundle) ParcelHelper.createFromParcel(parcel, Bundle.CREATOR));
                return true;
            case 6:
                onDeferredInstall((Bundle) ParcelHelper.createFromParcel(parcel, Bundle.CREATOR));
                return true;
            case 7:
                onGetSessionStates(parcel.createTypedArrayList(Bundle.CREATOR));
                return true;
            case 8:
                onError((Bundle) ParcelHelper.createFromParcel(parcel, Bundle.CREATOR));
                return true;
            default:
                return false;
        }
    }
}
