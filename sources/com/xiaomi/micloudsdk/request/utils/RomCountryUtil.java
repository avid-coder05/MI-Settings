package com.xiaomi.micloudsdk.request.utils;

/* loaded from: classes2.dex */
public class RomCountryUtil {
    public static String getRomCountry() {
        try {
            return (String) Class.forName("miui.os.Build").getDeclaredMethod("getRegion", new Class[0]).invoke(null, new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }
}
