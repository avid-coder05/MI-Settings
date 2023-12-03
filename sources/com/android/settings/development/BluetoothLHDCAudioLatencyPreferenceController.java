package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes.dex */
public class BluetoothLHDCAudioLatencyPreferenceController extends AbstractBluetoothA2dpPreferenceController {
    public BluetoothLHDCAudioLatencyPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getCurrentA2dpSettingIndex(BluetoothCodecConfig bluetoothCodecConfig) {
        int codecSpecific2 = (int) bluetoothCodecConfig.getCodecSpecific2();
        int i = (codecSpecific2 & 49152) == 49152 ? codecSpecific2 & 255 : 0;
        if (i > 2) {
            return 0;
        }
        return i;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getDefaultIndex() {
        return 0;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListSummaries() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_lhdc_latency_summaries);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListValues() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_lhdc_latency_values);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_codec_lhdc_latency";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected void writeConfigurationValues(Object obj) {
        int findIndexOfValue = ((AbstractBluetoothA2dpPreferenceController) this).mPreference.findIndexOfValue(obj.toString());
        int i = findIndexOfValue <= 2 ? 49152 | findIndexOfValue : 49152;
        synchronized (this.mBluetoothA2dpConfigStore) {
            BluetoothCodecConfig codecConfig = getCodecConfig(null);
            if (codecConfig != null && (codecConfig.getCodecType() == 9 || codecConfig.getCodecType() == 10 || codecConfig.getCodecType() == 11)) {
                this.mBluetoothA2dpConfigStore.setCodecSpecific2Value(i);
            }
        }
    }
}
