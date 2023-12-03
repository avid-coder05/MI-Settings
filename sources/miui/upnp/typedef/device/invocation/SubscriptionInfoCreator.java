package miui.upnp.typedef.device.invocation;

import java.util.UUID;
import miui.upnp.typedef.device.Service;

/* loaded from: classes4.dex */
public class SubscriptionInfoCreator {
    private SubscriptionInfoCreator() {
    }

    public static SubscriptionInfo create(Service service) {
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo();
        subscriptionInfo.setHostAddress(service.getDevice().getAddress());
        subscriptionInfo.setHostPort(service.getDevice().getHostPort());
        subscriptionInfo.setSubscriptionId(service.getSubscriptionId());
        subscriptionInfo.setEventSubUrl(service.getEventSubUrl());
        subscriptionInfo.setDiscoveryTypes(service.getDevice().getDiscoveryTypes());
        subscriptionInfo.setDeviceId(service.getDevice().getDeviceId());
        subscriptionInfo.setServiceId(service.getServiceId());
        subscriptionInfo.setCallbackUrl(String.format("/%s", UUID.nameUUIDFromBytes(service.getEventSubUrl().getBytes()).toString()));
        return subscriptionInfo;
    }
}
