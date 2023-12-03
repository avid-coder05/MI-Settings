package miui.cloud.util;

import android.os.Build;
import android.provider.SystemSettings$System;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.micloudsdk.utils.ReflectUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes3.dex */
public class DeviceUtils {
    public static final String MARKET_NAME = getAndroidSystemProperties(SystemSettings$System.RO_MARKET_NAME, null);

    private DeviceUtils() {
    }

    private static String getAndroidSystemProperties(String str, String str2) {
        Class loadClass = ReflectUtils.loadClass("android.os.SystemProperties");
        if (loadClass == null) {
            Log.d("getAndroidSystemProperties", "class SystemProperties not found");
            return str2;
        }
        Method method = ReflectUtils.getMethod(loadClass, "get", String.class, String.class);
        if (method == null) {
            Log.d("getAndroidSystemProperties", "no method get");
            return str2;
        }
        method.setAccessible(true);
        try {
            return (String) method.invoke(loadClass, str, str2);
        } catch (IllegalAccessException e) {
            Log.d("getAndroidSystemProperties", "error: " + e);
            return str2;
        } catch (InvocationTargetException e2) {
            Log.d("getAndroidSystemProperties", "error: " + e2);
            return str2;
        }
    }

    private static String getPhoneModel() {
        String str = MARKET_NAME;
        return !TextUtils.isEmpty(str) ? str : Build.MODEL;
    }

    public static boolean isMatchesModel(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return getPhoneModel().matches(str);
    }

    public static boolean isRedmiDigitSeries() {
        return isRedmiDigitSeries(getPhoneModel());
    }

    public static boolean isRedmiDigitSeries(String str) {
        return str.matches("(?i)^Redmi[\\s]*[0-9]+[^X]*$");
    }
}
