package com.google.android.play.core.splitinstall.protocol;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import java.util.List;

/* loaded from: classes2.dex */
public interface ISplitInstallServiceProxy extends IInterface {
    void cancelInstall(String str, int i, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException;

    void deferredInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException;

    void deferredUninstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException;

    void getSessionState(String str, int i, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException;

    void getSessionStates(String str, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException;

    void startInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallbackProxy iSplitInstallServiceCallbackProxy) throws RemoteException;
}
