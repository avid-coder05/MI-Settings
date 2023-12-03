package com.android.settingslib.media;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.media.RoutingSessionInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
public class InfoMediaManager extends MediaManager {
    private static final boolean DEBUG = Log.isLoggable("InfoMediaManager", 3);
    private LocalBluetoothManager mBluetoothManager;
    private MediaDevice mCurrentConnectedDevice;
    @VisibleForTesting
    final Executor mExecutor;
    @VisibleForTesting
    final RouterManagerCallback mMediaRouterCallback;
    @VisibleForTesting
    String mPackageName;
    @VisibleForTesting
    MediaRouter2Manager mRouterManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class RouterManagerCallback implements MediaRouter2Manager.Callback {
        RouterManagerCallback() {
        }

        public void onPreferredFeaturesChanged(String str, List<String> list) {
            if (TextUtils.equals(InfoMediaManager.this.mPackageName, str)) {
                InfoMediaManager.this.refreshDevices();
            }
        }

        public void onRequestFailed(int i) {
            InfoMediaManager.this.dispatchOnRequestFailed(i);
        }

        public void onRoutesAdded(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onRoutesChanged(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onRoutesRemoved(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onSessionUpdated(RoutingSessionInfo routingSessionInfo) {
            InfoMediaManager.this.dispatchDataChanged();
        }

        public void onTransferFailed(RoutingSessionInfo routingSessionInfo, MediaRoute2Info mediaRoute2Info) {
            InfoMediaManager.this.dispatchOnRequestFailed(0);
        }

        public void onTransferred(RoutingSessionInfo routingSessionInfo, RoutingSessionInfo routingSessionInfo2) {
            if (InfoMediaManager.DEBUG) {
                Log.d("InfoMediaManager", "onTransferred() oldSession : " + ((Object) routingSessionInfo.getName()) + ", newSession : " + ((Object) routingSessionInfo2.getName()));
            }
            InfoMediaManager.this.mMediaDevices.clear();
            InfoMediaManager.this.mCurrentConnectedDevice = null;
            if (TextUtils.isEmpty(InfoMediaManager.this.mPackageName)) {
                InfoMediaManager.this.buildAllRoutes();
            } else {
                InfoMediaManager.this.buildAvailableRoutes();
            }
            InfoMediaManager.this.dispatchConnectedDeviceChanged(InfoMediaManager.this.mCurrentConnectedDevice != null ? InfoMediaManager.this.mCurrentConnectedDevice.getId() : null);
        }
    }

    public InfoMediaManager(Context context, String str, Notification notification, LocalBluetoothManager localBluetoothManager) {
        super(context, notification);
        this.mMediaRouterCallback = new RouterManagerCallback();
        this.mExecutor = Executors.newSingleThreadExecutor();
        this.mRouterManager = MediaRouter2Manager.getInstance(context);
        this.mBluetoothManager = localBluetoothManager;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mPackageName = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void buildAllRoutes() {
        for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getAllRoutes()) {
            if (DEBUG) {
                Log.d("InfoMediaManager", "buildAllRoutes() route : " + ((Object) mediaRoute2Info.getName()) + ", volume : " + mediaRoute2Info.getVolume() + ", type : " + mediaRoute2Info.getType());
            }
            if (mediaRoute2Info.isSystemRoute()) {
                addMediaDevice(mediaRoute2Info);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void buildAvailableRoutes() {
        for (MediaRoute2Info mediaRoute2Info : getAvailableRoutes(this.mPackageName)) {
            if (DEBUG) {
                Log.d("InfoMediaManager", "buildAvailableRoutes() route : " + ((Object) mediaRoute2Info.getName()) + ", volume : " + mediaRoute2Info.getVolume() + ", type : " + mediaRoute2Info.getType());
            }
            addMediaDevice(mediaRoute2Info);
        }
    }

    private List<MediaRoute2Info> getAvailableRoutes(String str) {
        ArrayList arrayList = new ArrayList();
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo(str);
        if (routingSessionInfo != null) {
            arrayList.addAll(this.mRouterManager.getSelectedRoutes(routingSessionInfo));
        }
        for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getTransferableRoutes(str)) {
            boolean z = false;
            Iterator it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                if (TextUtils.equals(mediaRoute2Info.getId(), ((MediaRoute2Info) it.next()).getId())) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                arrayList.add(mediaRoute2Info);
            }
        }
        return arrayList;
    }

    private RoutingSessionInfo getRoutingSessionInfo() {
        return getRoutingSessionInfo(this.mPackageName);
    }

    private RoutingSessionInfo getRoutingSessionInfo(String str) {
        List routingSessions = this.mRouterManager.getRoutingSessions(str);
        if (routingSessions == null || routingSessions.isEmpty()) {
            return null;
        }
        return (RoutingSessionInfo) routingSessions.get(routingSessions.size() - 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshDevices() {
        this.mMediaDevices.clear();
        this.mCurrentConnectedDevice = null;
        if (TextUtils.isEmpty(this.mPackageName)) {
            buildAllRoutes();
        } else {
            buildAvailableRoutes();
        }
        dispatchDeviceListAdded();
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x00a6  */
    /* JADX WARN: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    @com.android.internal.annotations.VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void addMediaDevice(android.media.MediaRoute2Info r9) {
        /*
            r8 = this;
            int r0 = r9.getType()
            if (r0 == 0) goto L79
            r1 = 2000(0x7d0, float:2.803E-42)
            if (r0 == r1) goto L79
            r1 = 2
            if (r0 == r1) goto L6d
            r1 = 3
            if (r0 == r1) goto L6d
            r1 = 4
            if (r0 == r1) goto L6d
            r1 = 8
            if (r0 == r1) goto L45
            r1 = 9
            if (r0 == r1) goto L6d
            r1 = 22
            if (r0 == r1) goto L6d
            r1 = 23
            if (r0 == r1) goto L45
            r1 = 1001(0x3e9, float:1.403E-42)
            if (r0 == r1) goto L79
            r1 = 1002(0x3ea, float:1.404E-42)
            if (r0 == r1) goto L79
            switch(r0) {
                case 11: goto L6d;
                case 12: goto L6d;
                case 13: goto L6d;
                default: goto L2e;
            }
        L2e:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r1 = "addMediaDevice() unknown device type : "
            r9.append(r1)
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            java.lang.String r0 = "InfoMediaManager"
            android.util.Log.w(r0, r9)
            goto L6b
        L45:
            android.bluetooth.BluetoothAdapter r0 = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            java.lang.String r1 = r9.getAddress()
            android.bluetooth.BluetoothDevice r0 = r0.getRemoteDevice(r1)
            com.android.settingslib.bluetooth.LocalBluetoothManager r1 = r8.mBluetoothManager
            com.android.settingslib.bluetooth.CachedBluetoothDeviceManager r1 = r1.getCachedDeviceManager()
            com.android.settingslib.bluetooth.CachedBluetoothDevice r4 = r1.findDevice(r0)
            if (r4 == 0) goto L6b
            com.android.settingslib.media.BluetoothMediaDevice r0 = new com.android.settingslib.media.BluetoothMediaDevice
            android.content.Context r3 = r8.mContext
            android.media.MediaRouter2Manager r5 = r8.mRouterManager
            java.lang.String r7 = r8.mPackageName
            r2 = r0
            r6 = r9
            r2.<init>(r3, r4, r5, r6, r7)
            goto La4
        L6b:
            r0 = 0
            goto La4
        L6d:
            com.android.settingslib.media.PhoneMediaDevice r0 = new com.android.settingslib.media.PhoneMediaDevice
            android.content.Context r1 = r8.mContext
            android.media.MediaRouter2Manager r2 = r8.mRouterManager
            java.lang.String r3 = r8.mPackageName
            r0.<init>(r1, r2, r9, r3)
            goto La4
        L79:
            com.android.settingslib.media.InfoMediaDevice r0 = new com.android.settingslib.media.InfoMediaDevice
            android.content.Context r1 = r8.mContext
            android.media.MediaRouter2Manager r2 = r8.mRouterManager
            java.lang.String r3 = r8.mPackageName
            r0.<init>(r1, r2, r9, r3)
            java.lang.String r1 = r8.mPackageName
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto La4
            android.media.RoutingSessionInfo r1 = r8.getRoutingSessionInfo()
            java.util.List r1 = r1.getSelectedRoutes()
            java.lang.String r9 = r9.getId()
            boolean r9 = r1.contains(r9)
            if (r9 == 0) goto La4
            com.android.settingslib.media.MediaDevice r9 = r8.mCurrentConnectedDevice
            if (r9 != 0) goto La4
            r8.mCurrentConnectedDevice = r0
        La4:
            if (r0 == 0) goto Lab
            java.util.List<com.android.settingslib.media.MediaDevice> r8 = r8.mMediaDevices
            r8.add(r0)
        Lab:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.media.InfoMediaManager.addMediaDevice(android.media.MediaRoute2Info):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustSessionVolume(RoutingSessionInfo routingSessionInfo, int i) {
        if (routingSessionInfo == null) {
            Log.w("InfoMediaManager", "Unable to adjust session volume. RoutingSessionInfo is empty");
        } else {
            this.mRouterManager.setSessionVolume(routingSessionInfo, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean connectDeviceWithoutPackageName(MediaDevice mediaDevice) {
        List activeSessions = this.mRouterManager.getActiveSessions();
        if (activeSessions.size() > 0) {
            this.mRouterManager.transfer((RoutingSessionInfo) activeSessions.get(0), mediaDevice.mRouteInfo);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<RoutingSessionInfo> getActiveMediaSession() {
        return this.mRouterManager.getActiveSessions();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaDevice getCurrentConnectedDevice() {
        return this.mCurrentConnectedDevice;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldDisableMediaOutput(String str) {
        if (TextUtils.isEmpty(str)) {
            Log.w("InfoMediaManager", "shouldDisableMediaOutput() package name is null or empty!");
            return true;
        }
        return this.mRouterManager.getTransferableRoutes(str).isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @TargetApi(30)
    public boolean shouldEnableVolumeSeekBar(RoutingSessionInfo routingSessionInfo) {
        return false;
    }

    public void startScan() {
        this.mMediaDevices.clear();
        this.mRouterManager.registerCallback(this.mExecutor, this.mMediaRouterCallback);
        this.mRouterManager.startScan();
        refreshDevices();
    }

    public void stopScan() {
        this.mRouterManager.unregisterCallback(this.mMediaRouterCallback);
        this.mRouterManager.stopScan();
    }
}
