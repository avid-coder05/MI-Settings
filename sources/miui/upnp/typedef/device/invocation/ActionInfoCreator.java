package miui.upnp.typedef.device.invocation;

import android.util.Log;
import miui.upnp.typedef.device.Action;
import miui.upnp.typedef.device.Argument;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.Service;

/* loaded from: classes4.dex */
public class ActionInfoCreator {
    private static final String TAG = "ActionInfoCreator";

    public static ActionInfo create(Action action) {
        if (action == null) {
            Log.d(TAG, "action is null");
            return null;
        }
        Service service = action.getService();
        Device device = service.getDevice();
        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setAction(action);
        actionInfo.setDiscoveryTypes(device.getDiscoveryTypes());
        actionInfo.setServiceType(service.getType());
        actionInfo.setAddress(device.getAddress());
        actionInfo.setHostPort(device.getHostPort());
        actionInfo.setControlUrl(service.getControlUrl());
        actionInfo.setDeviceId(device.getDeviceId());
        actionInfo.setServiceId(service.getServiceId());
        for (Argument argument : action.getArguments()) {
            actionInfo.getProperties().put(argument.getRelatedProperty(), service.getProperty(argument.getRelatedProperty()));
        }
        return actionInfo;
    }

    public static ActionInfo create(Device device, String str, String str2) {
        Service service = device.getService(str);
        if (service == null) {
            Log.d(TAG, String.format("Service not found: %s", str));
            return null;
        }
        return create(service, str2);
    }

    public static ActionInfo create(Service service, String str) {
        Action action = service.getActions().get(str);
        if (action == null) {
            Log.d(TAG, String.format("Action not found: %s", str));
            return null;
        }
        return create(action);
    }
}
