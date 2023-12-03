package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes.dex */
public class BluetoothAudioSampleRatePreferenceController extends AbstractBluetoothA2dpPreferenceController {
    public BluetoothAudioSampleRatePreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getCurrentA2dpSettingIndex(BluetoothCodecConfig bluetoothCodecConfig) {
        int sampleRate = bluetoothCodecConfig.getSampleRate();
        if (sampleRate != 1) {
            if (sampleRate != 2) {
                if (sampleRate != 4) {
                    return sampleRate != 8 ? 0 : 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getDefaultIndex() {
        return 0;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListSummaries() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_sample_rate_summaries);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListValues() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_sample_rate_titles);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_sample_rate";
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected void writeConfigurationValues(Object obj) {
        int findIndexOfValue = ((AbstractBluetoothA2dpPreferenceController) this).mPreference.findIndexOfValue(obj.toString());
        int i = 4;
        if (findIndexOfValue != 0) {
            if (findIndexOfValue == 1) {
                i = 1;
            } else if (findIndexOfValue == 2) {
                i = 2;
            } else if (findIndexOfValue != 3) {
                if (findIndexOfValue == 4) {
                    i = 8;
                }
            }
            this.mBluetoothA2dpConfigStore.setSampleRate(i);
        }
        i = 0;
        this.mBluetoothA2dpConfigStore.setSampleRate(i);
    }
}
