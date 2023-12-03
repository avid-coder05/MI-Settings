package miui.cloud.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import com.xiaomi.micloudsdk.utils.ReflectUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import miui.cloud.Constants;
import miui.cloud.common.XLogger;
import miui.cloud.os.SystemProperties;
import miui.os.Build;
import miui.os.UserHandle;
import miui.telephony.exception.IllegalDeviceException;

/* loaded from: classes3.dex */
public class SysHelper {
    private static final int IMEI_LENGTH = 15;
    private static final int MEID_LENGTH = 14;
    private static final int PHONE_DEVID_MIN_LENGTH = 14;
    private static final String TAG = "SysHelper";

    @Deprecated
    public static String getQuantityStringWithUnit(long j) {
        return getQuantityStringWithUnit(null, j);
    }

    public static String getQuantityStringWithUnit(Context context, long j) {
        String format;
        float f = (float) j;
        String str = "MB";
        if (f > 1.07374184E8f) {
            format = String.format("%1$.2f", Float.valueOf(((f / 1024.0f) / 1024.0f) / 1024.0f));
            str = "GB";
        } else {
            format = f > 104857.6f ? String.format("%1$.2f", Float.valueOf((f / 1024.0f) / 1024.0f)) : f > 0.0f ? "0.1" : "0";
        }
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1 ? String.format("%s%s", str, format) : String.format("%s%s", format, str);
    }

    private static Intent getWarnIntent(String str) {
        Intent intent = new Intent(Constants.Intents.ACTION_WARN_INVALID_DEVICE_ID);
        intent.addFlags(268435456);
        intent.setPackage(Constants.XMSF_PACKAGE_NAME);
        intent.putExtra("device_id", str);
        return intent;
    }

    public static boolean hasModemCapability() {
        String valueOf = String.valueOf(false);
        return TextUtils.equals(SystemProperties.get("ro.radio.noril", valueOf), valueOf);
    }

    public static boolean hasSmsCapability(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        return telephonyManager != null && telephonyManager.isSmsCapable();
    }

    @Deprecated
    public static boolean hasTelephonyFeature(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    public static boolean hasVoiceCapability(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            return false;
        }
        Method method = ReflectUtils.getMethod(telephonyManager.getClass(), "isVoiceCapable", new Class[0]);
        if (method == null) {
            XLogger.logw("invoke isVoiceCapable Method not found!");
            return false;
        }
        method.setAccessible(true);
        try {
            return ((Boolean) method.invoke(telephonyManager, new Object[0])).booleanValue();
        } catch (IllegalAccessException unused) {
            XLogger.logw("invoke isVoiceCapable IllegalAccessException ");
            return false;
        } catch (InvocationTargetException unused2) {
            XLogger.logw("Impossible: invoke isVoiceCapable error for TelephonyManager, your runtime Android SDK is lower than 22?");
            return false;
        }
    }

    private static boolean isEmptyDeviceId(String str) {
        return TextUtils.isEmpty(str) || "0".equals(str) || "null".equalsIgnoreCase(str);
    }

    public static boolean isSecondUser() {
        return UserHandle.myUserId() > 0;
    }

    public static String maskHead(CharSequence charSequence, int i, char c) {
        int length = charSequence.length();
        int i2 = length / i;
        StringBuilder sb = new StringBuilder(length);
        for (int i3 = 0; i3 < length; i3++) {
            sb.append((length - i3) + (-1) < i2 ? charSequence.charAt(i3) : c);
        }
        return sb.toString();
    }

    public static String maskMiddle(CharSequence charSequence, int i, char c) {
        int length = charSequence.length();
        int i2 = length / i;
        StringBuilder sb = new StringBuilder(length);
        int i3 = 0;
        while (i3 < length) {
            sb.append((i3 < i2 || (length - i3) + (-1) < i2) ? charSequence.charAt(i3) : c);
            i3++;
        }
        return sb.toString();
    }

    public static String maskMiddle(CharSequence charSequence, int i, int i2, char c) {
        int length = charSequence.length();
        StringBuilder sb = new StringBuilder(length);
        int i3 = 0;
        while (i3 < length) {
            sb.append((i3 < i || (length - i3) + (-1) < i2) ? charSequence.charAt(i3) : c);
            i3++;
        }
        return sb.toString();
    }

    public static String maskTail(String str) {
        return maskTail(str, 3, 4);
    }

    public static String maskTail(String str, int i, int i2) {
        if (i2 >= 0) {
            if (i < 1) {
                i = 1;
            }
            if (str == null) {
                return "";
            }
            int length = i + (str.length() / 5);
            if (length <= i2) {
                i2 = length;
            }
            char[] charArray = str.toCharArray();
            for (int length2 = charArray.length - 1; length2 >= 0 && length2 >= charArray.length - i2; length2--) {
                charArray[length2] = '?';
            }
            return new String(charArray);
        }
        throw new IllegalArgumentException("maxMaskLength must be a non-negative integer");
    }

    public static void showInvalidDeviceIdWarning(Context context, String str) {
        if (Build.IS_STABLE_VERSION) {
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: miui.cloud.util.SysHelper.1
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(applicationContext, "Can't get a valid device ID", 1).show();
            }
        });
        XLogger.loge(TAG, "Can't get a valid device ID");
    }

    public static void showInvalidImeiIfNeeded(Context context, String str) throws IllegalDeviceException {
        if (validateIMEI(str)) {
            return;
        }
        showInvalidDeviceIdWarning(context, str);
        throw new IllegalDeviceException("device id is invalid");
    }

    public static void showInvalidMacIfNeeded(Context context, String str) throws IllegalDeviceException {
        if (validateMAC(str)) {
            return;
        }
        showInvalidDeviceIdWarning(context, str);
        throw new IllegalDeviceException("device id is invalid");
    }

    public static boolean validateIMEI(String str) {
        return !isEmptyDeviceId(str) && str.length() >= 14;
    }

    private static boolean validateImeiChecksum(long j) {
        int i = 0;
        for (int i2 = 15; i2 >= 1; i2--) {
            int i3 = (int) (j % 10);
            if (i2 % 2 == 0) {
                int i4 = i3 * 2;
                i += (i4 / 10) + (i4 % 10);
            } else {
                i += i3;
            }
            j /= 10;
        }
        return i % 10 == 0;
    }

    public static boolean validateMAC(String str) {
        return !isEmptyDeviceId(str);
    }
}
