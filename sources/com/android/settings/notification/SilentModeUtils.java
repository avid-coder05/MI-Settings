package com.android.settings.notification;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.provider.MiuiSettings;
import android.service.notification.ZenModeConfig;
import com.android.settings.R;
import com.android.settings.notification.SilentModeRuleBaseSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class SilentModeUtils {
    public static AutomaticZenRule createAutoZenRuleFromDND(Context context) {
        SilentModeRuleBaseSettings.RuleInfo ruleInfoFromDND = getRuleInfoFromDND(MiuiSettings.AntiSpam.getStartTimeForQuietMode(context), MiuiSettings.AntiSpam.getEndTimeForQuietMode(context), getDaysArray(parseDays(MiuiSettings.AntiSpam.getQuietRepeatType(context))));
        return new AutomaticZenRule(context.getString(R.string.default_rule_name), ruleInfoFromDND.serviceComponent, ruleInfoFromDND.defaultConditionId, NotificationManager.zenModeToInterruptionFilter(1), MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(context));
    }

    public static int[] getDaysArray(boolean[] zArr) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (int i2 = 0; i2 < zArr.length; i2++) {
            if (zArr[i2]) {
                arrayList.add(Integer.valueOf(((i2 + 1) % 7) + 1));
            }
        }
        Collections.sort(arrayList);
        int[] iArr = new int[arrayList.size()];
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            iArr[i] = ((Integer) it.next()).intValue();
            i++;
        }
        return iArr;
    }

    public static SilentModeRuleBaseSettings.RuleInfo getRuleInfoFromDND(int i, int i2, int[] iArr) {
        ZenModeConfig.ScheduleInfo scheduleInfo = new ZenModeConfig.ScheduleInfo();
        scheduleInfo.days = iArr;
        scheduleInfo.startHour = i / 60;
        scheduleInfo.startMinute = i % 60;
        scheduleInfo.endHour = i2 / 60;
        scheduleInfo.endMinute = i2 % 60;
        scheduleInfo.exitAtAlarm = false;
        SilentModeRuleBaseSettings.RuleInfo ruleInfo = new SilentModeRuleBaseSettings.RuleInfo();
        ruleInfo.settingsAction = "SilentModeRuleSettings";
        ruleInfo.defaultConditionId = ZenModeConfig.toScheduleConditionId(scheduleInfo);
        ruleInfo.serviceComponent = ZenModeConfig.getScheduleConditionProvider();
        return ruleInfo;
    }

    public static ZenModeConfig getZenModeConfig(Context context) {
        return NotificationManager.from(context).getZenModeConfig();
    }

    public static int parseDays(int[] iArr) {
        if (iArr == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < iArr.length; i2++) {
            i = iArr[i2] == 1 ? i + 64 : (int) (i + Math.pow(2.0d, (double) (iArr[i2] - 2)));
        }
        return i;
    }

    public static boolean[] parseDays(int i) {
        boolean[] zArr = new boolean[7];
        for (int i2 = 0; i2 < 7; i2++) {
            if (((1 << i2) & i) != 0) {
                zArr[i2] = true;
            }
        }
        return zArr;
    }
}
