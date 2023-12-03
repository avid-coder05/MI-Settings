package com.android.settings.notification;

import android.app.Activity;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.dndmode.Alarm;
import com.android.settings.dndmode.LabelPreference;
import com.android.settings.dndmode.RepeatPreference;
import com.android.settingslib.miuisettings.preference.EditTextPreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.Map;
import java.util.Set;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.widget.TimePicker;

/* loaded from: classes2.dex */
public abstract class SilentModeRuleBaseSettings extends SilentModeSettingsBase implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    protected Activity mActivity;
    protected Alarm.DaysOfWeek mBootDof;
    protected String mBootRepeatSummary;
    protected EditTextPreference mEditTitle;
    protected int mEndTime;
    protected LabelPreference mEndTimePS;
    protected String mHint;
    protected int mIntentMode;
    protected int mMode;
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.notification.SilentModeRuleBaseSettings.1
        @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            SilentModeRuleBaseSettings silentModeRuleBaseSettings = SilentModeRuleBaseSettings.this;
            if (silentModeRuleBaseSettings.mTimeFlag) {
                int i3 = (i * 60) + i2;
                silentModeRuleBaseSettings.mEndTime = i3;
                silentModeRuleBaseSettings.mEndTimePS.setLabel(silentModeRuleBaseSettings.timeTostring(i3));
                return;
            }
            int i4 = (i * 60) + i2;
            silentModeRuleBaseSettings.mStartTime = i4;
            silentModeRuleBaseSettings.mStartTimePS.setLabel(silentModeRuleBaseSettings.timeTostring(i4));
        }
    };
    protected CheckBoxPreference mQuietWristband;
    protected PreferenceCategory mQuietWristbandCategor;
    protected RepeatPreference mRepeatTime;
    protected PreferenceScreen mRoot;
    protected String mRuleId;
    protected DropDownPreference mSilentMode;
    protected int mStartTime;
    protected LabelPreference mStartTimePS;
    protected boolean mTimeFlag;

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
        addPreferencesFromResource(R.xml.new_dndm_time_settings);
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
        this.mRoot = (PreferenceScreen) findPreference("time_setting_root");
        this.mStartTimePS = (LabelPreference) findPreference("start_time");
        this.mEndTimePS = (LabelPreference) findPreference("end_time");
        this.mRepeatTime = (RepeatPreference) findPreference("repeat");
        this.mSilentMode = (DropDownPreference) findPreference("silent_mode");
        this.mEditTitle = (EditTextPreference) findPreference("key_edittitle");
        this.mQuietWristbandCategor = (PreferenceCategory) findPreference("key_quiet_wristband_category");
        this.mQuietWristband = (CheckBoxPreference) findPreference("key_quiet_wristband");
        setHasOptionsMenu(true);
        onCreateInternal();
        if (bundle != null) {
            restoreSaveInstanceState(bundle);
        }
        this.mEditTitle.setText(this.mHint);
        this.mEditTitle.setSummary(this.mHint);
        this.mEditTitle.setOnPreferenceChangeListener(this);
        this.mStartTimePS.setLabel(timeTostring(this.mStartTime));
        this.mStartTimePS.setOnPreferenceClickListener(this);
        this.mEndTimePS.setLabel(timeTostring(this.mEndTime));
        this.mEndTimePS.setOnPreferenceClickListener(this);
        this.mRepeatTime.setLabel(this.mBootRepeatSummary);
        this.mRepeatTime.setDaysOfWeek(this.mBootDof);
        this.mSilentMode.setOnPreferenceChangeListener(this);
        this.mSilentMode.setValue(this.mMode + "");
        DropDownPreference dropDownPreference = this.mSilentMode;
        dropDownPreference.setSummary(dropDownPreference.getEntry());
        this.mRoot.removePreference(this.mQuietWristbandCategor);
    }

    protected abstract void onCreateInternal();

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str;
        DropDownPreference dropDownPreference = this.mSilentMode;
        if (preference == dropDownPreference) {
            dropDownPreference.setValue((String) obj);
            DropDownPreference dropDownPreference2 = this.mSilentMode;
            dropDownPreference2.setSummary(dropDownPreference2.getEntry());
            this.mMode = this.mSilentMode.getOrder();
            return true;
        } else if (preference != this.mEditTitle || (str = (String) obj) == null || str.length() == 0) {
            return false;
        } else {
            this.mEditTitle.setSummary(str);
            this.mEditTitle.setText(str);
            this.mHint = str;
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int i;
        Activity activity = this.mActivity;
        TimePickerDialog.OnTimeSetListener onTimeSetListener = this.mOnTimeSetListener;
        int i2 = this.mStartTime;
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, onTimeSetListener, i2 / 60, i2 % 60, true);
        if (preference == this.mStartTimePS) {
            this.mTimeFlag = false;
            i = this.mStartTime;
        } else if (preference == this.mEndTimePS) {
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
        bundle.putInt("flag_bootdof", this.mRepeatTime.getDaysOfWeek().getCoded());
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
}
