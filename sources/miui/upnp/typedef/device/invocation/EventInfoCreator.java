package miui.upnp.typedef.device.invocation;

import android.util.Log;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.Service;
import miui.upnp.typedef.property.Property;

/* loaded from: classes4.dex */
public class EventInfoCreator {
    private static final String TAG = "EventInfoCreator";

    public static EventInfo create(Device device, String str) {
        Service service = device.getService(str);
        if (service == null) {
            Log.d(TAG, String.format("Service not found: %s", str));
            return null;
        }
        return create(service);
    }

    public static EventInfo create(Service service) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.setDiscoveryTypes(service.getDevice().getDiscoveryTypes());
        eventInfo.setServiceType(service.getType());
        eventInfo.setServiceId(service.getServiceId());
        eventInfo.setDeviceId(service.getDevice().getDeviceId());
        for (Property property : service.getProperties()) {
            if (property.getDefinition().isSendEvents()) {
                eventInfo.getProperties().add(property);
            }
        }
        return eventInfo;
    }
}
