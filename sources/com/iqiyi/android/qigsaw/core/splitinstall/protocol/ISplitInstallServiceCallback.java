package com.iqiyi.android.qigsaw.core.splitinstall.protocol;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* loaded from: classes2.dex */
public interface ISplitInstallServiceCallback extends IInterface {
    public static final String DESCRIPTOR = "com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback";

    /* loaded from: classes2.dex */
    public static class Default implements ISplitInstallServiceCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onCancelInstall(int i, Bundle bundle) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onCompleteInstall(int i) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onDeferredInstall(Bundle bundle) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onDeferredUninstall(Bundle bundle) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onError(Bundle bundle) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onGetSession(int i, Bundle bundle) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onGetSessionStates(List<Bundle> list) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
        public void onStartInstall(int i, Bundle bundle) throws RemoteException {
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISplitInstallServiceCallback {
        static final int TRANSACTION_onCancelInstall = 3;
        static final int TRANSACTION_onCompleteInstall = 2;
        static final int TRANSACTION_onDeferredInstall = 6;
        static final int TRANSACTION_onDeferredUninstall = 5;
        static final int TRANSACTION_onError = 8;
        static final int TRANSACTION_onGetSession = 4;
        static final int TRANSACTION_onGetSessionStates = 7;
        static final int TRANSACTION_onStartInstall = 1;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISplitInstallServiceCallback {
            public static ISplitInstallServiceCallback sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISplitInstallServiceCallback.DESCRIPTOR;
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onCancelInstall(int i, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onCancelInstall(i, bundle);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onCompleteInstall(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onCompleteInstall(i);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onDeferredInstall(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onDeferredInstall(bundle);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onDeferredUninstall(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onDeferredUninstall(bundle);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onError(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onError(bundle);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onGetSession(int i, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onGetSession(i, bundle);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onGetSessionStates(List<Bundle> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    obtain.writeTypedList(list);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onGetSessionStates(list);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback
            public void onStartInstall(int i, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallServiceCallback.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onStartInstall(i, bundle);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, ISplitInstallServiceCallback.DESCRIPTOR);
        }

        public static ISplitInstallServiceCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ISplitInstallServiceCallback.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISplitInstallServiceCallback)) ? new Proxy(iBinder) : (ISplitInstallServiceCallback) queryLocalInterface;
        }

        public static ISplitInstallServiceCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ISplitInstallServiceCallback iSplitInstallServiceCallback) {
            if (Proxy.sDefaultImpl == null) {
                if (iSplitInstallServiceCallback != null) {
                    Proxy.sDefaultImpl = iSplitInstallServiceCallback;
                    return true;
                }
                return false;
            }
            throw new IllegalStateException("setDefaultImpl() called twice");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(ISplitInstallServiceCallback.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onStartInstall(parcel.readInt(), parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onCompleteInstall(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onCancelInstall(parcel.readInt(), parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onGetSession(parcel.readInt(), parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onDeferredUninstall(parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onDeferredInstall(parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onGetSessionStates(parcel.createTypedArrayList(Bundle.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(ISplitInstallServiceCallback.DESCRIPTOR);
                    onError(parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void onCancelInstall(int i, Bundle bundle) throws RemoteException;

    void onCompleteInstall(int i) throws RemoteException;

    void onDeferredInstall(Bundle bundle) throws RemoteException;

    void onDeferredUninstall(Bundle bundle) throws RemoteException;

    void onError(Bundle bundle) throws RemoteException;

    void onGetSession(int i, Bundle bundle) throws RemoteException;

    void onGetSessionStates(List<Bundle> list) throws RemoteException;

    void onStartInstall(int i, Bundle bundle) throws RemoteException;
}
