package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;
import miui.vip.VipService;

/* loaded from: classes.dex */
public class BluetoothAudioQualityPreferenceController extends AbstractBluetoothA2dpPreferenceController {
    public BluetoothAudioQualityPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getCurrentA2dpSettingIndex(BluetoothCodecConfig bluetoothCodecConfig) {
        int codecSpecific1 = (int) bluetoothCodecConfig.getCodecSpecific1();
        switch (codecSpecific1) {
            case VipService.VIP_SERVICE_FAILURE /* 1000 */:
            case 1001:
            case 1002:
            case 1003:
                return codecSpecific1 % 10;
            default:
                return 3;
        }
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getDefaultIndex() {
        return 3;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListSummaries() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_ldac_playback_quality_summaries);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListValues() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_ldac_playback_quality_values);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_ldac_playback_quality";
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected void writeConfigurationValues(Object obj) {
        int findIndexOfValue = ((AbstractBluetoothA2dpPreferenceController) this).mPreference.findIndexOfValue(obj.toString());
        int i = (findIndexOfValue == 0 || findIndexOfValue == 1 || findIndexOfValue == 2 || findIndexOfValue == 3) ? findIndexOfValue + VipService.VIP_SERVICE_FAILURE : 0;
        synchronized (this.mBluetoothA2dpConfigStore) {
            this.mBluetoothA2dpConfigStore.setLdacSpecificValue(i);
        }
    }
}
