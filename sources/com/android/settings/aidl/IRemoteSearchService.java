package com.android.settings.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.settings.search.RemoteSearchResult;
import java.util.List;

/* loaded from: classes.dex */
public interface IRemoteSearchService extends IInterface {

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRemoteSearchService {
        static final int TRANSACTION_change = 2;
        static final int TRANSACTION_enquiry = 3;
        static final int TRANSACTION_search = 1;
        static final int TRANSACTION_visit = 4;

        /* loaded from: classes.dex */
        private static class Proxy implements IRemoteSearchService {
            public static IRemoteSearchService sDefaultImpl;
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
            attachInterface(this, "com.android.settings.aidl.IRemoteSearchService");
        }

        public static IRemoteSearchService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.settings.aidl.IRemoteSearchService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IRemoteSearchService)) ? new Proxy(iBinder) : (IRemoteSearchService) queryLocalInterface;
        }

        public static IRemoteSearchService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IRemoteSearchService iRemoteSearchService) {
            if (Proxy.sDefaultImpl == null) {
                if (iRemoteSearchService != null) {
                    Proxy.sDefaultImpl = iRemoteSearchService;
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
                parcel2.writeString("com.android.settings.aidl.IRemoteSearchService");
                return true;
            } else if (i == 1) {
                parcel.enforceInterface("com.android.settings.aidl.IRemoteSearchService");
                List<RemoteSearchResult> search = search(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeTypedList(search);
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.android.settings.aidl.IRemoteSearchService");
                boolean change = change(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(change ? 1 : 0);
                return true;
            } else if (i == 3) {
                parcel.enforceInterface("com.android.settings.aidl.IRemoteSearchService");
                int enquiry = enquiry(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(enquiry);
                return true;
            } else if (i != 4) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.android.settings.aidl.IRemoteSearchService");
                boolean visit = visit(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(visit ? 1 : 0);
                return true;
            }
        }
    }

    boolean change(String str, int i) throws RemoteException;

    int enquiry(String str) throws RemoteException;

    List<RemoteSearchResult> search(String str) throws RemoteException;

    boolean visit(String str, int i) throws RemoteException;
}
