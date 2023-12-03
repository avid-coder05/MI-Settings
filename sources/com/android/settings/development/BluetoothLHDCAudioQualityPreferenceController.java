package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes.dex */
public class BluetoothLHDCAudioQualityPreferenceController extends AbstractBluetoothA2dpPreferenceController {
    public BluetoothLHDCAudioQualityPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getCurrentA2dpSettingIndex(BluetoothCodecConfig bluetoothCodecConfig) {
        int codecSpecific1 = (int) bluetoothCodecConfig.getCodecSpecific1();
        int i = (49152 & codecSpecific1) == 32768 ? codecSpecific1 & 255 : 4;
        if (i > 4) {
            return 4;
        }
        return i;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getDefaultIndex() {
        return 4;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListSummaries() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_lhdc_playback_quality_summaries);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListValues() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_lhdc_playback_quality_values);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_lhdc_playback_quality";
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected void writeConfigurationValues(Object obj) {
        int findIndexOfValue = ((AbstractBluetoothA2dpPreferenceController) this).mPreference.findIndexOfValue(obj.toString());
        int i = findIndexOfValue <= 4 ? findIndexOfValue | MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON : 32772;
        synchronized (this.mBluetoothA2dpConfigStore) {
            this.mBluetoothA2dpConfigStore.setLhdcSpecificValue(i);
        }
    }
}
