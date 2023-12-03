package com.android.settings.device.controller;

import android.content.Context;
import android.content.Intent;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import java.util.Locale;
import miui.os.Build;

/* loaded from: classes.dex */
public class MaintenanceModeController extends BaseDeviceInfoController {
    private Context mContext;

    public MaintenanceModeController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "maintenancemode_key";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            Intent intent = new Intent();
            intent.setClassName("com.miui.maintenancemode", "com.miui.maintenancemode.ui.MaintenanceModeActivity");
            this.mContext.startActivity(intent);
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Build.IS_INTERNATIONAL_BUILD && MiuiUtils.isApplicationInstalled(this.mContext, "com.miui.maintenancemode") && Locale.getDefault().toLanguageTag().equals("zh-CN");
    }
}
