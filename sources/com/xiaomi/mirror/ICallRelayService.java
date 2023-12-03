package com.xiaomi.mirror;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.xiaomi.mirror.ICallRelayListener;

/* loaded from: classes2.dex */
public interface ICallRelayService extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements ICallRelayService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.xiaomi.mirror.ICallRelayService
        public void registerCallRelayListener(ICallRelayListener iCallRelayListener) {
        }

        @Override // com.xiaomi.mirror.ICallRelayService
        public void release() {
        }

        @Override // com.xiaomi.mirror.ICallRelayService
        public void sendRelayMessage(String str) {
        }

        @Override // com.xiaomi.mirror.ICallRelayService
        public void setCallState(int i) {
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallRelayService {
        private static final String DESCRIPTOR = "com.xiaomi.mirror.ICallRelayService";
        static final int TRANSACTION_registerCallRelayListener = 2;
        static final int TRANSACTION_release = 4;
        static final int TRANSACTION_sendRelayMessage = 1;
        static final int TRANSACTION_setCallState = 3;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ICallRelayService {
            public static ICallRelayService sDefaultImpl;
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

            @Override // com.xiaomi.mirror.ICallRelayService
            public void registerCallRelayListener(ICallRelayListener iCallRelayListener) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iCallRelayListener != null ? iCallRelayListener.asBinder() : null);
                    if (this.mRemote.transact(2, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().registerCallRelayListener(iCallRelayListener);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xiaomi.mirror.ICallRelayService
            public void release() {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(4, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().release();
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xiaomi.mirror.ICallRelayService
            public void sendRelayMessage(String str) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().sendRelayMessage(str);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xiaomi.mirror.ICallRelayService
            public void setCallState(int i) {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(3, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().setCallState(i);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICallRelayService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ICallRelayService)) ? new Proxy(iBinder) : (ICallRelayService) queryLocalInterface;
        }

        public static ICallRelayService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICallRelayService iCallRelayService) {
            if (Proxy.sDefaultImpl != null || iCallRelayService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCallRelayService;
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
                sendRelayMessage(parcel.readString());
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                registerCallRelayListener(ICallRelayListener.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            } else if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                setCallState(parcel.readInt());
                return true;
            } else if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                release();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    void registerCallRelayListener(ICallRelayListener iCallRelayListener);

    void release();

    void sendRelayMessage(String str);

    void setCallState(int i);
}
