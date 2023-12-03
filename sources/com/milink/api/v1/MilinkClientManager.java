package com.milink.api.v1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.milink.api.v1.aidl.IMcs;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.MilinkConfig;
import com.milink.api.v1.type.ReturnCode;
import com.milink.api.v1.type.SlideMode;

/* loaded from: classes2.dex */
public class MilinkClientManager implements IMilinkClientManager {
    private static final String TAG = "MilinkClientManager";
    private Context mContext;
    private McsDataSource mMcsDataSource;
    private McsDelegate mMcsDelegate;
    private McsDeviceListener mMcsDeviceListener;
    private MilinkClientManagerDelegate mDelegate = null;
    private IMcs mService = null;
    private boolean mIsbound = false;
    private String mDeviceName = null;
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.milink.api.v1.MilinkClientManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(MilinkClientManager.TAG, "onServiceConnected");
            new Handler().post(new Runnable() { // from class: com.milink.api.v1.MilinkClientManager.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (MilinkClientManager.this.mDelegate != null) {
                        MilinkClientManager.this.mDelegate.onOpen();
                    }
                }
            });
            MilinkClientManager.this.mService = IMcs.Stub.asInterface(iBinder);
            try {
                MilinkClientManager.this.mService.setDeviceName(MilinkClientManager.this.mDeviceName);
                MilinkClientManager.this.mService.setDelegate(MilinkClientManager.this.mMcsDelegate);
                MilinkClientManager.this.mService.setDataSource(MilinkClientManager.this.mMcsDataSource);
                MilinkClientManager.this.mService.setDeviceListener(MilinkClientManager.this.mMcsDeviceListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(MilinkClientManager.TAG, "onServiceDisconnected");
            new Handler().post(new Runnable() { // from class: com.milink.api.v1.MilinkClientManager.1.2
                @Override // java.lang.Runnable
                public void run() {
                    if (MilinkClientManager.this.mDelegate != null) {
                        MilinkClientManager.this.mDelegate.onClose();
                    }
                }
            });
            try {
                MilinkClientManager.this.mService.unsetDeviceListener(MilinkClientManager.this.mMcsDeviceListener);
                MilinkClientManager.this.mService.unsetDataSource(MilinkClientManager.this.mMcsDataSource);
                MilinkClientManager.this.mService.unsetDelegate(MilinkClientManager.this.mMcsDelegate);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            MilinkClientManager.this.mService = null;
        }
    };
    private McsOpenMiracastListener mMcsOpenMiracastListener = new McsOpenMiracastListener();
    private McsMiracastConnectCallback mMcsMiracastConnectCallback = new McsMiracastConnectCallback();
    private McsScanListCallback mMcsScanListCallback = new McsScanListCallback();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.milink.api.v1.MilinkClientManager$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$milink$api$v1$type$MediaType;

        static {
            int[] iArr = new int[MediaType.values().length];
            $SwitchMap$com$milink$api$v1$type$MediaType = iArr;
            try {
                iArr[MediaType.Audio.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$milink$api$v1$type$MediaType[MediaType.Video.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$milink$api$v1$type$MediaType[MediaType.Photo.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public MilinkClientManager(Context context) {
        this.mContext = null;
        this.mMcsDataSource = null;
        this.mMcsDelegate = null;
        this.mMcsDeviceListener = null;
        this.mContext = context;
        this.mMcsDelegate = new McsDelegate();
        this.mMcsDataSource = new McsDataSource();
        this.mMcsDeviceListener = new McsDeviceListener();
    }

    private void bindMilinkClientService() {
        if (this.mIsbound) {
            return;
        }
        Intent intent = new Intent(IMcs.class.getName());
        intent.setPackage(MilinkConfig.PACKAGE_NAME);
        this.mIsbound = this.mContext.bindService(intent, this.mServiceConnection, 1);
    }

    private ReturnCode getReturnCode(int i) {
        return i != -5 ? i != -4 ? i != -3 ? i != -2 ? (i == -1 || i != 0) ? ReturnCode.Error : ReturnCode.OK : ReturnCode.InvalidParams : ReturnCode.InvalidUrl : ReturnCode.NotConnected : ReturnCode.NotSupport;
    }

    private void unbindMilinkClientService() {
        if (this.mIsbound) {
            this.mContext.unbindService(this.mServiceConnection);
            this.mIsbound = false;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public void close() {
        unbindMilinkClientService();
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode connect(String str, int i) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.connect(str, i));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode connectWifiDisplay(String str, String str2, String str3, MiLinkClientMiracastConnectCallback miLinkClientMiracastConnectCallback) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            this.mMcsMiracastConnectCallback.setCallback(miLinkClientMiracastConnectCallback);
            this.mService.connectWifiDisplay(str, str2, str3, this.mMcsMiracastConnectCallback);
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode disconnect() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.disconnect());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode disconnectWifiDisplay() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            iMcs.disconnectWifiDisplay();
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode dismissScanList() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            iMcs.dismissScanList();
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public int getPlaybackDuration() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getPlaybackDuration();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public int getPlaybackProgress() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getPlaybackProgress();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public int getPlaybackRate() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getPlaybackRate();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public int getVolume() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return 0;
        }
        try {
            return iMcs.getVolume();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public void open() {
        bindMilinkClientService();
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode rotatePhoto(String str, boolean z, float f) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.rotatePhoto(str, z, f));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode selectDevice(String str, String str2, String str3) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            iMcs.selectDevice(str, str2, str3);
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public void setDataSource(MilinkClientManagerDataSource milinkClientManagerDataSource) {
        this.mMcsDataSource.setDataSource(milinkClientManagerDataSource);
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public void setDelegate(MilinkClientManagerDelegate milinkClientManagerDelegate) {
        this.mDelegate = milinkClientManagerDelegate;
        this.mMcsDelegate.setDelegate(milinkClientManagerDelegate);
        this.mMcsDeviceListener.setDelegate(milinkClientManagerDelegate);
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public void setDeviceListener(MiLinkClientDeviceListener miLinkClientDeviceListener) {
        this.mMcsDeviceListener.setDeviceListener(miLinkClientDeviceListener);
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public void setDeviceName(String str) {
        this.mDeviceName = str;
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode setPlaybackProgress(int i) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.setPlaybackProgress(i));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode setPlaybackRate(int i) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.setPlaybackRate(i));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode setVolume(int i) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.setVolume(i));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode show(String str) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.show(str));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return ReturnCode.InvalidParams;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode showScanList(MiLinkClientScanListCallback miLinkClientScanListCallback, int i) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            this.mMcsScanListCallback.setCallback(miLinkClientScanListCallback);
            this.mService.showScanList(this.mMcsScanListCallback, i);
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode startPlay(String str, String str2, int i, double d, MediaType mediaType) {
        return startPlay(str, str2, null, i, d, mediaType);
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode startPlay(String str, String str2, String str3, int i, double d, MediaType mediaType) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            int i2 = AnonymousClass2.$SwitchMap$com$milink$api$v1$type$MediaType[mediaType.ordinal()];
            if (i2 == 1) {
                return str3 == null ? getReturnCode(this.mService.startPlayAudio(str, str2, i, d)) : getReturnCode(this.mService.startPlayAudioEx(str, str2, str3, i, d));
            } else if (i2 == 2) {
                return str3 == null ? getReturnCode(this.mService.startPlayVideo(str, str2, i, d)) : getReturnCode(this.mService.startPlayVideoEx(str, str2, str3, i, d));
            } else {
                if (i2 == 3) {
                    ReturnCode returnCode2 = ReturnCode.InvalidParams;
                }
                return ReturnCode.InvalidParams;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode startShow() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.startShow());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode startSlideshow(int i, SlideMode slideMode) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.startSlideshow(i, slideMode == SlideMode.Recyle));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode startTvMiracast(String str, String str2, String str3, String str4, String str5, MiLinkClientOpenMiracastListener miLinkClientOpenMiracastListener) {
        if (this.mService == null) {
            return ReturnCode.NotConnected;
        }
        this.mMcsOpenMiracastListener.setOpenMiracastListener(miLinkClientOpenMiracastListener);
        ReturnCode returnCode = ReturnCode.OK;
        try {
            this.mService.startTvMiracast(str, str2, str3, str4, str5, this.mMcsOpenMiracastListener);
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode startWifiDisplayScan() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            iMcs.startWifiDisplayScan();
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode stopPlay() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.stopPlay());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode stopShow() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.stopShow());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode stopSlideshow() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.stopSlideshow());
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode stopWifiDisplayScan() {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            iMcs.stopWifiDisplayScan();
            return returnCode;
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }

    @Override // com.milink.api.v1.IMilinkClientManager
    public ReturnCode zoomPhoto(String str, int i, int i2, int i3, int i4, int i5, int i6, float f) {
        IMcs iMcs = this.mService;
        if (iMcs == null) {
            return ReturnCode.NotConnected;
        }
        ReturnCode returnCode = ReturnCode.OK;
        try {
            return getReturnCode(iMcs.zoomPhoto(str, i, i2, i3, i4, i5, i6, f));
        } catch (RemoteException e) {
            e.printStackTrace();
            return ReturnCode.ServiceException;
        }
    }
}
