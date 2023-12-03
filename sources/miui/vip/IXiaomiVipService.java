package miui.vip;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* loaded from: classes4.dex */
public interface IXiaomiVipService extends IInterface {

    /* loaded from: classes4.dex */
    public static class Default implements IXiaomiVipService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.vip.IXiaomiVipService
        public List<VipAchievement> getAchievements() throws RemoteException {
            return null;
        }

        @Override // miui.vip.IXiaomiVipService
        public List<VipBanner> getBanners() throws RemoteException {
            return null;
        }

        @Override // miui.vip.IXiaomiVipService
        public VipUserInfo getCurUserInfo() throws RemoteException {
            return null;
        }

        @Override // miui.vip.IXiaomiVipService
        public List<VipPhoneLevel> getVipLevelByPhoneNumber(List<String> list, String str) throws RemoteException {
            return null;
        }

        @Override // miui.vip.IXiaomiVipService
        public boolean isAvailable() throws RemoteException {
            return false;
        }

        @Override // miui.vip.IXiaomiVipService
        public void sendStatistic(String str) throws RemoteException {
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IXiaomiVipService {
        private static final String DESCRIPTOR = "miui.vip.IXiaomiVipService";
        static final int TRANSACTION_getAchievements = 4;
        static final int TRANSACTION_getBanners = 5;
        static final int TRANSACTION_getCurUserInfo = 1;
        static final int TRANSACTION_getVipLevelByPhoneNumber = 2;
        static final int TRANSACTION_isAvailable = 3;
        static final int TRANSACTION_sendStatistic = 6;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IXiaomiVipService {
            public static IXiaomiVipService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miui.vip.IXiaomiVipService
            public List<VipAchievement> getAchievements() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.createTypedArrayList(VipAchievement.CREATOR);
                    }
                    return Stub.getDefaultImpl().getAchievements();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.vip.IXiaomiVipService
            public List<VipBanner> getBanners() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.createTypedArrayList(VipBanner.CREATOR);
                    }
                    return Stub.getDefaultImpl().getBanners();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.vip.IXiaomiVipService
            public VipUserInfo getCurUserInfo() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0 ? VipUserInfo.CREATOR.createFromParcel(obtain2) : null;
                    }
                    return Stub.getDefaultImpl().getCurUserInfo();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // miui.vip.IXiaomiVipService
            public List<VipPhoneLevel> getVipLevelByPhoneNumber(List<String> list, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStringList(list);
                    obtain.writeString(str);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.createTypedArrayList(VipPhoneLevel.CREATOR);
                    }
                    return Stub.getDefaultImpl().getVipLevelByPhoneNumber(list, str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.vip.IXiaomiVipService
            public boolean isAvailable() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().isAvailable();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.vip.IXiaomiVipService
            public void sendStatistic(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().sendStatistic(str);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IXiaomiVipService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IXiaomiVipService)) ? new Proxy(iBinder) : (IXiaomiVipService) queryLocalInterface;
        }

        public static IXiaomiVipService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IXiaomiVipService iXiaomiVipService) {
            if (Proxy.sDefaultImpl != null || iXiaomiVipService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iXiaomiVipService;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    VipUserInfo curUserInfo = getCurUserInfo();
                    parcel2.writeNoException();
                    if (curUserInfo != null) {
                        parcel2.writeInt(1);
                        curUserInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<VipPhoneLevel> vipLevelByPhoneNumber = getVipLevelByPhoneNumber(parcel.createStringArrayList(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeTypedList(vipLevelByPhoneNumber);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean isAvailable = isAvailable();
                    parcel2.writeNoException();
                    parcel2.writeInt(isAvailable ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<VipAchievement> achievements = getAchievements();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(achievements);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<VipBanner> banners = getBanners();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(banners);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    sendStatistic(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    List<VipAchievement> getAchievements() throws RemoteException;

    List<VipBanner> getBanners() throws RemoteException;

    VipUserInfo getCurUserInfo() throws RemoteException;

    List<VipPhoneLevel> getVipLevelByPhoneNumber(List<String> list, String str) throws RemoteException;

    boolean isAvailable() throws RemoteException;

    void sendStatistic(String str) throws RemoteException;
}
