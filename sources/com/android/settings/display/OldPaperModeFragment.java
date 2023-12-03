package com.android.settings.display;

import android.app.job.JobScheduler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
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
import com.android.settings.dndmode.LabelPreference;
import com.android.settings.report.InternationalCompat;
import com.android.settings.widget.MiuiSeekBarPreference;
import com.android.settings.widget.SeekBarPreference;
import com.android.settings.widget.TipPreference;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.Calendar;
import miui.util.FeatureParser;
import miui.vip.VipService;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.TimePicker;

/* loaded from: classes.dex */
public class OldPaperModeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final float PAPER_MODE_MAX_LEVEL;
    private static final float PAPER_MODE_MIN_LEVEL;
    private static final float PER_LEVEL;
    private static int mEndTime;
    private static int mStartTime;
    private static String sDeviceInformation;
    private static Boolean sIsLcd;
    private static Boolean sIsSupportedHdr;
    private boolean isSupportHDRMode;
    private PaperModePreference mAutoTwilightPref;
    private Context mContext;
    private PaperModePreference mCustomizedTimePref;
    AlertDialog mLocationGetDialog;
    private TipPreference mPaperHintPref;
    private PreferenceGroup mPaperModeAdjustLevelGroup;
    private PreferenceGroup mPaperModeCustomizeTimeGroup;
    private ContentObserver mPaperModeEnableObserver;
    private ContentObserver mPaperModeScheduleObserver;
    private PreferenceGroup mPaperModeTimeGroup;
    private PreferenceGroup mPaperModeTimeRadioGroup;
    private TimePickerDialog mTimePickerDialog;
    private int mTimeZoneOffset;
    private MiuiSeekBarPreference paperModeAdjustLevel;
    private CheckBoxPreference paperModeEnable;
    private LabelPreference paperModeEndTime;
    private LabelPreference paperModeStartTime;
    private CheckBoxPreference paperModeTimeEnable;
    private boolean mTimeFlag = true;
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.display.OldPaperModeFragment.1
        @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            if (OldPaperModeFragment.this.mTimeFlag) {
                int unused = OldPaperModeFragment.mEndTime = (i * 60) + i2;
                PaperModeTimeModeUtil.setPaperModeEndTime(OldPaperModeFragment.this.mContext, OldPaperModeFragment.mEndTime);
                OldPaperModeFragment.this.paperModeEndTime.setLabel(OldPaperModeFragment.this.formatChoosenTime(i, i2));
            } else {
                int unused2 = OldPaperModeFragment.mStartTime = (i * 60) + i2;
                PaperModeTimeModeUtil.setPaperModeStartTime(OldPaperModeFragment.this.mContext, OldPaperModeFragment.mStartTime);
                OldPaperModeFragment.this.paperModeStartTime.setLabel(OldPaperModeFragment.this.formatChoosenTime(i, i2));
            }
            if (OldPaperModeFragment.isPaperModeTimeEnable(OldPaperModeFragment.this.mContext)) {
                PaperModeTimeModeUtil.startPaperModeAutoTime(OldPaperModeFragment.this.mContext, 2);
            }
        }
    };
    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.display.OldPaperModeFragment.2
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                Intent intent = new Intent();
                intent.setAction("android.settings.LOCATION_SOURCE_SETTINGS");
                OldPaperModeFragment.this.mContext.startActivity(intent);
            } else if (i == -2) {
                OldPaperModeFragment.this.mLocationGetDialog.dismiss();
            }
        }
    };

    static {
        float f = MiuiSettings.ScreenEffect.PAPER_MODE_MAX_LEVEL;
        PAPER_MODE_MAX_LEVEL = f;
        float floatValue = FeatureParser.getFloat("paper_mode_min_level", 1.0f).floatValue();
        PAPER_MODE_MIN_LEVEL = floatValue;
        PER_LEVEL = (f - floatValue) / 1000.0f;
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

    public static boolean isLcd() {
        if (sIsLcd == null) {
            sIsLcd = Boolean.valueOf(("oled".equals(SystemProperties.get("ro.vendor.display.type")) || "oled".equals(SystemProperties.get("ro.display.type"))) ? false : true);
        }
        return sIsLcd.booleanValue();
    }

    public static boolean isPaperModeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_paper_mode_enabled", false);
    }

    public static boolean isPaperModeTimeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_paper_mode_time_enabled", false);
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x004f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:22:0x0047 -> B:41:0x0058). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isSupportedHdrDevice() {
        /*
            r7 = this;
            java.lang.String r0 = "IOException"
            java.lang.String r1 = "OldPaperModeFragment"
            java.lang.Boolean r2 = com.android.settings.display.OldPaperModeFragment.sIsSupportedHdr
            if (r2 != 0) goto L58
            java.lang.Boolean r2 = java.lang.Boolean.FALSE
            com.android.settings.display.OldPaperModeFragment.sIsSupportedHdr = r2
            r2 = 0
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            java.io.FileReader r4 = new java.io.FileReader     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            java.lang.String r5 = "/sys/devices/soc0/soc_id"
            r4.<init>(r5)     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            java.lang.String r2 = r3.readLine()     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            com.android.settings.display.OldPaperModeFragment.sDeviceInformation = r2     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            if (r2 == 0) goto L31
            java.lang.String r2 = r2.trim()     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            java.lang.String r4 = "321"
            boolean r2 = r2.equals(r4)     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            com.android.settings.display.OldPaperModeFragment.sIsSupportedHdr = r2     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
        L31:
            r3.close()     // Catch: java.io.IOException -> L46
            goto L58
        L35:
            r2 = move-exception
            goto L3d
        L37:
            r7 = move-exception
            goto L4d
        L39:
            r3 = move-exception
            r6 = r3
            r3 = r2
            r2 = r6
        L3d:
            android.util.Log.e(r1, r0, r2)     // Catch: java.lang.Throwable -> L4b
            if (r3 == 0) goto L58
            r3.close()     // Catch: java.io.IOException -> L46
            goto L58
        L46:
            r2 = move-exception
            android.util.Log.e(r1, r0, r2)
            goto L58
        L4b:
            r7 = move-exception
            r2 = r3
        L4d:
            if (r2 == 0) goto L57
            r2.close()     // Catch: java.io.IOException -> L53
            goto L57
        L53:
            r2 = move-exception
            android.util.Log.e(r1, r0, r2)
        L57:
            throw r7
        L58:
            java.lang.Boolean r0 = com.android.settings.display.OldPaperModeFragment.sIsSupportedHdr
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L67
            boolean r7 = r7.isSupportHDRMode
            if (r7 == 0) goto L65
            goto L67
        L65:
            r7 = 0
            goto L68
        L67:
            r7 = 1
        L68:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.display.OldPaperModeFragment.isSupportedHdrDevice():boolean");
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
    public void updateHintPref() {
        this.mPaperModeAdjustLevelGroup.removePreference(this.mPaperHintPref);
        updateSideEffectPref();
        updateHDRTipPref();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePaperAdjustLevelGroup(Boolean bool) {
        if (this.mPaperModeAdjustLevelGroup == null) {
            return;
        }
        if (bool.booleanValue()) {
            getPreferenceScreen().addPreference(this.mPaperModeAdjustLevelGroup);
        } else if (findPreference("paper_mode_adjust_level_group") != null) {
            getPreferenceScreen().removePreference(this.mPaperModeAdjustLevelGroup);
        }
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

    private void updateSideEffectPref() {
        if (isLcd()) {
            if (((float) this.paperModeAdjustLevel.getProgress()) / 1000.0f > 0.7f) {
                this.mPaperHintPref.setTitle(R.string.paper_mode_side_effect_hint);
                this.mPaperModeAdjustLevelGroup.addPreference(this.mPaperHintPref);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return OldPaperModeFragment.class.getName();
    }

    public void initCallingLock() {
        if (this.mLocationGetDialog == null) {
            this.mLocationGetDialog = new AlertDialog.Builder(this.mContext).setTitle(R.string.paper_mode_get_location_dlg_title).setMessage(R.string.paper_mode_get_location_dlg_msg).setPositiveButton(R.string.paper_mode_get_location_dlg_positive_btn_text, this.dialogListener).setNegativeButton(R.string.paper_mode_get_location_dlg_negative_btn_text, this.dialogListener).setCancelable(false).create();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.old_paper_mode_settings);
        this.mContext = getActivity();
        this.isSupportHDRMode = MiuiUtils.isSupportHDRMode();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("paper_mode_enable");
        this.paperModeEnable = checkBoxPreference;
        checkBoxPreference.setChecked(isPaperModeEnable(this.mContext));
        this.paperModeEnable.setOnPreferenceChangeListener(this);
        this.mPaperModeTimeRadioGroup = (PreferenceGroup) findPreference("paper_mode_time_radio_group");
        this.mPaperModeTimeGroup = (PreferenceGroup) findPreference("paper_mode_time_group");
        this.mPaperModeCustomizeTimeGroup = (PreferenceGroup) findPreference("paper_mode_customize_time_group");
        this.mPaperModeAdjustLevelGroup = (PreferenceGroup) findPreference("paper_mode_adjust_level_group");
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("paper_mode_time_enable");
        this.paperModeTimeEnable = checkBoxPreference2;
        checkBoxPreference2.setChecked(isPaperModeTimeEnable(this.mContext));
        this.paperModeTimeEnable.setOnPreferenceChangeListener(this);
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
        MiuiSeekBarPreference miuiSeekBarPreference = (MiuiSeekBarPreference) findPreference("paper_mode_adjust_level");
        this.paperModeAdjustLevel = miuiSeekBarPreference;
        miuiSeekBarPreference.setMax(VipService.VIP_SERVICE_FAILURE);
        this.paperModeAdjustLevel.setProgress((int) ((getPaperModeLevel() - PAPER_MODE_MIN_LEVEL) / PER_LEVEL));
        this.paperModeAdjustLevel.setOnPreferenceChangeListener(this);
        this.mPaperHintPref = (TipPreference) findPreference("paper_mode_hdr_hint");
        updateHintPref();
        this.paperModeAdjustLevel.setContinuousUpdates(true);
        this.paperModeAdjustLevel.setStopTrackingTouchListener(new SeekBarPreference.StopTrackingTouchListener() { // from class: com.android.settings.display.OldPaperModeFragment.3
            @Override // com.android.settings.widget.SeekBarPreference.StopTrackingTouchListener
            public void onStopTrackingTouch() {
                OldPaperModeFragment.this.updateHintPref();
            }
        });
        Context context = this.mContext;
        TimePickerDialog.OnTimeSetListener onTimeSetListener = this.mOnTimeSetListener;
        int i3 = mStartTime;
        this.mTimePickerDialog = new TimePickerDialog(context, onTimeSetListener, i3 / 60, i3 % 60, DateFormat.is24HourFormat(context));
        this.mPaperModeEnableObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.display.OldPaperModeFragment.4
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                boolean isPaperModeEnable = OldPaperModeFragment.isPaperModeEnable(OldPaperModeFragment.this.mContext);
                OldPaperModeFragment.this.paperModeEnable.setChecked(isPaperModeEnable);
                OldPaperModeFragment.this.updatePaperAdjustLevelGroup(Boolean.valueOf(isPaperModeEnable));
            }
        };
        this.mPaperModeScheduleObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.display.OldPaperModeFragment.5
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (OldPaperModeFragment.this.getPaperModeSchedulerType() == 1) {
                    JobDispatcher.scheduleJob(OldPaperModeFragment.this.mContext, 44009);
                    OldPaperModeFragment.this.mContext.startService(new Intent(OldPaperModeFragment.this.mContext, PaperModeSunTimeService.class));
                } else {
                    OldPaperModeFragment.this.onStopLocated();
                }
                PaperModeTimeModeUtil.startPaperModeAutoTime(OldPaperModeFragment.this.mContext, OldPaperModeFragment.this.getPaperModeSchedulerType());
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
        String key = preference.getKey();
        if ("paper_mode_enable".equals(key)) {
            InternationalCompat.trackReportSwitchStatus("setting_Display_PE", obj);
            Boolean bool = (Boolean) obj;
            this.paperModeEnable.setChecked(bool.booleanValue());
            setPaperModeEnable(bool.booleanValue());
            updatePaperAdjustLevelGroup(bool);
            return true;
        } else if (!"paper_mode_time_enable".equals(key)) {
            if ("paper_mode_adjust_level".equals(key)) {
                setPaperModeLevel((int) ((((Integer) obj).intValue() * PER_LEVEL) + PAPER_MODE_MIN_LEVEL));
                return true;
            }
            return true;
        } else {
            InternationalCompat.trackReportSwitchStatus("setting_Display_PET", obj);
            Boolean bool2 = (Boolean) obj;
            setPaperModeTimeEnable(bool2.booleanValue());
            this.paperModeTimeEnable.setChecked(bool2.booleanValue());
            PaperModeTimeModeUtil.startPaperModeAutoTime(this.mContext, bool2.booleanValue() ? getPaperModeSchedulerType() : 0);
            updatePaperModeTimeGroup(bool2);
            OneTrackInterfaceUtils.trackSwitchEvent("ScreenEffect_PapermodeTimeControl", bool2.booleanValue());
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
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
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    public void onStopLocated() {
        ((JobScheduler) this.mContext.getSystemService("jobscheduler")).cancel(44009);
        this.mContext.stopService(new Intent(this.mContext, PaperModeSunTimeService.class));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        updatePaperModeTimeGroup(Boolean.valueOf(isPaperModeTimeEnable(this.mContext)));
        updatePaperAdjustLevelGroup(Boolean.valueOf(isPaperModeEnable(this.mContext)));
    }

    public void updateHDRTipPref() {
        if ((this.paperModeAdjustLevel.getProgress() * PER_LEVEL) + 1.0f >= 4.0f && isSupportedHdrDevice()) {
            this.mPaperHintPref.setTitle(R.string.screen_paper_mode_hdr_toast_new);
            this.mPaperModeAdjustLevelGroup.addPreference(this.mPaperHintPref);
            Log.d("OldPaperModeFragment", "updateHDRTipPref: true");
            return;
        }
        Log.d("OldPaperModeFragment", "updateHDRTipPref: false " + isSupportedHdrDevice());
    }
}
