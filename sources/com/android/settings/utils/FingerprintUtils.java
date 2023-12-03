package com.android.settings.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.settings.R;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;

/* loaded from: classes2.dex */
public class FingerprintUtils {
    private static final boolean FINGERPRINT_SIDE_CAP = SystemProperties.getBoolean("ro.hardware.fp.sideCap", false);
    public static final boolean IS_SUPPORT_LINEAR_MOTOR_VIBRATE = "linear".equals(SystemProperties.get("sys.haptic.motor"));

    public static void createCardFolmeTouchStyle(View view) {
        Folme.useAt(view).touch().handleTouchOf(view, new AnimConfig());
    }

    public static String generateFingerprintName(Context context, List<String> list) {
        try {
            boolean[] zArr = new boolean[5];
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                int parseFingerprintNameIndex = parseFingerprintNameIndex(context, it.next());
                if (parseFingerprintNameIndex > 0 && parseFingerprintNameIndex <= 5) {
                    zArr[parseFingerprintNameIndex - 1] = true;
                }
            }
            for (int i = 0; i < 5; i++) {
                if (!zArr[i]) {
                    return context.getString(R.string.fingerprint_base_title, Integer.valueOf(i + 1));
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(context.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }

    public static long getFingerprintCreateDate(Context context, String str) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fingerprint_create_date", 0);
        long j = sharedPreferences.getLong(str, 0L);
        if (j == 0) {
            long currentTimeMillis = System.currentTimeMillis();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putLong(str, currentTimeMillis);
            edit.commit();
            return currentTimeMillis;
        }
        return j;
    }

    public static String getFingerprintName(Context context, String str) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "settings_fingerprint_id_prefix_" + str, 0);
        if (TextUtils.isEmpty(stringForUser)) {
            stringForUser = context.getSharedPreferences("fingerprint_name", 0).getString(str, "");
            if (!TextUtils.isEmpty(stringForUser)) {
                setFingerprintName(context, str, stringForUser);
            }
        }
        return stringForUser;
    }

    public static String getIdOfFingerprintWithoutName(Context context, List<String> list) {
        for (String str : list) {
            if (parseFingerprintNameIndex(context, str) == -1) {
                return str;
            }
        }
        return null;
    }

    public static boolean isBroadSideFingerprint() {
        return FINGERPRINT_SIDE_CAP;
    }

    private static int parseFingerprintNameIndex(Context context, String str) {
        if (TextUtils.isEmpty(getFingerprintName(context, str))) {
            return -1;
        }
        return r0.charAt(r0.length() - 1) - '0';
    }

    public static void removeFingerprintData(Context context, String str) {
        SharedPreferences.Editor edit = context.getSharedPreferences("fingerprint_create_date", 0).edit();
        edit.remove(str);
        edit.commit();
        SharedPreferences.Editor edit2 = context.getSharedPreferences("fingerprint_name", 0).edit();
        edit2.remove(str);
        edit2.commit();
        Settings.Secure.putStringForUser(context.getContentResolver(), "settings_fingerprint_id_prefix_" + str, null, 0);
    }

    public static void setFingerprintName(Context context, String str, String str2) {
        Settings.Secure.putStringForUser(context.getContentResolver(), "settings_fingerprint_id_prefix_" + str, str2, 0);
    }
}
