package com.iqiyi.android.qigsaw.core.splitinstall.protocol;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;
import java.util.List;

/* loaded from: classes2.dex */
public interface ISplitInstallService extends IInterface {
    public static final String DESCRIPTOR = "com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService";

    /* loaded from: classes2.dex */
    public static class Default implements ISplitInstallService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void cancelInstall(String str, int i, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void deferredInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void deferredUninstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void getSessionState(String str, int i, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void getSessionStates(String str, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void startInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISplitInstallService {
        static final int TRANSACTION_cancelInstall = 2;
        static final int TRANSACTION_deferredInstall = 5;
        static final int TRANSACTION_deferredUninstall = 6;
        static final int TRANSACTION_getSessionState = 3;
        static final int TRANSACTION_getSessionStates = 4;
        static final int TRANSACTION_startInstall = 1;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISplitInstallService {
            public static ISplitInstallService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
            public void cancelInstall(String str, int i, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallService.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iSplitInstallServiceCallback != null ? iSplitInstallServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().cancelInstall(str, i, bundle, iSplitInstallServiceCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
            public void deferredInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallService.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeTypedList(list);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iSplitInstallServiceCallback != null ? iSplitInstallServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().deferredInstall(str, list, bundle, iSplitInstallServiceCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
            public void deferredUninstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallService.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeTypedList(list);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iSplitInstallServiceCallback != null ? iSplitInstallServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().deferredUninstall(str, list, bundle, iSplitInstallServiceCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return ISplitInstallService.DESCRIPTOR;
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
            public void getSessionState(String str, int i, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallService.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iSplitInstallServiceCallback != null ? iSplitInstallServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().getSessionState(str, i, iSplitInstallServiceCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
            public void getSessionStates(String str, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallService.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iSplitInstallServiceCallback != null ? iSplitInstallServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().getSessionStates(str, iSplitInstallServiceCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
            public void startInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISplitInstallService.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeTypedList(list);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iSplitInstallServiceCallback != null ? iSplitInstallServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().startInstall(str, list, bundle, iSplitInstallServiceCallback);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, ISplitInstallService.DESCRIPTOR);
        }

        public static ISplitInstallService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ISplitInstallService.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISplitInstallService)) ? new Proxy(iBinder) : (ISplitInstallService) queryLocalInterface;
        }

        public static ISplitInstallService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ISplitInstallService iSplitInstallService) {
            if (Proxy.sDefaultImpl == null) {
                if (iSplitInstallService != null) {
                    Proxy.sDefaultImpl = iSplitInstallService;
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
                parcel2.writeString(ISplitInstallService.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(ISplitInstallService.DESCRIPTOR);
                    String readString = parcel.readString();
                    Parcelable.Creator creator = Bundle.CREATOR;
                    startInstall(readString, parcel.createTypedArrayList(creator), parcel.readInt() != 0 ? (Bundle) creator.createFromParcel(parcel) : null, ISplitInstallServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(ISplitInstallService.DESCRIPTOR);
                    cancelInstall(parcel.readString(), parcel.readInt(), parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null, ISplitInstallServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(ISplitInstallService.DESCRIPTOR);
                    getSessionState(parcel.readString(), parcel.readInt(), ISplitInstallServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(ISplitInstallService.DESCRIPTOR);
                    getSessionStates(parcel.readString(), ISplitInstallServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(ISplitInstallService.DESCRIPTOR);
                    String readString2 = parcel.readString();
                    Parcelable.Creator creator2 = Bundle.CREATOR;
                    deferredInstall(readString2, parcel.createTypedArrayList(creator2), parcel.readInt() != 0 ? (Bundle) creator2.createFromParcel(parcel) : null, ISplitInstallServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(ISplitInstallService.DESCRIPTOR);
                    String readString3 = parcel.readString();
                    Parcelable.Creator creator3 = Bundle.CREATOR;
                    deferredUninstall(readString3, parcel.createTypedArrayList(creator3), parcel.readInt() != 0 ? (Bundle) creator3.createFromParcel(parcel) : null, ISplitInstallServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void cancelInstall(String str, int i, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException;

    void deferredInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException;

    void deferredUninstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException;

    void getSessionState(String str, int i, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException;

    void getSessionStates(String str, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException;

    void startInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) throws RemoteException;
}
