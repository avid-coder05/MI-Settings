package com.android.settingslib.media;

import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.text.TextUtils;
import android.util.Log;

/* loaded from: classes2.dex */
public abstract class MediaDevice implements Comparable<MediaDevice> {
    private int mConnectedRecord;
    protected final Context mContext;
    protected final String mPackageName;
    protected final MediaRoute2Info mRouteInfo;
    protected final MediaRouter2Manager mRouterManager;
    private int mState;
    int mType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaDevice(Context context, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        this.mContext = context;
        this.mRouteInfo = mediaRoute2Info;
        this.mRouterManager = mediaRouter2Manager;
        this.mPackageName = str;
        setType(mediaRoute2Info);
    }

    private void setType(MediaRoute2Info mediaRoute2Info) {
        if (mediaRoute2Info == null) {
            this.mType = 4;
            return;
        }
        int type = mediaRoute2Info.getType();
        if (type == 2) {
            this.mType = 7;
        } else if (type == 3 || type == 4) {
            this.mType = 2;
        } else {
            if (type != 8) {
                if (type != 9 && type != 22) {
                    if (type != 23) {
                        if (type == 2000) {
                            this.mType = 6;
                            return;
                        }
                        switch (type) {
                            case 11:
                            case 12:
                            case 13:
                                break;
                            default:
                                this.mType = 5;
                                return;
                        }
                    }
                }
                this.mType = 1;
                return;
            }
            this.mType = 4;
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(MediaDevice mediaDevice) {
        if (isConnected() ^ mediaDevice.isConnected()) {
            return isConnected() ? -1 : 1;
        }
        int i = this.mType;
        int i2 = mediaDevice.mType;
        if (i != i2) {
            return i < i2 ? -1 : 1;
        } else if (isFastPairDevice()) {
            return -1;
        } else {
            if (mediaDevice.isFastPairDevice()) {
                return 1;
            }
            if (isCarKitDevice()) {
                return -1;
            }
            if (mediaDevice.isCarKitDevice()) {
                return 1;
            }
            String lastSelectedDevice = ConnectionRecordManager.getInstance().getLastSelectedDevice();
            if (TextUtils.equals(lastSelectedDevice, getId())) {
                return -1;
            }
            if (TextUtils.equals(lastSelectedDevice, mediaDevice.getId())) {
                return 1;
            }
            int i3 = this.mConnectedRecord;
            int i4 = mediaDevice.mConnectedRecord;
            return (i3 == i4 || (i4 <= 0 && i3 <= 0)) ? getName().compareToIgnoreCase(mediaDevice.getName()) : i4 - i3;
        }
    }

    public boolean connect() {
        if (this.mRouteInfo == null) {
            Log.w("MediaDevice", "Unable to connect. RouteInfo is empty");
            return false;
        }
        setConnectedRecord();
        this.mRouterManager.selectRoute(this.mPackageName, this.mRouteInfo);
        return true;
    }

    public void disconnect() {
    }

    public boolean equals(Object obj) {
        if (obj instanceof MediaDevice) {
            return ((MediaDevice) obj).getId().equals(getId());
        }
        return false;
    }

    public int getDeviceType() {
        return this.mType;
    }

    public abstract String getId();

    public abstract String getName();

    public int getState() {
        return this.mState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initDeviceRecord() {
        ConnectionRecordManager.getInstance().fetchLastSelectedDevice(this.mContext);
        this.mConnectedRecord = ConnectionRecordManager.getInstance().fetchConnectionRecord(this.mContext, getId());
    }

    protected boolean isCarKitDevice() {
        return false;
    }

    public abstract boolean isConnected();

    protected boolean isFastPairDevice() {
        return false;
    }

    void setConnectedRecord() {
        this.mConnectedRecord++;
        ConnectionRecordManager.getInstance().setConnectionRecord(this.mContext, getId(), this.mConnectedRecord);
    }

    public void setState(int i) {
        this.mState = i;
    }
}
