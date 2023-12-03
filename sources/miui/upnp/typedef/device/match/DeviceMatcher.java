package miui.upnp.typedef.device.match;

import java.util.Iterator;
import java.util.List;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.Service;
import miui.upnp.typedef.device.urn.Urn;

/* loaded from: classes4.dex */
public class DeviceMatcher {
    public static boolean deviceIsMatched(Device device, List<Urn> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        for (Urn urn : list) {
            if (urn.getType() == Urn.Type.DEVICE) {
                if (device.getDeviceType().toString().equals(urn.toString())) {
                    return true;
                }
            } else if (serviceIsMatched(device, urn)) {
                return true;
            }
        }
        return false;
    }

    public static boolean serviceIsMatched(Device device, Urn urn) {
        Iterator<Service> it = device.getServices().values().iterator();
        while (it.hasNext()) {
            if (it.next().getType().toString().equals(urn.toString())) {
                return true;
            }
        }
        return false;
    }
}
