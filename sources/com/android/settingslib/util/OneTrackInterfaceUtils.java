package com.android.settingslib.util;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import java.util.HashMap;
import java.util.Map;
import miui.os.Build;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public class OneTrackInterfaceUtils {
    private static Context sContext;
    private static OneTrack sOneTrack;
    private static final boolean ENABLED = !Build.IS_INTERNATIONAL_BUILD;
    private static boolean sIsProvisioned = false;
    private static final Configuration mConfig = new Configuration.Builder().setAppId("31000000174").setChannel("xiaomi").setExceptionCatcherEnable(true).setMode(OneTrack.Mode.APP).build();

    public static final void init(Context context) {
        try {
            sContext = context.getApplicationContext();
            if (Build.IS_INTERNATIONAL_BUILD) {
                return;
            }
            sOneTrack = OneTrack.createInstance(context.getApplicationContext(), mConfig);
            OneTrack.setDebugMode(false);
            OneTrack.setUseSystemNetTrafficOnly();
            OneTrack.setAccessNetworkEnable(sContext, isProvisioned());
        } catch (Exception e) {
            Log.e("OneTrackInterfaceUtils", "init onetrack error", e);
        }
    }

    private static boolean isMonkeyRunning() {
        return ActivityManager.isUserAMonkey();
    }

    private static boolean isProvisioned() {
        Context context = sContext;
        if (context != null && !sIsProvisioned) {
            boolean z = Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
            sIsProvisioned = z;
            if (z) {
                OneTrack.setAccessNetworkEnable(sContext, z);
            }
        }
        return sIsProvisioned;
    }

    public static void track(String str, Map<String, Object> map) {
        OneTrack oneTrack;
        if (!ENABLED || isMonkeyRunning() || !isProvisioned() || (oneTrack = sOneTrack) == null) {
            return;
        }
        oneTrack.track(str, map);
    }

    public static final void trackMasterClearClick(String str, String str2) {
        if (!ENABLED || isMonkeyRunning() || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("page_name", str);
        hashMap.put("item_title", str2);
        hashMap.put("model", android.os.Build.DEVICE);
        track("preference_click", hashMap);
    }

    public static final void trackPreferenceClick(String str, String str2) {
        if (!ENABLED || isMonkeyRunning() || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("page_name", str);
        hashMap.put("item_title", str2);
        track("preference_click", hashMap);
    }

    public static final void trackPreferenceValue(String str, String str2) {
        if (!ENABLED || isMonkeyRunning() || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("preference", str);
        hashMap.put("value", str2);
        track("preference_value_change", hashMap);
    }

    public static final void trackSwitchEvent(String str, boolean z) {
        if (!ENABLED || isMonkeyRunning() || TextUtils.isEmpty(str)) {
            return;
        }
        HashMap hashMap = new HashMap();
        String str2 = z ? "on" : "off";
        hashMap.put("status", Boolean.valueOf(z));
        hashMap.put(ExtraTelephony.UnderstandInfo.CLASS, str + "_" + str2);
        track("preference_value_change", hashMap);
    }
}
