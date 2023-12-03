package com.android.settings.privacypassword;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.android.settings.FingerprintHelper;
import com.android.settings.R;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.content.res.ThemeResources;
import miui.security.SecurityManager;
import miui.util.FeatureParser;
import miui.util.HashUtils;

/* loaded from: classes2.dex */
public class PrivacyPasswordUtils {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final boolean IS_CETUS = Build.DEVICE.equals("cetus");

    public static void activateFingerprint(int i, int i2, Context context) {
        if (i != 0) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("privacy_password_sharedPreference", 0);
            Set<String> stringSet = sharedPreferences.getStringSet("privacy_verify_and_activate_fingerprint_" + i2, new ArraySet());
            stringSet.add(String.valueOf(i));
            sharedPreferences.edit().clear().putStringSet("privacy_verify_and_activate_fingerprint_" + i2, stringSet).commit();
        }
    }

    public static void adapteNotch(Context context, View view) {
        int statusBarHeight = getStatusBarHeight(context) - context.getResources().getDimensionPixelOffset(R.dimen.flat_screen_status_bar_height);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.topMargin += statusBarHeight;
        view.setLayoutParams(marginLayoutParams);
    }

    public static void adapteNotch(Context context, View view, int i, View view2, int i2) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        int statusBarHeight = getStatusBarHeight(context) - context.getResources().getDimensionPixelOffset(R.dimen.flat_screen_status_bar_height);
        layoutParams.topMargin = context.getResources().getDimensionPixelOffset(i) + statusBarHeight;
        view.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) view2.getLayoutParams();
        layoutParams2.height = context.getResources().getDimensionPixelOffset(i2) + statusBarHeight;
        view2.setLayoutParams(layoutParams2);
    }

    public static boolean appCheckAccess(SecurityManager securityManager, String str) {
        return securityManager.checkAccessControlPass(str);
    }

    private static String encodeHexString(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[length << 1];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i + 1;
            char[] cArr2 = DIGITS_LOWER;
            cArr[i] = cArr2[(bArr[i2] & 240) >>> 4];
            i = i3 + 1;
            cArr[i3] = cArr2[bArr[i2] & 15];
        }
        return new String(cArr);
    }

    public static boolean excludPreferenceItem(Context context, String str) {
        if (str.equals("privacy_mms")) {
            try {
                return context.getPackageManager().getPackageInfo("com.android.mms", 0) == null;
            } catch (Exception e) {
                Log.e("PrivacyPasswordUtils", "isInstalledPackage ", e);
                return true;
            }
        }
        return false;
    }

    public static Account findAccounts(Context context) {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.xiaomi");
        if (accountsByType == null || accountsByType.length <= 0) {
            return null;
        }
        return accountsByType[0];
    }

    public static int getCurrentWindowMode(Configuration configuration) {
        return configuration.windowConfiguration.getWindowingMode();
    }

    public static int getDimen(Context context, int i) {
        return context.getResources().getDimensionPixelSize(i);
    }

    public static int getFodPosition(Context context) {
        int[] iArr = new int[2];
        context.getResources().getDisplayMetrics();
        int i = getScreenRealSize(context)[0];
        int[] intArray = FeatureParser.getIntArray("screen_resolution_supported");
        String str = (intArray == null || intArray.length <= 1 || i != 1080) ? SystemProperties.get("persist.vendor.sys.fp.fod.location.X_Y", "") : SystemProperties.get("persist.vendor.sys.fp.fod.location.X_Y.fhd", "");
        if (TextUtils.isEmpty(str) || !str.contains(",")) {
            return iArr[0];
        }
        String[] split = str.split(",");
        iArr[0] = Integer.parseInt(split[0]);
        iArr[1] = Integer.parseInt(split[1]);
        return iArr[0] > iArr[1] ? iArr[0] : iArr[1];
    }

    private static MessageDigest getMd5Digest() {
        try {
            return MessageDigest.getInstance(HashUtils.MD5);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static int[] getScreenRealSize(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int[] iArr = new int[2];
        int i = point.x;
        int i2 = point.y;
        if (i >= i2) {
            iArr[0] = i2;
            iArr[1] = i;
        } else {
            iArr[0] = i;
            iArr[1] = i2;
        }
        return iArr;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getWrongFingerAttempts(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "privacy_password_finger_authentication_num", 0);
    }

    public static void invokeResetTimeout(Context context) {
        try {
            if (Build.VERSION.SDK_INT > 28) {
                Object systemService = context.getApplicationContext().getSystemService("biometric");
                systemService.getClass().getMethod("resetLockout", byte[].class).invoke(systemService, null);
            } else {
                Object systemService2 = context.getApplicationContext().getSystemService("fingerprint");
                systemService2.getClass().getMethod("resetTimeout", byte[].class).invoke(systemService2, null);
            }
        } catch (Exception e) {
            Log.e("PrivacyPasswordUtils", "Fail resetTimeout ", e);
        }
    }

    public static boolean isFingerprintHardWareDetected() {
        if (Build.VERSION.SDK_INT < 23) {
            String str = Build.DEVICE;
            if (!str.equals("hennessy") && !str.equals("kenzo") && !str.equals("ido") && !str.equals("aqua")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFodFingerprint() {
        return SystemProperties.getBoolean("ro.hardware.fp.fod", false);
    }

    public static boolean isFoldInternalScreen(Context context) {
        return isFoldScreen() && context != null && (new Configuration(context.getResources().getConfiguration()).screenLayout & 15) == 3;
    }

    private static boolean isFoldScreen() {
        return SystemProperties.getInt("persist.sys.muiltdisplay_type", 0) == 2;
    }

    public static boolean isNotch() {
        return SystemProperties.getInt("ro.miui.notch", 0) == 1;
    }

    public static boolean isPad() {
        return FeatureParser.getBoolean("is_pad", false);
    }

    public static boolean isSideFingerprint() {
        return SystemProperties.getBoolean("ro.hardware.fp.sideCap", false);
    }

    public static boolean isVerifyAndActivate(int i, int i2, Context context) {
        if (i == 0) {
            return false;
        }
        Set<String> stringSet = context.getSharedPreferences("privacy_password_sharedPreference", 0).getStringSet("privacy_verify_and_activate_fingerprint_" + i2, new ArraySet());
        return stringSet != null && stringSet.contains(String.valueOf(i));
    }

    private static byte[] md5(byte[] bArr) {
        return getMd5Digest().digest(bArr);
    }

    public static String md5Hex(byte[] bArr) {
        return encodeHexString(md5(bArr));
    }

    public static void postOnCheckPasswordResult(boolean z, Activity activity, Intent intent) {
        if (!z || activity == null || intent == null) {
            return;
        }
        activity.startActivityForResult(intent, 10000);
    }

    public static void putIntentExtra(Context context, Intent intent) {
        intent.putExtra("com.android.settings.bgColor", context.getResources().getColor(R.color.second_space_setting_bg));
        intent.putExtra("com.android.settings.titleColor", context.getResources().getColor(17170443));
        intent.putExtra("com.android.settings.ConfirmLockPattern.header", context.getResources().getString(R.string.privacy_password_confirm_SecondSpace_Password));
        intent.putExtra("com.android.settings.forgetPatternColor", context.getResources().getColor(17170443));
        intent.putExtra("com.android.settings.footerTextColor", context.getResources().getColor(17170443));
        intent.putExtra("com.android.settings.lockBtnWhite", true);
    }

    public static void setWrongFingerAttempts(Context context, int i) {
        Settings.Secure.putInt(context.getContentResolver(), "privacy_password_finger_authentication_num", i);
    }

    public static void upgradeFingerprints(final Context context, final int i, final FingerprintHelper fingerprintHelper) {
        AsyncExecuteUtils.execute(new Runnable() { // from class: com.android.settings.privacypassword.PrivacyPasswordUtils.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    boolean z = false;
                    if (Settings.Secure.getInt(context.getContentResolver(), "com_android_settings_privacypassword_fingerprint_upgrade", 0) == 1) {
                        return;
                    }
                    SharedPreferences sharedPreferences = context.getSharedPreferences("privacy_password_sharedPreference", 0);
                    Set<String> stringSet = sharedPreferences.getStringSet("privacy_verify_and_activate_fingerprint_" + i, new ArraySet());
                    List<String> fingerprintIds = fingerprintHelper.getFingerprintIds();
                    if (fingerprintHelper.isHardwareDetected() && fingerprintHelper.hasEnrolledFingerprintsAppLock()) {
                        z = true;
                    }
                    if (z && fingerprintIds != null && fingerprintIds.size() != 0) {
                        Iterator<String> it = fingerprintIds.iterator();
                        while (it.hasNext()) {
                            stringSet.add(it.next());
                        }
                        sharedPreferences.edit().clear().putStringSet("privacy_verify_and_activate_fingerprint_" + i, stringSet).commit();
                    }
                    Settings.Secure.putInt(context.getContentResolver(), "com_android_settings_privacypassword_fingerprint_upgrade", 1);
                } catch (Exception e) {
                    Log.d("PrivacyPasswordUtils", "upgradeFingerprints failed", e);
                }
            }
        });
    }

    public static void verifyAccountCountDownTimer(final SecurityManager securityManager, final String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        securityManager.addAccessControlPass(str);
        new Handler().postDelayed(new Runnable() { // from class: com.android.settings.privacypassword.PrivacyPasswordUtils.1
            @Override // java.lang.Runnable
            public void run() {
                securityManager.removeAccessControlPass(str);
            }
        }, 2000L);
    }
}
