package miui.upnp.manager.ctrlpoint;

import android.os.Parcelable;
import miui.upnp.typedef.device.Device;

/* loaded from: classes4.dex */
public abstract class AbstractDevice implements Parcelable {
    public Device device;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractDevice abstractDevice = (AbstractDevice) obj;
        Device device = this.device;
        if (device != null) {
            if (device.equals(abstractDevice.device)) {
                return true;
            }
        } else if (abstractDevice.device == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        Device device = this.device;
        if (device != null) {
            return device.hashCode();
        }
        return 0;
    }
}
