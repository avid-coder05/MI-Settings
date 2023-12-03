package com.android.settings;

import android.media.AudioSystem;
import android.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class PlatformUtils {
    public static int getDefaultStreamVolume(int i) {
        return AudioSystem.getDefaultStreamVolume(i);
    }

    public static String getTelephonyProperty(String str, int i, String str2) {
        return TelephonyManager.getTelephonyProperty(i, str, str2);
    }
}
