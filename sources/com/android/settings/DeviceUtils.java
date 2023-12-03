package com.android.settings;

import android.content.Context;
import android.provider.MiuiSettings;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.settings.msim.MSimUtils;
import java.util.Set;

/* loaded from: classes.dex */
public class DeviceUtils extends MSimUtils {
    public static final /* synthetic */ int $r8$clinit = 0;

    @Override // com.android.settings.MiuiUtils
    public Set<String> getHotSpotMacBlackSet(Context context) {
        return MiuiSettings.System.getHotSpotMacBlackSet(context);
    }

    @Override // com.android.settings.MiuiUtils
    public Phone getPhone(int i) {
        return PhoneFactory.getPhone(i);
    }

    @Override // com.android.settings.MiuiUtils
    public String getTetherDeviceChangedAction() {
        return "";
    }

    @Override // com.android.settings.MiuiUtils
    public boolean getWifiStaSapConcurrency(Context context) {
        return true;
    }

    @Override // com.android.settings.MiuiUtils
    public void setHotSpotMacBlackSet(Context context, Set<String> set) {
        MiuiSettings.System.setHotSpotMacBlackSet(context, set);
    }
}
