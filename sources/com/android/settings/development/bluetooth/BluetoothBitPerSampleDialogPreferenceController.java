package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothCodecConfig;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BluetoothBitPerSampleDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    int convertCfgToBtnIndex(int i) {
        int defaultIndex = getDefaultIndex();
        if (i != 1) {
            if (i != 2) {
                if (i != 4) {
                    Log.e("BtBitPerSampleCtr", "Unsupported config:" + i);
                    return defaultIndex;
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
            Log.e("BtBitPerSampleCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex(bluetoothCodecConfig.getBitsPerSample());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_bit_per_sample_settings";
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public List<Integer> getSelectableIndex() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(getDefaultIndex()));
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            int bitsPerSample = getSelectableByCodecType(currentCodecConfig.getCodecType()).getBitsPerSample();
            int i = 0;
            while (true) {
                int[] iArr = AbstractBluetoothDialogPreferenceController.BITS_PER_SAMPLES;
                if (i >= iArr.length) {
                    break;
                }
                if ((iArr[i] & bitsPerSample) != 0) {
                    arrayList.add(Integer.valueOf(convertCfgToBtnIndex(iArr[i])));
                }
                i++;
            }
        }
        return arrayList;
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    protected void writeConfigurationValues(int i) {
        int i2 = 2;
        if (i == 0) {
            BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
            if (currentCodecConfig != null) {
                i2 = AbstractBluetoothDialogPreferenceController.getHighestBitsPerSample(getSelectableByCodecType(currentCodecConfig.getCodecType()));
            }
            i2 = 0;
        } else if (i == 1) {
            i2 = 1;
        } else if (i != 2) {
            if (i == 3) {
                i2 = 4;
            }
            i2 = 0;
        }
        this.mBluetoothA2dpConfigStore.setBitsPerSample(i2);
    }
}
