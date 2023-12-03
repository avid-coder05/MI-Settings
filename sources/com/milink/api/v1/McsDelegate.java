package com.milink.api.v1;

import android.os.Handler;
import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDelegate;
import com.milink.api.v1.type.ErrorCode;

/* loaded from: classes2.dex */
public class McsDelegate extends IMcsDelegate.Stub {
    private Handler mHandler = new Handler();
    private MilinkClientManagerDelegate mDelegate = null;

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onConnected() throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.1
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onConnected();
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onConnectedFailed() throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.2
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onConnectedFailed(ErrorCode.ConnectTimeout);
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onDisconnected() throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.3
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onDisconnected();
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onLoading() throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.4
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onLoading();
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onNextAudio(final boolean z) throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.9
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onNextAudio(z);
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onPaused() throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.7
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onPaused();
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onPlaying() throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.5
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onPlaying();
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onPrevAudio(final boolean z) throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.10
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onPrevAudio(z);
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onStopped() throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.6
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onStopped();
                }
            }
        });
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onVolume(final int i) throws RemoteException {
        if (this.mDelegate == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.milink.api.v1.McsDelegate.8
            @Override // java.lang.Runnable
            public void run() {
                if (McsDelegate.this.mDelegate != null) {
                    McsDelegate.this.mDelegate.onVolume(i);
                }
            }
        });
    }

    public void setDelegate(MilinkClientManagerDelegate milinkClientManagerDelegate) {
        this.mDelegate = milinkClientManagerDelegate;
    }
}
