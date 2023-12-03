package miui.bluetooth.ble;

import android.content.Context;
import android.os.RemoteException;
import miui.bluetooth.ble.MiBleProfile;

/* loaded from: classes3.dex */
public class MiBleUnlockProfile extends MiBleProfile {

    /* loaded from: classes3.dex */
    public interface OnUnlockStateChangeListener {
        public static final byte STATE_AUTHORISED = 1;
        public static final byte STATE_LOCKED = 0;
        public static final byte STATE_UNLOCKED = 2;

        void onUnlocked(byte b);
    }

    public MiBleUnlockProfile(Context context, String str, MiBleProfile.IProfileStateChangeCallback iProfileStateChangeCallback) {
        super(context, str, iProfileStateChangeCallback);
    }

    public void registerUnlockListener(final OnUnlockStateChangeListener onUnlockStateChangeListener) {
        if (onUnlockStateChangeListener == null) {
            return;
        }
        registerPropertyNotifyCallback(1, new MiBleProfile.IPropertyNotifyCallback() { // from class: miui.bluetooth.ble.MiBleUnlockProfile.1
            @Override // miui.bluetooth.ble.MiBleProfile.IPropertyNotifyCallback
            public void notifyProperty(int i, byte[] bArr) {
                if (i == 1) {
                    onUnlockStateChangeListener.onUnlocked(bArr != null ? bArr[0] : (byte) 0);
                }
            }
        });
    }

    public boolean setLock(String str) {
        if (isReady()) {
            try {
                return this.mService.authorize(this.mDevice, this.mClientId, str);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean setRssiThreshold(int i) {
        if (isReady()) {
            try {
                return this.mService.setRssiThreshold(this.mDevice, this.mClientId, i);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean unlock() {
        if (isReady()) {
            try {
                return this.mService.authenticate(this.mDevice, this.mClientId);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void unregisterUnlockListener() {
        unregisterPropertyNotifyCallback(1);
    }
}
