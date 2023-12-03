package miui.upnp.manager.handler;

import java.util.List;
import miui.upnp.typedef.device.PropertyChanged;

/* loaded from: classes4.dex */
public interface UpnpEventListener {
    void onEvent(String str, List<PropertyChanged> list);

    void onSubscriptionExpired(String str);
}
