package com.xiaomi.accountsdk.utils;

import java.util.Locale;

/* loaded from: classes2.dex */
public class VersionUtils {
    public static String versionValue = String.format(Locale.US, "accountsdk-%s.%s.%s", "2020", "01", "09");

    public static String getVersion() {
        return versionValue;
    }
}
