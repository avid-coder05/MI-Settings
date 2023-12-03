package miui.upnp.manager.handler;

import miui.upnp.manager.ctrlpoint.AbstractDevice;
import miui.upnp.typedef.deviceupdate.DeviceUpdate;

/* loaded from: classes4.dex */
public interface UpnpScanListener {
    void onDeviceFound(AbstractDevice abstractDevice);

    void onDeviceLost(AbstractDevice abstractDevice);

    void onDeviceUpdate(DeviceUpdate deviceUpdate);
}
