package com.xiaomi.passport;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaomi.passport.servicetoken.ServiceTokenResult;

/* loaded from: classes2.dex */
public interface IPassportServiceTokenService extends IInterface {

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IPassportServiceTokenService {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IPassportServiceTokenService {
            public static IPassportServiceTokenService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.xiaomi.passport.IPassportServiceTokenService
            public ServiceTokenResult getServiceToken(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.passport.IPassportServiceTokenService");
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0 ? ServiceTokenResult.CREATOR.createFromParcel(obtain2) : null;
                    }
                    return Stub.getDefaultImpl().getServiceToken(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xiaomi.passport.IPassportServiceTokenService
            public ServiceTokenResult invalidateServiceToken(ServiceTokenResult serviceTokenResult) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.passport.IPassportServiceTokenService");
                    if (serviceTokenResult != null) {
                        obtain.writeInt(1);
                        serviceTokenResult.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0 ? ServiceTokenResult.CREATOR.createFromParcel(obtain2) : null;
                    }
                    return Stub.getDefaultImpl().invalidateServiceToken(serviceTokenResult);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xiaomi.passport.IPassportServiceTokenService
            public boolean supportServiceTokenUIResponse() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.xiaomi.passport.IPassportServiceTokenService");
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().supportServiceTokenUIResponse();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IPassportServiceTokenService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.xiaomi.passport.IPassportServiceTokenService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IPassportServiceTokenService)) ? new Proxy(iBinder) : (IPassportServiceTokenService) queryLocalInterface;
        }

        public static IPassportServiceTokenService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    ServiceTokenResult getServiceToken(String str) throws RemoteException;

    ServiceTokenResult invalidateServiceToken(ServiceTokenResult serviceTokenResult) throws RemoteException;

    boolean supportServiceTokenUIResponse() throws RemoteException;
}
