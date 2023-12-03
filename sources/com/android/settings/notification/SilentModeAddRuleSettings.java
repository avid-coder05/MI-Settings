package com.android.settings.notification;

import android.service.notification.ZenModeConfig;
import com.android.settings.R;
import com.android.settings.dndmode.Alarm;
import com.android.settings.notification.AutomaticZenRuleBaseSettings;

/* loaded from: classes2.dex */
public class SilentModeAddRuleSettings extends AutomaticZenRuleBaseSettings {
    public boolean commitRule() {
        ZenModeConfig.ZenRule zenRule = new ZenModeConfig.ZenRule();
        AutomaticZenRuleBaseSettings.RuleInfo ruleInfo = getRuleInfo();
        zenRule.name = getRuleName();
        zenRule.enabled = true;
        zenRule.zenMode = 1;
        zenRule.conditionId = ruleInfo.defaultConditionId;
        zenRule.component = ruleInfo.serviceComponent;
        return addZenRule(createAutomaticZenRule(zenRule)) != null;
    }

    @Override // com.android.settings.notification.AutomaticZenRuleBaseSettings
    protected void onCreateInternal() {
        this.mStartTime = 1380;
        this.mEndTime = 420;
        Alarm.DaysOfWeek daysOfWeek = new Alarm.DaysOfWeek(127);
        this.mBootDof = daysOfWeek;
        this.mBootRepeatSummary = daysOfWeek.toString(this.mActivity, true);
        this.mMode = 1;
        this.mHint = String.format(getResources().getString(R.string.timed_titlei), Integer.valueOf(this.mConfig.automaticRules.size() + 1));
    }
}
