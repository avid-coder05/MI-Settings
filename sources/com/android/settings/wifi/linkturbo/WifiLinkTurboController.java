package com.android.settings.wifi.linkturbo;

import android.content.Context;
import com.android.settings.wifi.MiuiWifiAssistFeatureSupport;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class WifiLinkTurboController extends AbstractPreferenceController {
    private final String LINK_TURBO_ENABLE_PREF;

    public WifiLinkTurboController(Context context) {
        super(context);
        this.LINK_TURBO_ENABLE_PREF = "linkturbo_is_enable";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "link_turbo";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return MiuiWifiAssistFeatureSupport.isLinkTurbAvailable(this.mContext);
    }
}
