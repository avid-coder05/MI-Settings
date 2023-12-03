package miui.upnp.manager.host;

import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.error.UpnpError;

/* loaded from: classes4.dex */
public abstract class ServiceHandler {
    public abstract UpnpError onAction(ActionInfo actionInfo);

    public String toCtrlUrl(String str, ServiceType serviceType) {
        return String.format("/upnp/%s/ctrl/%s", str, serviceType.getName());
    }

    public String toEventUrl(String str, ServiceType serviceType) {
        return String.format("/upnp/%s/event/%s", str, serviceType.getName());
    }

    public String toScpdUrl(String str, ServiceType serviceType) {
        return String.format("/upnp/%s/%s.xml", str, serviceType.getName());
    }

    public String toServiceId(ServiceType serviceType) {
        return String.format("urn:upnp-org:serviceId:%s", serviceType.getName());
    }
}
