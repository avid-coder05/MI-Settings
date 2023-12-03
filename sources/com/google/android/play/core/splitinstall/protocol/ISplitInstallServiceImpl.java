package com.google.android.play.core.splitinstall.protocol;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.binder.IInterfaceProxy;
import com.google.android.binder.ParcelHelper;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService;
import java.util.List;

/* loaded from: classes2.dex */
public class ISplitInstallServiceImpl extends IInterfaceProxy implements ISplitInstallServiceProxy {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ISplitInstallServiceImpl(IBinder iBinder) {
        super(iBinder, ISplitInstallService.DESCRIPTOR);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy
    public void cancelInstall(String str, int i, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException {
        Parcel obtainData = obtainData();
        obtainData.writeString(str);
        obtainData.writeInt(i);
        ParcelHelper.writeToParcel(obtainData, bundle);
        ParcelHelper.writeStrongBinder(obtainData, iSplitInstallServiceCallbackProxy);
        transact(2, obtainData);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy
    public void deferredInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException {
        Parcel obtainData = obtainData();
        obtainData.writeString(str);
        obtainData.writeTypedList(list);
        ParcelHelper.writeToParcel(obtainData, bundle);
        ParcelHelper.writeStrongBinder(obtainData, iSplitInstallServiceCallbackProxy);
        transact(5, obtainData);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy
    public void deferredUninstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException {
        Parcel obtainData = obtainData();
        obtainData.writeString(str);
        obtainData.writeTypedList(list);
        ParcelHelper.writeToParcel(obtainData, bundle);
        ParcelHelper.writeStrongBinder(obtainData, iSplitInstallServiceCallbackProxy);
        transact(6, obtainData);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy
    public void getSessionState(String str, int i, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException {
        Parcel obtainData = obtainData();
        obtainData.writeString(str);
        obtainData.writeInt(i);
        ParcelHelper.writeStrongBinder(obtainData, iSplitInstallServiceCallbackProxy);
        transact(3, obtainData);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy
    public void getSessionStates(String str, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException {
        Parcel obtainData = obtainData();
        obtainData.writeString(str);
        ParcelHelper.writeStrongBinder(obtainData, iSplitInstallServiceCallbackProxy);
        transact(4, obtainData);
    }

    @Override // com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy
    public final void startInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException {
        Parcel obtainData = obtainData();
        obtainData.writeString(str);
        obtainData.writeTypedList(list);
        ParcelHelper.writeToParcel(obtainData, bundle);
        ParcelHelper.writeStrongBinder(obtainData, iSplitInstallServiceCallbackProxy);
        transact(1, obtainData);
    }
}
