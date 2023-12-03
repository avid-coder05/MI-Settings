package com.xiaomi.settingsdk.backup;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public interface IBackupRestoreSettings extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IBackupRestoreSettings {
        public Stub() {
            throw new RuntimeException("Stub!");
        }

        public static IBackupRestoreSettings asInterface(IBinder iBinder) {
            throw new RuntimeException("Stub!");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            throw new RuntimeException("Stub!");
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            throw new RuntimeException("Stub!");
        }
    }

    void handleSettingsIntent(Intent intent) throws RemoteException;
}
