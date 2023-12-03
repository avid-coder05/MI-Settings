package com.android.settingslib.media;

import android.media.MediaRoute2Info;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;

/* loaded from: classes2.dex */
public class MediaDeviceUtils {
    public static String getId(MediaRoute2Info mediaRoute2Info) {
        return mediaRoute2Info.getId();
    }

    public static String getId(CachedBluetoothDevice cachedBluetoothDevice) {
        return cachedBluetoothDevice.isHearingAidDevice() ? Long.toString(cachedBluetoothDevice.getHiSyncId()) : cachedBluetoothDevice.getAddress();
    }
}
