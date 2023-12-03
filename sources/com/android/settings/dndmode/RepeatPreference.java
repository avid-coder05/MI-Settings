package com.android.settings.dndmode;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settings.CustomListPreference;
import com.android.settings.R;
import com.android.settings.dndmode.Alarm;
import java.text.DateFormatSymbols;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class RepeatPreference extends CustomListPreference {
    private Alarm.DaysOfWeek mDaysOfWeek;
    private Alarm.DaysOfWeek mNewDaysOfWeek;
    private String mRepeatLabel;

    public RepeatPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDaysOfWeek = new Alarm.DaysOfWeek(0);
        this.mNewDaysOfWeek = new Alarm.DaysOfWeek(0);
        this.mRepeatLabel = null;
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        String[] strArr = {weekdays[2], weekdays[3], weekdays[4], weekdays[5], weekdays[6], weekdays[7], weekdays[1]};
        setEntries(strArr);
        setEntryValues(strArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMultiChoiceDialog() {
        new AlertDialog.Builder(getContext()).setTitle(getTitle()).setMultiChoiceItems(getEntries(), this.mDaysOfWeek.getBooleanArray(), new DialogInterface.OnMultiChoiceClickListener() { // from class: com.android.settings.dndmode.RepeatPreference.3
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                RepeatPreference.this.mNewDaysOfWeek.set(i, z);
            }
        }).setPositiveButton(R.string.button_text_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.dndmode.RepeatPreference.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                RepeatPreference.this.onDialogClosed(true);
            }
        }).setNegativeButton(R.string.button_text_cancel, (DialogInterface.OnClickListener) null).show();
    }

    public Alarm.DaysOfWeek getDaysOfWeek() {
        return this.mDaysOfWeek;
    }

    @Override // com.android.settingslib.miuisettings.preference.ListPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        ((TextView) view.findViewById(R.id.label)).setText(this.mRepeatLabel);
        super.onBindView(view);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        String[] stringArray;
        boolean z = Build.IS_INTERNATIONAL_BUILD;
        if (z) {
            stringArray = getContext().getResources().getStringArray(R.array.alarm_repeat_type_no_workdays);
        } else {
            stringArray = getContext().getResources().getStringArray(R.array.alarm_repeat_type);
            HolidayHelper.isHolidayDataInvalid(getContext());
        }
        int alarmType = this.mDaysOfWeek.getAlarmType();
        final int[] intArray = getContext().getResources().getIntArray(z ? R.array.alarm_repeat_type_no_workdays_values : R.array.alarm_repeat_type_values);
        int i = -1;
        int i2 = 0;
        while (true) {
            if (i2 >= intArray.length) {
                break;
            } else if (alarmType == intArray[i2]) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        new AlertDialog.Builder(getContext()).setSingleChoiceItems(stringArray, i, new DialogInterface.OnClickListener() { // from class: com.android.settings.dndmode.RepeatPreference.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                int i4 = intArray[i3];
                if (i4 == 1) {
                    RepeatPreference.this.mNewDaysOfWeek.set(new Alarm.DaysOfWeek(127));
                    RepeatPreference.this.onDialogClosed(true);
                } else if (i4 == 2) {
                    RepeatPreference.this.mNewDaysOfWeek.set(new Alarm.DaysOfWeek(79));
                    RepeatPreference.this.onDialogClosed(true);
                } else if (i4 == 3) {
                    RepeatPreference.this.mNewDaysOfWeek.set(new Alarm.DaysOfWeek(48));
                    RepeatPreference.this.onDialogClosed(true);
                } else if (i4 == 4) {
                    RepeatPreference.this.showMultiChoiceDialog();
                }
                dialogInterface.cancel();
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onDialogClosed(boolean z) {
        if (!z) {
            this.mNewDaysOfWeek.set(this.mDaysOfWeek);
            return;
        }
        this.mDaysOfWeek.set(this.mNewDaysOfWeek);
        setLabel(this.mDaysOfWeek.toString(getContext(), true));
        callChangeListener(this.mDaysOfWeek);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setMultiChoiceItems(getEntries(), this.mDaysOfWeek.getBooleanArray(), new DialogInterface.OnMultiChoiceClickListener() { // from class: com.android.settings.dndmode.RepeatPreference.4
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                RepeatPreference.this.mNewDaysOfWeek.set(i, z);
            }
        });
    }

    public void setDaysOfWeek(Alarm.DaysOfWeek daysOfWeek) {
        if (daysOfWeek == null) {
            return;
        }
        this.mDaysOfWeek.set(daysOfWeek);
        this.mNewDaysOfWeek.set(daysOfWeek);
        setSummary(daysOfWeek.toString(getContext(), true));
    }

    public void setLabel(String str) {
        if (TextUtils.equals(this.mRepeatLabel, str)) {
            return;
        }
        this.mRepeatLabel = str;
        notifyChanged();
    }
}
