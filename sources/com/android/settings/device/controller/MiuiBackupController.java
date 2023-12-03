package com.android.settings.device.controller;

import android.content.Context;
import android.os.UserHandle;
import com.android.settings.MiuiUtils;

/* loaded from: classes.dex */
public class MiuiBackupController extends BaseDeviceInfoController {
    public MiuiBackupController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "privacy_settings";
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return UserHandle.myUserId() == 0 && (!MiuiUtils.isDeviceManaged(this.mContext) || MiuiUtils.isDeviceFinanceOwner(this.mContext));
    }
}
