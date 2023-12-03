package miui.bluetooth.ble;

import android.content.Context;
import android.os.RemoteException;
import java.nio.ByteBuffer;
import miui.bluetooth.ble.MiBleProfile;

/* loaded from: classes3.dex */
public class MiBlePayProfile extends MiBleProfile {

    /* loaded from: classes3.dex */
    public interface OnRSSIChangedListerner {
        void onRssi(int i);
    }

    public MiBlePayProfile(Context context, String str, MiBleProfile.IProfileStateChangeCallback iProfileStateChangeCallback) {
        super(context, str, iProfileStateChangeCallback);
    }

    public byte[] encrypt(byte[] bArr) {
        if (isReady()) {
            try {
                return this.mService.encrypt(this.mDevice, this.mClientId, bArr);
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void registerRssiChangedListener(final OnRSSIChangedListerner onRSSIChangedListerner) {
        if (onRSSIChangedListerner == null) {
            return;
        }
        registerPropertyNotifyCallback(4, new MiBleProfile.IPropertyNotifyCallback() { // from class: miui.bluetooth.ble.MiBlePayProfile.1
            @Override // miui.bluetooth.ble.MiBleProfile.IPropertyNotifyCallback
            public void notifyProperty(int i, byte[] bArr) {
                if (i == 4 && bArr != null && bArr.length == 4) {
                    onRSSIChangedListerner.onRssi(ByteBuffer.wrap(bArr).getInt());
                }
            }
        });
    }

    public boolean setEncryptionKey(byte[] bArr) {
        if (isReady()) {
            try {
                return this.mService.setEncryptionKey(this.mDevice, this.mClientId, bArr);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void unregisterRssiChangedListener() {
        unregisterPropertyNotifyCallback(4);
    }
}
