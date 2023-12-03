package com.android.settings.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class MiuiDockUtils {
    public static boolean getSecurityCenterSettings(Context context, String str, boolean z) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ExtraContacts.DefaultAccount.NAME, z);
            Bundle call = context.getContentResolver().call(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "isFeatureSupport", str, bundle);
            return call == null ? z : call.getBoolean(str, z);
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isDockSupport(Context context) {
        return getSecurityCenterSettings(context, "is_miui_dock_support", false);
    }

    public static boolean isFrontAssistantSupport(Context context) {
        return getSecurityCenterSettings(context, "is_front_assistant_support", false);
    }
}
