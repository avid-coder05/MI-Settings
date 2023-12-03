package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.DateTimePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes5.dex */
public class StretchablePickerPreference extends StretchableWidgetPreference {
    private Calendar mCalendar;
    private Context mContext;
    private boolean mIsLunar;
    private CharSequence mLunar;
    private DateTimePicker.LunarFormatter mLunarFormatter;
    private int mMinuteInterval;
    private OnTimeChangeListener mOnTimeChangeListener;
    private boolean mShowLunar;
    private long mTime;

    /* loaded from: classes5.dex */
    public interface OnTimeChangeListener {
        long onDateTimeChanged(long j);
    }

    public StretchablePickerPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.stretchablePickerPreferenceStyle);
    }

    public StretchablePickerPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Calendar calendar = new Calendar();
        this.mCalendar = calendar;
        this.mTime = calendar.getTimeInMillis();
        this.mContext = context;
        this.mLunarFormatter = new DateTimePicker.LunarFormatter(context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.StretchablePickerPreference, i, 0);
        this.mShowLunar = obtainStyledAttributes.getBoolean(R$styleable.StretchablePickerPreference_show_lunar, false);
        obtainStyledAttributes.recycle();
    }

    private void changeTimeState(SlidingButton slidingButton, final DateTimePicker dateTimePicker) {
        slidingButton.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: miuix.preference.StretchablePickerPreference.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                dateTimePicker.setLunarMode(z);
                StretchablePickerPreference.this.showTime(z, dateTimePicker.getTimeInMillis());
                StretchablePickerPreference.this.mIsLunar = z;
            }
        });
    }

    private String formatLunarTime(long j, Context context) {
        return this.mLunarFormatter.formatDay(this.mCalendar.get(1), this.mCalendar.get(5), this.mCalendar.get(9)) + " " + DateUtils.formatDateTime(context, j, 12);
    }

    private String formatSolorTime(long j) {
        return DateUtils.formatDateTime(this.mContext, j, 908);
    }

    private CharSequence getLunarText() {
        return this.mLunar;
    }

    private int getMinuteInterval() {
        return this.mMinuteInterval;
    }

    private void showSolarTime(long j) {
        setDetailMsgText(formatSolorTime(j));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTime(boolean z, long j) {
        if (z) {
            showLunarTime(j);
        } else {
            showSolarTime(j);
        }
    }

    private void updateTime(DateTimePicker dateTimePicker) {
        dateTimePicker.setOnTimeChangedListener(new DateTimePicker.OnDateTimeChangedListener() { // from class: miuix.preference.StretchablePickerPreference.1
            @Override // miuix.pickerwidget.widget.DateTimePicker.OnDateTimeChangedListener
            public void onDateTimeChanged(DateTimePicker dateTimePicker2, long j) {
                StretchablePickerPreference.this.mCalendar.setTimeInMillis(j);
                StretchablePickerPreference stretchablePickerPreference = StretchablePickerPreference.this;
                stretchablePickerPreference.showTime(stretchablePickerPreference.mIsLunar, j);
                StretchablePickerPreference.this.mTime = j;
                if (StretchablePickerPreference.this.mOnTimeChangeListener != null) {
                    StretchablePickerPreference.this.mOnTimeChangeListener.onDateTimeChanged(StretchablePickerPreference.this.mTime);
                }
                StretchablePickerPreference.this.notifyChanged();
            }
        });
    }

    @Override // miuix.preference.StretchableWidgetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View view = preferenceViewHolder.itemView;
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R$id.lunar_layout);
        DateTimePicker dateTimePicker = (DateTimePicker) view.findViewById(R$id.datetime_picker);
        SlidingButton slidingButton = (SlidingButton) view.findViewById(R$id.lunar_button);
        TextView textView = (TextView) view.findViewById(R$id.lunar_text);
        if (!this.mShowLunar) {
            relativeLayout.setVisibility(8);
        } else if (textView != null) {
            CharSequence lunarText = getLunarText();
            if (!TextUtils.isEmpty(lunarText)) {
                textView.setText(lunarText);
            }
        }
        dateTimePicker.setMinuteInterval(getMinuteInterval());
        this.mTime = dateTimePicker.getTimeInMillis();
        super.onBindViewHolder(preferenceViewHolder);
        changeTimeState(slidingButton, dateTimePicker);
        showTime(this.mIsLunar, dateTimePicker.getTimeInMillis());
        updateTime(dateTimePicker);
    }

    public void showLunarTime(long j) {
        setDetailMsgText(formatLunarTime(j, this.mContext));
    }
}
