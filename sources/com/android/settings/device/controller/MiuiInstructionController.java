package com.android.settings.device.controller;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;

/* loaded from: classes.dex */
public class MiuiInstructionController extends BaseDeviceInfoController {
    public MiuiInstructionController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "instruction";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(getPreferenceKey(), preference.getKey())) {
            Intent intent = new Intent(MiuiUtils.getInstance().getViewLicenseAction());
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 6);
            if (MiuiUtils.getInstance().canFindActivity(this.mContext, intent)) {
                preference.setIntent(intent);
            }
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }
}
