package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsScanListCallback;

/* loaded from: classes2.dex */
public class McsScanListCallback extends IMcsScanListCallback.Stub {
    private MiLinkClientScanListCallback mCallback;

    @Override // com.milink.api.v1.aidl.IMcsScanListCallback
    public void onConnectFail(String str, String str2) throws RemoteException {
        MiLinkClientScanListCallback miLinkClientScanListCallback = this.mCallback;
        if (miLinkClientScanListCallback != null) {
            miLinkClientScanListCallback.onConnectFail(str, str2);
        }
    }

    @Override // com.milink.api.v1.aidl.IMcsScanListCallback
    public void onConnectSuccess(String str, String str2) throws RemoteException {
        MiLinkClientScanListCallback miLinkClientScanListCallback = this.mCallback;
        if (miLinkClientScanListCallback != null) {
            miLinkClientScanListCallback.onConnectSuccess(str, str2);
        }
    }

    @Override // com.milink.api.v1.aidl.IMcsScanListCallback
    public void onSelectDevice(String str, String str2, String str3) throws RemoteException {
        MiLinkClientScanListCallback miLinkClientScanListCallback = this.mCallback;
        if (miLinkClientScanListCallback != null) {
            miLinkClientScanListCallback.onSelectDevice(str, str2, str3);
        }
    }

    public void setCallback(MiLinkClientScanListCallback miLinkClientScanListCallback) {
        this.mCallback = miLinkClientScanListCallback;
    }
}
