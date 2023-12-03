package com.android.settings.media;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.android.settingslib.media.MediaDevice;

/* loaded from: classes.dex */
public class MediaOutputSliceWorker extends MediaDeviceUpdateWorker {
    private static final boolean DBG = Log.isLoggable("MediaOutputSliceWorker", 3);
    private int mAppliedDeviceCountWithinRemoteGroup;
    private int mConnectedBluetoothDeviceCount;
    private int mRemoteDeviceCount;
    private int mWiredDeviceCount;

    public MediaOutputSliceWorker(Context context, Uri uri) {
        super(context, uri);
    }

    private void updateLoggingDeviceCount() {
        this.mRemoteDeviceCount = 0;
        this.mConnectedBluetoothDeviceCount = 0;
        this.mWiredDeviceCount = 0;
        this.mAppliedDeviceCountWithinRemoteGroup = 0;
        for (MediaDevice mediaDevice : this.mMediaDevices) {
            if (mediaDevice.isConnected()) {
                int deviceType = mediaDevice.getDeviceType();
                if (deviceType == 1 || deviceType == 2) {
                    this.mWiredDeviceCount++;
                } else if (deviceType == 4) {
                    this.mConnectedBluetoothDeviceCount++;
                } else if (deviceType == 5 || deviceType == 6) {
                    this.mRemoteDeviceCount++;
                }
            }
        }
        if (DBG) {
            Log.d("MediaOutputSliceWorker", "connected devices: wired: " + this.mWiredDeviceCount + " bluetooth: " + this.mConnectedBluetoothDeviceCount + " remote: " + this.mRemoteDeviceCount);
        }
    }

    @Override // com.android.settings.media.MediaDeviceUpdateWorker, com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onRequestFailed(int i) {
        if (DBG) {
            Log.e("MediaOutputSliceWorker", "onRequestFailed - " + i);
        }
        updateLoggingDeviceCount();
        super.onRequestFailed(i);
    }

    @Override // com.android.settings.media.MediaDeviceUpdateWorker, com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        if (DBG) {
            Log.d("MediaOutputSliceWorker", "onSelectedDeviceStateChanged - " + mediaDevice.toString());
        }
        updateLoggingDeviceCount();
        super.onSelectedDeviceStateChanged(mediaDevice, i);
    }
}
