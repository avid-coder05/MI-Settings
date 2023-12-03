package miui.upnp.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;
import miui.upnp.manager.ctrlpoint.AbstractDevice;
import miui.upnp.manager.ctrlpoint.DeviceFactory;
import miui.upnp.manager.handler.UpnpCompletionHandler;
import miui.upnp.manager.handler.UpnpEventListener;
import miui.upnp.manager.handler.UpnpInvokeCompletionHandler;
import miui.upnp.manager.handler.UpnpScanListener;
import miui.upnp.service.controlpoint.IUpnpControlPointService;
import miui.upnp.service.handler.ICompletionHandler;
import miui.upnp.service.handler.IEventListener;
import miui.upnp.service.handler.IInvokeCompletionHandler;
import miui.upnp.service.handler.IScanListener;
import miui.upnp.service.handler.ISubscribeCompletionHandler;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.PropertyChanged;
import miui.upnp.typedef.device.Service;
import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.invocation.SubscriptionInfo;
import miui.upnp.typedef.device.invocation.SubscriptionInfoCreator;
import miui.upnp.typedef.device.urn.Urn;
import miui.upnp.typedef.deviceupdate.DeviceUpdate;
import miui.upnp.typedef.error.UpnpError;
import miui.upnp.typedef.exception.UpnpException;
import miui.upnp.utils.Binding;

/* loaded from: classes4.dex */
public class UpnpControlPoint extends Binding {
    private static String SVC_NAME = IUpnpControlPointService.class.getName();
    private static final String TAG = "UpnpControlPoint";
    private static final String UPNP_SERVICE_PACKAGE_NAME = "com.xiaomi.upnp";
    private IUpnpControlPointService serviceInstance;

    public UpnpControlPoint(Context context) {
        super(context);
    }

    public synchronized boolean bind() {
        return super.bind(UPNP_SERVICE_PACKAGE_NAME, SVC_NAME);
    }

