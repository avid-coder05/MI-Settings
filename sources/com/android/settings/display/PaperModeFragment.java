package com.android.settings.display;

import android.app.Activity;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.JobDispatcher;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.dndmode.LabelPreference;
import com.android.settings.report.InternationalCompat;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.Calendar;
import java.util.HashMap;
import miui.os.Build;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.TimePicker;

/* loaded from: classes.dex */
public class PaperModeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final int PAPER_MODE_MAX_LEVEL;
    private static final float PAPER_MODE_MIN_LEVEL;
    private static final float PER_LEVEL;
    private static int mEndTime;
    private static int mStartTime;
    private PaperRadioButtonPreference classicPreference;
    private boolean isSupportHDRMode;
    private CheckBoxPreference mAutoAdjustPreference;
    private PaperModePreference mAutoTwilightPref;
    private Context mContext;
    private PaperModePreference mCustomizedTimePref;
    AlertDialog mLocationGetDialog;
    private PreferenceGroup mPaperEffectGroup;
    private PreferenceGroup mPaperModeCustomizeTimeGroup;
    private ContentObserver mPaperModeEnableObserver;
    private ContentObserver mPaperModeScheduleObserver;
    private PreferenceGroup mPaperModeTimeGroup;
    private PreferenceGroup mPaperModeTimeRadioGroup;
    private TimePickerDialog mTimePickerDialog;
    private int mTimeZoneOffset;
    private CheckBoxPreference paperModeEnable;
    private LabelPreference paperModeEndTime;
    private LabelPreference paperModeStartTime;
    private CheckBoxPreference paperModeTimeEnable;
    private PaperRadioButtonPreference paperPreference;
    private boolean mTimeFlag = true;
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.display.PaperModeFragment.1
        @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            if (PaperModeFragment.this.mTimeFlag) {
                int unused = PaperModeFragment.mEndTime = (i * 60) + i2;
                PaperModeTimeModeUtil.setPaperModeEndTime(PaperModeFragment.this.mContext, PaperModeFragment.mEndTime);
                PaperModeFragment.this.paperModeEndTime.setLabel(PaperModeFragment.this.formatChoosenTime(i, i2));
            } else {
                int unused2 = PaperModeFragment.mStartTime = (i * 60) + i2;
                PaperModeTimeModeUtil.setPaperModeStartTime(PaperModeFragment.this.mContext, PaperModeFragment.mStartTime);
                PaperModeFragment.this.paperModeStartTime.setLabel(PaperModeFragment.this.formatChoosenTime(i, i2));
            }
            if (PaperModeFragment.isPaperModeTimeEnable(PaperModeFragment.this.mContext)) {
                PaperModeTimeModeUtil.startPaperModeAutoTime(PaperModeFragment.this.mContext, 2);
            }
        }
    };
    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.display.PaperModeFragment.2
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                Intent intent = new Intent();
                intent.setAction("android.settings.LOCATION_SOURCE_SETTINGS");
                PaperModeFragment.this.mContext.startActivity(intent);
            } else if (i == -2) {
                PaperModeFragment.this.mLocationGetDialog.dismiss();
            }
        }
    };

    static {
        int i = MiuiSettings.ScreenEffect.PAPER_MODE_MAX_LEVEL;
        PAPER_MODE_MAX_LEVEL = i;
        float floatValue = FeatureParser.getFloat("paper_mode_min_level", 1.0f).floatValue();
        PAPER_MODE_MIN_LEVEL = floatValue;
        PER_LEVEL = (i - floatValue) / 1000.0f;
    }

    public static boolean autoAdjustState(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_auto_adjust", 1) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatChoosenTime(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, i);
        calendar.set(12, i2);
        return DateUtils.formatDateTime(getActivity().getApplicationContext(), calendar.getTimeInMillis(), 12);
    }

    private int getPaperModeLevel() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "screen_paper_mode_level", MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPaperModeSchedulerType() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "paper_mode_scheduler_type", 2);
    }

    public static boolean isPaperModeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_paper_mode_enabled", false);
    }

    public static boolean isPaperModeTimeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_paper_mode_time_enabled", false);
    }

    private void setAutoAdjustLevel(boolean z) {
        Log.d("PaperModeFragment", "setAutoAdjustLevel enable : " + z);
        MiuiSettings.System.putBoolean(this.mContext.getContentResolver(), "screen_auto_adjust", z);
    }

    private void setPaperModeEnable(boolean z) {
        MiuiSettings.System.putBoolean(this.mContext.getContentResolver(), "screen_paper_mode_enabled", z);
    }

    private void setPaperModeLevel(int i) {
        if (i != getPaperModeLevel()) {
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_paper_mode_level", i);
        }
    }

    private void setPaperModeSchedulerType(int i) {
        Settings.System.putInt(this.mContext.getContentResolver(), "paper_mode_scheduler_type", i);
    }

    private void setPaperModeTimeEnable(boolean z) {
        MiuiSettings.System.putBoolean(this.mContext.getContentResolver(), "screen_paper_mode_time_enabled", z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePaperEffectGroup(Boolean bool) {
        if (this.mPaperEffectGroup == null) {
            return;
        }
        if (bool.booleanValue()) {
            getPreferenceScreen().addPreference(this.mPaperEffectGroup);
            if (MiuiUtils.supportSmartEyeCare()) {
                this.mAutoAdjustPreference.setVisible(true);
            }
            int i = Settings.System.getInt(this.mContext.getContentResolver(), "screen_mode_type", 0);
            this.classicPreference.setChecked(i == 0);
            this.paperPreference.setChecked(i == 1);
            return;
        }
        if (findPreference("paper_effect") != null) {
            getPreferenceScreen().removePreference(this.mPaperEffectGroup);
        }
        if (this.mAutoAdjustPreference == null || !MiuiUtils.supportSmartEyeCare()) {
            return;
        }
        this.mAutoAdjustPreference.setVisible(false);
    }

    private void updatePaperModeTimeGroup(Boolean bool) {
        if (this.mPaperModeTimeGroup == null || this.mPaperModeTimeRadioGroup == null) {
            return;
        }
        if (bool.booleanValue()) {
            this.mPaperModeTimeGroup.addPreference(this.mPaperModeTimeRadioGroup);
        } else if (this.mPaperModeTimeGroup.findPreference("paper_mode_time_radio_group") != null) {
            this.mPaperModeTimeGroup.removePreference(this.mPaperModeTimeRadioGroup);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return PaperModeFragment.class.getName();
    }

    public void initCallingLock() {
        if (this.mLocationGetDialog == null) {
            this.mLocationGetDialog = new AlertDialog.Builder(this.mContext).setTitle(R.string.paper_mode_get_location_dlg_title).setMessage(R.string.paper_mode_get_location_dlg_msg).setPositiveButton(R.string.paper_mode_get_location_dlg_positive_btn_text, this.dialogListener).setNegativeButton(R.string.paper_mode_get_location_dlg_negative_btn_text, this.dialogListener).setCancelable(false).create();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.paper_mode_settings);
        this.mContext = getActivity();
        this.isSupportHDRMode = MiuiUtils.isSupportHDRMode();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("paper_mode_enable");
        this.paperModeEnable = checkBoxPreference;
        checkBoxPreference.setChecked(isPaperModeEnable(this.mContext));
        this.paperModeEnable.setOnPreferenceChangeListener(this);
        this.mPaperModeTimeRadioGroup = (PreferenceGroup) findPreference("paper_mode_time_radio_group");
        this.mPaperModeTimeGroup = (PreferenceGroup) findPreference("paper_mode_time_group");
        this.mPaperModeCustomizeTimeGroup = (PreferenceGroup) findPreference("paper_mode_customize_time_group");
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("paper_mode_time_enable");
        this.paperModeTimeEnable = checkBoxPreference2;
        checkBoxPreference2.setChecked(isPaperModeTimeEnable(this.mContext));
        this.paperModeTimeEnable.setOnPreferenceChangeListener(this);
        this.mPaperEffectGroup = (PreferenceGroup) findPreference("paper_effect");
        this.classicPreference = (PaperRadioButtonPreference) findPreference("classic_mode");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.display.ClassicProtectionActivity"));
        this.classicPreference.setTargetIntent((Activity) this.mContext, intent);
        this.classicPreference.setOnPreferenceClickListener(this);
        this.paperPreference = (PaperRadioButtonPreference) findPreference("paper_mode");
        Intent intent2 = new Intent();
        intent2.setComponent(new ComponentName("com.android.settings", "com.android.settings.display.PaperProtectionActivity"));
        this.paperPreference.setTargetIntent((Activity) this.mContext, intent2);
        this.paperPreference.setOnPreferenceClickListener(this);
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("auto_adjust_effect");
        this.mAutoAdjustPreference = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        this.mAutoAdjustPreference.setChecked(autoAdjustState(this.mContext));
        PaperModePreference paperModePreference = (PaperModePreference) findPreference("paper_mode_auto_twilight");
        this.mAutoTwilightPref = paperModePreference;
        paperModePreference.setChecked(getPaperModeSchedulerType() == 1);
        this.mAutoTwilightPref.setOnPreferenceClickListener(this);
        PaperModePreference paperModePreference2 = (PaperModePreference) findPreference("paper_mode_customize_time");
        this.mCustomizedTimePref = paperModePreference2;
        paperModePreference2.setChecked(getPaperModeSchedulerType() == 2);
        this.mCustomizedTimePref.setOnPreferenceClickListener(this);
        this.mTimeZoneOffset = Calendar.getInstance().getTimeZone().getRawOffset();
        mStartTime = PaperModeTimeModeUtil.getPaperModeStartTime(this.mContext);
        mEndTime = PaperModeTimeModeUtil.getPaperModeEndTime(this.mContext);
        this.paperModeStartTime = (LabelPreference) findPreference("paper_mode_start_time");
        this.paperModeEndTime = (LabelPreference) findPreference("paper_mode_end_time");
        LabelPreference labelPreference = this.paperModeStartTime;
        int i = mStartTime;
        labelPreference.setLabel(formatChoosenTime(i / 60, i % 60));
        LabelPreference labelPreference2 = this.paperModeEndTime;
        int i2 = mEndTime;
        labelPreference2.setLabel(formatChoosenTime(i2 / 60, i2 % 60));
        this.paperModeStartTime.setOnPreferenceClickListener(this);
        this.paperModeEndTime.setOnPreferenceClickListener(this);
        if (MiuiAboutPhoneUtils.getInstance(this.mContext).isMIUILite()) {
            getPreferenceScreen().removePreference(findPreference("top_image"));
        }
        Context context = this.mContext;
        TimePickerDialog.OnTimeSetListener onTimeSetListener = this.mOnTimeSetListener;
        int i3 = mStartTime;
        this.mTimePickerDialog = new TimePickerDialog(context, onTimeSetListener, i3 / 60, i3 % 60, DateFormat.is24HourFormat(context));
        this.mPaperModeEnableObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.display.PaperModeFragment.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                PaperModeFragment.this.paperModeEnable.setChecked(PaperModeFragment.isPaperModeEnable(PaperModeFragment.this.mContext));
                PaperModeFragment paperModeFragment = PaperModeFragment.this;
                paperModeFragment.updatePaperEffectGroup(Boolean.valueOf(PaperModeFragment.isPaperModeEnable(paperModeFragment.mContext)));
            }
        };
        this.mPaperModeScheduleObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.display.PaperModeFragment.4
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (PaperModeFragment.this.getPaperModeSchedulerType() == 1) {
                    JobDispatcher.scheduleJob(PaperModeFragment.this.mContext, 44009);
                    PaperModeFragment.this.mContext.startService(new Intent(PaperModeFragment.this.mContext, PaperModeSunTimeService.class));
                } else {
                    PaperModeFragment.this.onStopLocated();
                }
                PaperModeTimeModeUtil.startPaperModeAutoTime(PaperModeFragment.this.mContext, PaperModeFragment.this.getPaperModeSchedulerType());
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("paper_mode_scheduler_type"), false, this.mPaperModeScheduleObserver);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_paper_mode_enabled"), false, this.mPaperModeEnableObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.unregisterContentObserver(this.mPaperModeEnableObserver);
        contentResolver.unregisterContentObserver(this.mPaperModeScheduleObserver);
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (getListView().isComputingLayout()) {
            return false;
        }
        String key = preference.getKey();
        if ("paper_mode_enable".equals(key)) {
            InternationalCompat.trackReportSwitchStatus("setting_Display_PE", obj);
            Boolean bool = (Boolean) obj;
            this.paperModeEnable.setChecked(bool.booleanValue());
            setPaperModeEnable(bool.booleanValue());
            updatePaperEffectGroup(bool);
            OneTrackInterfaceUtils.trackSwitchEvent("paper_mode_enable", bool.booleanValue());
            return true;
        } else if (!"paper_mode_time_enable".equals(key)) {
            if ("paper_mode_adjust_level".equals(key)) {
                setPaperModeLevel((int) ((((Integer) obj).intValue() * PER_LEVEL) + PAPER_MODE_MIN_LEVEL));
                return true;
            } else if ("auto_adjust_effect".equals(key)) {
                setAutoAdjustLevel(((Boolean) obj).booleanValue());
                return true;
            } else {
                return true;
            }
        } else {
            InternationalCompat.trackReportSwitchStatus("setting_Display_PET", obj);
            Boolean bool2 = (Boolean) obj;
            setPaperModeTimeEnable(bool2.booleanValue());
            this.paperModeTimeEnable.setChecked(bool2.booleanValue());
            PaperModeTimeModeUtil.startPaperModeAutoTime(this.mContext, bool2.booleanValue() ? getPaperModeSchedulerType() : 0);
            OneTrackInterfaceUtils.trackSwitchEvent("ScreenEffect_PapermodeTimeControl", bool2.booleanValue());
            updatePaperModeTimeGroup(bool2);
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        HashMap hashMap = new HashMap();
        if ("paper_mode_auto_twilight".equals(key)) {
            LocationManager locationManager = (LocationManager) this.mContext.getSystemService("location");
            if (Settings.Secure.getInt(this.mContext.getContentResolver(), "location_mode", 0) == 0) {
                initCallingLock();
                this.mLocationGetDialog.show();
            }
            setPaperModeSchedulerType(1);
            this.mAutoTwilightPref.setChecked(true);
            this.mCustomizedTimePref.setChecked(false);
        } else if ("paper_mode_customize_time".equals(key)) {
            setPaperModeSchedulerType(2);
            this.mAutoTwilightPref.setChecked(false);
            this.mCustomizedTimePref.setChecked(true);
        } else if ("paper_mode_start_time".equals(key)) {
            this.mTimeFlag = false;
            int i = mStartTime;
            if (i > 0) {
                this.mTimePickerDialog.updateTime(i / 60, i % 60);
            } else {
                this.mTimePickerDialog.updateTime(0, 0);
            }
            this.mTimePickerDialog.show();
        } else if ("paper_mode_end_time".equals(key)) {
            this.mTimeFlag = true;
            int i2 = mEndTime;
            if (i2 > 0) {
                this.mTimePickerDialog.updateTime(i2 / 60, i2 % 60);
            } else {
                this.mTimePickerDialog.updateTime(0, 0);
            }
            this.mTimePickerDialog.show();
        } else if ("classic_mode".equals(key)) {
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_mode_type", 0);
        } else if ("paper_mode".equals(key)) {
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_mode_type", 1);
            if (!Build.IS_INTERNATIONAL_BUILD) {
                hashMap.put("status", Boolean.TRUE);
                OneTrackInterfaceUtils.track("texture_eyecare_status", hashMap);
            }
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.paperModeEnable.setChecked(isPaperModeEnable(this.mContext));
    }

    public void onStopLocated() {
        ((JobScheduler) this.mContext.getSystemService("jobscheduler")).cancel(44009);
        this.mContext.stopService(new Intent(this.mContext, PaperModeSunTimeService.class));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (!MiuiUtils.supportPaperEyeCare()) {
            this.paperPreference.setVisible(false);
        }
        if (!MiuiUtils.supportSmartEyeCare()) {
            this.mAutoAdjustPreference.setVisible(false);
        }
        updatePaperModeTimeGroup(Boolean.valueOf(isPaperModeTimeEnable(this.mContext)));
        updatePaperEffectGroup(Boolean.valueOf(isPaperModeEnable(this.mContext)));
    }
}
