package miui.upnp.manager.handler;

import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.error.UpnpError;

/* loaded from: classes4.dex */
public interface UpnpInvokeCompletionHandler {
    void onFailed(UpnpError upnpError);

    void onSucceed(ActionInfo actionInfo);
}
