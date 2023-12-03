package com.xiaomi.security.devicecredential;

import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/* loaded from: classes2.dex */
public class SecurityDeviceCredentialManager {
    private static IBinder sService;

    /* loaded from: classes2.dex */
    private static class OnForceReloadFinishedListener extends OnRemoteCallFinishedListener {
        private OnForceReloadFinishedListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void checkReloadResult() throws OperationFailedException, InterruptedException, RemoteException {
            waitForResult();
            checkResultCode();
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        protected void onForceReloadFinished() {
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onGetSecurityDeviceIdFinished(String str) {
            throw new IllegalStateException("wrong callback!");
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onSignFinished(byte[] bArr) {
            throw new IllegalStateException("wrong callback!");
        }
    }

    /* loaded from: classes2.dex */
    private static class OnGetSecurityDeviceIdFinishListener extends OnRemoteCallFinishedListener {
        private String mSecurityDeviceId;

        private OnGetSecurityDeviceIdFinishListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getSecurityDeviceId() throws OperationFailedException, InterruptedException, RemoteException {
            waitForResult();
            checkResultCode();
            return this.mSecurityDeviceId;
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onForceReloadFinished() {
            throw new IllegalStateException("wrong callback!");
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onGetSecurityDeviceIdFinished(String str) {
            this.mSecurityDeviceId = str;
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onSignFinished(byte[] bArr) {
            throw new IllegalStateException("wrong callback!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class OnSignFinishedListener extends OnRemoteCallFinishedListener {
        private byte[] mSignResult;

        private OnSignFinishedListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public byte[] getSignResult() throws OperationFailedException, InterruptedException, RemoteException {
            waitForResult();
            checkResultCode();
            return this.mSignResult;
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onForceReloadFinished() {
            throw new IllegalStateException("wrong callback!");
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onGetSecurityDeviceIdFinished(String str) {
            throw new IllegalStateException("wrong callback!");
        }

        @Override // com.xiaomi.security.devicecredential.OnRemoteCallFinishedListener
        public void onSignFinished(byte[] bArr) {
            this.mSignResult = bArr;
        }
    }

    /* loaded from: classes2.dex */
    public static class OperationFailedException extends Exception {
        public final int errorCode;

        public OperationFailedException(int i) {
            this.errorCode = i;
        }

        @Override // java.lang.Throwable
        public String toString() {
            return "OperationFailedException{errorCode=" + this.errorCode + "}";
        }
    }

    public static void forceReload() throws RemoteException, InterruptedException, OperationFailedException {
        IBinder service = getService();
        OnForceReloadFinishedListener onForceReloadFinishedListener = new OnForceReloadFinishedListener();
        while (true) {
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("com.xiaomi.security.devicecredential.ISecurityDeviceCredentialManager.v1");
                obtain.writeStrongBinder(onForceReloadFinishedListener);
                service.transact(4, obtain, obtain2, 0);
                obtain2.readException();
                try {
                    onForceReloadFinishedListener.checkReloadResult();
                    return;
                } catch (OperationFailedException e) {
                    if (e.errorCode != -101) {
                        throw e;
                    }
                    Log.e("SecurityDeviceCredentialManager", "forceReload: Hardware service not ready, retry...");
                    Thread.sleep(500L);
                }
            } finally {
                obtain.recycle();
                obtain2.recycle();
            }
        }
    }

    public static String getSecurityDeviceId() throws RemoteException, InterruptedException, OperationFailedException {
        IBinder service = getService();
        OnGetSecurityDeviceIdFinishListener onGetSecurityDeviceIdFinishListener = new OnGetSecurityDeviceIdFinishListener();
        while (true) {
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("com.xiaomi.security.devicecredential.ISecurityDeviceCredentialManager.v1");
                obtain.writeStrongBinder(onGetSecurityDeviceIdFinishListener);
                service.transact(2, obtain, obtain2, 0);
                obtain2.readException();
                try {
                    return onGetSecurityDeviceIdFinishListener.getSecurityDeviceId();
                } catch (OperationFailedException e) {
                    if (e.errorCode != -101) {
                        throw e;
                    }
                    Log.e("SecurityDeviceCredentialManager", "getSecurityDeviceId: Hardware service not ready, retry...");
                    Thread.sleep(500L);
                }
            } finally {
                obtain.recycle();
                obtain2.recycle();
            }
        }
    }

    private static synchronized IBinder getService() throws InterruptedException, OperationFailedException {
        IBinder iBinder;
        synchronized (SecurityDeviceCredentialManager.class) {
            if (Build.VERSION.SDK_INT < 21) {
                throw new OperationFailedException(-100);
            }
            boolean z = false;
            if (sService != null) {
                Log.i("SecurityDeviceCredentialManager", "getService: sService != null. ");
                z = sService.pingBinder();
            } else {
                Log.i("SecurityDeviceCredentialManager", "getService: sService == null. ");
            }
            if (z) {
                Log.i("SecurityDeviceCredentialManager", "getService: binder alive. ");
            } else {
                Log.w("SecurityDeviceCredentialManager", "getService: binder not alive. ");
                while (true) {
                    IBinder service = ServiceManager.getService("miui.sedc");
                    sService = service;
                    if (service != null) {
                        break;
                    }
                    Log.e("SecurityDeviceCredentialManager", "getService: NULL binder, retry...");
                    Thread.sleep(500L);
                }
            }
            iBinder = sService;
        }
        return iBinder;
    }

    public static boolean isThisDeviceSupported() throws RemoteException, InterruptedException, OperationFailedException {
        IBinder service = getService();
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.xiaomi.security.devicecredential.ISecurityDeviceCredentialManager.v1");
            service.transact(1, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readInt() != 0;
        } finally {
            obtain.recycle();
            obtain2.recycle();
        }
    }

    public static byte[] sign(int i, byte[] bArr, boolean z) throws RemoteException, InterruptedException, OperationFailedException {
        IBinder service = getService();
        OnSignFinishedListener onSignFinishedListener = new OnSignFinishedListener();
        while (true) {
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("com.xiaomi.security.devicecredential.ISecurityDeviceCredentialManager.v1");
                obtain.writeStrongBinder(onSignFinishedListener);
                obtain.writeInt(i);
                obtain.writeByteArray(bArr);
                obtain.writeInt(z ? 1 : 0);
                service.transact(3, obtain, obtain2, 0);
                obtain2.readException();
                try {
                    return onSignFinishedListener.getSignResult();
                } catch (OperationFailedException e) {
                    if (e.errorCode != -101) {
                        throw e;
                    }
                    Log.e("SecurityDeviceCredentialManager", "sign: Hardware service not ready, retry...");
                    Thread.sleep(500L);
                }
            } finally {
                obtain.recycle();
                obtain2.recycle();
            }
        }
    }

    public static byte[] signWithDeviceCredential(byte[] bArr, boolean z) throws InterruptedException, RemoteException, OperationFailedException {
        return sign(1, bArr, z);
    }
}
