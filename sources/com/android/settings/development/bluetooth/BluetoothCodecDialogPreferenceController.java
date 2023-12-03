package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BluetoothCodecDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    private final AbstractBluetoothPreferenceController.Callback mCallback;

    private List<Integer> getIndexFromConfig(BluetoothCodecConfig[] bluetoothCodecConfigArr) {
        ArrayList arrayList = new ArrayList();
        for (BluetoothCodecConfig bluetoothCodecConfig : bluetoothCodecConfigArr) {
            arrayList.add(Integer.valueOf(convertCfgToBtnIndex(bluetoothCodecConfig.getCodecType())));
        }
        return arrayList;
    }

    int convertCfgToBtnIndex(int i) {
        int defaultIndex = getDefaultIndex();
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            switch (i) {
                                case 9:
                                    return 9;
                                case 10:
                                    return 8;
                                case 11:
                                    return 10;
                                default:
                                    Log.e("BtCodecCtr", "Unsupported config:" + i);
                                    return defaultIndex;
                            }
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((BaseBluetoothDialogPreference) this.mPreference).setCallback(this);
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    protected int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtCodecCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex(bluetoothCodecConfig.getCodecType());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_audio_codec_settings";
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public List<Integer> getSelectableIndex() {
        BluetoothCodecConfig[] selectableConfigs;
        ArrayList arrayList = new ArrayList();
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        arrayList.add(Integer.valueOf(getDefaultIndex()));
        if (bluetoothA2dp == null) {
            return arrayList;
        }
        BluetoothDevice activeDevice = bluetoothA2dp.getActiveDevice();
        if (activeDevice == null) {
            Log.d("BtCodecCtr", "Unable to get selectable index. No Active Bluetooth device");
            return arrayList;
        } else if (bluetoothA2dp.isOptionalCodecsEnabled(activeDevice) != 1 || (selectableConfigs = getSelectableConfigs(activeDevice)) == null) {
            arrayList.add(Integer.valueOf(convertCfgToBtnIndex(0)));
            return arrayList;
        } else {
            return getIndexFromConfig(selectableConfigs);
        }
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController, com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public void onIndexUpdated(int i) {
        super.onIndexUpdated(i);
        this.mCallback.onBluetoothCodecChanged();
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    protected void writeConfigurationValues(int i) {
        int i2 = 0;
        int i3 = 1000000;
        switch (i) {
            case 0:
                i2 = AbstractBluetoothDialogPreferenceController.getHighestCodec(getSelectableConfigs(this.mBluetoothA2dp.getActiveDevice()));
                break;
            case 1:
                break;
            case 2:
                i2 = 1;
                break;
            case 3:
                i2 = 2;
                break;
            case 4:
                i2 = 3;
                break;
            case 5:
                i2 = 4;
                break;
            case 6:
            case 7:
            default:
                i3 = 0;
                break;
            case 8:
                i2 = 10;
                break;
            case 9:
                i2 = 9;
                break;
            case 10:
                i2 = 11;
                break;
            case 11:
                synchronized (this.mBluetoothA2dpConfigStore) {
                    if (this.mBluetoothA2dp != null) {
                        this.mBluetoothA2dp.enableOptionalCodecs(null);
                    }
                }
                return;
            case 12:
                synchronized (this.mBluetoothA2dpConfigStore) {
                    if (this.mBluetoothA2dp != null) {
                        this.mBluetoothA2dp.disableOptionalCodecs(null);
                    }
                }
                return;
        }
        this.mBluetoothA2dpConfigStore.setCodecType(i2);
        this.mBluetoothA2dpConfigStore.setCodecPriority(i3);
        BluetoothCodecConfig selectableByCodecType = getSelectableByCodecType(i2);
        if (selectableByCodecType == null) {
            Log.d("BtCodecCtr", "Selectable config is null. Unable to reset");
        }
        this.mBluetoothA2dpConfigStore.setSampleRate(AbstractBluetoothDialogPreferenceController.getHighestSampleRate(selectableByCodecType));
        this.mBluetoothA2dpConfigStore.setBitsPerSample(AbstractBluetoothDialogPreferenceController.getHighestBitsPerSample(selectableByCodecType));
        this.mBluetoothA2dpConfigStore.setChannelMode(AbstractBluetoothDialogPreferenceController.getHighestChannelMode(selectableByCodecType));
    }
}
