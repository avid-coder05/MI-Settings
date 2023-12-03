package com.android.settings;

import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class FakeCellSettings {
    public static boolean supportDetectFakecell() {
        if (Build.IS_INTERNATIONAL_BUILD || Build.IS_TABLET || !"qcom".equals(FeatureParser.getString("vendor")) || UserHandle.myUserId() != 0) {
            return false;
        }
        if (supportFakecellRilHookNV()) {
            return true;
        }
        return supportFakecellNativeNV();
    }

    private static boolean supportFakecellNativeNV() {
        int i;
        try {
            String str = (String) TelephonyManager.class.getMethod("nvReadItem", Integer.TYPE).invoke((TelephonyManager) TelephonyManager.class.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]), 6854);
            if (!TextUtils.isEmpty(str) && str.contains(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
                int parseInt = Integer.parseInt(str.substring(0, str.indexOf(59)));
                if ((parseInt & 1) != 0 && ((i = (parseInt >> 1) & 3) == 1 || i == 2)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean supportFakecellRilHookNV() {
        return SystemProperties.getBoolean("persist.radio.support.fakecell", false);
    }
}
