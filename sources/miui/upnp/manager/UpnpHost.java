package miui.upnp.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import miui.upnp.manager.handler.UpnpActionHandler;
import miui.upnp.manager.handler.UpnpCompletionHandler;
import miui.upnp.service.handler.IActionListener;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.service.host.IUpnpHostService;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.invocation.EventInfo;
import miui.upnp.typedef.error.UpnpError;
import miui.upnp.typedef.exception.UpnpException;
import miui.upnp.utils.Binding;

/* loaded from: classes4.dex */
public class UpnpHost extends Binding {
    private static String SVC_NAME = IUpnpHostService.class.getName();
    private static final String TAG = "UpnpHost";
    private static final String UPNP_SERVICE_PACKAGE_NAME = "com.xiaomi.upnp";
    private IUpnpHostService serviceInstance;

    public UpnpHost(Context context) {
        super(context);
    }

    public synchronized boolean bind() {
        return super.bind(UPNP_SERVICE_PACKAGE_NAME, SVC_NAME);
    }

    @Override // miui.upnp.utils.Binding
    protected void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.serviceInstance = IUpnpHostService.Stub.asInterface(iBinder);
    }

    @Override // miui.upnp.utils.Binding
    protected void onServiceDisconnected(ComponentName componentName) {
        this.serviceInstance = null;
    }

    public void register(Device device, final UpnpCompletionHandler upnpCompletionHandler, final UpnpActionHandler upnpActionHandler) throws UpnpException {
        Log.d(TAG, "register");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        if (device == null) {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
        try {
            this.serviceInstance.register(device, new ICompletionHandler.Stub() { // from class: miui.upnp.manager.UpnpHost.1
                @Override // miui.upnp.service.handler.ICompletionHandler
                public void onFailed(UpnpError upnpError) throws RemoteException {
                    try {
                        upnpCompletionHandler.onFailed(upnpError);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // miui.upnp.service.handler.ICompletionHandler
                public void onSucceed() throws RemoteException {
                    try {
                        upnpCompletionHandler.onSucceed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new IActionListener.Stub() { // from class: miui.upnp.manager.UpnpHost.2
                @Override // miui.upnp.service.handler.IActionListener
                public UpnpError onAction(ActionInfo actionInfo) throws RemoteException {
                    try {
                        return upnpActionHandler.onAction(actionInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return UpnpError.INTERNAL;
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UpnpException(UpnpError.INTERNAL, "register failed: RemoteException");
        }
    }

    public void sendEvents(EventInfo eventInfo) throws UpnpException {
        Log.d(TAG, "sendEvents");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        if (eventInfo == null) {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
        try {
            this.serviceInstance.sendEvents(eventInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UpnpException(UpnpError.INTERNAL, "sendEvents failed: RemoteException");
        }
    }

    public void start() throws UpnpException {
        Log.d(TAG, "start");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        try {
            this.serviceInstance.start();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UpnpException(UpnpError.INTERNAL, "start failed: RemoteException");
        }
    }

    public void stop() throws UpnpException {
        Log.d(TAG, "stop");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        try {
            this.serviceInstance.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UpnpException(UpnpError.INTERNAL, "stop failed: RemoteException");
        }
    }

    public void unregister(Device device, final UpnpCompletionHandler upnpCompletionHandler) throws UpnpException {
        Log.d(TAG, "unregister");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        if (device == null) {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
        try {
            this.serviceInstance.unregister(device, new ICompletionHandler.Stub() { // from class: miui.upnp.manager.UpnpHost.3
                @Override // miui.upnp.service.handler.ICompletionHandler
                public void onFailed(UpnpError upnpError) throws RemoteException {
                    try {
                        upnpCompletionHandler.onFailed(upnpError);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // miui.upnp.service.handler.ICompletionHandler
                public void onSucceed() throws RemoteException {
                    try {
                        upnpCompletionHandler.onSucceed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UpnpException(UpnpError.INTERNAL, "unregister failed: RemoteException");
        }
    }
}
