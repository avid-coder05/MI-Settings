package com.android.settings.notification;

import android.service.notification.ZenModeConfig;
import com.android.settings.dndmode.Alarm;

/* loaded from: classes2.dex */
public class SilentModeEditRuleSettings extends AutomaticZenRuleBaseSettings {
    private boolean refreshRuleOrFinish() {
        ZenModeConfig.ZenRule zenRule = (ZenModeConfig.ZenRule) this.mConfig.automaticRules.get(this.mRuleId);
        this.mRule = zenRule;
        if (setRule(zenRule)) {
            return false;
        }
        getActivity().finish();
        return true;
    }

    private boolean setRule(ZenModeConfig.ZenRule zenRule) {
        ZenModeConfig.ScheduleInfo tryParseScheduleConditionId = zenRule != null ? ZenModeConfig.tryParseScheduleConditionId(zenRule.conditionId) : null;
        this.mSchedule = tryParseScheduleConditionId;
        return tryParseScheduleConditionId != null;
    }

    public boolean commitRule() {
        this.mRule.name = getRuleName();
        ZenModeConfig.ScheduleInfo scheduleInfo = this.mSchedule;
        int i = this.mStartTime;
        scheduleInfo.startHour = i / 60;
        scheduleInfo.startMinute = i % 60;
        int i2 = this.mEndTime;
        scheduleInfo.endHour = i2 / 60;
        scheduleInfo.endMinute = i2 % 60;
        scheduleInfo.days = SilentModeUtils.getDaysArray(this.mRepeatDaysPref.getDaysOfWeek().getBooleanArray());
        ZenModeConfig.ScheduleInfo scheduleInfo2 = this.mSchedule;
        scheduleInfo2.exitAtAlarm = false;
        this.mRule.conditionId = ZenModeConfig.toScheduleConditionId(scheduleInfo2);
        ZenModeConfig.ZenRule zenRule = this.mRule;
        zenRule.zenMode = 1;
        zenRule.condition = null;
        zenRule.snoozing = false;
        return setZenRule(this.mRuleId, createAutomaticZenRule(zenRule));
    }

    @Override // com.android.settings.notification.AutomaticZenRuleBaseSettings
    protected void onCreateInternal() {
        refreshRuleOrFinish();
        ZenModeConfig.ScheduleInfo scheduleInfo = this.mSchedule;
        this.mStartTime = (scheduleInfo.startHour * 60) + scheduleInfo.startMinute;
        this.mEndTime = (scheduleInfo.endHour * 60) + scheduleInfo.endMinute;
        Alarm.DaysOfWeek daysOfWeek = new Alarm.DaysOfWeek(SilentModeUtils.parseDays(scheduleInfo.days));
        this.mBootDof = daysOfWeek;
        this.mBootRepeatSummary = daysOfWeek.toString(this.mActivity, true);
        ZenModeConfig.ZenRule zenRule = this.mRule;
        this.mMode = zenRule.zenMode;
        this.mHint = zenRule.name;
    }
}
