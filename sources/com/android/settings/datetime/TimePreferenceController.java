package com.android.settings.datetime;

import android.app.Activity;
import android.app.timedetector.TimeDetector;
import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Calendar;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.widget.TimePicker;

/* loaded from: classes.dex */
public class TimePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, TimePickerDialog.OnTimeSetListener {
    private final AutoTimePreferenceController mAutoTimePreferenceController;
    private final TimePreferenceHost mHost;
    TimePickerDialog timePickerDialog;

    /* loaded from: classes.dex */
    public interface TimePreferenceHost extends UpdateTimeAndDateCallback {
        void showTimePicker();
    }

    public TimePreferenceController(Context context, TimePreferenceHost timePreferenceHost, AutoTimePreferenceController autoTimePreferenceController) {
        super(context);
        this.mHost = timePreferenceHost;
        this.mAutoTimePreferenceController = autoTimePreferenceController;
    }

    public TimePickerDialog buildTimePicker(Activity activity) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, this, calendar.get(11), calendar.get(12), DateFormat.is24HourFormat(activity));
        this.timePickerDialog = timePickerDialog;
        return timePickerDialog;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "time";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals("time", preference.getKey())) {
            this.mHost.showTimePicker();
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        Context context = this.mContext;
        if (context != null) {
            Settings.System.putInt(context.getContentResolver(), "time_set_by_settings", 1);
            setTime(i, i2);
            this.mHost.updateTimeAndDateDisplay(this.mContext);
        }
        TimePickerDialog timePickerDialog = this.timePickerDialog;
        if (timePickerDialog != null) {
            timePickerDialog.dismissWithoutAnimation();
        }
    }

    void setTime(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, i);
        calendar.set(12, i2);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long max = Math.max(calendar.getTimeInMillis(), 1194220800000L);
        if (max / 1000 < 2147483647L) {
            ((TimeDetector) this.mContext.getSystemService(TimeDetector.class)).suggestManualTime(TimeDetector.createManualTimeSuggestion(max, "Settings: Set time"));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            preference.setSummary(DateFormat.getTimeFormat(this.mContext).format(Calendar.getInstance().getTime()));
            if (((RestrictedPreference) preference).isDisabledByAdmin()) {
                return;
            }
            preference.setEnabled(!this.mAutoTimePreferenceController.isEnabled());
            if (MiuiSettings.Secure.isTimeChangeDisallow(this.mContext.getContentResolver())) {
                preference.setEnabled(false);
            }
        }
    }
}
