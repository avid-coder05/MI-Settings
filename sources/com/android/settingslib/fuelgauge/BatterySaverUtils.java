package com.android.settingslib.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.KeyValueListParser;
import android.util.Slog;
import miui.content.res.ThemeResources;

/* loaded from: classes2.dex */
public class BatterySaverUtils {

    /* loaded from: classes2.dex */
    private static class Parameters {
        public final int endNth;
        private final Context mContext;
        public final int startNth;

        public Parameters(Context context) {
            this.mContext = context;
            String string = Settings.Global.getString(context.getContentResolver(), "low_power_mode_suggestion_params");
            KeyValueListParser keyValueListParser = new KeyValueListParser(',');
            try {
                keyValueListParser.setString(string);
            } catch (IllegalArgumentException unused) {
                Slog.wtf("BatterySaverUtils", "Bad constants: " + string);
            }
            this.startNth = keyValueListParser.getInt("start_nth", 4);
            this.endNth = keyValueListParser.getInt("end_nth", 8);
        }
    }

    private static Intent getSystemUiBroadcast(String str, Bundle bundle) {
        Intent intent = new Intent(str);
        intent.setFlags(268435456);
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        intent.putExtras(bundle);
        return intent;
    }

    public static boolean maybeShowBatterySaverConfirmation(Context context, Bundle bundle) {
        if (Settings.Secure.getInt(context.getContentResolver(), "low_power_warning_acknowledged", 0) != 0) {
            return false;
        }
        context.sendBroadcast(getSystemUiBroadcast("PNW.startSaverConfirmation", bundle));
        return true;
    }

    public static void revertScheduleToNoneIfNeeded(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int i = Settings.Global.getInt(contentResolver, "automatic_power_save_mode", 0);
        boolean z = !TextUtils.isEmpty(context.getString(17039883));
        if (i != 1 || z) {
            return;
        }
        Settings.Global.putInt(contentResolver, "low_power_trigger_level", 0);
        Settings.Global.putInt(contentResolver, "automatic_power_save_mode", 0);
    }

    public static void setAutoBatterySaverTriggerLevel(Context context, int i) {
        if (i > 0) {
            suppressAutoBatterySaver(context);
        }
        Settings.Global.putInt(context.getContentResolver(), "low_power_trigger_level", i);
    }

    private static void setBatterySaverConfirmationAcknowledged(Context context) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "low_power_warning_acknowledged", 1, -2);
    }

    public static synchronized boolean setPowerSaveMode(Context context, boolean z, boolean z2) {
        synchronized (BatterySaverUtils.class) {
            ContentResolver contentResolver = context.getContentResolver();
            Bundle bundle = new Bundle(1);
            bundle.putBoolean("extra_confirm_only", false);
            if (z && z2 && maybeShowBatterySaverConfirmation(context, bundle)) {
                return false;
            }
            if (z && !z2) {
                setBatterySaverConfirmationAcknowledged(context);
            }
            if (((PowerManager) context.getSystemService(PowerManager.class)).setPowerSaveModeEnabled(z)) {
                if (z) {
                    int i = Settings.Secure.getInt(contentResolver, "low_power_manual_activation_count", 0) + 1;
                    Settings.Secure.putInt(contentResolver, "low_power_manual_activation_count", i);
                    Parameters parameters = new Parameters(context);
                    if (i >= parameters.startNth && i <= parameters.endNth && Settings.Global.getInt(contentResolver, "low_power_trigger_level", 0) == 0 && Settings.Secure.getInt(contentResolver, "suppress_auto_battery_saver_suggestion", 0) == 0) {
                        showAutoBatterySaverSuggestion(context, bundle);
                    }
                }
                return true;
            }
            return false;
        }
    }

    private static void showAutoBatterySaverSuggestion(Context context, Bundle bundle) {
        context.sendBroadcast(getSystemUiBroadcast("PNW.autoSaverSuggestion", bundle));
    }

    public static void suppressAutoBatterySaver(Context context) {
        Settings.Secure.putInt(context.getContentResolver(), "suppress_auto_battery_saver_suggestion", 1);
    }
}