package miui.upnp.manager.handler;

import miui.upnp.typedef.error.UpnpError;

/* loaded from: classes4.dex */
public interface UpnpCompletionHandler {
    void onFailed(UpnpError upnpError);

    void onSucceed();
}
