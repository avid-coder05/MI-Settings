package com.android.settings.soundsettings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.CustomListPreference;
import com.android.settings.R;
import com.android.settings.R$styleable;
import com.android.settings.dndmode.Alarm;
import java.text.DateFormatSymbols;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class RepeatPreferenceWithBg extends CustomListPreference {
    private static final String TAG = RepeatPreferenceWithBg.class.getSimpleName();
    private int mBackground;
    private Context mContext;
    private Alarm.DaysOfWeek mDaysOfWeek;
    private boolean mIsLast;
    private Alarm.DaysOfWeek mNewDaysOfWeek;
    private String mRepeatLabel;

    public RepeatPreferenceWithBg(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDaysOfWeek = new Alarm.DaysOfWeek(0);
        this.mNewDaysOfWeek = new Alarm.DaysOfWeek(0);
        initTypedArray(context, attributeSet);
        this.mRepeatLabel = null;
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        String[] strArr = {weekdays[2], weekdays[3], weekdays[4], weekdays[5], weekdays[6], weekdays[7], weekdays[1]};
        setEntries(strArr);
        setEntryValues(strArr);
    }

    private void initTypedArray(Context context, AttributeSet attributeSet) {
        this.mContext = context;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RepeatPreferenceWithBg);
            this.mBackground = obtainStyledAttributes.getResourceId(R$styleable.RepeatPreferenceWithBg_backgroundRes, 0);
            this.mIsLast = obtainStyledAttributes.getBoolean(R$styleable.RepeatPreferenceWithBg_last, false);
            obtainStyledAttributes.recycle();
        }
    }

    private void showMultiChoiceDialog() {
        new AlertDialog.Builder(getContext()).setTitle(getTitle()).setMultiChoiceItems(getEntries(), this.mDaysOfWeek.getBooleanArray(), new DialogInterface.OnMultiChoiceClickListener() { // from class: com.android.settings.soundsettings.RepeatPreferenceWithBg.2
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                RepeatPreferenceWithBg.this.mNewDaysOfWeek.set(i, z);
            }
        }).setPositiveButton(R.string.button_text_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.soundsettings.RepeatPreferenceWithBg.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                RepeatPreferenceWithBg.this.onDialogClosed(true);
            }
        }).setNegativeButton(R.string.button_text_cancel, (DialogInterface.OnClickListener) null).show();
    }

    public Alarm.DaysOfWeek getDaysOfWeek() {
        return this.mDaysOfWeek;
    }

    @Override // com.android.settingslib.miuisettings.preference.ListPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        Resources resources = this.mContext.getResources();
        int i = R.dimen.auto_rule_margin_left;
        layoutParams.setMargins(resources.getDimensionPixelOffset(i), 0, this.mContext.getResources().getDimensionPixelOffset(i), this.mIsLast ? this.mContext.getResources().getDimensionPixelOffset(R.dimen.auto_rule_first_margin_top) : 0);
        view.setLayoutParams(layoutParams);
        int dimensionPixelOffset = this.mContext.getResources().getDimensionPixelOffset(R.dimen.auto_rule_padding_left);
        view.setPadding(dimensionPixelOffset, 0, dimensionPixelOffset, this.mIsLast ? this.mContext.getResources().getDimensionPixelOffset(R.dimen.auto_rule_last_item_padding_bottom) : 0);
        ((TextView) view.findViewById(R.id.label)).setText(this.mRepeatLabel);
        super.onBindView(view);
        int i2 = this.mBackground;
        if (i2 != 0) {
            view.setBackground(this.mContext.getDrawable(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        showMultiChoiceDialog();
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
        builder.setMultiChoiceItems(getEntries(), this.mDaysOfWeek.getBooleanArray(), new DialogInterface.OnMultiChoiceClickListener() { // from class: com.android.settings.soundsettings.RepeatPreferenceWithBg.3
            @Override // android.content.DialogInterface.OnMultiChoiceClickListener
            public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                RepeatPreferenceWithBg.this.mNewDaysOfWeek.set(i, z);
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
        setLabel(daysOfWeek.toString(getContext(), true));
    }

    public void setLabel(String str) {
        if (TextUtils.equals(this.mRepeatLabel, str)) {
            return;
        }
        this.mRepeatLabel = str;
        notifyChanged();
    }
}
