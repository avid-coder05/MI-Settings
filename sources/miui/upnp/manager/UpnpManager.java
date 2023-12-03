package miui.upnp.manager;

import android.content.Context;
import android.util.Log;
import miui.upnp.manager.ctrlpoint.UnknownDevice;
import miui.upnp.typedef.deviceclass.DeviceClass;
import miui.upnp.typedef.error.UpnpError;
import miui.upnp.typedef.exception.UpnpException;

/* loaded from: classes4.dex */
public class UpnpManager {
    private static final String TAG = "UpnpManager";
    private static final Object classLock = UpnpManager.class;
    private static UpnpManager instance;
    private UpnpClassProvider classProvider;
    private UpnpControlPoint cp;
    private UpnpHost host;

    private UpnpManager() {
    }

    public static UpnpManager getInstance() {
        UpnpManager upnpManager;
        synchronized (classLock) {
            if (instance == null) {
                instance = new UpnpManager();
            }
            upnpManager = instance;
        }
        return upnpManager;
    }

    public void close() throws UpnpException {
        UpnpClassProvider upnpClassProvider = this.classProvider;
        if (upnpClassProvider != null) {
            upnpClassProvider.clear();
            this.classProvider = null;
        }
        UpnpControlPoint upnpControlPoint = this.cp;
        if (upnpControlPoint != null) {
            if (!upnpControlPoint.unbind()) {
                Log.d(TAG, "UpnpControlPoint unbind failed");
                throw new UpnpException(UpnpError.SERVICE_UNBIND_FAILED);
            }
            this.cp = null;
        }
        UpnpHost upnpHost = this.host;
        if (upnpHost == null || upnpHost.unbind()) {
            return;
        }
        Log.d(TAG, "UpnpHost unbind failed");
        throw new UpnpException(UpnpError.SERVICE_UNBIND_FAILED);
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public UpnpClassProvider getClassProvider() {
        return this.classProvider;
    }

    public UpnpControlPoint getControlPoint() {
        return this.cp;
    }

    public UpnpHost getHost() {
        return this.host;
    }

    public void open(Context context) throws UpnpException {
        this.cp = new UpnpControlPoint(context);
        this.host = new UpnpHost(context);
        UpnpClassProvider upnpClassProvider = new UpnpClassProvider();
        this.classProvider = upnpClassProvider;
        upnpClassProvider.addDeviceClass(new DeviceClass(UnknownDevice.DEVICE_TYPE, UnknownDevice.class));
        if (!this.cp.bind()) {
            Log.w(TAG, "UpnpControlPoint bind failed");
            throw new UpnpException(UpnpError.SERVICE_BIND_FAILED);
        } else if (this.host.bind()) {
        } else {
            Log.w(TAG, "UpnpHost bind failed");
            throw new UpnpException(UpnpError.SERVICE_BIND_FAILED);
        }
    }
}
