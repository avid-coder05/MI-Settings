package com.xiaomi.mirror.synergy;

import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.mirror.ICallRelayListener;
import com.xiaomi.mirror.ICallRelayService;

/* loaded from: classes2.dex */
public class CallRelayService implements IBinder.DeathRecipient {
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_IN_USE = 1;
    private final Object mLock = new Object();
    private ICallRelayService mService;
    private RelayListenerWrapper mWrapper;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RelayListenerWrapper extends ICallRelayListener.Stub {
        private CallRelayListener mListener;

        private RelayListenerWrapper() {
        }

        @Override // com.xiaomi.mirror.ICallRelayListener
        public void onAddressUpdate(String str) {
            CallRelayListener callRelayListener = this.mListener;
            if (callRelayListener != null) {
                callRelayListener.onAddressUpdate(str);
            }
        }

        @Override // com.xiaomi.mirror.ICallRelayListener
        public void onMessage(String str) {
            CallRelayListener callRelayListener = this.mListener;
            if (callRelayListener != null) {
                callRelayListener.onMessage(str);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallRelayService(IBinder iBinder) {
        ICallRelayService asInterface = ICallRelayService.Stub.asInterface(iBinder);
        this.mService = asInterface;
        try {
            asInterface.asBinder().linkToDeath(this, 0);
        } catch (RemoteException unused) {
            this.mService = null;
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this.mLock) {
            RelayListenerWrapper relayListenerWrapper = this.mWrapper;
            if (relayListenerWrapper != null && relayListenerWrapper.mListener != null) {
                this.mWrapper.mListener.onLost();
            }
            this.mService = null;
        }
    }

    public void release() {
        synchronized (this.mLock) {
            ICallRelayService iCallRelayService = this.mService;
            if (iCallRelayService != null) {
                try {
                    iCallRelayService.release();
                    this.mService = null;
                } catch (RemoteException unused) {
                }
            }
            this.mWrapper = null;
        }
    }

    public void sendRelayMessage(String str) {
        synchronized (this.mLock) {
            ICallRelayService iCallRelayService = this.mService;
            if (iCallRelayService != null) {
                try {
                    iCallRelayService.sendRelayMessage(str);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public void setCallState(int i) {
        synchronized (this.mLock) {
            ICallRelayService iCallRelayService = this.mService;
            if (iCallRelayService != null) {
                try {
                    iCallRelayService.setCallState(i);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public void setRelayListener(CallRelayListener callRelayListener) {
        synchronized (this.mLock) {
            if (this.mWrapper == null) {
                this.mWrapper = new RelayListenerWrapper();
            }
            this.mWrapper.mListener = callRelayListener;
            ICallRelayService iCallRelayService = this.mService;
            if (iCallRelayService != null) {
                try {
                    iCallRelayService.registerCallRelayListener(this.mWrapper);
                } catch (RemoteException unused) {
                }
            } else {
                binderDied();
            }
        }
    }
}
