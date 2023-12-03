package com.android.settings.device.controller;

import android.content.Context;
import com.android.settings.MiuiUtils;

/* loaded from: classes.dex */
public class MiuiFindDeviceController extends BaseDeviceInfoController {
    private Context mContext;

    public MiuiFindDeviceController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "find_device";
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !MiuiUtils.isDeviceManaged(this.mContext);
    }
}
