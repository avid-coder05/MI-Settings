package com.android.settings.display;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dndmode.LabelPreference;
import java.util.Calendar;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.TimePicker;

/* loaded from: classes.dex */
public class DarkModeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, TimePickerDialog.OnTimeSetListener {
    private String DARK_MODE_OPEN_BEFORE_TIME_MODE = "dark_mode_open_before_time_mode";
    private boolean isSetStartTime;
    private Context mContext;
    private DarkModePreference mDarkModeAutoTimePref;
    private DarkModePreference mDarkModeSunTimeModePref;
    private CheckBoxPreference mDarkModeTimeEnablePref;
    private PreferenceGroup mDarkModeTimeGroup;
    private PreferenceGroup mDarkModeTimeRadioGroup;
    private int mEndTime;
    private LabelPreference mEndTimePref;
    private Handler mHandler;
    private int mStartTime;
    private LabelPreference mStartTimePref;
    private TimePickerDialog mTimePickerDialog;
    private Toast mToast;

    private <T extends Preference> T findPreferenceImpl(String str) {
        return (T) super.findPreference(str);
    }

    private String formatChooseTime(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, i);
        calendar.set(12, i2);
        return DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(), 12);
    }

    private Activity getActivityImpl() {
        return super.getActivity();
    }

    private int getDarkModeTimeType() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "dark_mode_time_type", 2);
    }

    private void initPreference() {
        this.mDarkModeTimeGroup = (PreferenceGroup) findPreferenceImpl("dark_mode_time_group");
        this.mDarkModeTimeRadioGroup = (PreferenceGroup) findPreferenceImpl("dark_mode_time_radio_group");
        this.mDarkModeTimeEnablePref = (CheckBoxPreference) findPreferenceImpl("dark_mode_time_enable");
        this.mDarkModeSunTimeModePref = (DarkModePreference) findPreferenceImpl("dark_mode_sun_time_mode");
        this.mDarkModeAutoTimePref = (DarkModePreference) findPreferenceImpl("dark_mode_auto_time_mode");
        this.mStartTimePref = (LabelPreference) findPreferenceImpl("dark_mode_start_time");
        this.mEndTimePref = (LabelPreference) findPreferenceImpl("dark_mode_end_time");
        this.mDarkModeTimeEnablePref.setChecked(DarkModeTimeModeUtil.isDarkModeTimeEnable(this.mContext));
        this.mDarkModeSunTimeModePref.setChecked(DarkModeTimeModeUtil.isSunRiseSunSetMode(this.mContext));
        this.mDarkModeAutoTimePref.setChecked(DarkModeTimeModeUtil.isDarkModeAutoTimeEnable(this.mContext));
        LabelPreference labelPreference = this.mStartTimePref;
        int i = this.mStartTime;
        labelPreference.setLabel(formatChooseTime(i / 60, i % 60));
        LabelPreference labelPreference2 = this.mEndTimePref;
        int i2 = this.mEndTime;
        labelPreference2.setLabel(formatChooseTime(i2 / 60, i2 % 60));
    }

    private void initPreferenceListener() {
        this.mDarkModeTimeEnablePref.setOnPreferenceChangeListener(this);
        this.mDarkModeSunTimeModePref.setOnPreferenceChangeListener(this);
        this.mDarkModeAutoTimePref.setOnPreferenceChangeListener(this);
        this.mStartTimePref.setOnPreferenceClickListener(this);
        this.mEndTimePref.setOnPreferenceClickListener(this);
    }

    private boolean isDarkModeOpenBeforeTimeMode(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), this.DARK_MODE_OPEN_BEFORE_TIME_MODE, false);
    }

    private boolean isDarkModeOpenInTimeSchedule(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "dark_mode_open_in_time_mode", false);
    }

    private void setDarkModeTimeType(int i) {
        Settings.System.putInt(this.mContext.getContentResolver(), "dark_mode_time_type", i);
    }

    private void showTimePickerDelay(final int i) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.postDelayed(new Runnable() { // from class: com.android.settings.display.DarkModeFragment.1
                @Override // java.lang.Runnable
                public void run() {
                    DarkModeFragment.this.showTimePicker(i);
                }
            }, 150L);
        }
    }

    private void showWarnToast() {
        Toast toast = this.mToast;
        if (toast != null) {
            toast.cancel();
        }
        Toast makeText = Toast.makeText(getActivity().getApplicationContext(), R.string.screen_dark_mode_select_time_error_summary, 0);
        this.mToast = makeText;
        makeText.show();
    }

    private void updateDarkModeTimeGroup(Boolean bool) {
        if (this.mDarkModeTimeGroup == null || this.mDarkModeTimeRadioGroup == null) {
            return;
        }
        if (bool.booleanValue()) {
            this.mDarkModeTimeGroup.addPreference(this.mDarkModeTimeRadioGroup);
        } else if (this.mDarkModeTimeGroup.findPreference("dark_mode_time_radio_group") != null) {
            this.mDarkModeTimeGroup.removePreference(this.mDarkModeTimeRadioGroup);
        }
    }

    private void updateDarkModeTimeGroupStatus() {
        this.mDarkModeSunTimeModePref.setChecked(getDarkModeTimeType() == 2);
        this.mDarkModeAutoTimePref.setChecked(getDarkModeTimeType() == 1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.dark_mode_settings);
        this.mContext = getActivityImpl();
        this.mHandler = new Handler();
        this.mStartTime = DarkModeTimeModeUtil.getDarkModeStartTime(this.mContext);
        this.mEndTime = DarkModeTimeModeUtil.getDarkModeEndTime(this.mContext);
        initPreference();
        initPreferenceListener();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Toast toast = this.mToast;
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        String key = preference.getKey();
        key.hashCode();
        char c = 65535;
        switch (key.hashCode()) {
            case -1609391848:
                if (key.equals("dark_mode_auto_time_mode")) {
                    c = 0;
                    break;
                }
                break;
            case -1456256958:
                if (key.equals("dark_mode_time_enable")) {
                    c = 1;
                    break;
                }
                break;
            case 224680815:
                if (key.equals("dark_mode_sun_time_mode")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Log.d("DarkModeFragment", "mDarkModeAutoTimePref isCheck = " + booleanValue);
                this.mDarkModeAutoTimePref.setChecked(booleanValue);
                this.mDarkModeSunTimeModePref.setChecked(false);
                setDarkModeTimeType(1);
                DarkModeTimeModeUtil.setDarkModeAutoTimeEnable(this.mContext, booleanValue);
                DarkModeTimeModeUtil.startDarkModeAutoTime(this.mContext, booleanValue);
                break;
            case 1:
                Log.d("DarkModeFragment", "mDarkModeTimeEnablePref isCheck = " + booleanValue);
                this.mDarkModeTimeEnablePref.setChecked(booleanValue);
                updateDarkModeTimeGroup(Boolean.valueOf(booleanValue));
                updateDarkModeTimeGroupStatus();
                setDarkModeTimeType(getDarkModeTimeType());
                DarkModeTimeModeUtil.setDarkModeTimeEnable(this.mContext, booleanValue);
                if (!booleanValue && isDarkModeOpenInTimeSchedule(this.mContext) && !isDarkModeOpenBeforeTimeMode(this.mContext)) {
                    DarkModeTimeModeUtil.setDarkModeEnable(this.mContext, false, false);
                    break;
                } else if (booleanValue) {
                    DarkModeTimeModeUtil.startDarkModeAutoTime(this.mContext, booleanValue);
                    break;
                }
                break;
            case 2:
                Log.d("DarkModeFragment", "mDarkModeSunTimeModePref isCheck = " + booleanValue);
                this.mDarkModeSunTimeModePref.setChecked(booleanValue);
                this.mDarkModeAutoTimePref.setChecked(false);
                setDarkModeTimeType(2);
                DarkModeTimeModeUtil.setSunRiseSunSetMode(this.mContext, booleanValue);
                break;
        }
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        Context context = this.mContext;
        int i = this.mStartTime;
        this.mTimePickerDialog = new TimePickerDialog(context, this, i / 60, i % 60, DateFormat.is24HourFormat(context));
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("dark_mode_start_time")) {
            this.isSetStartTime = true;
            showTimePicker(this.mStartTime);
        } else if (key.equals("dark_mode_end_time")) {
            this.isSetStartTime = false;
            showTimePicker(this.mEndTime);
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        int i3 = (i * 60) + i2;
        if (this.isSetStartTime) {
            if (i3 == this.mEndTime) {
                showWarnToast();
                showTimePickerDelay(this.mStartTime);
                return;
            }
            this.mStartTime = i3;
            this.mStartTimePref.setLabel(formatChooseTime(i, i2));
            DarkModeTimeModeUtil.setDarkModeStartTime(this.mContext, this.mStartTime);
        } else if (i3 == this.mStartTime) {
            showWarnToast();
            showTimePickerDelay(this.mEndTime);
            return;
        } else {
            this.mEndTime = i3;
            this.mEndTimePref.setLabel(formatChooseTime(i, i2));
            DarkModeTimeModeUtil.setDarkModeEndTime(this.mContext, this.mEndTime);
        }
        if (DarkModeTimeModeUtil.isDarkModeTimeEnable(this.mContext)) {
            DarkModeTimeModeUtil.startDarkModeAutoTime(this.mContext, true);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        updateDarkModeTimeGroup(Boolean.valueOf(DarkModeTimeModeUtil.isDarkModeTimeEnable(this.mContext)));
        updateDarkModeTimeGroupStatus();
    }

    public void showTimePicker(int i) {
        if (i > 0) {
            this.mTimePickerDialog.updateTime(i / 60, i % 60);
        } else {
            this.mTimePickerDialog.updateTime(0, 0);
        }
        this.mTimePickerDialog.setTitle(this.isSetStartTime ? R.string.paper_mode_start_time_title : R.string.paper_mode_end_time_title);
        this.mTimePickerDialog.show();
    }
}
