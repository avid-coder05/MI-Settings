package com.xiaomi.mirror;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* loaded from: classes2.dex */
public interface ICallRelayListener extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements ICallRelayListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.xiaomi.mirror.ICallRelayListener
        public void onAddressUpdate(String str) {
        }

        @Override // com.xiaomi.mirror.ICallRelayListener
        public void onMessage(String str) {
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallRelayListener {
        private static final String DESCRIPTOR = "com.xiaomi.mirror.ICallRelayListener";
        static final int TRANSACTION_onAddressUpdate = 2;
        static final int TRANSACTION_onMessage = 1;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ICallRelayListener {
            public static ICallRelayListener sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.xiaomi.mirror.ICallRelayListener
            public void onAddressUpdate(String str) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(2, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onAddressUpdate(str);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xiaomi.mirror.ICallRelayListener
            public void onMessage(String str) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onMessage(str);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICallRelayListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ICallRelayListener)) ? new Proxy(iBinder) : (ICallRelayListener) queryLocalInterface;
        }

        public static ICallRelayListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICallRelayListener iCallRelayListener) {
            if (Proxy.sDefaultImpl != null || iCallRelayListener == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCallRelayListener;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                onMessage(parcel.readString());
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onAddressUpdate(parcel.readString());
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    void onAddressUpdate(String str);

    void onMessage(String str);
}
