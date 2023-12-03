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
import com.miui.enterprise.RestrictionsHelper;
import java.util.Calendar;
import miuix.appcompat.app.DatePickerDialog;
import miuix.pickerwidget.widget.DatePicker;

/* loaded from: classes.dex */
public class DatePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, DatePickerDialog.OnDateSetListener {
    DatePickerDialog datePickerDialog;
    private final AutoTimePreferenceController mAutoTimePreferenceController;
    private final DatePreferenceHost mHost;

    /* loaded from: classes.dex */
    public interface DatePreferenceHost extends UpdateTimeAndDateCallback {
        void showDatePicker();
    }

    public DatePreferenceController(Context context, DatePreferenceHost datePreferenceHost, AutoTimePreferenceController autoTimePreferenceController) {
        super(context);
        this.mHost = datePreferenceHost;
        this.mAutoTimePreferenceController = autoTimePreferenceController;
    }

    public DatePickerDialog buildDatePicker(Activity activity) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, this, calendar.get(1), calendar.get(2), calendar.get(5));
        calendar.clear();
        calendar.set(2007, 10, 5);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.clear();
        calendar.set(2037, 11, 31);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.setLunarMode(false);
        this.datePickerDialog = datePickerDialog;
        return datePickerDialog;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "date";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "date")) {
            this.mHost.showDatePicker();
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // miuix.appcompat.app.DatePickerDialog.OnDateSetListener
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        Settings.System.putInt(this.mContext.getContentResolver(), "time_set_by_settings", 1);
        setDate(i, i2, i3);
        this.mHost.updateTimeAndDateDisplay(this.mContext);
        DatePickerDialog datePickerDialog = this.datePickerDialog;
        if (datePickerDialog != null) {
            datePickerDialog.dismissWithoutAnimation();
        }
    }

    void setDate(int i, int i2, int i3) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, i);
        calendar.set(2, i2);
        calendar.set(5, i3);
        long max = Math.max(calendar.getTimeInMillis(), 1194220800000L);
        if (max / 1000 < 2147483647L) {
            ((TimeDetector) this.mContext.getSystemService(TimeDetector.class)).suggestManualTime(TimeDetector.createManualTimeSuggestion(max, "Settings: Set date"));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            preference.setSummary(DateFormat.getLongDateFormat(this.mContext).format(Calendar.getInstance().getTime()));
            if (!((RestrictedPreference) preference).isDisabledByAdmin()) {
                preference.setEnabled(!this.mAutoTimePreferenceController.isEnabled());
            }
            if (MiuiSettings.Secure.isTimeChangeDisallow(this.mContext.getContentResolver())) {
                preference.setEnabled(false);
            }
            if (RestrictionsHelper.hasRestriction(preference.getContext(), "disallow_timeset")) {
                preference.setEnabled(false);
            }
        }
    }
}
