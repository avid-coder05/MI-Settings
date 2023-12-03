package com.android.settings.display;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.util.Calendar;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class PaperModeTimeModeUtil {
    public static void cancleOnOffTime(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.PAPER_MODE_ON");
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        alarmManager.cancel(PendingIntent.getBroadcast(context, 0, intent, 201326592));
        Intent intent2 = new Intent();
        intent2.setAction("miui.intent.action.PAPER_MODE_OFF");
        intent2.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        alarmManager.cancel(PendingIntent.getBroadcast(context, 0, intent2, 201326592));
    }

    public static long getAlarmInMills(int i) {
        Calendar calendar = Calendar.getInstance();
        if ((calendar.get(11) * 60) + calendar.get(12) >= i) {
            calendar.add(6, 1);
        }
        calendar.set(11, i / 60);
        calendar.set(12, i % 60);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static int getPaperModeEndTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_paper_mode_time_end", 0);
    }

    public static int getPaperModeSchedulerType(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "paper_mode_scheduler_type", 2);
    }

    public static int getPaperModeStartTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_paper_mode_time_start", 0);
    }

    public static int getPaperModeTwilightSunriseTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_paper_mode_twilight_start", 360);
    }

    public static int getPaperModeTwilightSunsetTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_paper_mode_twilight_end", 1080);
    }

    public static boolean isPaperModeTimeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_paper_mode_time_enabled", false);
    }

    public static void setPaperModeEnabled(boolean z, Context context) {
        Log.w("PaperModeTimeModeUtil", " setPaperModeEnabled OnOff: " + z);
        MiuiSettings.System.putBoolean(context.getContentResolver(), "screen_paper_mode_enabled", z);
    }

    public static void setPaperModeEndTime(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "screen_paper_mode_time_end", i);
    }

    public static void setPaperModeStartTime(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "screen_paper_mode_time_start", i);
    }

    private static void setPaperModeTimeStartEndAlarm(Context context, long j, long j2) {
        Log.w("PaperModeTimeModeUtil", "setPaperModeTimeStartEndAlarm startTime:" + j + " endTime:" + j2);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.PAPER_MODE_ON");
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 201326592);
        Intent intent2 = new Intent();
        intent2.setAction("miui.intent.action.PAPER_MODE_OFF");
        intent2.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        PendingIntent broadcast2 = PendingIntent.getBroadcast(context, 0, intent2, 201326592);
        alarmManager.setExact(1, j, broadcast);
        alarmManager.setExact(1, j2, broadcast2);
    }

    public static void setPaperModeTwilightSunriseTime(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "screen_paper_mode_twilight_start", i);
    }

    public static void setPaperModeTwilightSunsetTime(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "screen_paper_mode_twilight_end", i);
    }

    public static void startPaperModeAutoTime(Context context, int i) {
        startPaperModeAutoTime(context, i, true);
    }

    public static void startPaperModeAutoTime(Context context, int i, boolean z) {
        cancleOnOffTime(context);
        if (i == 0) {
            return;
        }
        int paperModeStartTime = i == 2 ? getPaperModeStartTime(context) : getPaperModeTwilightSunsetTime(context);
        int paperModeEndTime = i == 2 ? getPaperModeEndTime(context) : getPaperModeTwilightSunriseTime(context);
        if (z) {
            if (MiuiSettings.ScreenEffect.isInPaperModeTimeSchedule(context, paperModeStartTime, paperModeEndTime)) {
                setPaperModeEnabled(true, context);
            } else {
                setPaperModeEnabled(false, context);
            }
        }
        if (paperModeStartTime != paperModeEndTime) {
            setPaperModeTimeStartEndAlarm(context, getAlarmInMills(paperModeStartTime), getAlarmInMills(paperModeEndTime));
        }
    }
}
