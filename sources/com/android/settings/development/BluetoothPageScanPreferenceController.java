package com.android.settings.development;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.search.tree.DevelopmentSettingsTree;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class BluetoothPageScanPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String BLUETOOTH_ENABLE_PAGE_SCAN_PROPERTY = "persist.bluetooth.enablepagescan";
    private final BluetoothAdapter mAdapter;

    public BluetoothPageScanPreferenceController(Context context) {
        super(context);
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return DevelopmentSettingsTree.BLUETOOTH_ENABLE_PAGE_SCAN;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        SystemProperties.set(BLUETOOTH_ENABLE_PAGE_SCAN_PROPERTY, booleanValue ? "true" : "false");
        BluetoothAdapter bluetoothAdapter = this.mAdapter;
        if (bluetoothAdapter != null) {
            if (booleanValue) {
                bluetoothAdapter.setScanMode(21);
                return true;
            }
            bluetoothAdapter.setScanMode(20);
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = SystemProperties.getBoolean(BLUETOOTH_ENABLE_PAGE_SCAN_PROPERTY, true);
        ((SwitchPreference) this.mPreference).setChecked(z);
        BluetoothAdapter bluetoothAdapter = this.mAdapter;
        if (bluetoothAdapter != null) {
            if (z) {
                bluetoothAdapter.setScanMode(21);
            } else {
                bluetoothAdapter.setScanMode(20);
            }
        }
    }
}
