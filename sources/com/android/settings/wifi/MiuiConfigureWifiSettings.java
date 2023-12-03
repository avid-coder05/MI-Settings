package com.android.settings.wifi;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.preference.Preference;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;
import miui.os.Build;

/* loaded from: classes2.dex */
public class MiuiConfigureWifiSettings extends ConfigureWifiSettings {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.ConfigureWifiSettings, com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        List<AbstractPreferenceController> createPreferenceControllers = super.createPreferenceControllers(context);
        createPreferenceControllers.add(new CmccPreferenceController(context));
        createPreferenceControllers.add(new WapiPreferenceController(context));
        createPreferenceControllers.add(new NetworkCheckController(context));
        createPreferenceControllers.add(new ConnectModeController(context));
        createPreferenceControllers.add(new SavedAccessPointsController(context));
        return createPreferenceControllers;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.ConfigureWifiSettings, com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MiuiConfigureWifiSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getActivity() == null || SettingsFeatures.isSplitTablet(getContext())) {
            return;
        }
        getActivity().setRequestedOrientation(1);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        Preference findPreference;
        super.onStart();
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.privateFlags &= -524289;
            window.setAttributes(attributes);
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        if (!Build.IS_GLOBAL_BUILD || getPreferenceScreen() == null || !devicePolicyManager.isDeviceManaged() || (findPreference = findPreference("install_credentials")) == null) {
            return;
        }
        getPreferenceScreen().removePreference(findPreference);
    }
}
