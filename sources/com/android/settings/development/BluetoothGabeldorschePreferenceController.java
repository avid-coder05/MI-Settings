package com.android.settings.development;

import android.content.Context;
import android.provider.DeviceConfig;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.milink.api.v1.type.DeviceType;

/* loaded from: classes.dex */
public class BluetoothGabeldorschePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String CURRENT_GD_FLAG = "INIT_gd_scanning";

    public BluetoothGabeldorschePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_gabeldorsche_enable";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        DeviceConfig.setProperty(DeviceType.BLUETOOTH, CURRENT_GD_FLAG, (String) null, false);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        DeviceConfig.setProperty(DeviceType.BLUETOOTH, CURRENT_GD_FLAG, ((Boolean) obj).booleanValue() ? "true" : "false", false);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(DeviceConfig.getBoolean(DeviceType.BLUETOOTH, CURRENT_GD_FLAG, false));
    }
}
