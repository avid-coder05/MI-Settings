package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.os.SystemProperties;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;

/* loaded from: classes.dex */
public class BluetoothAptxAdaptiveModePreferenceController extends AbstractBluetoothA2dpPreferenceController {
    public BluetoothAptxAdaptiveModePreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getCurrentA2dpSettingIndex(BluetoothCodecConfig bluetoothCodecConfig) {
        return ((int) bluetoothCodecConfig.getCodecSpecific4()) == 134225920 ? 1 : 0;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getDefaultIndex() {
        return 0;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListSummaries() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_aptxadaptive_mode_summaries);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListValues() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_aptxadaptive_mode_values);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_aptxadaptive_mode";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return SystemProperties.getBoolean("persist.bluetooth.aptxadaptive_offload.enabled", false);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected void writeConfigurationValues(Object obj) {
        DropDownPreference dropDownPreference = ((AbstractBluetoothA2dpPreferenceController) this).mPreference;
        if (dropDownPreference == null) {
            return;
        }
        this.mBluetoothA2dpConfigStore.setCodecSpecific4Value(dropDownPreference.findIndexOfValue(obj.toString()) == 0 ? 0 : 134225920);
    }
}
