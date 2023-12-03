package com.android.settings.device.controller;

import android.content.Context;
import com.android.settings.device.MiuiAboutPhoneUtils;

/* loaded from: classes.dex */
public class MiuiPreInstallController extends BaseDeviceInfoController {
    public MiuiPreInstallController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pre_installed_application";
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return MiuiAboutPhoneUtils.supportDisplayPreInstalledApplication();
    }
}
