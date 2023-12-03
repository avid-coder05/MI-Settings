package com.android.settings.wireless;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.vpn2.MiuiVpnUtils;
import com.android.settings.vpn2.VpnManager;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class VpnEntryController extends AbstractPreferenceController {
    public VpnEntryController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "vpn_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        UserManager userManager = (UserManager) this.mContext.getSystemService("user");
        new VpnManager(this.mContext);
        boolean z = UserHandle.myUserId() != 0;
        int configuredVpnStatus = MiuiVpnUtils.getConfiguredVpnStatus(this.mContext);
        if (SettingsFeatures.isSplitTablet(this.mContext)) {
            configuredVpnStatus = 0;
        }
        return (z || userManager.hasUserRestriction("no_config_vpn") || configuredVpnStatus > 0) ? false : true;
    }
}
