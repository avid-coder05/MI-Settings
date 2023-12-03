package com.android.settings.device.controller;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;

/* loaded from: classes.dex */
public class MiuiSafetylegalController extends BaseDeviceInfoController {
    public MiuiSafetylegalController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "safetylegal";
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !TextUtils.isEmpty(SystemProperties.get("ro.url.safetylegal"));
    }
}
