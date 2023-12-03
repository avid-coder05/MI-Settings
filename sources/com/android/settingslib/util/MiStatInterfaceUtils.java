package com.android.settingslib.util;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.os.Build;

/* loaded from: classes2.dex */
public class MiStatInterfaceUtils {
    private static Context sContext;
    private static AtomicBoolean sInited = new AtomicBoolean(false);
    private static boolean sIsProvisioned = false;

    private static final boolean checkEventName(String str) {
        return !TextUtils.isEmpty(str) && str.matches("^[a-zA-Z][a-zA-Z0-9_]*$") && str.length() < 64;
    }

    private static final void enableExceptionCatcher(boolean z) {
    }

    public static final void initMiStatistics(Context context) {
        try {
            sContext = context.getApplicationContext();
            initialize(context);
            setUseSystemUploadingService(true);
            setUploadNetworkType();
            setUploadInterval(60);
            enableExceptionCatcher(true);
            setInternationalRegion();
            setDebugModeEnabled(false);
            sInited.set(true);
        } catch (Exception unused) {
            Log.e("MiStatInterfaceUtils", "initMiStatistics exception");
        }
    }

    private static final void initialize(Context context) {
    }

    private static boolean isMonkeyRunning() {
        return ActivityManager.isUserAMonkey();
    }

    private static boolean isProvisioned() {
        if (!sIsProvisioned) {
            sIsProvisioned = Settings.Global.getInt(sContext.getContentResolver(), "device_provisioned", 0) == 1;
        }
        return sIsProvisioned;
    }

    public static final void recordCountEvent(String str, String str2) {
        trackEvent(str + "_" + str2);
    }

    public static void recordCountEvent(String str, String str2, Map<String, String> map) {
    }

    public static final void recordCountEventAnonymous(String str, String str2) {
    }

    private static final void setDebugModeEnabled(boolean z) {
    }

    private static final void setInternationalRegion() {
        boolean z = Build.IS_INTERNATIONAL_BUILD;
    }

    private static final void setUploadInterval(int i) {
    }

    private static final void setUploadNetworkType() {
    }

    private static final void setUseSystemUploadingService(boolean z) {
    }

    public static final void trackEvent(String str) {
        if (sInited.get() && !Build.IS_INTERNATIONAL_BUILD && isProvisioned()) {
            String str2 = "settings_" + str;
            if (isMonkeyRunning()) {
                return;
            }
            checkEventName(str2);
        }
    }

    public static void trackException(Throwable th) {
        if (!sInited.get() || Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        isProvisioned();
    }

    public static final void trackMasterClearClick(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        trackPreferenceClick(str, str2 + "_" + android.os.Build.DEVICE);
    }

    public static void trackPageEnd(String str) {
        if (!sInited.get() || Build.IS_INTERNATIONAL_BUILD || isMonkeyRunning()) {
            return;
        }
        isProvisioned();
    }

    public static void trackPageStart(String str) {
        if (!sInited.get() || Build.IS_INTERNATIONAL_BUILD || isMonkeyRunning()) {
            return;
        }
        isProvisioned();
    }

    public static final void trackPreferenceClick(String str, String str2) {
    }

    public static final void trackPreferenceValue(String str, String str2) {
    }

    public static final void trackSwitchEvent(String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        trackPreferenceValue(str, z ? "on" : "off");
    }
}
