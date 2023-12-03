package com.android.settings.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* loaded from: classes.dex */
public interface IReverseSearchService extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IReverseSearchService {
        static final int TRANSACTION_getResults = 1;

        /* loaded from: classes.dex */
        private static class Proxy implements IReverseSearchService {
            public static IReverseSearchService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }
        }

        public Stub() {
            attachInterface(this, "com.android.settings.aidl.IReverseSearchService");
        }

        public static IReverseSearchService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.settings.aidl.IReverseSearchService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IReverseSearchService)) ? new Proxy(iBinder) : (IReverseSearchService) queryLocalInterface;
        }

        public static IReverseSearchService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IReverseSearchService iReverseSearchService) {
            if (Proxy.sDefaultImpl == null) {
                if (iReverseSearchService != null) {
                    Proxy.sDefaultImpl = iReverseSearchService;
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
                parcel2.writeString("com.android.settings.aidl.IReverseSearchService");
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.android.settings.aidl.IReverseSearchService");
                List<String> results = getResults(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeStringList(results);
                return true;
            }
        }
    }

    List<String> getResults(String str) throws RemoteException;
}
