package com.android.settings.device.controller;

import android.content.Context;
import android.os.UserHandle;

/* loaded from: classes.dex */
public class MiuiFactoryResetController extends BaseDeviceInfoController {
    public MiuiFactoryResetController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "factory_reset_key";
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return UserHandle.myUserId() == 0;
    }
}
