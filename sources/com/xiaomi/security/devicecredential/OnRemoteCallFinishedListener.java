package com.xiaomi.security.devicecredential;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaomi.security.devicecredential.SecurityDeviceCredentialManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public abstract class OnRemoteCallFinishedListener extends Binder implements IInterface {
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private int mResultCode;

    private void notifyResultArrive() {
        this.mCountDownLatch.countDown();
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkResultCode() throws SecurityDeviceCredentialManager.OperationFailedException {
        if (this.mResultCode != 0) {
            throw new SecurityDeviceCredentialManager.OperationFailedException(this.mResultCode);
        }
    }

    protected abstract void onForceReloadFinished();

    public final void onForceReloadFinished(int i) throws RemoteException {
        this.mResultCode = i;
        onForceReloadFinished();
        notifyResultArrive();
    }

    public final void onGetSecurityDeviceIdFinished(int i, String str) throws RemoteException {
        this.mResultCode = i;
        onGetSecurityDeviceIdFinished(str);
        notifyResultArrive();
    }

    protected abstract void onGetSecurityDeviceIdFinished(String str);

    public final void onSignFinished(int i, byte[] bArr) throws RemoteException {
        this.mResultCode = i;
        onSignFinished(bArr);
        notifyResultArrive();
    }

    protected abstract void onSignFinished(byte[] bArr);

    @Override // android.os.Binder
    protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (i == 1) {
            parcel.enforceInterface("com.xiaomi.security.devicecredential.ionremotecallfinishedlistener.v0");
            onGetSecurityDeviceIdFinished(parcel.readInt(), parcel.readString());
            return true;
        } else if (i == 2) {
            parcel.enforceInterface("com.xiaomi.security.devicecredential.ionremotecallfinishedlistener.v0");
            onSignFinished(parcel.readInt(), parcel.createByteArray());
            return true;
        } else if (i == 3) {
            parcel.enforceInterface("com.xiaomi.security.devicecredential.ionremotecallfinishedlistener.v0");
            onForceReloadFinished(parcel.readInt());
            return true;
        } else if (i != 1598968902) {
            return super.onTransact(i, parcel, parcel2, i2);
        } else {
            parcel2.writeString("com.xiaomi.security.devicecredential.ionremotecallfinishedlistener.v0");
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void waitForResult() throws InterruptedException, RemoteException {
        if (!this.mCountDownLatch.await(300000L, TimeUnit.MILLISECONDS)) {
            throw new RemoteException("remotecall timeout.");
        }
    }
}
