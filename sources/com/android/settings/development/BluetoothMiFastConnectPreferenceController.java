package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.report.InternationalCompat;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class BluetoothMiFastConnectPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private boolean disable_bluetooth_fast_connect;
    private Context mContext;

    public BluetoothMiFastConnectPreferenceController(Context context) {
        super(context);
        this.disable_bluetooth_fast_connect = false;
        this.mContext = context;
        this.disable_bluetooth_fast_connect = FeatureParser.getBoolean("disable_bluetooth_fast_connect", false);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_mi_fast_connect";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SystemProperties.set("persist.bluetooth.disablemifastconnect", "false");
        ((SwitchPreference) this.mPreference).setChecked(true);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        SystemProperties.set("persist.bluetooth.disablemifastconnect", booleanValue ? "false" : "true");
        Log.d("BluetoothMiFastConnectPreferenceController", "onPreferenceChange isEnabled is " + booleanValue);
        if (this.mContext != null) {
            Intent intent = new Intent("miui.bluetooth.FAST_CONNECT_STOP_BLE_SCAN");
            intent.putExtra("FAST_CONNECT_STOP_BLE_SCAN_TIME", 0);
            intent.setPackage("com.xiaomi.bluetooth");
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.BLUETOOTH");
        }
        InternationalCompat.trackReportSwitchStatus("setting_blueteeth_quickconnect", obj);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(!SystemProperties.getBoolean("persist.bluetooth.disablemifastconnect", false));
        if (this.disable_bluetooth_fast_connect) {
            preference.setEnabled(false);
        }
    }
}
