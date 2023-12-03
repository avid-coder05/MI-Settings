package miui.upnp.manager.host;

import android.content.Context;
import android.net.wifi.WifiManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.device.urn.DeviceType;
import miui.upnp.typedef.exception.UpnpException;

/* loaded from: classes4.dex */
public class DeviceConfig {
    private String deviceName;
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private String manufacturer;
    private String manufacturerUrl;
    private String modelDescription;
    private String modelName;
    private String modelNumber;
    private String modelUrl;

    private String genDeviceId(Context context) {
        return "uuid:" + UUID.nameUUIDFromBytes(((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress().getBytes()).toString();
    }

    public void addDiscoveryType(DiscoveryType discoveryType) {
        this.discoveryTypes.add(discoveryType);
    }

    public Device build(Context context, DeviceType deviceType) throws UpnpException {
        String genDeviceId = genDeviceId(context);
        Device device = new Device(deviceType);
        Iterator<DiscoveryType> it = this.discoveryTypes.iterator();
        while (it.hasNext()) {
            device.addDiscoveryType(it.next());
        }
        device.setDeviceId(genDeviceId);
        device.setFriendlyName(this.deviceName);
        device.setLocation("/upnp/" + genDeviceId + "/description.xml");
        device.setModelNumber(this.modelNumber);
        device.setModelName(this.modelName);
        device.setModelDescription(this.modelDescription);
        device.setModelUrl(this.modelUrl);
        device.setManufacturer(this.manufacturer);
        device.setManufacturerUrl(this.manufacturerUrl);
        return device;
    }

    public void deviceName(String str) {
        this.deviceName = str;
    }

    public void manufacturer(String str) {
        this.manufacturer = str;
    }

    public void manufacturerUrl(String str) {
        this.manufacturerUrl = str;
    }

    public void modelDescription(String str) {
        this.modelDescription = str;
    }

    public void modelName(String str) {
        this.modelName = str;
    }

    public void modelNumber(String str) {
        this.modelNumber = str;
    }

    public void modelUrl(String str) {
        this.modelUrl = str;
    }
}
