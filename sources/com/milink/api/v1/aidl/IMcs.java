package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDataSource;
import com.milink.api.v1.aidl.IMcsDelegate;
import com.milink.api.v1.aidl.IMcsDeviceListener;
import com.milink.api.v1.aidl.IMcsMiracastConnectCallback;
import com.milink.api.v1.aidl.IMcsOpenMiracastListener;
import com.milink.api.v1.aidl.IMcsScanListCallback;

/* loaded from: classes2.dex */
public interface IMcs extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements IMcs {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int connect(String str, int i) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int connectWifiDisplay(String str, String str2, String str3, IMcsMiracastConnectCallback iMcsMiracastConnectCallback) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int disconnect() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int disconnectWifiDisplay() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void dismissScanList() throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int getPlaybackDuration() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int getPlaybackProgress() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int getPlaybackRate() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int getVolume() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int rotatePhoto(String str, boolean z, float f) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void selectDevice(String str, String str2, String str3) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void setDataSource(IMcsDataSource iMcsDataSource) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void setDelegate(IMcsDelegate iMcsDelegate) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void setDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void setDeviceName(String str) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int setPlaybackProgress(int i) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int setPlaybackRate(int i) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int setVolume(int i) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int show(String str) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void showScanList(IMcsScanListCallback iMcsScanListCallback, int i) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int startPlayAudio(String str, String str2, int i, double d) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int startPlayAudioEx(String str, String str2, String str3, int i, double d) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int startPlayVideo(String str, String str2, int i, double d) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int startPlayVideoEx(String str, String str2, String str3, int i, double d) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int startShow() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int startSlideshow(int i, boolean z) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int startTvMiracast(String str, String str2, String str3, String str4, String str5, IMcsOpenMiracastListener iMcsOpenMiracastListener) throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public boolean startWifiDisplayScan() throws RemoteException {
            return false;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int stopPlay() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int stopShow() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int stopSlideshow() throws RemoteException {
            return 0;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public boolean stopWifiDisplayScan() throws RemoteException {
            return false;
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void unsetDataSource(IMcsDataSource iMcsDataSource) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void unsetDelegate(IMcsDelegate iMcsDelegate) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public void unsetDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcs
        public int zoomPhoto(String str, int i, int i2, int i3, int i4, int i5, int i6, float f) throws RemoteException {
            return 0;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMcs {
        private static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcs";
        static final int TRANSACTION_connect = 13;
        static final int TRANSACTION_connectWifiDisplay = 14;
        static final int TRANSACTION_disconnect = 17;
        static final int TRANSACTION_disconnectWifiDisplay = 15;
        static final int TRANSACTION_dismissScanList = 9;
        static final int TRANSACTION_getPlaybackDuration = 31;
        static final int TRANSACTION_getPlaybackProgress = 30;
        static final int TRANSACTION_getPlaybackRate = 28;
        static final int TRANSACTION_getVolume = 33;
        static final int TRANSACTION_rotatePhoto = 34;
        static final int TRANSACTION_selectDevice = 10;
        static final int TRANSACTION_setDataSource = 3;
        static final int TRANSACTION_setDelegate = 5;
        static final int TRANSACTION_setDeviceListener = 1;
        static final int TRANSACTION_setDeviceName = 7;
        static final int TRANSACTION_setPlaybackProgress = 29;
        static final int TRANSACTION_setPlaybackRate = 27;
        static final int TRANSACTION_setVolume = 32;
        static final int TRANSACTION_show = 19;
        static final int TRANSACTION_showScanList = 8;
        static final int TRANSACTION_startPlayAudio = 25;
        static final int TRANSACTION_startPlayAudioEx = 36;
        static final int TRANSACTION_startPlayVideo = 24;
        static final int TRANSACTION_startPlayVideoEx = 35;
        static final int TRANSACTION_startShow = 18;
        static final int TRANSACTION_startSlideshow = 22;
        static final int TRANSACTION_startTvMiracast = 16;
        static final int TRANSACTION_startWifiDisplayScan = 11;
        static final int TRANSACTION_stopPlay = 26;
        static final int TRANSACTION_stopShow = 21;
        static final int TRANSACTION_stopSlideshow = 23;
        static final int TRANSACTION_stopWifiDisplayScan = 12;
        static final int TRANSACTION_unsetDataSource = 4;
        static final int TRANSACTION_unsetDelegate = 6;
        static final int TRANSACTION_unsetDeviceListener = 2;
        static final int TRANSACTION_zoomPhoto = 20;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IMcs {
            public static IMcs sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int connect(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(13, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().connect(str, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int connectWifiDisplay(String str, String str2, String str3, IMcsMiracastConnectCallback iMcsMiracastConnectCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeStrongBinder(iMcsMiracastConnectCallback != null ? iMcsMiracastConnectCallback.asBinder() : null);
                    if (this.mRemote.transact(14, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().connectWifiDisplay(str, str2, str3, iMcsMiracastConnectCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int disconnect() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(17, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().disconnect();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int disconnectWifiDisplay() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(15, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().disconnectWifiDisplay();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void dismissScanList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(9, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().dismissScanList();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int getPlaybackDuration() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(31, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getPlaybackDuration();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int getPlaybackProgress() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(30, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getPlaybackProgress();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int getPlaybackRate() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(28, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getPlaybackRate();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int getVolume() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(33, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().getVolume();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int rotatePhoto(String str, boolean z, float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(34, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().rotatePhoto(str, z, f);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void selectDevice(String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().selectDevice(str, str2, str3);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void setDataSource(IMcsDataSource iMcsDataSource) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMcsDataSource != null ? iMcsDataSource.asBinder() : null);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setDataSource(iMcsDataSource);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void setDelegate(IMcsDelegate iMcsDelegate) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMcsDelegate != null ? iMcsDelegate.asBinder() : null);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setDelegate(iMcsDelegate);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void setDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMcsDeviceListener != null ? iMcsDeviceListener.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setDeviceListener(iMcsDeviceListener);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void setDeviceName(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setDeviceName(str);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int setPlaybackProgress(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(29, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().setPlaybackProgress(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int setPlaybackRate(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(27, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().setPlaybackRate(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int setVolume(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(32, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().setVolume(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int show(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(19, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().show(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void showScanList(IMcsScanListCallback iMcsScanListCallback, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMcsScanListCallback != null ? iMcsScanListCallback.asBinder() : null);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().showScanList(iMcsScanListCallback, i);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int startPlayAudio(String str, String str2, int i, double d) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeDouble(d);
                    if (this.mRemote.transact(25, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().startPlayAudio(str, str2, i, d);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int startPlayAudioEx(String str, String str2, String str3, int i, double d) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    obtain.writeDouble(d);
                    if (this.mRemote.transact(36, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().startPlayAudioEx(str, str2, str3, i, d);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int startPlayVideo(String str, String str2, int i, double d) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    obtain.writeDouble(d);
                    if (this.mRemote.transact(24, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().startPlayVideo(str, str2, i, d);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int startPlayVideoEx(String str, String str2, String str3, int i, double d) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeInt(i);
                    obtain.writeDouble(d);
                    if (this.mRemote.transact(35, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().startPlayVideoEx(str, str2, str3, i, d);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int startShow() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(18, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().startShow();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int startSlideshow(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(22, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().startSlideshow(i, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int startTvMiracast(String str, String str2, String str3, String str4, String str5, IMcsOpenMiracastListener iMcsOpenMiracastListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    obtain.writeString(str5);
                    obtain.writeStrongBinder(iMcsOpenMiracastListener != null ? iMcsOpenMiracastListener.asBinder() : null);
                    if (this.mRemote.transact(16, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().startTvMiracast(str, str2, str3, str4, str5, iMcsOpenMiracastListener);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public boolean startWifiDisplayScan() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(11, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().startWifiDisplayScan();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int stopPlay() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(26, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().stopPlay();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int stopShow() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(21, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().stopShow();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int stopSlideshow() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(23, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().stopSlideshow();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public boolean stopWifiDisplayScan() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(12, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt() != 0;
                    }
                    return Stub.getDefaultImpl().stopWifiDisplayScan();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void unsetDataSource(IMcsDataSource iMcsDataSource) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMcsDataSource != null ? iMcsDataSource.asBinder() : null);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unsetDataSource(iMcsDataSource);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void unsetDelegate(IMcsDelegate iMcsDelegate) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMcsDelegate != null ? iMcsDelegate.asBinder() : null);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unsetDelegate(iMcsDelegate);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public void unsetDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iMcsDeviceListener != null ? iMcsDeviceListener.asBinder() : null);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unsetDeviceListener(iMcsDeviceListener);
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcs
            public int zoomPhoto(String str, int i, int i2, int i3, int i4, int i5, int i6, float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    obtain.writeInt(i5);
                    obtain.writeInt(i6);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(20, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        return obtain2.readInt();
                    }
                    return Stub.getDefaultImpl().zoomPhoto(str, i, i2, i3, i4, i5, i6, f);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMcs asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMcs)) ? new Proxy(iBinder) : (IMcs) queryLocalInterface;
        }

        public static IMcs getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IMcs iMcs) {
            if (Proxy.sDefaultImpl != null || iMcs == null) {
                return false;
            }
            Proxy.sDefaultImpl = iMcs;
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
                    setDeviceListener(IMcsDeviceListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unsetDeviceListener(IMcsDeviceListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    setDataSource(IMcsDataSource.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    unsetDataSource(IMcsDataSource.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    setDelegate(IMcsDelegate.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    unsetDelegate(IMcsDelegate.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setDeviceName(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    showScanList(IMcsScanListCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    dismissScanList();
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    selectDevice(parcel.readString(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean startWifiDisplayScan = startWifiDisplayScan();
                    parcel2.writeNoException();
                    parcel2.writeInt(startWifiDisplayScan ? 1 : 0);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean stopWifiDisplayScan = stopWifiDisplayScan();
                    parcel2.writeNoException();
                    parcel2.writeInt(stopWifiDisplayScan ? 1 : 0);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    int connect = connect(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(connect);
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    int connectWifiDisplay = connectWifiDisplay(parcel.readString(), parcel.readString(), parcel.readString(), IMcsMiracastConnectCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(connectWifiDisplay);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    int disconnectWifiDisplay = disconnectWifiDisplay();
                    parcel2.writeNoException();
                    parcel2.writeInt(disconnectWifiDisplay);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    int startTvMiracast = startTvMiracast(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), IMcsOpenMiracastListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(startTvMiracast);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    int disconnect = disconnect();
                    parcel2.writeNoException();
                    parcel2.writeInt(disconnect);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    int startShow = startShow();
                    parcel2.writeNoException();
                    parcel2.writeInt(startShow);
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    int show = show(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(show);
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    int zoomPhoto = zoomPhoto(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readFloat());
                    parcel2.writeNoException();
                    parcel2.writeInt(zoomPhoto);
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    int stopShow = stopShow();
                    parcel2.writeNoException();
                    parcel2.writeInt(stopShow);
                    return true;
                case 22:
                    parcel.enforceInterface(DESCRIPTOR);
                    int startSlideshow = startSlideshow(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(startSlideshow);
                    return true;
                case 23:
                    parcel.enforceInterface(DESCRIPTOR);
                    int stopSlideshow = stopSlideshow();
                    parcel2.writeNoException();
                    parcel2.writeInt(stopSlideshow);
                    return true;
                case 24:
                    parcel.enforceInterface(DESCRIPTOR);
                    int startPlayVideo = startPlayVideo(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readDouble());
                    parcel2.writeNoException();
                    parcel2.writeInt(startPlayVideo);
                    return true;
                case 25:
                    parcel.enforceInterface(DESCRIPTOR);
                    int startPlayAudio = startPlayAudio(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readDouble());
                    parcel2.writeNoException();
                    parcel2.writeInt(startPlayAudio);
                    return true;
                case 26:
                    parcel.enforceInterface(DESCRIPTOR);
                    int stopPlay = stopPlay();
                    parcel2.writeNoException();
                    parcel2.writeInt(stopPlay);
                    return true;
                case 27:
                    parcel.enforceInterface(DESCRIPTOR);
                    int playbackRate = setPlaybackRate(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(playbackRate);
                    return true;
                case 28:
                    parcel.enforceInterface(DESCRIPTOR);
                    int playbackRate2 = getPlaybackRate();
                    parcel2.writeNoException();
                    parcel2.writeInt(playbackRate2);
                    return true;
                case 29:
                    parcel.enforceInterface(DESCRIPTOR);
                    int playbackProgress = setPlaybackProgress(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(playbackProgress);
                    return true;
                case 30:
                    parcel.enforceInterface(DESCRIPTOR);
                    int playbackProgress2 = getPlaybackProgress();
                    parcel2.writeNoException();
                    parcel2.writeInt(playbackProgress2);
                    return true;
                case 31:
                    parcel.enforceInterface(DESCRIPTOR);
                    int playbackDuration = getPlaybackDuration();
                    parcel2.writeNoException();
                    parcel2.writeInt(playbackDuration);
                    return true;
                case 32:
                    parcel.enforceInterface(DESCRIPTOR);
                    int volume = setVolume(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(volume);
                    return true;
                case 33:
                    parcel.enforceInterface(DESCRIPTOR);
                    int volume2 = getVolume();
                    parcel2.writeNoException();
                    parcel2.writeInt(volume2);
                    return true;
                case 34:
                    parcel.enforceInterface(DESCRIPTOR);
                    int rotatePhoto = rotatePhoto(parcel.readString(), parcel.readInt() != 0, parcel.readFloat());
                    parcel2.writeNoException();
                    parcel2.writeInt(rotatePhoto);
                    return true;
                case 35:
                    parcel.enforceInterface(DESCRIPTOR);
                    int startPlayVideoEx = startPlayVideoEx(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readDouble());
                    parcel2.writeNoException();
                    parcel2.writeInt(startPlayVideoEx);
                    return true;
                case 36:
                    parcel.enforceInterface(DESCRIPTOR);
                    int startPlayAudioEx = startPlayAudioEx(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readDouble());
                    parcel2.writeNoException();
                    parcel2.writeInt(startPlayAudioEx);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    int connect(String str, int i) throws RemoteException;

    int connectWifiDisplay(String str, String str2, String str3, IMcsMiracastConnectCallback iMcsMiracastConnectCallback) throws RemoteException;

    int disconnect() throws RemoteException;

    int disconnectWifiDisplay() throws RemoteException;

    void dismissScanList() throws RemoteException;

    int getPlaybackDuration() throws RemoteException;

    int getPlaybackProgress() throws RemoteException;

    int getPlaybackRate() throws RemoteException;

    int getVolume() throws RemoteException;

    int rotatePhoto(String str, boolean z, float f) throws RemoteException;

    void selectDevice(String str, String str2, String str3) throws RemoteException;

    void setDataSource(IMcsDataSource iMcsDataSource) throws RemoteException;

    void setDelegate(IMcsDelegate iMcsDelegate) throws RemoteException;

    void setDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException;

    void setDeviceName(String str) throws RemoteException;

    int setPlaybackProgress(int i) throws RemoteException;

    int setPlaybackRate(int i) throws RemoteException;

    int setVolume(int i) throws RemoteException;

    int show(String str) throws RemoteException;

    void showScanList(IMcsScanListCallback iMcsScanListCallback, int i) throws RemoteException;

    int startPlayAudio(String str, String str2, int i, double d) throws RemoteException;

    int startPlayAudioEx(String str, String str2, String str3, int i, double d) throws RemoteException;

    int startPlayVideo(String str, String str2, int i, double d) throws RemoteException;

    int startPlayVideoEx(String str, String str2, String str3, int i, double d) throws RemoteException;

    int startShow() throws RemoteException;

    int startSlideshow(int i, boolean z) throws RemoteException;

    int startTvMiracast(String str, String str2, String str3, String str4, String str5, IMcsOpenMiracastListener iMcsOpenMiracastListener) throws RemoteException;

    boolean startWifiDisplayScan() throws RemoteException;

    int stopPlay() throws RemoteException;

    int stopShow() throws RemoteException;

    int stopSlideshow() throws RemoteException;

    boolean stopWifiDisplayScan() throws RemoteException;

    void unsetDataSource(IMcsDataSource iMcsDataSource) throws RemoteException;

    void unsetDelegate(IMcsDelegate iMcsDelegate) throws RemoteException;

    void unsetDeviceListener(IMcsDeviceListener iMcsDeviceListener) throws RemoteException;

    int zoomPhoto(String str, int i, int i2, int i3, int i4, int i5, int i6, float f) throws RemoteException;
}
