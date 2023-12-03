package com.android.settings.dndmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import com.android.settings.dndmode.Alarm;
import java.util.Calendar;
import java.util.Date;
import miui.os.UserHandle;

/* loaded from: classes.dex */
public class DoNotDisturbModeReceiver extends BroadcastReceiver {
    private long getAlarmEndTime(Context context) {
        return DoNotDisturbModeUtils.getAlarmTimeInMillis(MiuiSettings.AntiSpam.getEndTimeForQuietMode(context));
    }

    private long getAlarmStartTime(Context context) {
        return DoNotDisturbModeUtils.getAlarmTimeInMillis(MiuiSettings.AntiSpam.getStartTimeForQuietMode(context));
    }

    private boolean isEffective(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        int quietRepeatType = MiuiSettings.AntiSpam.getQuietRepeatType(context);
        if (quietRepeatType != 79) {
            if (quietRepeatType != 127 && !new Alarm.DaysOfWeek(MiuiSettings.AntiSpam.getQuietRepeatType(context)).isAlarmDay()) {
                return false;
            }
        } else if (HolidayHelper.isWeekEnd(calendar)) {
            return false;
        }
        return true;
    }

    private boolean isEffectiveTurnOff(Context context) {
        return isEffective(context) || System.currentTimeMillis() < MiuiSettings.AntiSpam.getNextAutoStartTime(context);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (MiuiSettings.SilenceMode.isSupported) {
            return;
        }
        String action = intent.getAction();
        if ("android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.TIME_SET".equals(action)) {
            if (MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(context)) {
                DoNotDisturbModeUtils.startAutoTime(context);
            }
            if ("android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                MiuiSettings.AntiSpam.setNextAutoStartTime(context, 0L);
                MiuiSettings.AntiSpam.setNextAutoEndTime(context, 0L);
            }
        } else if ("com.android.settings.dndm.AUTO_TIME_TURN_ON".equals(action)) {
            if (MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(context) && DoNotDisturbModeUtils.isInDoNotDisturbModeTimeNow(context) && isEffective(context)) {
                MiuiSettings.AntiSpam.setQuietMode(context, true, UserHandle.myUserId());
                MiuiSettings.AntiSpam.setNextAutoStartTime(context, getAlarmStartTime(context));
            }
        } else if ("com.android.settings.dndm.AUTO_TIME_TURN_OFF".equals(action) && MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(context)) {
            if (!DoNotDisturbModeUtils.isInDoNotDisturbModeTimeNow(context) && isEffectiveTurnOff(context)) {
                MiuiSettings.AntiSpam.setQuietMode(context, false, UserHandle.myUserId());
                MiuiSettings.AntiSpam.setNextAutoEndTime(context, getAlarmEndTime(context));
            }
            DoNotDisturbModeUtils.startAutoTime(context);
        }
    }
}
