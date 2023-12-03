package com.android.settings.bluetooth;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import com.android.settings.R;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import miui.bluetooth.ble.MiBleDeviceManager;

/* loaded from: classes.dex */
public class MiuiBluetoothDevicePreference extends BluetoothDevicePreference {
    public MiuiBluetoothDevicePreference(Context context, CachedBluetoothDevice cachedBluetoothDevice, MiBleDeviceManager miBleDeviceManager, boolean z) {
        super(context, cachedBluetoothDevice, z, miBleDeviceManager);
        onDeviceAttributesChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDevicePreference
    public Pair<Drawable, String> getBtClassDrawableWithDescription() {
        if (this.mBleDeviceMgr != null) {
            boolean z = GattProfile.isBond(this.mCachedDevice.getDevice()) || this.mCachedDevice.getBondState() == 12;
            int deviceType = this.mBleDeviceMgr.getDeviceType(getCachedDevice().getDevice().getAddress());
            if (deviceType != 1) {
                if (deviceType == 2) {
                    return new Pair<>(getContext().getDrawable(z ? R.drawable.ic_ble_mivr_controller_bonded : R.drawable.ic_ble_mivr_controller), getContext().getString(R.string.bluetooth_talkback_bluetooth));
                } else if (deviceType == 69) {
                    return new Pair<>(getContext().getDrawable(z ? R.drawable.ic_ble_mikey_bonded : R.drawable.ic_ble_mikey), getContext().getString(R.string.bluetooth_talkback_bluetooth));
                } else if (deviceType != 999) {
                    Log.w("MiuiBluetoothDevicePreference", "getBtClassDrawableWithDescription: TYPE_MI is UNKNOWN!");
                }
            }
            return new Pair<>(getContext().getDrawable(z ? R.drawable.ic_ble_bracelet_bonded : R.drawable.ic_ble_bracelet), getContext().getString(R.string.bluetooth_talkback_bluetooth));
        }
        return super.getBtClassDrawableWithDescription();
    }

    @Override // com.android.settings.bluetooth.BluetoothDevicePreference
    public void updateAttributes() {
        super.updateAttributes();
        if (getSummary() == null && GattProfile.isBond(this.mCachedDevice.getDevice())) {
            setSummary(R.string.bluetooth_paired);
        }
    }
}
