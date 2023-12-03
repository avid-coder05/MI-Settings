package miuix.stretchablewidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.DateTimePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes5.dex */
public class StretchableDatePicker extends StretchableWidget {
    private Calendar mCalendar;
    private DateTimePicker mDateTimePicker;
    private boolean mIsLunar;
    private SlidingButton mLunarButton;
    private DateTimePicker.LunarFormatter mLunarFormatter;
    private RelativeLayout mLunarLayout;
    private String mLunarResId;
    private TextView mLunarText;
    private int mMinuteInterval;
    private OnTimeChangeListener mOnTimeChangeListener;
    private LinearLayout mPickerContainer;
    private boolean mShowLunar;
    private long mTime;
    private int pickerContainerHeight;

    /* loaded from: classes5.dex */
    public interface OnTimeChangeListener {
        long onDateTimeChanged(long j);
    }

    public StretchableDatePicker(Context context) {
        this(context, null);
    }

    public StretchableDatePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StretchableDatePicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMinuteInterval = 1;
    }

    private String formatLunarTime(long j, Context context) {
        return this.mLunarFormatter.formatDay(this.mCalendar.get(1), this.mCalendar.get(5), this.mCalendar.get(9)) + " " + DateUtils.formatDateTime(context, j, 12);
    }

    private String formatSolorTime(long j, Context context) {
        return DateUtils.formatDateTime(context, j, 908);
    }

    private void init(final Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.StretchableDatePicker, i, 0);
        this.mShowLunar = obtainStyledAttributes.getBoolean(R$styleable.StretchableDatePicker_show_lunar, false);
        this.mLunarResId = obtainStyledAttributes.getString(R$styleable.StretchableDatePicker_lunar_text);
        this.mMinuteInterval = obtainStyledAttributes.getInteger(R$styleable.StretchableDatePicker_minuteInterval, 1);
        obtainStyledAttributes.recycle();
        LinearLayout linearLayout = (LinearLayout) ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.miuix_stretchable_widget_picker_part, (ViewGroup) null);
        this.mPickerContainer = linearLayout;
        this.mDateTimePicker = (DateTimePicker) linearLayout.findViewById(R$id.datetime_picker);
        this.mLunarLayout = (RelativeLayout) this.mPickerContainer.findViewById(R$id.lunar_layout);
        this.mLunarText = (TextView) this.mPickerContainer.findViewById(R$id.lunar_text);
        this.mLunarButton = (SlidingButton) this.mPickerContainer.findViewById(R$id.lunar_button);
        if (!this.mShowLunar) {
            this.mLunarLayout.setVisibility(8);
        }
        this.mLunarButton.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: miuix.stretchablewidget.StretchableDatePicker.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                StretchableDatePicker.this.mDateTimePicker.setLunarMode(z);
                StretchableDatePicker.this.showTime(z, context);
                StretchableDatePicker.this.mIsLunar = z;
            }
        });
        this.mPickerContainer.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        this.pickerContainerHeight = this.mPickerContainer.getMeasuredHeight();
        setLayout(this.mPickerContainer);
        this.mCalendar = new Calendar();
        setLunarText(this.mLunarResId);
        this.mLunarFormatter = new DateTimePicker.LunarFormatter(context);
        setMinuteInterval(this.mMinuteInterval);
        showSolarTime(context);
        this.mTime = this.mCalendar.getTimeInMillis();
        this.mDateTimePicker.setOnTimeChangedListener(new DateTimePicker.OnDateTimeChangedListener() { // from class: miuix.stretchablewidget.StretchableDatePicker.2
            @Override // miuix.pickerwidget.widget.DateTimePicker.OnDateTimeChangedListener
            public void onDateTimeChanged(DateTimePicker dateTimePicker, long j) {
                StretchableDatePicker.this.mCalendar.setTimeInMillis(j);
                StretchableDatePicker stretchableDatePicker = StretchableDatePicker.this;
                stretchableDatePicker.showTime(stretchableDatePicker.mIsLunar, context);
                StretchableDatePicker.this.mTime = j;
                if (StretchableDatePicker.this.mOnTimeChangeListener != null) {
                    StretchableDatePicker.this.mOnTimeChangeListener.onDateTimeChanged(j);
                }
            }
        });
    }

    private void showSolarTime(Context context) {
        setDetailMessage(formatSolorTime(this.mCalendar.getTimeInMillis(), context));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTime(boolean z, Context context) {
        if (z) {
            showLunarTime(context);
        } else {
            showSolarTime(context);
        }
    }

    @Override // miuix.stretchablewidget.StretchableWidget
    protected void afterSetView() {
        this.mHeight = this.pickerContainerHeight;
    }

    public long getTime() {
        return this.mTime;
    }

    @Override // miuix.stretchablewidget.StretchableWidget
    protected void preSetView(Context context, AttributeSet attributeSet, int i) {
        init(context, attributeSet, i);
    }

    public void setLunarText(String str) {
        this.mLunarText.setText(str);
    }

    public void setMinuteInterval(int i) {
        this.mDateTimePicker.setMinuteInterval(i);
    }

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        this.mOnTimeChangeListener = onTimeChangeListener;
    }

    public void showLunarTime(Context context) {
        setDetailMessage(formatLunarTime(this.mCalendar.getTimeInMillis(), context));
    }
}
