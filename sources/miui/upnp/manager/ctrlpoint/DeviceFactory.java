package miui.upnp.manager.ctrlpoint;

import android.annotation.TargetApi;
import android.util.Log;
import miui.upnp.manager.UpnpManager;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.deviceclass.DeviceClass;

/* loaded from: classes4.dex */
public class DeviceFactory {
    private static final String TAG = "DeviceFactroy";

    @TargetApi(19)
    public static AbstractDevice createDevice(Device device) {
        Log.e(TAG, "device type is: " + device.getDeviceType());
        DeviceClass deviceClass = UpnpManager.getInstance().getClassProvider().getDeviceClass(device.getDeviceType());
        if (deviceClass == null) {
            Log.e(TAG, "unknown device class: " + device.getDeviceType());
            deviceClass = UpnpManager.getInstance().getClassProvider().getDeviceClass(UnknownDevice.DEVICE_TYPE);
            if (deviceClass == null) {
                Log.e(TAG, "default device class not found");
                return null;
            }
        }
        Class<?> clazz = deviceClass.getClazz();
        if (clazz != null) {
            try {
                return (AbstractDevice) clazz.getMethod("create", Device.class).invoke(null, device);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return null;
            }
        }
        Log.e(TAG, "class not found: " + deviceClass.getDeviceType());
        return null;
    }
}
