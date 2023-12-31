package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.widget.CompoundButton;
import com.android.settings.R;
import com.android.settings.bluetooth.BluetoothPairingDialogFragment;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import java.util.Locale;

/* loaded from: classes.dex */
public class BluetoothPairingController implements CompoundButton.OnCheckedChangeListener, BluetoothPairingDialogFragment.BluetoothPairingDialogListener {
    private LocalBluetoothManager mBluetoothManager;
    private BluetoothDevice mDevice;
    private String mDeviceName;
    private int mInitiator;
    private int mPasskey;
    private String mPasskeyFormatted;
    private LocalBluetoothProfile mPbapClientProfile;
    int mType;
    private String mUserInput;

    public BluetoothPairingController(Intent intent, Context context) {
        this.mBluetoothManager = Utils.getLocalBtManager(context);
        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        this.mDevice = bluetoothDevice;
        if (this.mBluetoothManager == null) {
            throw new IllegalStateException("Could not obtain LocalBluetoothManager");
        }
        if (bluetoothDevice == null) {
            throw new IllegalStateException("Could not find BluetoothDevice");
        }
        this.mType = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
        this.mPasskey = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE);
        this.mInitiator = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_INITIATOR", Integer.MIN_VALUE);
        this.mDeviceName = this.mBluetoothManager.getCachedDeviceManager().getName(this.mDevice);
        this.mPbapClientProfile = this.mBluetoothManager.getProfileManager().getPbapClientProfile();
        this.mPasskeyFormatted = formatKey(this.mPasskey);
    }

    private String formatKey(int i) {
        int i2 = this.mType;
        if (i2 == 2 || i2 == 4) {
            return String.format(Locale.US, "%06d", Integer.valueOf(i));
        }
        if (i2 != 5) {
            return null;
        }
        return String.format("%04d", Integer.valueOf(i));
    }

    private void onPair(String str) {
        Log.d("BTPairingController", "Pairing dialog accepted");
        switch (this.mType) {
            case 0:
            case 7:
                this.mDevice.setPin(str);
                return;
            case 1:
            case 4:
            case 5:
            case 6:
                return;
            case 2:
            case 3:
                this.mDevice.setPairingConfirmation(true);
                return;
            default:
                Log.e("BTPairingController", "Incorrect pairing type received");
                return;
        }
    }

    public boolean deviceEquals(BluetoothDevice bluetoothDevice) {
        return this.mDevice == bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return this.mDevice;
    }

    public boolean getContactSharingState() {
        BluetoothClass bluetoothClass;
        int phonebookAccessPermission = this.mDevice.getPhonebookAccessPermission();
        if (phonebookAccessPermission != 1) {
            return (phonebookAccessPermission == 2 || (bluetoothClass = this.mDevice.getBluetoothClass()) == null || bluetoothClass.getDeviceClass() != 1032) ? false : true;
        }
        return true;
    }

    public int getDeviceMaxPasskeyLength() {
        int i = this.mType;
        if (i != 0) {
            if (i != 1) {
                return i != 7 ? 0 : 16;
            }
            return 6;
        }
        return 16;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public int getDeviceVariantMessageHintId() {
        int i = this.mType;
        if (i == 0 || i == 1) {
            return R.string.bluetooth_pin_values_hint;
        }
        if (i != 7) {
            return -1;
        }
        return R.string.bluetooth_pin_values_hint_16_digits;
    }

    public int getDeviceVariantMessageId() {
        int i = this.mType;
        if (i != 0) {
            if (i == 1) {
                return R.string.bluetooth_enter_passkey_other_device;
            }
            if (i != 7) {
                return -1;
            }
        }
        return R.string.bluetooth_enter_pin_other_device;
    }

    public int getDialogType() {
        switch (this.mType) {
            case 0:
            case 1:
            case 7:
                return 0;
            case 2:
            case 3:
            case 6:
                return 1;
            case 4:
            case 5:
                return 2;
            default:
                return -1;
        }
    }

    public String getPairingContent() {
        if (hasPairingContent()) {
            return this.mPasskeyFormatted;
        }
        return null;
    }

    public boolean hasPairingContent() {
        int i = this.mType;
        return i == 2 || i == 4 || i == 5;
    }

    public boolean isDisplayPairingKeyVariant() {
        int i = this.mType;
        return i == 4 || i == 5 || i == 6;
    }

    public boolean isPasskeyValid(Editable editable) {
        boolean z = this.mType == 7;
        if (editable.length() < 16 || !z) {
            return editable.length() > 0 && !z;
        }
        return true;
    }

    public boolean isProfileReady() {
        LocalBluetoothProfile localBluetoothProfile = this.mPbapClientProfile;
        return localBluetoothProfile != null && localBluetoothProfile.isProfileReady();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyDialogDisplayed() {
        int i = this.mType;
        if (i == 4) {
            this.mDevice.setPairingConfirmation(true);
        } else if (i == 5) {
            this.mDevice.setPin(this.mPasskeyFormatted);
        }
    }

    public void onCancel() {
        Log.d("BTPairingController", "Pairing dialog canceled");
        this.mDevice.cancelPairing();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.mDevice.setPhonebookAccessPermission(1);
        } else {
            this.mDevice.setPhonebookAccessPermission(2);
        }
    }

    public void onDialogNegativeClick(BluetoothPairingDialogFragment bluetoothPairingDialogFragment) {
        this.mDevice.setPhonebookAccessPermission(2);
        onCancel();
    }

    public void onDialogPositiveClick(BluetoothPairingDialogFragment bluetoothPairingDialogFragment) {
        if (getDialogType() == 0) {
            onPair(this.mUserInput);
        } else {
            onPair(null);
        }
    }

    public boolean pairingCodeIsAlphanumeric() {
        return this.mType != 1;
    }

    public void setContactSharingState() {
        if (this.mDevice.getPhonebookAccessPermission() == 1 || this.mDevice.getPhonebookAccessPermission() == 2) {
            if (this.mDevice.getPhonebookAccessPermission() == 1) {
                onCheckedChanged(null, true);
                return;
            } else {
                onCheckedChanged(null, false);
                return;
            }
        }
        BluetoothClass bluetoothClass = this.mDevice.getBluetoothClass();
        if (bluetoothClass == null || bluetoothClass.getDeviceClass() != 1032) {
            onCheckedChanged(null, false);
        } else {
            onCheckedChanged(null, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateUserInput(String str) {
        this.mUserInput = str;
    }
}
