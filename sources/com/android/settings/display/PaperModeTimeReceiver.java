package com.android.settings.display;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import miui.provider.Weather;

/* loaded from: classes.dex */
public class PaperModeTimeReceiver extends BroadcastReceiver {
    private boolean isAutoTimeOn() {
        return Settings.Global.getInt(AppGlobals.getInitialApplication().getContentResolver(), "auto_time", 0) > 0;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int paperModeSchedulerType = PaperModeTimeModeUtil.getPaperModeSchedulerType(context);
        boolean isPaperModeTimeEnable = PaperModeTimeModeUtil.isPaperModeTimeEnable(context);
        int paperModeStartTime = paperModeSchedulerType == 2 ? PaperModeTimeModeUtil.getPaperModeStartTime(context) : PaperModeTimeModeUtil.getPaperModeTwilightSunsetTime(context);
        int paperModeEndTime = paperModeSchedulerType == 2 ? PaperModeTimeModeUtil.getPaperModeEndTime(context) : PaperModeTimeModeUtil.getPaperModeTwilightSunriseTime(context);
        Log.w("PaperModeTimeReceiver", "onReceive:" + action + " paperModeSchedulerType:" + paperModeSchedulerType + " isPaperModeTimeEnable:" + isPaperModeTimeEnable);
        if (MiuiSettings.ScreenEffect.isScreenPaperModeSupported && isPaperModeTimeEnable) {
            if ("android.intent.action.TIME_SET".equals(action)) {
                PaperModeTimeModeUtil.startPaperModeAutoTime(context, paperModeSchedulerType, !isAutoTimeOn());
            } else if ("android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.DATE_CHANGED".equals(action)) {
                PaperModeTimeModeUtil.startPaperModeAutoTime(context, paperModeSchedulerType);
            } else if ("miui.intent.action.PAPER_MODE_ON".equals(action)) {
                if (MiuiSettings.ScreenEffect.isInPaperModeTimeSchedule(context, paperModeStartTime, paperModeEndTime)) {
                    PaperModeTimeModeUtil.setPaperModeEnabled(true, context);
                }
            } else if ("miui.intent.action.PAPER_MODE_OFF".endsWith(action)) {
                if (!MiuiSettings.ScreenEffect.isInPaperModeTimeSchedule(context, paperModeStartTime, paperModeEndTime)) {
                    PaperModeTimeModeUtil.setPaperModeEnabled(false, context);
                }
                PaperModeTimeModeUtil.startPaperModeAutoTime(context, paperModeSchedulerType);
            } else if ("miui.intent.action.LOCATION_CHANGED".equals(action)) {
                Log.w("PaperModeTimeReceiver", " receive intent " + action + " state = " + intent.getIntExtra("state", 0) + " sunrise=" + intent.getIntExtra(Weather.WeatherBaseColumns.SUNRISE, 0) + " sunset=" + intent.getIntExtra(Weather.WeatherBaseColumns.SUNSET, 0));
                PaperModeTimeModeUtil.setPaperModeTwilightSunriseTime(context, intent.getIntExtra(Weather.WeatherBaseColumns.SUNRISE, 360));
                PaperModeTimeModeUtil.setPaperModeTwilightSunsetTime(context, intent.getIntExtra(Weather.WeatherBaseColumns.SUNSET, 1080));
                PaperModeTimeModeUtil.startPaperModeAutoTime(context, paperModeSchedulerType);
            }
        }
    }
}
