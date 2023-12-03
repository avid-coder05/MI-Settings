package com.android.settingslib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.CarrierConfigManager;

/* loaded from: classes2.dex */
public class TetherUtil {
    static boolean isEntitlementCheckRequired(Context context) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (carrierConfigManager == null || carrierConfigManager.getConfig() == null) {
            return true;
        }
        return carrierConfigManager.getConfig().getBoolean("require_entitlement_checks_bool");
    }

    public static boolean isProvisioningNeeded(Context context) {
        String[] stringArray = context.getResources().getStringArray(17236067);
        return !SystemProperties.getBoolean("net.tethering.noprovisioning", false) && stringArray != null && isEntitlementCheckRequired(context) && stringArray.length == 2;
    }

    public static boolean isTetherAvailable(Context context) {
        return (((ConnectivityManager) context.getSystemService(ConnectivityManager.class)).isTetheringSupported() || (RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_config_tethering", UserHandle.myUserId()) != null)) && !RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_config_tethering", UserHandle.myUserId());
    }
}
