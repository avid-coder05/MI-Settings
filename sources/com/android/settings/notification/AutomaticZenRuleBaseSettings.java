package com.android.settings.notification;

import android.app.Activity;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.dndmode.Alarm;
import com.android.settings.dndmode.LabelPreference;
import com.android.settings.dndmode.RepeatPreference;
import java.util.Map;
import java.util.Set;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.widget.TimePicker;

/* loaded from: classes2.dex */
public abstract class AutomaticZenRuleBaseSettings extends SilentModeSettingsBase implements Preference.OnPreferenceClickListener {
    protected Activity mActivity;
    protected Alarm.DaysOfWeek mBootDof;
    protected String mBootRepeatSummary;
    protected int mEndTime;
    protected String mHint;
    protected int mIntentMode;
    protected int mMode;
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.notification.AutomaticZenRuleBaseSettings.1
        @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            AutomaticZenRuleBaseSettings automaticZenRuleBaseSettings = AutomaticZenRuleBaseSettings.this;
            if (automaticZenRuleBaseSettings.mTimeFlag) {
                int i3 = (i * 60) + i2;
                automaticZenRuleBaseSettings.mEndTime = i3;
                automaticZenRuleBaseSettings.mTimeTurnOffPref.setLabel(automaticZenRuleBaseSettings.timeTostring(i3));
                return;
            }
            int i4 = (i * 60) + i2;
            automaticZenRuleBaseSettings.mStartTime = i4;
            automaticZenRuleBaseSettings.mTimeTurnOnPref.setLabel(automaticZenRuleBaseSettings.timeTostring(i4));
        }
    };
    protected RepeatPreference mRepeatDaysPref;
    protected ZenModeConfig.ZenRule mRule;
    protected String mRuleId;
    protected ZenRuleNamePreference mRuleNamePref;
    protected ZenModeConfig.ScheduleInfo mSchedule;
    protected int mStartTime;
    protected boolean mTimeFlag;
    protected LabelPreference mTimeTurnOffPref;
    protected LabelPreference mTimeTurnOnPref;

    /* loaded from: classes2.dex */
    public static class RuleInfo {
        public Uri defaultConditionId;
        public ComponentName serviceComponent;
        public String settingsAction;
    }

    private Set<Map.Entry<String, AutomaticZenRule>> getZenModeRules() {
        return NotificationManager.from(this.mContext).getAutomaticZenRules().entrySet();
    }

    private String minutes(int i) {
        StringBuilder sb;
        if (i < 10) {
            sb = new StringBuilder();
            sb.append("0");
            sb.append(i);
        } else {
            sb = new StringBuilder();
            sb.append(i);
            sb.append("");
        }
        return sb.toString();
    }

    private void restoreSaveInstanceState(Bundle bundle) {
        this.mStartTime = bundle.getInt("start_time");
        this.mEndTime = bundle.getInt("end_time");
        this.mBootDof.set(new Alarm.DaysOfWeek(bundle.getInt("flag_bootdof")));
        this.mBootRepeatSummary = this.mBootDof.toString(this.mActivity, true);
        this.mMode = bundle.getInt("silent_mode");
        this.mHint = bundle.getString("key_edittitle");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String timeTostring(int i) {
        return (i / 60) + ":" + minutes(i % 60);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String addZenRule(AutomaticZenRule automaticZenRule) {
        try {
            String addAutomaticZenRule = NotificationManager.from(this.mContext).addAutomaticZenRule(automaticZenRule);
            maybeRefreshRules(NotificationManager.from(this.mContext).getAutomaticZenRule(addAutomaticZenRule) != null, true);
            return addAutomaticZenRule;
        } catch (Exception unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AutomaticZenRule createAutomaticZenRule(ZenModeConfig.ZenRule zenRule) {
        return new AutomaticZenRule(zenRule.name, zenRule.component, zenRule.conditionId, NotificationManager.zenModeToInterruptionFilter(zenRule.zenMode), zenRule.enabled);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RuleInfo getRuleInfo() {
        ZenModeConfig.ScheduleInfo scheduleInfo = new ZenModeConfig.ScheduleInfo();
        scheduleInfo.days = SilentModeUtils.getDaysArray(this.mRepeatDaysPref.getDaysOfWeek().getBooleanArray());
        int i = this.mStartTime;
        scheduleInfo.startHour = i / 60;
        scheduleInfo.startMinute = i % 60;
        int i2 = this.mEndTime;
        scheduleInfo.endHour = i2 / 60;
        scheduleInfo.endMinute = i2 % 60;
        scheduleInfo.exitAtAlarm = false;
        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.settingsAction = "SilentModeRuleSettings";
        ruleInfo.defaultConditionId = ZenModeConfig.toScheduleConditionId(scheduleInfo);
        ruleInfo.serviceComponent = ZenModeConfig.getScheduleConditionProvider();
        return ruleInfo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getRuleName() {
        return this.mRuleNamePref.getText() == null ? this.mRuleNamePref.getHint() : this.mRuleNamePref.getText();
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase
    protected void maybeRefreshRules(boolean z, boolean z2) {
        if (z) {
            Log.d("ZenModeSettings", "Refreshed mRules=" + getZenModeRules());
            if (z2) {
                onZenModeConfigChanged();
            }
        }
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.automatic_zen_rule_settings);
        FragmentActivity activity = getActivity();
        this.mActivity = activity;
        this.mIntentMode = activity.getIntent().getIntExtra(ExtraTelephony.FirewallLog.MODE, 0);
        String stringExtra = this.mActivity.getIntent().getStringExtra("rule_id");
        this.mRuleId = stringExtra;
        if (stringExtra == null) {
            Log.w("ZenModeSettings", "rule id is null");
            getActivity().finish();
            return;
        }
        setHasOptionsMenu(true);
        onCreateInternal();
        ZenRuleNamePreference zenRuleNamePreference = (ZenRuleNamePreference) findPreference("rule_name_edit");
        this.mRuleNamePref = zenRuleNamePreference;
        zenRuleNamePreference.setRuleName(this.mHint);
        LabelPreference labelPreference = (LabelPreference) findPreference("time_turn_on");
        this.mTimeTurnOnPref = labelPreference;
        labelPreference.setLabel(timeTostring(this.mStartTime));
        this.mTimeTurnOnPref.setOnPreferenceClickListener(this);
        LabelPreference labelPreference2 = (LabelPreference) findPreference("time_turn_off");
        this.mTimeTurnOffPref = labelPreference2;
        labelPreference2.setLabel(timeTostring(this.mEndTime));
        this.mTimeTurnOffPref.setOnPreferenceClickListener(this);
        RepeatPreference repeatPreference = (RepeatPreference) findPreference("repeat_days");
        this.mRepeatDaysPref = repeatPreference;
        repeatPreference.setLabel(this.mBootRepeatSummary);
        this.mRepeatDaysPref.setDaysOfWeek(this.mBootDof);
        if (bundle != null) {
            restoreSaveInstanceState(bundle);
        }
    }

    protected abstract void onCreateInternal();

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int i;
        Activity activity = this.mActivity;
        TimePickerDialog.OnTimeSetListener onTimeSetListener = this.mOnTimeSetListener;
        int i2 = this.mStartTime;
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, onTimeSetListener, i2 / 60, i2 % 60, true);
        if (preference == this.mTimeTurnOnPref) {
            this.mTimeFlag = false;
            i = this.mStartTime;
        } else if (preference == this.mTimeTurnOffPref) {
            this.mTimeFlag = true;
            i = this.mEndTime;
        } else {
            i = 0;
        }
        if (i <= 0) {
            i = 0;
        }
        timePickerDialog.updateTime(i / 60, i % 60);
        timePickerDialog.show();
        return false;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("start_time", this.mStartTime);
        bundle.putInt("end_time", this.mEndTime);
        bundle.putInt("flag_bootdof", this.mRepeatDaysPref.getDaysOfWeek().getCoded());
        bundle.putInt("silent_mode", this.mMode);
        bundle.putString("key_edittitle", this.mHint);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase
    protected void onZenModeChanged() {
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase
    protected void onZenModeConfigChanged() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean setZenRule(String str, AutomaticZenRule automaticZenRule) {
        boolean updateAutomaticZenRule = NotificationManager.from(this.mContext).updateAutomaticZenRule(str, automaticZenRule);
        maybeRefreshRules(updateAutomaticZenRule, true);
        return updateAutomaticZenRule;
    }
}
