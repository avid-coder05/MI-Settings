package com.android.settings.dndmode;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import miui.app.constants.ThemeManagerConstants;
import miui.provider.ExtraContacts;
import miui.provider.ExtraTelephony;

/* loaded from: classes.dex */
public class DoNotDisturbModeUtils {
    private static String MODE = "mode";

    public static void cancelAutoTime(Context context) {
        Log.d("AntiSpamUtils", "Cancel auto DNDM.");
        Intent intent = new Intent(context, DoNotDisturbModeReceiver.class);
        intent.setAction("com.android.settings.dndm.AUTO_TIME_TURN_ON");
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        alarmManager.cancel(broadcast);
        intent.setAction("com.android.settings.dndm.AUTO_TIME_TURN_OFF");
        alarmManager.cancel(PendingIntent.getBroadcast(context, 0, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE));
    }

    public static String formatTime(Context context, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, i / 60);
        calendar.set(12, i % 60);
        return new SimpleDateFormat(context.getString(DateFormat.is24HourFormat(context) ? R.string.dndm_twenty_four_hour_time_format : R.string.dndm_twelve_hour_time_format)).format(calendar.getTime());
    }

    public static long getAlarmTimeInMillis(int i) {
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

    public static boolean isInDoNotDisturbModeTimeNow(Context context) {
        int startTimeForQuietMode = MiuiSettings.AntiSpam.getStartTimeForQuietMode(context);
        int endTimeForQuietMode = MiuiSettings.AntiSpam.getEndTimeForQuietMode(context);
        Calendar calendar = Calendar.getInstance();
        int i = (calendar.get(11) * 60) + calendar.get(12);
        if (startTimeForQuietMode > endTimeForQuietMode) {
            if (i < endTimeForQuietMode || i >= startTimeForQuietMode) {
                return true;
            }
        } else if (startTimeForQuietMode < endTimeForQuietMode && i >= startTimeForQuietMode && i < endTimeForQuietMode) {
            return true;
        }
        return false;
    }

    public static void sendAutoTimeTurnOnOff(Context context) {
        Log.d("AntiSpamUtils", "Start auto DNDM.");
        triggerStartEndAlarm(context, getAlarmTimeInMillis(MiuiSettings.AntiSpam.getStartTimeForQuietMode(context)), getAlarmTimeInMillis(MiuiSettings.AntiSpam.getEndTimeForQuietMode(context)));
    }

    public static void startAutoTime(Context context) {
        cancelAutoTime(context);
        sendAutoTimeTurnOnOff(context);
    }

    public static void startImportVipList(Context context, String[] strArr) {
        Intent intent = new Intent("miui.intent.action.ADD_FIREWALL");
        intent.setType(ExtraTelephony.Blacklist.CONTENT_ITEM_TYPE);
        intent.putExtra(MODE, 4);
        intent.putExtra(ExtraContacts.Phone.NUMBERS, strArr);
        context.startActivity(intent);
    }

    public static void triggerStartEndAlarm(Context context, long j, long j2) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        Intent intent = new Intent(context, DoNotDisturbModeReceiver.class);
        intent.setAction("com.android.settings.dndm.AUTO_TIME_TURN_ON");
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 201326592);
        Intent intent2 = new Intent(context, DoNotDisturbModeReceiver.class);
        intent2.setAction("com.android.settings.dndm.AUTO_TIME_TURN_OFF");
        PendingIntent broadcast2 = PendingIntent.getBroadcast(context, 0, intent2, 201326592);
        alarmManager.setExact(0, j, broadcast);
        alarmManager.setExact(0, j2, broadcast2);
    }
}
