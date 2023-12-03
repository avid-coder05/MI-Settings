package com.android.settings.wireless;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import com.android.settings.connection.ScreenProjectionController;
import com.android.settingslib.core.AbstractPreferenceController;
import miui.os.Build;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class MiuiWifiDisplayController extends AbstractPreferenceController {
    public MiuiWifiDisplayController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wfd_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        WifiDisplayStatus wifiDisplayStatus = ((DisplayManager) this.mContext.getSystemService("display")).getWifiDisplayStatus();
        if (FeatureParser.getBoolean("support_nvdia_wifi_display", false) || wifiDisplayStatus.getFeatureState() != 0) {
            return Build.IS_INTERNATIONAL_BUILD || ScreenProjectionController.isNeedRemoveScreenProjection();
        }
        return false;
    }
}
