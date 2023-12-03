package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.search.tree.DevelopmentSettingsTree;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.provider.ExtraContacts;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class BluetoothAbsoluteVolumePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String BLUETOOTH_DISABLE_ABSOLUTE_VOLUME_PROPERTY = "persist.bluetooth.disableabsvol";
    private Context mContext;

    public BluetoothAbsoluteVolumePreferenceController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return DevelopmentSettingsTree.BLUETOOTH_DISABLE_ABSOLUTE_VOLUME;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        try {
            boolean equals = "mediatek".equals(FeatureParser.getString("vendor"));
            boolean equals2 = "qcom".equals(FeatureParser.getString("vendor"));
            String str = ExtraContacts.DefaultAccount.NAME;
            if (equals) {
                if (Settings.Global.getInt(this.mContext.getContentResolver(), "persist_vendor_bt_a2dp_absvolfeature_mtk", 0) == 1) {
                    str = "true";
                }
                Log.i("BluetoothAbsoluteVolumePreferenceController", "persist.vendor.bluetooth.a2dp.absvolfeature is " + str);
            } else if (equals2) {
                str = SystemProperties.get("persist.vendor.bt.a2dp.absvolfeature", ExtraContacts.DefaultAccount.NAME);
                Log.i("BluetoothAbsoluteVolumePreferenceController", "persist.vendor.bt.a2dp.absvolfeature is " + str);
            } else {
                str = "false";
                Log.v("BluetoothAbsoluteVolumePreferenceController", "no work: ");
            }
            if ("true".equals(str)) {
                Log.d("BluetoothAbsoluteVolumePreferenceController", "absvolfeature is true");
                return false;
            }
        } catch (Exception e) {
            Log.d("BluetoothAbsoluteVolumePreferenceController", " getBoolean absvolfeature failed " + e);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SystemProperties.set(BLUETOOTH_DISABLE_ABSOLUTE_VOLUME_PROPERTY, ((Boolean) obj).booleanValue() ? "true" : "false");
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(SystemProperties.getBoolean(BLUETOOTH_DISABLE_ABSOLUTE_VOLUME_PROPERTY, false));
    }
}
