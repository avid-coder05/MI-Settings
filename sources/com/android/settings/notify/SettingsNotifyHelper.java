package com.android.settings.notify;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.device.UpdateBroadcastManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class SettingsNotifyHelper {
    private static final List<Integer> SETTINGS_CARED_MODIFICATION = new ArrayList<Integer>() { // from class: com.android.settings.notify.SettingsNotifyHelper.1
        {
            add(Integer.valueOf(R.id.wifi_settings));
            add(Integer.valueOf(R.id.bluetooth_settings));
            add(Integer.valueOf(R.id.wifi_tether_settings));
            add(Integer.valueOf(R.id.wireless_settings));
            add(Integer.valueOf(R.id.display_settings));
            add(Integer.valueOf(R.id.sound_settings));
            add(Integer.valueOf(R.id.notification_center));
            add(Integer.valueOf(R.id.launcher_settings));
            add(Integer.valueOf(R.id.other_advanced_settings));
        }
    };

    /* loaded from: classes2.dex */
    static class XiaomiAccountStatus {
        static long cacheLastUpdate;

        public static void reset() {
            cacheLastUpdate = 0L;
        }
    }

    public static void ensureSettingsModification(Context context, int i) {
        if (SETTINGS_CARED_MODIFICATION.contains(Integer.valueOf(i))) {
            int i2 = Settings.System.getInt(context.getContentResolver(), "settings.notify.key.settings.modified", 0);
            if ((i2 & 2) == 0 && (i2 & 1) == 0) {
                setSettingsModified(context);
            }
        }
    }

    public static boolean isEasyModeToNotify(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "elderly_mode", 0) == 1;
    }

    public static boolean isPhoneRecycledToNotify(Context context) {
        int i = Settings.System.getInt(context.getContentResolver(), "settings.notify.key.phone.recycled", 0);
        return (i & 2) != 0 && (i & 1) == 0;
    }

    public static void resetXiaomiAccountCachedStatus() {
        XiaomiAccountStatus.reset();
    }

    public static void setPhoneRecycled(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "settings.notify.key.phone.recycled", z ? 2 : 0);
        Log.d("SettingsNotifyHelper", "setPhoneRecycled, recycled=" + z);
    }

    public static void setPhoneRecycledAndUserOp(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "settings.notify.key.phone.recycled", 3);
        if (z) {
            UpdateBroadcastManager.updateSuperscript(context, 2, false);
        }
        Log.d("SettingsNotifyHelper", "setPhoneRecycledAndUserOp, cancelSuperscript=" + z);
    }

    public static void setSettingsModified(Context context) {
        Settings.System.putInt(context.getContentResolver(), "settings.notify.key.settings.modified", 2);
        Log.d("SettingsNotifyHelper", "setSettingsModified");
    }
}