    public void invoke(ActionInfo actionInfo, final UpnpInvokeCompletionHandler upnpInvokeCompletionHandler) throws UpnpException {
        Log.d(TAG, "invoke");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        if (actionInfo == null) {
            throw new UpnpException(UpnpError.INVALID_ARGUMENT);
        }
        try {
            this.serviceInstance.invoke(actionInfo, new IInvokeCompletionHandler.Stub() { // from class: miui.upnp.manager.UpnpControlPoint.4
                @Override // miui.upnp.service.handler.IInvokeCompletionHandler
                public void onFailed(UpnpError upnpError) throws RemoteException {
                    try {
                        upnpInvokeCompletionHandler.onFailed(upnpError);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // miui.upnp.service.handler.IInvokeCompletionHandler
                public void onSucceed(ActionInfo actionInfo2) throws RemoteException {
                    try {
                        upnpInvokeCompletionHandler.onSucceed(actionInfo2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RemoteException unused) {
            throw new UpnpException(UpnpError.INTERNAL, "invoke failed: RemoteException");
        }
    }

    @Override // miui.upnp.utils.Binding
    protected void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.serviceInstance = IUpnpControlPointService.Stub.asInterface(iBinder);
    }

    @Override // miui.upnp.utils.Binding
    protected void onServiceDisconnected(ComponentName componentName) {
        this.serviceInstance = null;
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

    public void startScan(List<Urn> list, final UpnpCompletionHandler upnpCompletionHandler, final UpnpScanListener upnpScanListener) throws UpnpException {
        Log.d(TAG, "startScan");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        try {
            this.serviceInstance.startScan(list, new ICompletionHandler.Stub() { // from class: miui.upnp.manager.UpnpControlPoint.1
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
            }, new IScanListener.Stub() { // from class: miui.upnp.manager.UpnpControlPoint.2
                @Override // miui.upnp.service.handler.IScanListener
                public void onDeviceFound(Device device) throws RemoteException {
                    Log.d(UpnpControlPoint.TAG, "onDeviceFound");
                    AbstractDevice createDevice = DeviceFactory.createDevice(device);
                    if (createDevice == null) {
                        Log.d(UpnpControlPoint.TAG, "DeviceFactory createDevice failed");
                        return;
                    }
                    try {
                        upnpScanListener.onDeviceFound(createDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // miui.upnp.service.handler.IScanListener
                public void onDeviceLost(Device device) throws RemoteException {
                    Log.d(UpnpControlPoint.TAG, "onDeviceLost");
                    AbstractDevice createDevice = DeviceFactory.createDevice(device);
                    if (createDevice == null) {
                        Log.d(UpnpControlPoint.TAG, "DeviceFactory createDevice failed");
                        return;
                    }
                    try {
                        upnpScanListener.onDeviceLost(createDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // miui.upnp.service.handler.IScanListener
                public void onDeviceUpdate(DeviceUpdate deviceUpdate) throws RemoteException {
                    Log.d(UpnpControlPoint.TAG, "onDeviceUpdate");
                    try {
                        upnpScanListener.onDeviceUpdate(deviceUpdate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UpnpException(UpnpError.INTERNAL, "startScan failed: RemoteException");
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

    public void stopScan(final UpnpCompletionHandler upnpCompletionHandler) throws UpnpException {
        Log.d(TAG, "stopScan");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        try {
            this.serviceInstance.stopScan(new ICompletionHandler.Stub() { // from class: miui.upnp.manager.UpnpControlPoint.3
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
        } catch (RemoteException unused) {
            throw new UpnpException(UpnpError.INTERNAL, "stopScan failed: RemoteException");
        }
    }

    public void subscribe(final Service service, final UpnpCompletionHandler upnpCompletionHandler, final UpnpEventListener upnpEventListener) throws UpnpException {
        Log.d(TAG, "subscribe");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        if (service.isSubscribed()) {
            throw new UpnpException(UpnpError.SERVICE_SUBSCRIBED);
        }
        try {
            this.serviceInstance.subscribe(SubscriptionInfoCreator.create(service), new ISubscribeCompletionHandler.Stub() { // from class: miui.upnp.manager.UpnpControlPoint.5
                @Override // miui.upnp.service.handler.ISubscribeCompletionHandler
                public void onFailed(UpnpError upnpError) throws RemoteException {
                    try {
                        upnpCompletionHandler.onFailed(upnpError);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // miui.upnp.service.handler.ISubscribeCompletionHandler
                public void onSucceed(SubscriptionInfo subscriptionInfo) throws RemoteException {
                    service.setSubscriptionId(subscriptionInfo.getSubscriptionId());
                    service.setSubscribed(true);
                    try {
                        upnpCompletionHandler.onSucceed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new IEventListener.Stub() { // from class: miui.upnp.manager.UpnpControlPoint.6
                @Override // miui.upnp.service.handler.IEventListener
                public void onEvent(String str, List<PropertyChanged> list) throws RemoteException {
                    try {
                        upnpEventListener.onEvent(str, list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // miui.upnp.service.handler.IEventListener
                public void onSubscriptionExpired(String str) throws RemoteException {
                    service.setSubscribed(false);
                    try {
                        upnpEventListener.onSubscriptionExpired(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RemoteException unused) {
            throw new UpnpException(UpnpError.INTERNAL, "invoke failed: RemoteException");
        }
    }

    public void unsubscribe(final Service service, final UpnpCompletionHandler upnpCompletionHandler) throws UpnpException {
        Log.d(TAG, "unsubscribe");
        if (!super.isBound()) {
            throw new UpnpException(UpnpError.SERVICE_NOT_BOUND);
        }
        if (!service.isSubscribed()) {
            throw new UpnpException(UpnpError.SERVICE_SUBSCRIBED);
        }
        try {
            this.serviceInstance.unsubscribe(SubscriptionInfoCreator.create(service), new ICompletionHandler.Stub() { // from class: miui.upnp.manager.UpnpControlPoint.7
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
                    service.setSubscribed(false);
                    service.setSubscriptionId("");
                    try {
                        upnpCompletionHandler.onSucceed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RemoteException unused) {
            throw new UpnpException(UpnpError.INTERNAL, "invoke failed: RemoteException");
        }
    }
}
