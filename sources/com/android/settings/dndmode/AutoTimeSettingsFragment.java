package com.android.settings.dndmode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dndmode.Alarm;
import com.android.settings.search.tree.SecuritySettingsTree;
import java.util.Locale;
import miui.bluetooth.ble.MiBleProfile;
import miui.os.Build;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.widget.TimePicker;

/* loaded from: classes.dex */
public class AutoTimeSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private Activity mActivity;
    private int mEndTime;
    private LabelPreference mEndTimePS;
    private CheckBoxPreference mQuietWristband;
    private PreferenceCategory mQuietWristbandCategor;
    private RepeatPreference mRepeatTime;
    private PreferenceScreen mRoot;
    private int mStartTime;
    private LabelPreference mStartTimePS;
    private boolean mTimeFlag;
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.dndmode.AutoTimeSettingsFragment.1
        @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            if (AutoTimeSettingsFragment.this.mTimeFlag) {
                AutoTimeSettingsFragment.this.mEndTime = (i * 60) + i2;
                MiuiSettings.AntiSpam.setEndTimeForQuietMode(AutoTimeSettingsFragment.this.mActivity, AutoTimeSettingsFragment.this.mEndTime);
                LabelPreference labelPreference = AutoTimeSettingsFragment.this.mEndTimePS;
                AutoTimeSettingsFragment autoTimeSettingsFragment = AutoTimeSettingsFragment.this;
                labelPreference.setLabel(autoTimeSettingsFragment.timeToString(autoTimeSettingsFragment.mEndTime));
            } else {
                AutoTimeSettingsFragment.this.mStartTime = (i * 60) + i2;
                MiuiSettings.AntiSpam.setStartTimeForQuietMode(AutoTimeSettingsFragment.this.mActivity, AutoTimeSettingsFragment.this.mStartTime);
                LabelPreference labelPreference2 = AutoTimeSettingsFragment.this.mStartTimePS;
                AutoTimeSettingsFragment autoTimeSettingsFragment2 = AutoTimeSettingsFragment.this;
                labelPreference2.setLabel(autoTimeSettingsFragment2.timeToString(autoTimeSettingsFragment2.mStartTime));
            }
            if (MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(AutoTimeSettingsFragment.this.mActivity)) {
                DoNotDisturbModeUtils.startAutoTime(AutoTimeSettingsFragment.this.mActivity);
            }
        }
    };
    private ContentObserver mWristbandObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.dndmode.AutoTimeSettingsFragment.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            AutoTimeSettingsFragment.this.mQuietWristband.setChecked(MiuiSettings.AntiSpam.isQuietWristband(AutoTimeSettingsFragment.this.mActivity));
        }
    };

    private void closeQuietWristband() {
        Intent intent = new Intent("quietWristband");
        intent.setComponent(new ComponentName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.antispam.service.AntiSpamService"));
        intent.putExtra("check", false);
        this.mActivity.startService(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String timeToString(int i) {
        return String.format(Locale.getDefault(), "%d:%02d", Integer.valueOf(i / 60), Integer.valueOf(i % 60));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.dndm_time_settings);
        this.mActivity = getActivity();
        this.mRoot = (PreferenceScreen) findPreference("time_setting_root");
        this.mStartTime = MiuiSettings.AntiSpam.getStartTimeForQuietMode(this.mActivity);
        this.mEndTime = MiuiSettings.AntiSpam.getEndTimeForQuietMode(this.mActivity);
        this.mStartTimePS = (LabelPreference) findPreference("start_time");
        this.mEndTimePS = (LabelPreference) findPreference("end_time");
        this.mRepeatTime = (RepeatPreference) findPreference("repeat");
        this.mQuietWristbandCategor = (PreferenceCategory) findPreference("key_quiet_wristband_category");
        this.mQuietWristband = (CheckBoxPreference) findPreference("key_quiet_wristband");
        this.mStartTimePS.setLabel(timeToString(this.mStartTime));
        this.mStartTimePS.setOnPreferenceClickListener(this);
        this.mEndTimePS.setLabel(timeToString(this.mEndTime));
        this.mEndTimePS.setOnPreferenceClickListener(this);
        Alarm.DaysOfWeek daysOfWeek = new Alarm.DaysOfWeek(MiuiSettings.AntiSpam.getQuietRepeatType(this.mActivity));
        this.mRepeatTime.setLabel(daysOfWeek.toString(this.mActivity, true));
        this.mRepeatTime.setDaysOfWeek(daysOfWeek);
        if (Build.IS_TABLET) {
            this.mRoot.removePreference(this.mQuietWristbandCategor);
        } else {
            this.mQuietWristband.setOnPreferenceChangeListener(this);
            this.mQuietWristband.setChecked(MiuiSettings.AntiSpam.isQuietWristband(this.mActivity));
        }
        this.mActivity.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("quiet_wristband"), false, this.mWristbandObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mActivity.getContentResolver().unregisterContentObserver(this.mWristbandObserver);
        MiuiSettings.AntiSpam.setQuietRepeatType(this.mActivity, this.mRepeatTime.getDaysOfWeek().getCoded());
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mQuietWristband) {
            if (!((Boolean) obj).booleanValue()) {
                closeQuietWristband();
                return false;
            }
            String bluetoothAddressToUnlock = new MiuiLockPatternUtils(this.mActivity).getBluetoothAddressToUnlock();
            if (!TextUtils.isEmpty(bluetoothAddressToUnlock)) {
                startQuietWristband(bluetoothAddressToUnlock);
                return false;
            }
            Intent intent = new Intent(MiBleProfile.ACTION_SELECT_DEVICE);
            intent.putExtra(MiBleProfile.EXTRA_MIBLE_PROPERTY, 1);
            this.mActivity.startActivityForResult(intent, 1);
            return false;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        Activity activity = this.mActivity;
        TimePickerDialog.OnTimeSetListener onTimeSetListener = this.mOnTimeSetListener;
        int i = this.mStartTime;
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, onTimeSetListener, i / 60, i % 60, true);
        if (preference == this.mStartTimePS) {
            this.mTimeFlag = false;
            int i2 = this.mStartTime;
            if (i2 > 0) {
                timePickerDialog.updateTime(i2 / 60, i2 % 60);
            } else {
                timePickerDialog.updateTime(0, 0);
            }
            timePickerDialog.show();
        } else if (preference == this.mEndTimePS) {
            this.mTimeFlag = true;
            int i3 = this.mEndTime;
            if (i3 > 0) {
                timePickerDialog.updateTime(i3 / 60, i3 % 60);
            } else {
                timePickerDialog.updateTime(0, 0);
            }
            timePickerDialog.show();
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    public void startQuietWristband(String str) {
        Intent intent = new Intent("quietWristband");
        intent.setComponent(new ComponentName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.antispam.service.AntiSpamService"));
        intent.putExtra("mac", str);
        intent.putExtra("check", true);
        this.mActivity.startService(intent);
    }
}
