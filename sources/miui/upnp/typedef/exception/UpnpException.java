package miui.upnp.typedef.exception;

import miui.upnp.typedef.error.UpnpError;

/* loaded from: classes4.dex */
public class UpnpException extends Exception {
    private int errorCode;

    public UpnpException(int i, String str) {
        super(str);
        this.errorCode = i;
    }

    public UpnpException(UpnpError upnpError) {
        super(upnpError.getMessage());
        this.errorCode = upnpError.getCode();
    }

    public UpnpException(UpnpError upnpError, String str) {
        super(upnpError.getMessage() + " -> " + str);
        this.errorCode = upnpError.getCode();
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public UpnpError toUpnpError() {
        return new UpnpError(this.errorCode, getMessage());
    }
}
