package miui.upnp.manager;

import java.util.HashMap;
import java.util.Map;
import miui.upnp.typedef.device.urn.DeviceType;
import miui.upnp.typedef.deviceclass.DeviceClass;

/* loaded from: classes4.dex */
public class UpnpClassProvider {
    private Map<String, DeviceClass> classes = new HashMap();

    public synchronized void addDeviceClass(DeviceClass deviceClass) {
        this.classes.put(deviceClass.getDeviceType().toString(), deviceClass);
    }

    public synchronized void clear() {
        this.classes.clear();
    }

    public synchronized DeviceClass getDeviceClass(String str) {
        return this.classes.get(str);
    }

    public synchronized DeviceClass getDeviceClass(DeviceType deviceType) {
        return getDeviceClass(deviceType.toString());
    }
}
