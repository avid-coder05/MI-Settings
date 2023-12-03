package miui.content.res;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes3.dex */
public interface IThemeService extends IInterface {

    /* loaded from: classes3.dex */
    public static class Default implements IThemeService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveCustomizedIcon(String str, Bitmap bitmap) throws RemoteException {
            return false;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveIcon(String str) throws RemoteException {
            return false;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveLockWallpaper(String str) throws RemoteException {
            return false;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveWallpaper(String str) throws RemoteException {
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IThemeService {
        private static final String DESCRIPTOR = "miui.content.res.IThemeService";
        static final int TRANSACTION_saveCustomizedIcon = 4;
        static final int TRANSACTION_saveIcon = 2;
        static final int TRANSACTION_saveLockWallpaper = 1;
        static final int TRANSACTION_saveWallpaper = 3;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IThemeService {
            public static IThemeService sDefaultImpl;
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

            @Override // miui.content.res.IThemeService
            public boolean saveCustomizedIcon(String str, Bitmap bitmap) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (bitmap != null) {
                        obtain.writeInt(1);
                        bitmap.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().saveCustomizedIcon(str, bitmap);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.content.res.IThemeService
            public boolean saveIcon(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().saveIcon(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.content.res.IThemeService
            public boolean saveLockWallpaper(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().saveLockWallpaper(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // miui.content.res.IThemeService
            public boolean saveWallpaper(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().saveWallpaper(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IThemeService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IThemeService)) ? new Proxy(iBinder) : (IThemeService) queryLocalInterface;
        }

        public static IThemeService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IThemeService iThemeService) {
            if (Proxy.sDefaultImpl != null || iThemeService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iThemeService;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean saveLockWallpaper = saveLockWallpaper(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(saveLockWallpaper ? 1 : 0);
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean saveIcon = saveIcon(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(saveIcon ? 1 : 0);
                return true;
            } else if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                boolean saveWallpaper = saveWallpaper(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(saveWallpaper ? 1 : 0);
                return true;
            } else if (i != 4) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(DESCRIPTOR);
                return true;
            } else {
                parcel.enforceInterface(DESCRIPTOR);
                boolean saveCustomizedIcon = saveCustomizedIcon(parcel.readString(), parcel.readInt() != 0 ? (Bitmap) Bitmap.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                parcel2.writeInt(saveCustomizedIcon ? 1 : 0);
                return true;
            }
        }
    }

    boolean saveCustomizedIcon(String str, Bitmap bitmap) throws RemoteException;

    boolean saveIcon(String str) throws RemoteException;

    boolean saveLockWallpaper(String str) throws RemoteException;

    boolean saveWallpaper(String str) throws RemoteException;
}
